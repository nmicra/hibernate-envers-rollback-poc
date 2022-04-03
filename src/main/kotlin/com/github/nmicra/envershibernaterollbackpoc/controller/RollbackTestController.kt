package com.github.nmicra.envershibernaterollbackpoc.controller

import com.github.nmicra.envershibernaterollbackpoc.context.RollbackContextProvider
import com.github.nmicra.envershibernaterollbackpoc.entity.Customer
import com.github.nmicra.envershibernaterollbackpoc.entity.Subscriber
import com.github.nmicra.envershibernaterollbackpoc.entity.SubscriberEntity
import com.github.nmicra.envershibernaterollbackpoc.entity.toEntity
import com.github.nmicra.envershibernaterollbackpoc.repository.CustomerRepository
import com.github.nmicra.envershibernaterollbackpoc.repository.SubscriberRepository
import com.github.nmicra.envershibernaterollbackpoc.service.BusinessService
import com.github.nmicra.envershibernaterollbackpoc.service.RollbackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*
import javax.annotation.PostConstruct

@RestController
class RollbackTestController {


    @Autowired
    lateinit var businessService: BusinessService

    @Autowired
    lateinit var contextProvider : RollbackContextProvider

    @Autowired
    lateinit var rollbackService : RollbackService

    @Autowired
    lateinit var subscriberRepository: SubscriberRepository

    @GetMapping("/update/{uuid}")
    fun update(@PathVariable uuid : String) : String {
        businessService.updateSubscriberBalanceAndCustomerPath(uuid)
        return contextProvider.getActionId() ?: "something went wrong - NO actionID"
    }

    @GetMapping("/delete/{uuid}")
    fun deleteSubscriber(@PathVariable uuid : String) : String {
        subscriberRepository.deleteById(UUID.fromString(uuid))
        return contextProvider.getActionId() ?: "something went wrong - NO actionID"
    }

    @GetMapping("/rollback/{uuid}")
    fun rollback(@PathVariable uuid : String) : String {
        rollbackService.rollbackRequest(uuid)
        return "done!"
    }
}