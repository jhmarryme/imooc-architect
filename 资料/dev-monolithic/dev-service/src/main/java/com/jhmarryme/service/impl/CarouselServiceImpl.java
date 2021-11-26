package com.jhmarryme.service.impl;

import com.jhmarryme.mapper.CarouselMapper;
import com.jhmarryme.pojo.Carousel;
import com.jhmarryme.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/3/14 13:17
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<Carousel> queryAll(Integer isShow) {
        Example example = new Example(Carousel.class);

        example.orderBy("sort").desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isShow", isShow);

        List<Carousel> carouselList = carouselMapper.selectByExample(example);
        return carouselList;
    }
}
