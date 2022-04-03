package com.github.nmicra.envershibernaterollbackpoc.entity

import com.github.nmicra.envershibernaterollbackpoc.envers.RollbackAuditListener
import com.github.nmicra.envershibernaterollbackpoc.envers.RollbackRevisionEntityListener
import org.hibernate.annotations.Cascade
import org.hibernate.envers.Audited
import java.time.Instant
import java.util.*
import javax.persistence.*

@Audited
@EntityListeners(RollbackAuditListener::class)
@Entity
@Table(name = "subscriber")
open class SubscriberEntity {
    @Id
    @Column(name = "subscriber_id", nullable = false)
    open var id: UUID? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    open var customer: CustomerEntity? = null

    @Column(name = "created_date", nullable = false)
    open var createdDate: Instant? = null

    @Column(name = "updated_date")
    open var updatedDate: Instant? = null

    @Column(name = "balance")
    open var balance: Long? = null

    @Column(name = "path", nullable = false, length = 512)
    open var path: String? = null

}

data class Subscriber(
    var id: UUID? = null,
    val createdDate: Instant,
    val updatedDate: Instant,
    val balance: Long,
    val path: String,
    val customer: Customer? = null
)

fun Subscriber.toEntity() = SubscriberEntity().also {
    it.id = this.id
    it.createdDate = this.createdDate
    it.updatedDate = this.updatedDate
    it.balance = this.balance
    it.path = this.path
    it.customer = this.customer?.toEntity()
}

fun SubscriberEntity.toDataClass() = Subscriber(
    this.id, this.createdDate ?: Instant.now(),
    this.updatedDate ?: Instant.now(), this.balance ?: 0, this.path ?: "", this.customer?.toDataClass()
)