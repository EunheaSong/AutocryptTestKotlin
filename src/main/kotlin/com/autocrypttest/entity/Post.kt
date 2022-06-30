package com.autocrypttest.entity

import com.autocrypttest.dto.PostDto
import javax.persistence.*

@Entity
class Post(dto: PostDto, user: User) {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long? = null

    @Column(nullable = false)
    var title: String

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String

    @Column(nullable = false)
    var lock : Boolean

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    var user: User? = user


    init {
        title = dto.title
        content = dto.content
        lock = dto.lock
        user.addPost(this)
    }

    fun editPost(dto: PostDto) {
        title =dto.title
        content = dto.content
        lock = dto.lock
    }

}