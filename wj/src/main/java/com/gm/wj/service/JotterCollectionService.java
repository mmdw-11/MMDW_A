package com.gm.wj.service;

import com.gm.wj.dao.JotterCollectionDAO;
import com.gm.wj.entity.JotterCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class JotterCollectionService {
    @Autowired
    JotterCollectionDAO jotterCollectionDAO;

    // 查询是否已收藏
    public boolean isCollected(int uid, int aid) {
        JotterCollection collection = jotterCollectionDAO.findByUidAndAid(uid, aid);
        return null != collection;
    }

    // 添加收藏
    public void addCollection(int uid, int aid) {
        JotterCollection collection = new JotterCollection();
        collection.setUid(uid);
        collection.setAid(aid);
        collection.setCreateTime(new Date());
        jotterCollectionDAO.save(collection);
    }

    // 取消收藏 (涉及删除，必须加事务注解)
    @Transactional
    public void removeCollection(int uid, int aid) {
        jotterCollectionDAO.deleteByUidAndAid(uid, aid);
    }
}
