package com.example.order.pojo.bo;

import lombok.Data;
import lombok.ToString;

/**
 * 用于创建订单的BO对象
 */
@Data
@ToString
public class SubmitOrderBO {

    private String userId;
    private String itemSpecIds;
    private String addressId;
    private Integer payMethod;
    private String leftMsg;
    private String token;
}
