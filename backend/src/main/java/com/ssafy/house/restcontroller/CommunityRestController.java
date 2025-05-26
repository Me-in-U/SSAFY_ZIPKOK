package com.ssafy.house.restcontroller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dto.Post;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.PageResult;
import com.ssafy.house.model.service.CommunityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityRestController {

    private final CommunityService communityService;

    /** 1) 페이징된 게시글 목록 조회 */
    @GetMapping("/posts")
    public PageResult<Post> list(
        @RequestParam(defaultValue="all") String category,
        @RequestParam(required=false) String search,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="5") int size
    ) {
      return communityService.getPosts(category, search, page, size);
    }
    /** 2) 게시글 상세 조회 (+조회수 증가) */
    @GetMapping("/posts/{postId}")
    public Post detail(@PathVariable int postId) {
        return communityService.getPostDetail(postId);
    }

    /** 3) 새 게시글 작성 */
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @RequestBody Post post,
            Principal principal
    ) {
        communityService.createPost(post, principal.getName());
    }

    /** 4) 게시글 수정 */
    @PutMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable int postId,
            @RequestBody Post post,
            Principal principal
    ) {
        communityService.updatePost(postId, post, principal.getName());
    }

    /** 5) 게시글 삭제 */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable int postId,
            Principal principal
    ) {
        communityService.deletePost(postId, principal.getName());
    }

    /** 6) 댓글 작성 */
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(
            @PathVariable int postId,
            @RequestBody Comment comment,
            Principal principal
    ) {
        communityService.addComment(postId, comment, principal.getName());
    }

    /** 7) 댓글 수정 */
    @PutMapping("/posts/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @RequestBody Comment comment,
            Principal principal
    ) {
        communityService.updateComment(postId, commentId, comment, principal.getName());
    }

    /** 8) 댓글 삭제 */
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            Principal principal
    ) {
        communityService.deleteComment(postId, commentId, principal.getName());
    }
}
