package com.example.order.service.feign;

import com.example.item.service.ItemCommentsService;
import com.example.order.service.feign.fallback.ItemCommentsFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/1 上午9:31
 */
@FeignClient(
        name = "foodie-item-service",
        contextId = "item-comments-api-custom",
        path = "/foodie-item-service"
        ,
        fallback = ItemCommentsFeignClientFallback.class
)
public interface ItemCommentsFeignClient extends ItemCommentsService {
}
