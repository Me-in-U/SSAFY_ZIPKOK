package com.ssafy.house.api.community.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.house.api.community.dto.internal.CommunityCommentView;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 댓글 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class CommunityCommentQueryRepository {

    private final EntityManager entityManager;

    /**
     * 게시글에 속한 댓글 목록을 조회한다.
     *
     * @param postId 게시글 식별자
     * @return 댓글 목록
     */
    @SuppressWarnings("unchecked")
    public List<CommunityCommentView> findCommentsByPostId(int postId) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    c.comment_id,
                    c.post_id,
                    c.author_id,
                    m.name AS author_name,
                    c.content,
                    c.created_at
                FROM community_comment c
                JOIN member m
                  ON c.author_id = m.mno
                WHERE c.post_id = :postId
                ORDER BY c.created_at ASC
                """)
                .setParameter("postId", postId)
                .getResultList();

        return rows.stream()
                .map(this::mapToCommentView)
                .toList();
    }

    private CommunityCommentView mapToCommentView(Object[] row) {
        return new CommunityCommentView(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                ((Number) row[2]).intValue(),
                (String) row[3],
                (String) row[4],
                (Timestamp) row[5]);
    }
}
