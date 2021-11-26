package com.jhmaryme.web.controller;

import com.jhmarryme.utils.CommonResult;
import com.jhmarryme.utils.PagedGridResult;
import com.jhmaryme.web.service.ItemsESService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/hello")
    public Object hello() {
        return "Hello Elasticsearch~";
    }

    @GetMapping("/es/search")
    public CommonResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return CommonResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page--;

        PagedGridResult grid = itemsESService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return CommonResult.ok(grid);
    }

}
