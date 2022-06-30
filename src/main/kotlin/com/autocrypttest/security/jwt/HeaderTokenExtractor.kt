package com.autocrypttest.security.jwt

import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class HeaderTokenExtractor {

    val HEADER_PREFIX = "Bearer "

    fun extract(header: String?, request: HttpServletRequest): String? {
        /*
         * - Token 값이 올바르지 않은경우 -
         * header 값이 비어있거나 또는 HEADER_PREFIX 값보다 짧은 경우
         */
        if (header == null || header == "" || header.length < HEADER_PREFIX.length) {
            println("error request : " + request.requestURI)
            throw NoSuchElementException("올바른 JWT 정보가 아닙니다.")
        }

        /*
         * - Token 값이 존재하는 경우 -
         * (bearer ) 부분만 제거 후 token 값 반환
         */return header.substring(
                HEADER_PREFIX.length,
                header.length
        )
    }
}