package com.example.library_management.domain.board.repository;

import com.example.library_management.domain.board.dto.request.BoardSearchCondition;
import com.example.library_management.domain.board.dto.response.BoardSearchResult;
import com.example.library_management.domain.board.dto.response.QBoardSearchResult;
import com.example.library_management.domain.board.enums.BoardSearchType;
import com.example.library_management.domain.board.enums.BoardStatus;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.library_management.domain.board.entity.QBoard.board;
import static com.example.library_management.domain.boardComment.entity.QBoardComment.boardComment;
import static com.example.library_management.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardSearchResult> search(BoardSearchCondition condition, User currentUser, Pageable pageable) {
        // 검색 결과 조회
        List<BoardSearchResult> content = queryFactory
                .select(new QBoardSearchResult(
                        board.id,
                        board.title,
                        board.user.userName,
                        board.viewCount,
                        board.isSecret,
                        board.isPinned,
                        board.createdAt,
                        board.commentList.size()
                ))
                .from(board)
                .leftJoin(board.user, user)
                .leftJoin(board.commentList, boardComment)
                .where(
                        searchKeywordContains(condition.getKeyword(), condition.getSearchType()),
                        board.boardType.eq(condition.getBoardType()),
                        board.status.eq(BoardStatus.ACTIVE),
                        secretBoardAccessible(condition.isIncludeSecret(), currentUser)
                )
                .groupBy(board.id)
                .orderBy(
                        board.isPinned.desc(),
                        board.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(board.countDistinct())
                .from(board)
                .leftJoin(board.user, user)
                .where(
                        searchKeywordContains(condition.getKeyword(), condition.getSearchType()),
                        board.boardType.eq(condition.getBoardType()),
                        board.status.eq(BoardStatus.ACTIVE),
                        secretBoardAccessible(condition.isIncludeSecret(), currentUser)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 검색어 조건 생성
    private BooleanExpression searchKeywordContains(String keyword, BoardSearchType searchType) {
        if (keyword == null || keyword.trim().isEmpty()){
            return null;
        }

        switch (searchType){
            case TITLE:
                return board.title.containsIgnoreCase(keyword);
            case CONTENT:
                return board.content.containsIgnoreCase(keyword);
            case WRITER:
                return board.user.userName.containsIgnoreCase(keyword);
            case ALL:
                return board.title.containsIgnoreCase(keyword)
                        .or(board.content.containsIgnoreCase(keyword));
            default:
                return null;
        }
    }
    
    // 비밀글 접근 조건 생성
    private BooleanExpression secretBoardAccessible(boolean includeSecret, User currentUser) {
        // 관리자는 모든 게시물 접근 가능
        if (currentUser.getRole().equals(UserRole.ROLE_ADMIN)){
            return null;
        }

        // 일반 사용자이고 비밀글 포함하지 않는 경우
        if (!includeSecret){
            return board.isSecret.isFalse();
        }

        // 일반 사용자이고 비밀글 포함하는 경우 - 자신의 비밀글만 포함
        return board.isSecret.isFalse()
                .or(board.user.id.eq(currentUser.getId()));
    }
}
