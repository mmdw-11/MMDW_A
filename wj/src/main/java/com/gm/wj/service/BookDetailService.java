package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.ReviewDAO;
import com.gm.wj.dao.QuoteDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.Review;
import com.gm.wj.entity.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookDetailService {
    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private ReviewDAO reviewDAO;

    @Autowired
    private QuoteDAO quoteDAO;

    // 获取图书的所有评论
    public List<Review> getReviewsByBook(Integer bookId) {
        Book book = bookDAO.findById(bookId).orElse(null);
        if (book == null) {
            return null;
        }
        List<Review> reviews = reviewDAO.findByBookOrderByCreateTimeDesc(book);

        // 为每个评论设置临时用户信息（前端显示用）
        for (Review review : reviews) {
            // 这里可以设置一些临时用户信息
            // review.setTempUsername("用户" + review.getId());
        }

        return reviews;
    }

    // 获取图书的所有金句
    public List<Quote> getQuotesByBook(Integer bookId) {
        Book book = bookDAO.findById(bookId).orElse(null);
        if (book == null) {
            return null;
        }
        return quoteDAO.findByBookOrderByLikesDesc(book);
    }

    // 添加评论
    public Review addReview(Integer bookId, String content) {
        Book book = bookDAO.findById(bookId).orElse(null);
        if (book == null) {
            return null;
        }

        Review review = new Review();
        review.setBook(book);
        review.setContent(content);

        return reviewDAO.save(review);
    }

    // 添加金句
    public Quote addQuote(Integer bookId, String content, Integer pageNumber) {
        Book book = bookDAO.findById(bookId).orElse(null);
        if (book == null) {
            return null;
        }

        Quote quote = new Quote();
        quote.setBook(book);
        quote.setContent(content);
        quote.setPageNumber(pageNumber);

        return quoteDAO.save(quote);
    }

    // 点赞评论
//    public Review likeReview(Integer reviewId) {
//        Review review = reviewDAO.findById(reviewId).orElse(null);
//        if (review != null) {
//            review.setLikes(review.getLikes() + 1);
//            return reviewDAO.save(review);
//        }
//        return null;
//    }
    public Review likeReview(Integer reviewId, boolean isLike) {
        Review review = reviewDAO.findById(reviewId).orElse(null);
        if (review != null) {
            int currentLikes = review.getLikes() == null ? 0 : review.getLikes();
            if (isLike) {
                review.setLikes(currentLikes + 1);
            } else {
                review.setLikes(Math.max(0, currentLikes - 1)); // 确保不减成负数
            }
            return reviewDAO.save(review);
        }
        return null;
    }

    // 点赞金句
//    public Quote likeQuote(Integer quoteId) {
//        Quote quote = quoteDAO.findById(quoteId).orElse(null);
//        if (quote != null) {
//            quote.setLikes(quote.getLikes() + 1);
//            return quoteDAO.save(quote);
//        }
//        return null;
//    }
    public Quote likeQuote(Integer quoteId, boolean isLike) { // 这里增加了 boolean 参数
        Quote quote = quoteDAO.findById(quoteId).orElse(null);
        if (quote != null) {
            int currentLikes = quote.getLikes() == null ? 0 : quote.getLikes();
            if (isLike) {
                quote.setLikes(currentLikes + 1);
            } else {
                quote.setLikes(Math.max(0, currentLikes - 1)); // 确保不减成负数
            }
            return quoteDAO.save(quote);
        }
        return null;
    }

    // 删除评论
    public boolean deleteReview(Integer reviewId) {
        if (reviewDAO.existsById(reviewId)) {
            reviewDAO.deleteById(reviewId);
            return true;
        }
        return false;
    }

    // 删除金句
    public boolean deleteQuote(Integer quoteId) {
        if (quoteDAO.existsById(quoteId)) {
            quoteDAO.deleteById(quoteId);
            return true;
        }
        return false;
    }


}
