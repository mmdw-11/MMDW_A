package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteDAO extends JpaRepository<Quote, Integer> {
    List<Quote> findByBook(Book book);
    List<Quote> findByBookOrderByCreateTimeDesc(Book book);
    List<Quote> findByBookOrderByLikesDesc(Book book);
    Integer countByBook(Book book);
}
