package com.example.item.service;

import com.example.pojo.PagedGridResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 * @author JiaHao Wang
 * @date 2021/11/29 下午12:34
 */
@RequestMapping("item-comments-api")
public interface ItemCommentsService {

    /**
     * 我的评价查询 分页
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("myComments")
    public PagedGridResult queryMyComments(@RequestParam("userId") String userId,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "pageSize", required = false)Integer pageSize);

    @PostMapping("saveComments")
    public void saveComments(@RequestBody Map<String, Object> map);
}
