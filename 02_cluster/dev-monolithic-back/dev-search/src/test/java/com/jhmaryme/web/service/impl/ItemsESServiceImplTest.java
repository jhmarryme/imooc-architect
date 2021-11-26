package com.jhmaryme.web.service.impl;

import com.jhmarryme.utils.PagedGridResult;
import com.jhmaryme.SearchApplication;
import com.jhmaryme.web.service.ItemsESService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author JiaHao Wang
 * @date 2021/5/26 13:19
 */
@SpringBootTest(classes = SearchApplication.class)
class ItemsESServiceImplTest {

    @Autowired
    private ItemsESService itemsESService;

    @Test
    @Disabled
    void searhItems() {
        PagedGridResult pagedGridResult = itemsESService.searhItems("冬日必备红茶", "c", 0, 40);
        Assertions.assertNotNull(pagedGridResult);
    }
}