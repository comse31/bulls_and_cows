package org.w2fc.covs.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.w2fc.covs.model.UserEntity
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUserName(userName: String): UserEntity?
}