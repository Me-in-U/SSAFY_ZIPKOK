package com.ssafy.house.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.house.model.dto.Comment;

@Mapper
public interface CommunityCommentDao {
    List<Comment> selectCommentsByPostId(int postId);
    int           insertComment(Comment comment);
}
