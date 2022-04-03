package com.github.nmicra.envershibernaterollbackpoc.context

import org.springframework.stereotype.Service

@Service
class RollbackContextProvider {

    private val actionIdContext = ThreadLocal<String>()
    private val tableAndIDsConcatContext = ThreadLocal<String>()

    fun setActionId(ActionId: String) {
        actionIdContext.set(ActionId)
    }

    fun getActionId(): String? {
        return actionIdContext.get()
    }

    fun getTableIDSConcatStr(): String? {
        return tableAndIDsConcatContext.get()
    }

    fun addTableAndID(table: String, id: String) {
        val newEntry = "$table:$id"
        val existing = tableAndIDsConcatContext.get()
        if (existing == null || "" == existing) {
            tableAndIDsConcatContext.set(newEntry)
        } else {
            tableAndIDsConcatContext.set("$existing,$newEntry")
        }
//        tableAndIDsConcatContext.set("$table:$id")
    }

    fun clear() {
        tableAndIDsConcatContext.remove()
        actionIdContext.remove()
    }
}