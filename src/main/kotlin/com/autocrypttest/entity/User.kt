package com.autocrypttest.entity

import javax.persistence.*

@Entity
class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long? = null

    @Column(nullable = false, unique = true, length = 30)
    var username //회원 ID
            : String? = null

    @Column(nullable = false)
    var password //회원 비밀번호
            : String? = null

    @Column(nullable = false, length = 20)
    var nickname //회원 닉네임
            : String? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    val posts: MutableList<Post> = ArrayList()

    constructor(username: String?, password: String?, nickname: String?) {
        this.username = username
        this.password = password
        this.nickname = nickname
    }

    fun addPost(post: Post) {
        posts.add(post)
    }
}