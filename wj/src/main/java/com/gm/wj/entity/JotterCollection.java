package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 收藏表实体类
 */
@Data
@Entity
@Table(name = "jotter_collection")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class JotterCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "uid")
    private int uid;

    @Column(name = "aid")
    private int aid;

    @Column(name = "create_time")
    private Date createTime;
}
