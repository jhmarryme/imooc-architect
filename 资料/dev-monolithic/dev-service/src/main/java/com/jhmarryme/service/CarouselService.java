package com.jhmarryme.service;

import com.jhmarryme.pojo.Carousel;

import java.util.List;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/3/14 13:15
 */
public interface CarouselService {

    /**
     * 查询所有轮播图列表
     * @param isShow
     */
    List<Carousel> queryAll(Integer isShow);
}
