package com.github.nmicra.envershibernaterollbackpoc.entity

import com.github.nmicra.envershibernaterollbackpoc.envers.RollbackRevisionEntityListener
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table


@Table(name = "envers_revision")
@Entity
@RevisionEntity(RollbackRevisionEntityListener::class)
open class EnversRevisionEntity : DefaultRevisionEntity() {


    @Column(name = "actionId", nullable = false, length = 100)
    open var actionId: String? = null


    @Column(name = "tables_and_ids", nullable = false, length = 512)
    open var tablesAndIDs: String? = null
}