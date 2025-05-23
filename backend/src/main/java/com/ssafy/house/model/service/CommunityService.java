package com.ssafy.house.model.service;


import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.PageResult;
import com.ssafy.house.model.dto.Post;


/**
 * 커뮤니티 서비스
 * - 게시글 목록 조회
 * - 게시글 상세 조회
 * - 게시글 작성
 * - 댓글 작성
 */
public interface CommunityService {
    PageResult<Post> getPosts(String categoryId, String searchQuery, int page, int size);
    Post             getPostDetail(int postId);
    void             createPost(Post post);
    void             addComment(Comment comment);
}
