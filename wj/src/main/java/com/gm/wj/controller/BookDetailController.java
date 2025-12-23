package com.gm.wj.controller;

import com.gm.wj.entity.Review;
import com.gm.wj.entity.Quote;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class BookDetailController {

    @Autowired
    private BookDetailService bookDetailService;

    // 获取图书评论
    @GetMapping("/api/books/{id}/reviews")
    public Result getBookReviews(@PathVariable("id") Integer id) {
        List<Review> reviews = bookDetailService.getReviewsByBook(id);
        if (reviews == null) {
            return ResultFactory.buildFailResult("图书不存在");
        }
        return ResultFactory.buildSuccessResult(reviews);
    }

    // 获取图书金句
    @GetMapping("/api/books/{id}/quotes")
    public Result getBookQuotes(@PathVariable("id") Integer id) {
        List<Quote> quotes = bookDetailService.getQuotesByBook(id);
        if (quotes == null) {
            return ResultFactory.buildFailResult("图书不存在");
        }
        return ResultFactory.buildSuccessResult(quotes);
    }

    // 添加评论
    @PostMapping("/api/books/reviews")
    public Result addReview(@RequestBody ReviewRequest request) {
        if (request.getBookId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResultFactory.buildFailResult("参数错误");
        }

        Review review = bookDetailService.addReview(request.getBookId(), request.getContent());
        if (review == null) {
            return ResultFactory.buildFailResult("添加评论失败");
        }
        return ResultFactory.buildSuccessResult(review);
    }

    // 添加金句
    @PostMapping("/api/books/quotes")
    public Result addQuote(@RequestBody QuoteRequest request) {
        if (request.getBookId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResultFactory.buildFailResult("参数错误");
        }

        Quote quote = bookDetailService.addQuote(request.getBookId(), request.getContent(), request.getPageNumber());
        if (quote == null) {
            return ResultFactory.buildFailResult("添加金句失败");
        }
        return ResultFactory.buildSuccessResult(quote);
    }

    // 点赞评论
//    @PostMapping("/api/books/reviews/{id}/like")
//    public Result likeReview(@PathVariable("id") Integer id) {
//        Review review = bookDetailService.likeReview(id);
//        if (review == null) {
//            return ResultFactory.buildFailResult("评论不存在");
//        }
//        return ResultFactory.buildSuccessResult(review);
//    }
//
//    // 点赞金句
//    @PostMapping("/api/books/quotes/{id}/like")
//    public Result likeQuote(@PathVariable("id") Integer id) {
//        Quote quote = bookDetailService.likeQuote(id);
//        if (quote == null) {
//            return ResultFactory.buildFailResult("金句不存在");
//        }
//        return ResultFactory.buildSuccessResult(quote);
//    }
    // 修改后的 Controller 方法
    @PostMapping("/api/books/reviews/{id}/like")
    public Result likeReview(@PathVariable("id") Integer id, @RequestParam boolean isLike) {
        // 这里的 isLike 是由前端传过来的（true代表点赞，false代表取消）
        Review review = bookDetailService.likeReview(id, isLike);
        if (review == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        return ResultFactory.buildSuccessResult(review);
    }

    // 金句部分也做同样修改
    @PostMapping("/api/books/quotes/{id}/like")
    public Result likeQuote(@PathVariable("id") Integer id, @RequestParam boolean isLike) {
        Quote quote = bookDetailService.likeQuote(id, isLike);
        if (quote == null) {
            return ResultFactory.buildFailResult("金句不存在");
        }
        return ResultFactory.buildSuccessResult(quote);
    }

    // 删除评论
    @DeleteMapping("/api/books/reviews/{id}")
    public Result deleteReview(@PathVariable("id") Integer id) {
        bookDetailService.deleteReview(id);
        return ResultFactory.buildSuccessResult("评论已删除");
    }

    // 删除金句
    @DeleteMapping("/api/books/quotes/{id}")
    public Result deleteQuote(@PathVariable("id") Integer id) {
        bookDetailService.deleteQuote(id);
        return ResultFactory.buildSuccessResult("金句已删除");
    }

    // 请求对象类
    public static class ReviewRequest {
        private Integer bookId;
        private String content;

        public Integer getBookId() { return bookId; }
        public void setBookId(Integer bookId) { this.bookId = bookId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class QuoteRequest {
        private Integer bookId;
        private String content;
        private Integer pageNumber;

        public Integer getBookId() { return bookId; }
        public void setBookId(Integer bookId) { this.bookId = bookId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getPageNumber() { return pageNumber; }
        public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    }
}
