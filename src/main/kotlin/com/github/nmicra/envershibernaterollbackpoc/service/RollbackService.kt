package com.github.nmicra.envershibernaterollbackpoc.service

import com.github.nmicra.envershibernaterollbackpoc.entity.RollbackLog
import com.github.nmicra.envershibernaterollbackpoc.repository.EnversRevisionRepository
import com.github.nmicra.envershibernaterollbackpoc.repository.RollbackLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Service
class RollbackService {

    @Autowired
    lateinit var enversRevisionRepository: EnversRevisionRepository

    @Autowired
    lateinit var rollbackLogRepository: RollbackLogRepository


    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var initData: InitData

    var rowMapper: RowMapper<Revision> = RowMapper<Revision> { resultSet: ResultSet, rowIndex: Int ->
        Revision(resultSet.getLong("rev"), resultSet.getLong("revtype"))
    }


    @Transactional
    fun rollbackRequest(actionId: String) {
        val revisions = enversRevisionRepository.findAllByActionId(actionId).sortedByDescending { it.revisionDate }
        for (revision in revisions) {
            val affectedRecords = revision.tablesAndIDs!!.split(",")
                .associate {
                    it.split(":")
                        .let { (tableName, rowId) -> tableName to rowId }
                }
            for ((tableName, originalTableIdToRollback) in affectedRecords) {

                val audTable = "${tableName}_aud"


                var audSortedRevisions = jdbcTemplate.query(
                    "SELECT * FROM $audTable WHERE $originalTableIdToRollback",
                    rowMapper
                ).filterNot { it.rev > revision.id }
                    .sortedByDescending { it.rev }


                val updateStr = when {

                    audSortedRevisions[0].revType == 0L -> // DELETE
                        """
                    DELETE FROM $tableName
                    WHERE $tableName.$originalTableIdToRollback
                    """.trimIndent()

                    audSortedRevisions[0].revType == 1L -> { // UPDATE
                        val fieldsToUpdateInRollback = initData.auditTableFieldsMetadata
                            .filter { it.tableName == "${tableName}_aud" }
                            .filterNot { it.columnName == "rev" || it.columnName == "revtype" }
                            .filterNot { it.columnName == originalTableIdToRollback.split("=")[0] } // exclude primary-key [id]
                            .map { it.columnName }.toSet()
                        """
                            UPDATE $tableName
                            SET
                            ${getUpdateStatementString(fieldsToUpdateInRollback)}
                            FROM $audTable aud
                            WHERE aud.rev=${audSortedRevisions[1].rev} AND $tableName.${originalTableIdToRollback}
                        """.trimIndent()
                    }


                    audSortedRevisions[0].revType == 2L -> { // INSERT
                        val fieldsToInsertInRollback = initData.auditTableFieldsMetadata
                            .filter { it.tableName == "${tableName}_aud" }
                            .filterNot { it.columnName == "rev" || it.columnName == "revtype" }
                            .map { it.columnName }.toSet()
                        """
                            INSERT INTO $tableName(${fieldsToInsertInRollback.joinToString(",")})
                            SELECT ${fieldsToInsertInRollback.joinToString(",")} FROM $audTable aud
                            WHERE aud.${originalTableIdToRollback} AND aud.rev=${audSortedRevisions[1].rev}
                        """.trimIndent()
                    }
                    else -> error("not supported case $audSortedRevisions")
                }

                println(">>> $updateStr")
                jdbcTemplate.execute(updateStr)
                val rollbackLog = RollbackLog().also { it.actionId = actionId
                                                            it.sqlStatement = updateStr}
                rollbackLogRepository.save(rollbackLog)
            }
        }

    }

    private fun getUpdateStatementString(fields : Set<String>) = fields.joinToString(",") { "${it}=aud.${it}" }
}

data class Revision(val rev: Long, val revType: Long)