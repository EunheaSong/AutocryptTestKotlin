package com.autocrypttest.service

import com.autocrypttest.dto.UserJoinDto
import com.autocrypttest.entity.User
import com.autocrypttest.repository.UserRepository
import com.autocrypttest.security.UserDetailsImpl
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.regex.Matcher
import java.util.regex.Pattern


@Service
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder ) {

    //아이디 중복 체크
    fun checkUsername(username: String?): String? {
        require(!userRepository.findByUsername(username)?.isPresent!!) { "already exists ID." }
        return username
    }

    //닉네임 중복 체크
    fun checkNickname(nickname: String?): String? {
        require(!userRepository.findByNickname(nickname)?.isPresent!!) { "already exists Nickname" }
        return nickname
    }

    //회원 가입
    fun registerUser(dto: UserJoinDto) {
        val password = passwordEncoder!!.encode(dto.password)
        val passwordCheck = passwordEncoder.encode(dto.passwordCheck)
        require(passwordEncoder.matches(dto.password, passwordCheck)) { "Passwords do not match." }
        val user = User(dto.username, password, dto.nickname)
        userRepository.save(user)
    }

    fun secessionUser(username: String?, userDetails: UserDetailsImpl?) {
        val user: User? = userRepository.findByUsername(username)?.orElseThrow { IllegalArgumentException("No matching users.") }
        if (user != null) {
            userRepository.delete(user)
        }
    }

    //비밀번호 규칙 -> 8~20자 내외 . 특수문자 최소1개 + 숫자 + 알파벳(대소문자구분없음)로 입력.
    fun userRegister(dto: UserJoinDto) {
        val username: String = dto.username
        val password: String = dto.password
        val passwordCheck: String = dto.passwordCheck
        val nickname: String = dto.nickname
        val idPattern: Pattern = Pattern.compile("^(?=.*[a-zA-Z])[0-9]{5,15}")
        val nicknamePattern: Pattern = Pattern.compile("^[a-zA-Z가-힣0-9]{2,10}")

        require(idPattern.matcher(username).matches()) { "ID format is incorrect." }
        require(nicknamePattern.matcher(nickname).matches()) { "Nickname format is incorrect." }

        val passwordPattern: Pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*]){8,20}")
//        val passwordMather: Matcher = passwordPattern.matcher(password)

        require(passwordPattern.matcher(password).matches()) { "Password format is incorrect." }
        require(passwordEncoder.matches(password,passwordCheck)) { "Password do not match." }
    }
}