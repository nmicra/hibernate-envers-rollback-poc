package com.github.nmicra.envershibernaterollbackpoc.envers

import com.github.nmicra.envershibernaterollbackpoc.context.RollbackContextProvider
import com.github.nmicra.envershibernaterollbackpoc.entity.EnversRevisionEntity
import org.hibernate.envers.RevisionListener
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class RollbackRevisionEntityListener : RevisionListener{

    @Autowired
    lateinit var contextProvider: RollbackContextProvider

    override fun newRevision(revisionEntity: Any?) {
        val rev = revisionEntity as EnversRevisionEntity
        rev.actionId = contextProvider.getActionId() ?: "source-not-recognized-${System.currentTimeMillis()}"
        rev.tablesAndIDs = contextProvider.getTableIDSConcatStr()
    }
}