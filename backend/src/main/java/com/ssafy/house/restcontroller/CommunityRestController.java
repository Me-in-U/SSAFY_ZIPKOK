package com.ssafy.house.restcontroller;

import java.security.Principal;             // <- 수정
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dao.MemberDao;
import com.ssafy.house.model.dto.Comment;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.Post;
import com.ssafy.house.model.dto.PageResult; // <- 추가
import com.ssafy.house.model.service.CommunityService;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityRestController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private MemberDao        memberDao;

    /** 1) 게시글 목록 (페이징 포함) */
    @GetMapping("/posts")
    public ResponseEntity<PageResult<Post>> list(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(required = false)     String search,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size
    ) {
        PageResult<Post> result = communityService.getPosts(category, search, page, size);
        return ResponseEntity.ok(result);
    }

    /** 2) 게시글 상세 조회 (+조회수 증가) */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> detail(@PathVariable int postId) {
        Post post = communityService.getPostDetail(postId);
        return ResponseEntity.ok(post);
    }

    /** 3) 새 게시글 작성 */
    @PostMapping("/posts")
    public ResponseEntity<Void> create(
            @RequestBody Post post,
            Principal principal             // <- java.security.Principal
    ) {
        // 1) 로그인한 email
        String email = principal.getName();

        // 2) email → Member → mno
        Member member = memberDao.selectByEmail(email);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        post.setAuthorId(member.getMno());

        communityService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 4) 댓글 작성 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> comment(
            @PathVariable int postId,
            @RequestBody Comment comment,
            Principal principal
    ) {
        // 1) postId 세팅
        comment.setPostId(postId);

        // 2) 로그인한 사용자 mno
        String email = principal.getName();
        Member member = memberDao.selectByEmail(email);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        comment.setAuthorId(member.getMno());

        communityService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
