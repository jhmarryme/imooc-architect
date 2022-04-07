package com.example.order.service.feign.fallback;

import com.example.item.pojo.vo.MyCommentVO;
import com.example.order.service.feign.ItemCommentsFeignClient;
import com.example.pojo.PagedGridResult;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/1 上午9:33
 */
@Component
public class ItemCommentsFeignClientFallback implements ItemCommentsFeignClient {
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        // 采用静默处理，响应默认值
        MyCommentVO myCommentVO = new MyCommentVO();
        myCommentVO.setContent("正在加载中...");

        PagedGridResult result = new PagedGridResult();
        result.setRows(Lists.newArrayList(myCommentVO));
        result.setTotal(1);
        result.setRecords(1);
        return result;
    }

    @Override
    public void saveComments(Map<String, Object> map) {

    }
}
