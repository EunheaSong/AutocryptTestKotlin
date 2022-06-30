package com.autocrypttest.dto

import com.autocrypttest.entity.Post


class PostResponseDto(post: Post) {
    private var title: String? = null

    private var content: String? = null

    private var nickname: String? = null

    private var lock = false

    fun PostResponseDto() {}

    init {
        title = post.title
        content = post.content
        nickname = post.user?.username
        lock = post.lock
    }
}