package com.example.order.service.center;

import com.example.order.pojo.OrderItems;
import com.example.order.pojo.bo.center.OrderItemsCommentBO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("order-comments-api")
public interface MyCommentsService {

    /**
     * 根据订单id查询关联的商品
     * @param orderId
     * @return
     */
    @GetMapping("orderItems")
    List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户的评论
     * @param orderId
     * @param userId
     * @param commentList
     */
    @PostMapping("saveOrderComments")
    void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList);


    // TODO 移到了itemCommentsService里
    // /**
    //  * 我的评价查询 分页
    //  * @param userId
    //  * @param page
    //  * @param pageSize
    //  * @return
    //  */
    // public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);
}
