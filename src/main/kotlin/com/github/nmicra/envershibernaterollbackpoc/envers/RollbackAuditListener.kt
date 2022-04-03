package com.github.nmicra.envershibernaterollbackpoc.envers

import com.github.nmicra.envershibernaterollbackpoc.context.RollbackContextProvider
import org.hibernate.metamodel.internal.MetamodelImpl
import org.hibernate.persister.entity.SingleTableEntityPersister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.util.ReflectionUtils
import javax.persistence.*

class RollbackAuditListener {

    @Autowired
    lateinit var contextProvider : RollbackContextProvider

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @PreUpdate
    @PreRemove
    @PostPersist
    private fun beforeAnyOperation(modifiedObj: Any) {

        val table = extractTableName(modifiedObj.javaClass)
        val idField = requireNotNull(ReflectionUtils.findField(modifiedObj.javaClass, "id"))
                                                        {"The id field of @Audited table MUST be called `id`"}
        idField.isAccessible = true
        val realIdColumnName = idField.getAnnotation(Column::class.java)?.name ?: "id"
        contextProvider.addTableAndID(table, "$realIdColumnName='${idField[modifiedObj]}'")
    }

    private fun extractTableName(modelClazz: Class<*>): String {
        val metamodel = applicationContext.getBean(EntityManager::class.java).metamodel as MetamodelImpl
        val entityPersister = metamodel.entityPersister(modelClazz)
        return if (entityPersister is SingleTableEntityPersister) {
            entityPersister.tableName
        } else {
            throw IllegalArgumentException("$modelClazz does not map to a single table.")
        }
    }
}