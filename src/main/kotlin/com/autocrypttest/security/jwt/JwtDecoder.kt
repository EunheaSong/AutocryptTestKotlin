package com.autocrypttest.security.jwt

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtDecoder {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun decodeUsername(token: String): String? {
        return try {
            val decodedJWT: DecodedJWT = isValidToken(token)
                    .orElseThrow { IllegalArgumentException("유효한 토큰이 아닙니다.") }
            val expiredDate: Date = decodedJWT
                    .getClaim(JwtTokenUtils.CLAIM_EXPIRED_DATE)
                    .asDate()
            val now = Date()
            require(!expiredDate.before(now)) {
                "만료된 토큰입니다."
                //                request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN);
                //todo : 유효기간이 끝났을경우 , 재발급을 해야된다는 의미의 에러를 내려주자
            }
            decodedJWT
                    .getClaim(JwtTokenUtils.CLAIM_USER_NAME)
                    .asString()
        } catch (e: Exception) {
            e.printStackTrace()
            //            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
            //            response.addHeader("Authorization", "TimeOut");
            e.message
        }
    }

    private fun isValidToken(token: String): Optional<DecodedJWT> {
        var jwt: DecodedJWT? = null
        try {
            val algorithm: Algorithm = Algorithm.HMAC256(JwtTokenUtils.JWT_SECRET)
            val verifier: JWTVerifier = JWT
                    .require(algorithm)
                    .build()
            jwt = verifier.verify(token)
        } catch (e: Exception) {
            log.error(e.message)
        }
        return Optional.ofNullable<DecodedJWT>(jwt)
    }

    fun decodeRefresh(token: String): String? {
        val decodedJWT: DecodedJWT = isValidToken(token)
                .orElseThrow { IllegalArgumentException("유효한 토큰이 아닙니다.") }
        val expiredDate: Date = decodedJWT
                .getClaim(JwtTokenUtils.CLAIM_EXPIRED_DATE)
                .asDate()
        val now = Date()
        require(!expiredDate.before(now)) {
            "TimeOut"
            //                request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN);
            //todo : 유효기간이 끝났을경우 , 재발급을 해야된다는 의미의 에러를 내려주자
        }
        return decodedJWT
                .getClaim(JwtTokenUtils.CLAIM_USER_NAME)
                .asString()
//        return username;
    }
}