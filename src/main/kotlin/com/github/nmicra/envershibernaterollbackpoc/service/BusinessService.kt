package com.github.nmicra.envershibernaterollbackpoc.service

import com.github.nmicra.envershibernaterollbackpoc.entity.SubscriberEntity
import com.github.nmicra.envershibernaterollbackpoc.repository.SubscriberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BusinessService {

    @Autowired
    lateinit var subscriberRepository: SubscriberRepository

    @Transactional
    fun updateSubscriberBalanceAndCustomerPath(subscriberUUID : String){
        val subsc : Optional<SubscriberEntity> = subscriberRepository.findById(UUID.fromString(subscriberUUID))
        if (!subsc.isPresent) error("subscriberId [$subscriberUUID] doesn't exist")
        subsc.ifPresent {
            it.balance = (1..1000).random().toLong()
            it.customer?.path = "@here => ${System.currentTimeMillis()}"
        }
        subscriberRepository.save(subsc.get())
    }
}