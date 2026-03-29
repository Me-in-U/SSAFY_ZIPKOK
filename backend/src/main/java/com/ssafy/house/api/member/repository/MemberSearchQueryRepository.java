package com.ssafy.house.api.member.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.dto.internal.MemberPageResult;
import com.ssafy.house.api.member.entity.MemberEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

/**
 * 회원 검색과 페이지 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class MemberSearchQueryRepository {

    private final EntityManager entityManager;

    /**
     * 회원 목록을 페이지 단위로 조회한다.
     *
     * @param key 검색 키
     * @param word 검색어
     * @param currentPage 현재 페이지 번호
     * @param itemsPerPage 페이지 크기
     * @return 회원 페이지 결과
     */
    public MemberPageResult search(String key, String word, int currentPage, int itemsPerPage) {
        StringBuilder fromClause = new StringBuilder(" from MemberEntity m");
        StringBuilder whereClause = new StringBuilder();

        if (StringUtils.hasText(key) && StringUtils.hasText(word)) {
            if ("name".equals(key)) {
                whereClause.append(" where lower(m.name) like lower(:word)");
            } else if ("email".equals(key)) {
                whereClause.append(" where lower(m.email) like lower(:word)");
            }
        }

        TypedQuery<MemberEntity> query = entityManager.createQuery(
                "select m" + fromClause + whereClause + " order by m.mno desc",
                MemberEntity.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "select count(m)" + fromClause + whereClause,
                Long.class);

        if (whereClause.length() > 0) {
            String searchWord = "%" + word + "%";
            query.setParameter("word", searchWord);
            countQuery.setParameter("word", searchWord);
        }

        int safeCurrentPage = Math.max(currentPage, 1);
        int safeItemsPerPage = Math.max(itemsPerPage, 1);
        query.setFirstResult((safeCurrentPage - 1) * safeItemsPerPage);
        query.setMaxResults(safeItemsPerPage);

        List<MemberAccount> members = query.getResultList().stream()
                .map(this::mapToAccount)
                .toList();
        int totalItems = countQuery.getSingleResult().intValue();
        int totalPages = (int) Math.ceil((double) totalItems / safeItemsPerPage);
        int navSize = 5;
        int startPage = (safeCurrentPage - 1) / navSize * navSize + 1;
        int endPage = Math.min(startPage + navSize - 1, Math.max(totalPages, 1));
        boolean hasPre = startPage != 1;
        boolean hasNext = endPage < totalPages;

        return new MemberPageResult(
                members,
                safeCurrentPage,
                safeItemsPerPage,
                totalItems,
                totalPages,
                hasPre,
                hasNext,
                startPage,
                endPage);
    }

    private MemberAccount mapToAccount(MemberEntity member) {
        return new MemberAccount(member.getMno(), member.getName(), member.getEmail(), member.getPassword());
    }
}
