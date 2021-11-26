package com.jhmaryme.web.service.impl;

import com.jhmarryme.utils.PagedGridResult;
import com.jhmaryme.es.pojo.Items;
import com.jhmaryme.web.service.ItemsESService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author JiaHao Wang
 * @date 2021/5/26 13:08
 */
@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(page, pageSize);

        SortBuilder sortBuilder = null;
        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts")
                    .order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price")
                    .order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword")
                    .order(SortOrder.ASC);
        }
        String itemNameFiled = "itemName";

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameFiled, keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameFiled))
                .withPageable(pageRequest)
                .withSort(sortBuilder)
                .build();

        SearchHits<Items> search = restTemplate.search(searchQuery, Items.class);
        // 构建分页信息
        SearchPage<Items> searchPage = SearchHitSupport.searchPageFor(search, searchQuery.getPageable());

        // 高亮字段会被单独存储
        // 这里配合前端, 将高亮字段提取出来. 直接放到搜索的结果中
        List<Items> collect = searchPage.getSearchHits()
                .stream()
                .map(itemsSearchHit -> {
                    Items content = itemsSearchHit.getContent();
                    content.setItemName(StringUtils.join(itemsSearchHit.getHighlightField(itemNameFiled), ""));
                    return content;
                })
                .collect(Collectors.toList());

        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(collect);
        gridResult.setPage(page + 1);
        gridResult.setTotal(searchPage.getTotalPages());
        gridResult.setRecords(searchPage.getTotalElements());

        return gridResult;
    }
}
