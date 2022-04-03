package com.github.nmicra.envershibernaterollbackpoc.repository

import com.github.nmicra.envershibernaterollbackpoc.entity.CustomerEntity
import com.github.nmicra.envershibernaterollbackpoc.entity.EnversRevisionEntity
import com.github.nmicra.envershibernaterollbackpoc.entity.RollbackLog
import com.github.nmicra.envershibernaterollbackpoc.entity.SubscriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import java.util.stream.Stream

interface CustomerRepository : JpaRepository<CustomerEntity, UUID> {
}

interface SubscriberRepository : JpaRepository<SubscriberEntity, UUID> {
}

interface EnversRevisionRepository : JpaRepository<EnversRevisionEntity, Long> {

    fun findAllByActionId(actionId: String): List<EnversRevisionEntity>
}

interface RollbackLogRepository : JpaRepository<RollbackLog, Long> {
}
