package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.CommunityPostDao;
import com.ssafy.house.model.dao.CommunityCommentDao;
import com.ssafy.house.model.dao.MemberDao;
import com.ssafy.house.model.dto.Post;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.PageResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostDao    postDao;
    private final CommunityCommentDao commentDao;
    private final MemberDao           memberDao;

    @Override
    public PageResult<Post> getPosts(String categoryId, String searchQuery, int page, int size) {
        int offset = page * size;
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId",  categoryId);
        params.put("searchQuery", searchQuery);
        params.put("offset",      offset);
        params.put("size",        size);

        List<Post> content = postDao.selectPosts(params);
        int totalCount  = postDao.selectPostsCount(params);
        int totalPages  = (int) Math.ceil((double) totalCount / size);

        return new PageResult<>(content, totalPages);
    }

    @Override
    @Transactional
    public Post getPostDetail(int postId) {
        postDao.incrementViews(postId);
        Post post = postDao.selectPostById(postId);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        post.setComments(commentDao.selectCommentsByPostId(postId));
        return post;
    }

    @Override
    public void createPost(Post post, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        if (m == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인된 사용자 정보가 없습니다.");
        }
        post.setAuthorId(m.getMno());
        postDao.insertPost(post);
    }
    @Override
    public void updatePost(int postId, Post post, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        Post existing = postDao.selectPostById(postId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.");
        }
        // primitive int 비교
        if (existing.getAuthorId() != m.getMno()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 수정 권한이 없습니다.");
        }
        post.setPostId(postId);
        post.setAuthorId(m.getMno());
        postDao.updatePost(post);
    }
    
    @Override
    public void deletePost(int postId, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        Post existing = postDao.selectPostById(postId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.");
        }
        if (existing.getAuthorId() != m.getMno()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 삭제 권한이 없습니다.");
        }
        postDao.deletePost(postId);
    }

    @Override
    public void addComment(int postId, Comment comment, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        if (m == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인된 사용자 정보가 없습니다.");
        }
        comment.setPostId(postId);
        comment.setAuthorId(m.getMno());
        commentDao.insertComment(comment);
    }

    @Override
    public void updateComment(int postId, int commentId, Comment comment, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        Comment existing = commentDao.selectCommentById(commentId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 없습니다.");
        }
        if (existing.getAuthorId() != m.getMno()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 수정 권한이 없습니다.");
        }
        comment.setCommentId(commentId);
        commentDao.updateComment(comment);
    }
    
    @Override
    public void deleteComment(int postId, int commentId, String userEmail) {
        Member m = memberDao.selectByEmail(userEmail);
        Comment existing = commentDao.selectCommentById(commentId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 없습니다.");
        }
        if (existing.getAuthorId() != m.getMno()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }
        commentDao.deleteComment(commentId);
    }
}
