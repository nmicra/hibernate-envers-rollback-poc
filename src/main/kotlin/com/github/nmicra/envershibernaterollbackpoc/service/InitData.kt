package com.github.nmicra.envershibernaterollbackpoc.service

import com.github.nmicra.envershibernaterollbackpoc.context.RollbackContextProvider
import com.github.nmicra.envershibernaterollbackpoc.entity.Customer
import com.github.nmicra.envershibernaterollbackpoc.entity.Subscriber
import com.github.nmicra.envershibernaterollbackpoc.entity.toEntity
import com.github.nmicra.envershibernaterollbackpoc.repository.CustomerRepository
import com.github.nmicra.envershibernaterollbackpoc.repository.SubscriberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.time.Instant
import java.util.*
import javax.annotation.PostConstruct

@Service
class InitData {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var subscriberRepository: SubscriberRepository

    @Autowired
    lateinit var contextProvider: RollbackContextProvider

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    lateinit var auditTableFieldsMetadata:  List<AuditTableField>


    @PostConstruct
    fun initData(){

        auditTableFieldsMetadata = createFieldsMetadata()
        println("$auditTableFieldsMetadata")
        contextProvider.setActionId("init-data")

        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val customer1 =  Customer(uuid1, Instant.now(), Instant.now(),null, "$uuid1")
        val customer2 =  Customer(uuid2, Instant.now(), Instant.now(),uuid1, "$uuid1/$uuid2")
        customerRepository.save(customer1.toEntity())
        customerRepository.save(customer2.toEntity())


        val subscriber1 =  Subscriber(UUID.randomUUID(), Instant.now(), Instant.now(),100, "a/b", customer2)
        val subscriber2 =  Subscriber(UUID.randomUUID(), Instant.now(), Instant.now(),200, "c/d", customer2)
        val subscriber3 =  Subscriber(UUID.randomUUID(), Instant.now(), Instant.now(),300, "c/d/e", customer1)

        subscriberRepository.save(subscriber1.toEntity())
        subscriberRepository.save(subscriber2.toEntity())
        subscriberRepository.save(subscriber3.toEntity())

        contextProvider.clear()
    }

    private fun createFieldsMetadata() : List<AuditTableField>{

        val rowMapper: RowMapper<AuditTableField> = RowMapper<AuditTableField> { resultSet: ResultSet, _: Int ->
            AuditTableField(resultSet.getString("table_name"), resultSet.getString("column_name"), resultSet.getString("data_type"))
        }
        var results = jdbcTemplate.query("""
            SELECT *
                FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name   like '%_aud'
        """.trimIndent(), rowMapper)
        return results
    }

}

data class AuditTableField(val tableName : String, val columnName : String, val dataType : String)