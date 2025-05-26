package com.ssafy.house.model.service;

import com.ssafy.house.model.dto.Post;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.PageResult;

import java.util.List;

public interface CommunityService {
    PageResult<Post> getPosts(String categoryId, String searchQuery, int page, int size);
    Post             getPostDetail(int postId);
    void             createPost(Post post, String userEmail);
    void             updatePost(int postId, Post post, String userEmail);
    void             deletePost(int postId, String userEmail);

    void             addComment(int postId, Comment comment, String userEmail);
    void             updateComment(int postId, int commentId, Comment comment, String userEmail);
    void             deleteComment(int postId, int commentId, String userEmail);
}
