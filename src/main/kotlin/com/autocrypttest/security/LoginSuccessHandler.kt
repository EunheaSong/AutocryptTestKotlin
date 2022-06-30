package com.autocrypttest.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginSuccessHandler : SavedRequestAwareAuthenticationSuccessHandler() {

    val AUTH_HEADER = "Authorization"

    val TOKEN_TYPE = "BEARER"

    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse,
                                         authentication: Authentication) {
        val userDetails = authentication.principal as UserDetailsImpl
        val token: String = JwtTokenUtils.generateJwtToken(userDetails)
        response.addHeader(AUTH_HEADER, "$TOKEN_TYPE $token")
    }
}