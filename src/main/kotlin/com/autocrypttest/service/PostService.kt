package com.autocrypttest.service

import com.autocrypttest.customEx.UserNotMatchException
import com.autocrypttest.dto.PostDto
import com.autocrypttest.dto.PostResponseDto
import com.autocrypttest.entity.Post
import com.autocrypttest.repository.PostRepository
import com.autocrypttest.security.UserDetailsImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class PostService(val postRepository: PostRepository) {

    //게시글 등록
    @Transactional
    fun newPost(dto: PostDto, userDetails: UserDetailsImpl): Long? {
        if (dto.title.isEmpty() || dto.content.isEmpty()) {
            throw NullPointerException("Add Post Exception.")
        }
        val post = Post(dto, userDetails.getUser())
        postRepository.save(post)
            return post.id
    }

    //게시글 수정
    @Transactional
    fun editPost(postId: Long, dto: PostDto, userDetails: UserDetailsImpl): Long? {
        val post: Post = postRepository.findById(postId).orElseThrow { NoSuchElementException("Not Found Post.") }
        if (!userDetails.username.equals(post.user?.username)) {
            throw userDetails.username?.let { post.user?.username?.let { it1 -> UserNotMatchException(it, it1) } }!!
        }
        if (dto.title.isEmpty() || dto.content.isEmpty()) {
            throw NullPointerException("Edit Post Exception.")
        }
        post.editPost(dto)
        postRepository.save(post)
        return post.id
    }

    //게시글 삭제
    @Transactional
    fun removePost(postId: Long?, userDetails: UserDetailsImpl) {
        val post: Post? = postId?.let { postRepository.findById(it).orElseThrow { NoSuchElementException("Not Found Post.") } }
        if (post != null) {
            if (!userDetails.username.equals(post.user?.username)) {
                throw userDetails.username?.let { post.user?.username?.let { it1 -> UserNotMatchException(it, it1) } }!!
            }
        }
        if (post != null) {
            postRepository.delete(post)
        }
    }

    //특정 게시글 조회
    fun getPost(postId: Long?, userDetails: UserDetailsImpl): PostResponseDto? {
        val post: Post? = postId?.let { postRepository.findById(it).orElseThrow { NoSuchElementException("Not Found Post.") } }
        //게시물에 lock = true 인 경우 , 작성자 본인만 조회가능.
        if (post != null) {
            if (post.lock) {
                require(userDetails.username.equals(post.user?.username)) { "Usernames do not match." }
            }
        }
        return post?.let { PostResponseDto(it) }
    }

    //전체 게시글 조회
    fun getPostList(pageable: Pageable?): List<PostResponseDto>? {
        val postList: Page<Post?>? = postRepository.findAllByOrderByIdDesc(pageable)
        val dtoList: MutableList<PostResponseDto> = ArrayList<PostResponseDto>()
        if (postList != null) {
            for (p in postList) {
                val dto = p?.let { PostResponseDto(it) }
                if (dto != null) {
                    dtoList.add(dto)
                }
            }
        }
        return dtoList
    }

}

