package com.autocrypttest.repository

import com.autocrypttest.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long>{

    fun findByUsername(username: String?): Optional<User?>?
    fun findByNickname(nickname: String?): Optional<User?>?

}