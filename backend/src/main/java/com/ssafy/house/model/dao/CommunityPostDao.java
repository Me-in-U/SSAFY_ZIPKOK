package com.ssafy.house.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.house.model.dto.Post;

@Mapper
public interface CommunityPostDao {
    List<Post> selectPosts(Map<String, Object> params);
    int        selectPostsCount(Map<String,Object> params);
    Post       selectPostById(int postId);
    int        incrementViews(int postId);
    int        insertPost(Post post);
}