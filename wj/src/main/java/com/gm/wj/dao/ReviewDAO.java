package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review, Integer> {
    List<Review> findByBook(Book book);
    List<Review> findByBookOrderByCreateTimeDesc(Book book);
    List<Review> findByBookOrderByLikesDesc(Book book);
    Integer countByBook(Book book);
}
