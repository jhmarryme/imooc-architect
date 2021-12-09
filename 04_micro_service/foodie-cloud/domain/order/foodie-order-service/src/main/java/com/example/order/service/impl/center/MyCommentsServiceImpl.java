package com.example.order.service.impl.center;

import com.example.enums.YesOrNo;
import com.example.item.service.ItemCommentsService;
import com.example.order.mapper.OrderItemsMapper;
import com.example.order.mapper.OrderStatusMapper;
import com.example.order.mapper.OrdersMapper;
import com.example.order.pojo.OrderItems;
import com.example.order.pojo.OrderStatus;
import com.example.order.pojo.Orders;
import com.example.order.pojo.bo.center.OrderItemsCommentBO;
import com.example.order.service.center.MyCommentsService;
import com.example.service.BaseService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    public OrderItemsMapper orderItemsMapper;

    @Autowired
    public OrdersMapper ordersMapper;

    @Autowired
    public OrderStatusMapper orderStatusMapper;

    // @Autowired
    // public ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    // TODO feign章节里改成item-api
    // @Autowired
    // private LoadBalancerClient client;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItemCommentsService itemCommentsService;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId,
                             List<OrderItemsCommentBO> commentList) {

        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemCommentsService.saveComments(map);
        // itemsCommentsMapperCustom.saveComments(map);
        // ServiceInstance instance = client.choose("FOODIE-ITEM-SERVICE");
        // String url = String.format("http://%s:%s/item-comments-api/saveComments",
        //         instance.getHost(),
        //         instance.getPort());
        // TODO 偷个懒，不判断返回status，等下个章节用Feign重写
        // restTemplate.postForLocation(url, map);

        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    // TODO 移到了itemCommentService
    // @Transactional(propagation = Propagation.SUPPORTS)
    // @Override
    // public PagedGridResult queryMyComments(String userId,
    //                                        Integer page,
    //                                        Integer pageSize) {
    //
    //     Map<String, Object> map = new HashMap<>();
    //     map.put("userId", userId);
    //
    //     PageHelper.startPage(page, pageSize);
    //     List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
    //
    //     return setterPagedGrid(list, page);
    // }
}
