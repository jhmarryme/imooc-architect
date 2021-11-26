package com.jhmaryme.web.service;

import com.jhmarryme.utils.PagedGridResult;

/**
 *
 * @author JiaHao Wang
 * @date 2021/5/26 13:05
 */
public interface ItemsESService {

    PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize);
}
