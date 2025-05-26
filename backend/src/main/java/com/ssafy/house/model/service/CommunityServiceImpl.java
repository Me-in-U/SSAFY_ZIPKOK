package com.ssafy.house.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.model.dao.CommunityCommentDao;
import com.ssafy.house.model.dao.CommunityPostDao;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.PageResult;
import com.ssafy.house.model.dto.Post;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    private final CommunityPostDao postDao;
    private final CommunityCommentDao commentDao;

    @Override
    public PageResult<Post> getPosts(String categoryId, String searchQuery, int page, int size) {
        int offset = page * size;
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("searchQuery", searchQuery);
        params.put("offset", offset);
        params.put("size", size);

        // 1) 페이징된 목록
        List<Post> content = postDao.selectPosts(params);

        // 2) 전체 개수로 계산한 총 페이지 수
        int totalCount = postDao.selectPostsCount(params);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResult<>(content, totalPages);
    }

    @Override
    @Transactional
    public Post getPostDetail(int postId) {
        postDao.incrementViews(postId);
        Post post = postDao.selectPostById(postId);
        post.setComments(commentDao.selectCommentsByPostId(postId));
        return post;
    }

    @Override
    public void createPost(Post post) {
        postDao.insertPost(post);
    }

    @Override
    public void addComment(Comment comment) {
        commentDao.insertComment(comment);
    }
}