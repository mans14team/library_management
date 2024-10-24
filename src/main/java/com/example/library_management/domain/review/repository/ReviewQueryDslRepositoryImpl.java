package com.example.library_management.domain.review.repository;

import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.library_management.domain.review.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewQueryDslRepositoryImpl implements ReviewQueryDslRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<ReviewsGetResponse> findAllByMultipleConditions(

            Pageable pageable,
            Long bookId,
            Integer reviewStar) {
        // 페이징 된 데이터 가져오기
        List<ReviewsGetResponse> results = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReviewsGetResponse.class,
                                review.book.id,
                                review.id,
                                review.reviewStar,
                                review.reviewTitle,
                                review.reviewDescription,
                                review.createdAt,
                                review.modifiedAt

                        )
                )
                .from(review)
                .where(
                        bookIdAndReviewStar(bookId, reviewStar)

                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();

        // 전체 페이지 데이터 개수 구하기
        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(review)
                .where(bookIdAndReviewStar(bookId, reviewStar)
                ).fetchOne();

        return new PageImpl<>(results, pageable, totalCount);

    }

    //해당하는 책과 리뷰 별점을 같이 조회하고 싶을때
    private BooleanExpression bookIdAndReviewStar(Long bookId, Integer reviewStar) {

        if (bookId != null && reviewStar != null) {
            return review.book.id.eq(bookId).and(review.reviewStar.eq(reviewStar));
        } else if (bookId != null) {
            return review.book.id.eq(bookId);
        }else if(reviewStar !=null){
            return review.reviewStar.eq(reviewStar);
        }
        return null;


    }
}
