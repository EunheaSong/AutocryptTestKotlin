package com.autocrypttest.security.provider

import com.autocrypttest.repository.UserRepository
import com.autocrypttest.security.UserDetailsImpl
import com.autocrypttest.security.jwt.JwtDecoder
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import com.autocrypttest.entity.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletResponse

@Component
class JWTAuthProvider(val jwtDecoder: JwtDecoder, val userRepository: UserRepository, val response: HttpServletResponse) : AuthenticationProvider {


    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        val token = authentication.principal as String
        val username: String? = jwtDecoder.decodeUsername(token)//복호화

        val user: User? = userRepository.findByUsername(username)
                .orElseThrow { UsernameNotFoundException("Can't find $username") }
        val userDetails = UserDetailsImpl(user)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return JwtPreProcessingToken::class.java.isAssignableFrom(authentication)
    }
}