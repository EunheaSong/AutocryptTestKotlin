package com.autocrypttest.security.filter

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FormLoginFilter : UsernamePasswordAuthenticationFilter() {

    private var objectMapper: ObjectMapper? = null

    fun FormLoginFilter(authenticationManager: AuthenticationManager?) {
        super.setAuthenticationManager(authenticationManager)
        objectMapper = ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        //http 애플리케이션 Json으로 넘어오는 정보를 뽑아서 확인한다.
        //objectMapper를 통해서 Json을 자바 형태로 전환해준다.
        val authRequest: UsernamePasswordAuthenticationToken = try {
            val requestBody = objectMapper!!.readTree(request.inputStream)
            val username = requestBody["username"].asText()
            val password = requestBody["password"].asText()
            UsernamePasswordAuthenticationToken(username, password)
            //objectMapper에서 받아온 username과 password를 토큰으로 생성해준다.
        } catch (e: Exception) {
            throw RuntimeException("username, password 입력이 필요합니다. (JSON)")
        }
        setDetails(request, authRequest)
        return this.authenticationManager.authenticate(authRequest)
        // 만든 토큰으로 authenticate요청을 보낸다.
    }
}