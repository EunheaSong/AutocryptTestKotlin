package com.autocrypttest.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class JwtPreprocessingToken(extract: String?) : UsernamePasswordAuthenticationToken(){

    fun JwtPreProcessingToken(
            principal: Any,
            credentials: Any
    ) {
        super(
                principal,
                credentials
        )
    }

    fun JwtPreProcessingToken(token: String) {
        this(
                token,
                token.length
        )
    }
}