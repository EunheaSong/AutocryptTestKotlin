package com.autocrypttest.security.filter

import com.autocrypttest.security.FilterSkipMatcher
import com.autocrypttest.security.jwt.HeaderTokenExtractor
import com.autocrypttest.security.jwt.JwtPreprocessingToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthFilter(matcher: FilterSkipMatcher, extractor: HeaderTokenExtractor) : AbstractAuthenticationProcessingFilter() {

    private var extractor: HeaderTokenExtractor? = null

    constructor(
            requiresAuthenticationRequestMatcher: RequestMatcher?,
            extractor: HeaderTokenExtractor
    ) : this() {
        super(requiresAuthenticationRequestMatcher)
        this.extractor = extractor
    }

    @Throws(AuthenticationException::class, IOException::class)
    override fun attemptAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse?
    ): Authentication? {

        // JWT 값을 담아주는 변수 TokenPayload
        val tokenPayload = request.getHeader("Authorization")
        if (tokenPayload == null) {
            println("올바른 토큰 정보가 아닙니다.")
            return null
        }
        val jwtToken = JwtPreprocessingToken(
                extractor?.extract(tokenPayload, request))
        return super
                .getAuthenticationManager()
                .authenticate(jwtToken)
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
            request: HttpServletRequest?,
            response: HttpServletResponse?,
            chain: FilterChain,
            authResult: Authentication?
    ) {

        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)

        chain.doFilter(
                request,
                response
        )
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
            request: HttpServletRequest?,
            response: HttpServletResponse?,
            failed: AuthenticationException?
    ) {

        SecurityContextHolder.clearContext()
        super.unsuccessfulAuthentication(
                request,
                response,
                failed
        )
    }
}