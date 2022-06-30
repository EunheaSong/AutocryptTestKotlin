package com.autocrypttest.security.provider

import com.autocrypttest.security.UserDetailsImpl
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.annotation.Resource

class FormLoginAuthProvider(passwordEncoder: BCryptPasswordEncoder?) : AuthenticationProvider {

    @Resource(name = "userDetailsServiceImpl") val userDetailsService: UserDetailsService? = null
    var passwordEncoder: BCryptPasswordEncoder? = null

//    fun FormLoginAuthProvider(passwordEncoder: BCryptPasswordEncoder) {
//        this.passwordEncoder = passwordEncoder
//    }

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        val token = authentication as UsernamePasswordAuthenticationToken
        // FormLoginFilter 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        val username = token.name
        val password = token.credentials as String

        // UserDetailsService 를 통해 DB에서 username 으로 사용자 조회
        val userDetails: UserDetailsImpl = userDetailsService!!.loadUserByUsername(username) as UserDetailsImpl
        if (!passwordEncoder!!.matches(password, userDetails.password)) {
            throw BadCredentialsException(userDetails.username + "Invalid password")
        }
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}