package com.autocrypttest.security

import com.autocrypttest.security.filter.FormLoginFilter
import com.autocrypttest.security.filter.JwtAuthFilter
import com.autocrypttest.security.jwt.HeaderTokenExtractor
import com.autocrypttest.security.provider.FormLoginAuthProvider
import com.autocrypttest.security.provider.JWTAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
class WebSecurityConfig(val jwtAuthProvider: JWTAuthProvider, val headerTokenExtractor: HeaderTokenExtractor) : WebSecurityConfigurerAdapter() {

//    private val jwtAuthProvider: JWTAuthProvider? = null
//    private val headerTokenExtractor: HeaderTokenExtractor? = null

//    fun WebSecurityConfig(jwtAuthProvider: JWTAuthProvider?, headerTokenExtractor: HeaderTokenExtractor?) {
//        this.headerTokenExtractor = headerTokenExtractor
//        this.jwtAuthProvider = jwtAuthProvider
//    }

    @Bean
    fun encodePassword(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    } //스프링 시큐리티에서 권고하는 BCrypt 해시 함수를 사용해서 비밀번호를 받도록 한다.


    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider)
    }

    override fun configure(web: WebSecurity) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**")
    } //시큐리티 앞단 설정.


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .headers()
                .frameOptions().sameOrigin()


        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 FormLoginFilter, JwtFilter 를 등록합니다.
         * FormLoginFilter : 로그인 인증을 실시합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
        http.authorizeRequests() //웹소켓 접근
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/chatting/**").permitAll()
                .antMatchers("/sub/**").permitAll()
                .antMatchers("/pub/**").permitAll()
                .anyRequest()
                .permitAll()
                .and() // [로그아웃 기능]
                .logout() // 로그아웃 요청 처리 URL
                .logoutUrl("/user/logout") //todo : addLogoutHandler-> 로그아웃 이후 일어날 로직을 입력.
                // CustomLogoutHandler -> LogoutHandler를 구현. 리프레시 토큰을 삭제해주는 로그아웃 핸들러 추가.
                //                .addLogoutHandler(new CustomLogoutHandler())
                .permitAll()
                .and()
                .exceptionHandling() // "접근 불가" 페이지 URL 설정
                .accessDeniedPage("/forbidden.html")
    }

    @Bean
    @Throws(Exception::class)
    fun formLoginFilter(): FormLoginFilter? {
        val formLoginFilter = FormLoginFilter(authenticationManager())
        formLoginFilter.setFilterProcessesUrl("/user/login")
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler())
        formLoginFilter.afterPropertiesSet()
        return formLoginFilter
    }

    @Bean
    fun formLoginSuccessHandler(): LoginSuccessHandler? {
        return LoginSuccessHandler()
    }

    @Bean
    fun formLoginAuthProvider(): FormLoginAuthProvider? {
        return FormLoginAuthProvider(encodePassword())
    }

    @Throws(Exception::class)
    private fun jwtFilter(): JwtAuthFilter? {
        val skipPathList: MutableList<String> = ArrayList()

        // h2-console 허용
        skipPathList.add("GET,/h2-console/**")
        skipPathList.add("POST,/h2-console/**")
        skipPathList.add("POST,/user/join") // 회원가입 허용
        skipPathList.add("GET,/check/**") //아이디, 닉네임 중복 체크 허용.
        val matcher = FilterSkipMatcher(
                skipPathList,
                "/**"
        )
        val filter = JwtAuthFilter(
                matcher,
                headerTokenExtractor
        )
        filter.setAuthenticationManager(super.authenticationManagerBean())
        return filter
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}