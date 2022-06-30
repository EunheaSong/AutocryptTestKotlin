package com.autocrypttest.security

import com.autocrypttest.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(private var user: User?) : UserDetails {


    fun getUser(): User? {
        return user
    }

    fun getUserNickname(): String? {
        return user?.nickname
    }

    override fun getPassword(): String? {
        return user?.password
    }

    override fun getUsername(): String? {
        return user?.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return null
    }
}