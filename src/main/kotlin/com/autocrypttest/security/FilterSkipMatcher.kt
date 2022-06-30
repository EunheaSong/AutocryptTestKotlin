package com.autocrypttest.security

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.util.function.Function
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

open class FilterSkipMatcher(pathToSkip: List<String?>, processingPath: String?) : RequestMatcher {

    private var orRequestMatcher: OrRequestMatcher? = null
    private var processingMatcher: RequestMatcher? = null

    init {
        this.orRequestMatcher = OrRequestMatcher(pathToSkip
                .stream()
                .map(Function<String?, AntPathRequestMatcher> { skipPath: String? -> this.httpPath(skipPath) })
                .collect(Collectors.toList()) as List<RequestMatcher>?)
        this.processingMatcher = AntPathRequestMatcher(processingPath)
    }

    open fun httpPath(skipPath: String): AntPathRequestMatcher? {
        val splitStr = skipPath.split(",").toTypedArray()

        /*
         * 배열 [1] httpMethod 방식 post get 인지 구분
         * 배열 [0] 제외하는 url
         * */return AntPathRequestMatcher(
                splitStr[1],
                splitStr[0]
        )
    }

    override fun matches(req: HttpServletRequest?): Boolean {
        return !orRequestMatcher!!.matches(req) && processingMatcher!!.matches(req)
    }
}