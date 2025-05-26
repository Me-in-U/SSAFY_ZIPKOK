package com.ssafy.house.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.house.model.dto.Comment;

@Mapper
public interface CommunityCommentDao {
    List<Comment> selectCommentsByPostId(int postId);
    Comment       selectCommentById(int commentId);
    int           insertComment(Comment comment);
    int           updateComment(Comment comment);
    int           deleteComment(int commentId);
}
