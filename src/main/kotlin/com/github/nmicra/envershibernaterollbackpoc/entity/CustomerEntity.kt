package com.github.nmicra.envershibernaterollbackpoc.entity

import com.github.nmicra.envershibernaterollbackpoc.envers.RollbackAuditListener
import com.github.nmicra.envershibernaterollbackpoc.envers.RollbackRevisionEntityListener
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.GenericGenerator
import org.hibernate.envers.Audited
import java.time.Instant
import java.util.*
import javax.persistence.*

@Audited
@EntityListeners(RollbackAuditListener::class)
@Entity
@Table(name = "customer")
open class CustomerEntity {
    @Id
    @Column(name = "customer_id", nullable = false)
    open var id: UUID? = null

    @Column(name = "created_date", nullable = false)
    open var createdDate: Instant? = null

    @Column(name = "updated_date")
    open var updatedDate: Instant? = null

    @Column(name = "path", nullable = false, length = 512)
    open var path: String? = null

    @Column(name = "parent_customer_id")
    open var parentCustomerId: UUID? = null

    @OneToMany(mappedBy = "customer")
    @Cascade(value=[org.hibernate.annotations.CascadeType.ALL])
    open var subscribers: MutableSet<SubscriberEntity> = mutableSetOf()
}

data class Customer(
    var id: UUID? = null,
    val createdDate: Instant,
    val updatedDate: Instant,
    var parentCustomerId: UUID? = null,
    val path: String,
    val subscribers: MutableSet<Subscriber> = mutableSetOf()
)

fun Customer.toEntity() :CustomerEntity {
    return CustomerEntity().also { it ->
        it.id = this.id
        it.createdDate = this.createdDate
        it.updatedDate = this.updatedDate
        it.parentCustomerId = this.parentCustomerId
        it.path = this.path
        it.subscribers = this.subscribers.map { subscriber ->  subscriber.toEntity() }.toMutableSet()
    }
}

fun CustomerEntity.toDataClass() : Customer {
    return Customer(
        this.id, this.createdDate ?: Instant.now(),
        this.updatedDate ?: Instant.now(), this.parentCustomerId, this.path ?: "",this.subscribers.map { subscriber ->  subscriber.toDataClass() }.toMutableSet()
    )
}