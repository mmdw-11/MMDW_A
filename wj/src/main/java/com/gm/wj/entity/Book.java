package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Book entity.
 *
 * @author Evan
 * @date 2019/4
 */
@Data
@Entity
@Table(name = "book")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * Title of the book.
     */
    private String title;

    /**
     * Author name.
     */
    private String author;

    /**
     * Publication date.
     */
    private String date;

    /**
     * Press.
     */
    private String press;

    /**
     * Abstract of the book.
     */
    @Lob
    @Column(name = "abs", columnDefinition = "TEXT")
    private String abs;

    /**
     * The url of the book's cover.
     */
    private String cover;

    /**
     * Category id.
     */
    @ManyToOne
    @JoinColumn(name="cid")
    private Category category;

//    // 新增字段
//    @Column(name = "average_rating")
//    private Double averageRating = 0.0;  // 平均评分
//
//    @Column(name = "rating_count")
//    private Integer ratingCount = 0;      // 评分人数
//
//    @Column(name = "review_count")
//    private Integer reviewCount = 0;      // 评论数量
//
//    @Column(name = "quote_count")
//    private Integer quoteCount = 0;       // 摘录数量

//    @Transient
//    private List<Review> recentReviews;   // 最近评论（不存数据库）
//
//    @Transient
//    private List<Quote> popularQuotes;    // 热门摘录（不存数据库）
}
