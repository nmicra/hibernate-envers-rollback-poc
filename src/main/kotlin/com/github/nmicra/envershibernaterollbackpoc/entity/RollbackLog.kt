package com.github.nmicra.envershibernaterollbackpoc.entity

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "rollback_log")
open class RollbackLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "created_date", nullable = false)
    open var createdDate: Instant = Instant.now()

    @Column(name = "action_id", nullable = false, length = 100)
    open var actionId: String? = null

    @Column(name = "sql_statement", nullable = false, length = 512)
    open var sqlStatement: String? = null
}