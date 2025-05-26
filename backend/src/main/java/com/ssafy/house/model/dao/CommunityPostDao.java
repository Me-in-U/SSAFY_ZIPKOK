package com.ssafy.house.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.Post;

@Mapper
public interface CommunityPostDao {
    List<Post> selectPosts(Map<String,Object> params);           // 페이징 목록
    int        selectPostsCount(Map<String,Object> params);
    List<Post> selectAllPosts(@Param("categoryId") String categoryId);
    Post       selectPostById(int postId);
    int        insertPost(Post post);
    int        updatePost(Post post);
    int        deletePost(int postId);
    int        incrementViews(int postId);
}