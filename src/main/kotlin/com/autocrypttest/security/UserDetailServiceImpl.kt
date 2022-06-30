package com.autocrypttest.security

import com.autocrypttest.entity.User
import com.autocrypttest.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl @Autowired constructor(userRepository: UserRepository) : UserDetailsService {

    private final var userRepository: UserRepository? = userRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val user: User? = userRepository?.findByUsername(username)
                ?.orElseThrow { UsernameNotFoundException("Can't find $username") }
        return UserDetailsImpl(user)
    }
}