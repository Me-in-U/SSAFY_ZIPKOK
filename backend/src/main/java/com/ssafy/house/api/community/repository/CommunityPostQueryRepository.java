package com.ssafy.house.api.community.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ssafy.house.api.community.dto.internal.CommunityCommentView;
import com.ssafy.house.api.community.dto.internal.CommunityPostPageResult;
import com.ssafy.house.api.community.dto.internal.CommunityPostView;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 목록과 상세 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class CommunityPostQueryRepository {

    private final EntityManager entityManager;

    /**
     * 게시글 목록을 페이지 단위로 조회한다.
     *
     * @param categoryId 카테고리 식별자
     * @param searchQuery 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 페이지 결과
     */
    @SuppressWarnings("unchecked")
    public CommunityPostPageResult findPostPage(String categoryId, String searchQuery, int page, int size) {
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        boolean useCategory = StringUtils.hasText(categoryId) && !"all".equals(categoryId);
        boolean useSearch = StringUtils.hasText(searchQuery);
        if (useCategory) {
            whereClause.append(" AND p.category_id = :categoryId");
        }
        if (useSearch) {
            whereClause.append(
                    " AND (LOWER(p.title) LIKE LOWER(:searchQuery) OR LOWER(p.content) LIKE LOWER(:searchQuery))");
        }

        var listQuery = entityManager.createNativeQuery("""
                SELECT
                    p.post_id,
                    p.category_id,
                    p.author_id,
                    m.name AS author_name,
                    p.title,
                    p.content,
                    p.created_at,
                    p.views,
                    (SELECT COUNT(*) FROM community_comment c WHERE c.post_id = p.post_id) AS comment_count
                FROM community_post p
                JOIN member m
                  ON p.author_id = m.mno
                """ + whereClause + """
                ORDER BY p.created_at DESC
                LIMIT :limit OFFSET :offset
                """);
        listQuery.setParameter("limit", size);
        listQuery.setParameter("offset", Math.max(page, 0) * Math.max(size, 1));
        if (useCategory) {
            listQuery.setParameter("categoryId", categoryId);
        }
        if (useSearch) {
            listQuery.setParameter("searchQuery", "%" + searchQuery + "%");
        }
        List<Object[]> rows = listQuery.getResultList();

        var countQuery = entityManager.createNativeQuery("""
                SELECT COUNT(*)
                FROM community_post p
                """ + whereClause);
        if (useCategory) {
            countQuery.setParameter("categoryId", categoryId);
        }
        if (useSearch) {
            countQuery.setParameter("searchQuery", "%" + searchQuery + "%");
        }
        Number totalCount = (Number) countQuery.getSingleResult();

        int safeSize = Math.max(size, 1);
        int totalPages = (int) Math.ceil(totalCount.doubleValue() / safeSize);
        return new CommunityPostPageResult(rows.stream().map(this::mapToSummaryView).toList(), totalPages);
    }

    /**
     * 게시글 상세를 조회한다.
     *
     * @param postId 게시글 식별자
     * @param comments 댓글 목록
     * @return 게시글 상세 조회 모델
     */
    @SuppressWarnings("unchecked")
    public CommunityPostView findPostDetail(int postId, List<CommunityCommentView> comments) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    p.post_id,
                    p.category_id,
                    p.author_id,
                    m.name AS author_name,
                    p.title,
                    p.content,
                    p.created_at,
                    p.views,
                    (SELECT COUNT(*) FROM community_comment c WHERE c.post_id = p.post_id) AS comment_count
                FROM community_post p
                JOIN member m
                  ON p.author_id = m.mno
                WHERE p.post_id = :postId
                """)
                .setParameter("postId", postId)
                .getResultList();
        if (rows.isEmpty()) {
            return null;
        }

        Object[] postRow = rows.getFirst();
        return new CommunityPostView(
                ((Number) postRow[0]).intValue(),
                (String) postRow[1],
                ((Number) postRow[2]).intValue(),
                (String) postRow[3],
                (String) postRow[4],
                (String) postRow[5],
                (Timestamp) postRow[6],
                ((Number) postRow[7]).intValue(),
                ((Number) postRow[8]).intValue(),
                comments);
    }

    private CommunityPostView mapToSummaryView(Object[] row) {
        return new CommunityPostView(
                ((Number) row[0]).intValue(),
                (String) row[1],
                ((Number) row[2]).intValue(),
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (Timestamp) row[6],
                ((Number) row[7]).intValue(),
                ((Number) row[8]).intValue(),
                List.of());
    }
}
