package com.autocrypttest.security.jwt

import com.autocrypttest.security.UserDetailsImpl
import org.springframework.security.config.Elements.JWT
import java.util.*

final class JwtTokenUtils : JwtProperties {

    val SEC = 1
    val MINUTE = 60 * SEC
    val HOUR = 60 * MINUTE
    val DAY = 24 * HOUR

    // JWT 토큰의 유효기간: 1시간 (단위: seconds)
    val JWT_TOKEN_VALID_SEC = DAY * 3
    // JWT 토큰의 유효기간: 1시간 (단위: milliseconds)
    val JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000

    val CLAIM_EXPIRED_DATE = "EXPIRED_DATE"
    val CLAIM_USER_NAME = "USER_NAME"
    val CLAIM_USER_NIK = "USER_NIK"

    val JWT_SECRET: String = key //todo : 시크릿키 보이지않도록 프로퍼티스에 넣어놓기


    fun generateJwtToken(userDetails: UserDetailsImpl): String? {
        var token: String? = null
        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_NAME, userDetails.getUsername())
                    .withClaim(CLAIM_USER_NIK, userDetails.getUserNickname()) // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm())
        } catch (e: Exception) {
            println(e.message)
        }
        return token
    }

    /*
          todo : refresh token 생성! 사용자 이메일 + 유효기간만 담아주기.
     */
    fun generateJwtReFreshToken(userDetails: UserDetailsImpl): String? {
        var reFreshToken: String? = null
        try {
            reFreshToken = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_NAME, userDetails.getUsername()) // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간) //30분 부여 .
                    .withClaim(CLAIM_EXPIRED_DATE, Date(System.currentTimeMillis() + MINUTE * 30 * 1000))
                    .sign(generateAlgorithm())
        } catch (e: Exception) {
            println(e.message)
        }
        return reFreshToken
    }

    /*
    todo : access token을 재발급 해주는 메소드를 생성!
        generateJwtToken 처럼 정적 메소드로 만들어주면, 사용처에서 메소드를 임포트만 해주면 간단히 사용할 수 있다.
        -> 정적 메소드로 만들어주자 . => static method
        한번 사용되었던 access token은 블랙리스트 라고 불리우며 저장 공간을 마련해서 저장을 한다고 한다.
        -> 아마도 이미 만들어진 토큰과 똑같이 생성이 안되는 듯하다. => 블랙리스트에 보관된 토큰이 사용이 될경우, 그건 해커일테니 저장을 하는 것 같다.
     */

    /*
    todo : access token을 재발급 해주는 메소드를 생성!
        generateJwtToken 처럼 정적 메소드로 만들어주면, 사용처에서 메소드를 임포트만 해주면 간단히 사용할 수 있다.
        -> 정적 메소드로 만들어주자 . => static method
        한번 사용되었던 access token은 블랙리스트 라고 불리우며 저장 공간을 마련해서 저장을 한다고 한다.
        -> 아마도 이미 만들어진 토큰과 똑같이 생성이 안되는 듯하다. => 블랙리스트에 보관된 토큰이 사용이 될경우, 그건 해커일테니 저장을 하는 것 같다.
     */
    open fun generateAlgorithm(): Algorithm? {
        return Algorithm.HMAC256(JWT_SECRET)
    }
}