package com.example.order.pojo.vo;

import com.example.pojo.ShopcartBO;
import lombok.Data;

import java.util.List;


@Data
public class OrderVO {

    private String orderId;

    private MerchantOrdersVO merchantOrdersVO;

    private List<ShopcartBO> toBeRemovedShopcatdList;

}