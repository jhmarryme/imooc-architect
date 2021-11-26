package com.jhmaryme.es;

import com.jhmaryme.SearchApplication;
import com.jhmaryme.es.pojo.Stu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author JiaHao Wang
 * @date 2021/5/20 16:26
 */
@SpringBootTest(classes = SearchApplication.class)
@Slf4j
class StuTest {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private StuRepository stuRepository;


    /**
     * 创建索引,并添加映射
     * <p>使用`@Mapping @Setting`注解通过json文件 自定义setting和mapping, 可以完成一些复杂的映射
     *
     */
    @Test
    @DisplayName("创建索引,并添加映射")
    public void whenCreateIndexSuccess() {
        final IndexOperations indexOperations = elasticsearchOperations.indexOps(Stu.class);
        if (!indexOperations.exists()) {
            // 创建索引
            indexOperations.create();
            // 创建映射
            Document mapping = indexOperations.createMapping(Stu.class);
            indexOperations.putMapping(mapping);
        }
    }


    @Test
    @DisplayName("删除索引")
    @Disabled
    public void whenDeleteIndexSuccess() {
        final IndexOperations indexOperations = elasticsearchOperations.indexOps(Stu.class);
        // 只选择删除已存在的索引
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
    }

    @Test
    @DisplayName("新增doc")
    public void whenInsertDocSuccess() {
        Stu stu = createStu();
        IndexCoordinates indexCoordinates = restTemplate.getIndexCoordinatesFor(Stu.class);
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(stu.getStuId().toString())
                .withObject(stu)
                .build();
        String id = restTemplate.index(indexQuery, indexCoordinates);
        log.info("新增doc, id: {}", id);
    }

    @Test
    @DisplayName("批量新增doc")
    public void whenBulkInsertDocSuccess() {
        List<IndexQuery> indexQueryList = new ArrayList<>();
        Stream.iterate(0, integer -> integer + 1)
                .limit(1000)
                .forEach(integer -> {
                    Stu stu = createStu();
                    IndexQuery indexQuery = new IndexQueryBuilder()
                            .withId(stu.getStuId().toString())
                            .withObject(stu)
                            .build();
                    indexQueryList.add(indexQuery);
                });
//        IndexCoordinates indexCoordinates = restTemplate.getIndexCoordinatesFor(Stu.class);
        IndexCoordinates indexCoordinates = IndexCoordinates.of("stu");
        List<IndexedObjectInformation> bulkIndex = restTemplate.bulkIndex(indexQueryList, indexCoordinates);
        bulkIndex.forEach(System.out::println);
    }

    @Test
    @DisplayName("另一种批量新增doc")
    public void whenAnotherBulkInsertDocSuccess() {
        List<Stu> stuList = new ArrayList<>();
        Stream.iterate(0, integer -> integer + 1)
                .limit(1000)
                .forEach(integer -> {
                    stuList.add(createStu());
                });
        Iterable<Stu> stus = stuRepository.saveAll(stuList);
        stus.forEach(System.out::println);
    }

    @Test
    @DisplayName("根据id获取doc")
    public void whenGetDocByIdSuccess() {
        // 已过时
        Stu stu = restTemplate.queryForObject(GetQuery.getById(String.valueOf(177754)), Stu.class);
        log.info("根据id获取doc: {}", stu);

        Stu stu1 = restTemplate.get(String.valueOf(177754), Stu.class);
        log.info("根据id获取doc: {}", stu);
    }

    @Test
    @DisplayName("更新doc")
    public void whenUpdateDocSuccess() {

        Stu stu = restTemplate.get(String.valueOf(177754), Stu.class);
        log.info("根据id获取doc: {}", stu);
        assert stu != null;
        stu.setAge(11111);
        restTemplate.save(stu);
    }

    @Test
    @DisplayName("删除所有")
    public void whenDeleteAllDocSuccess() {
        stuRepository.deleteAll();
    }

    @Test
    @DisplayName("查询所有文档")
    public void whenSearchAllDocSuccess() {
        SearchHits<Stu> stuSearchHits = restTemplate.search(Query.findAll(), Stu.class);
        stuSearchHits.stream().forEach(System.out::println);
    }

    @Test
    @DisplayName("使用ElasticsearchRestTemplate分页查询文档")
    public void whenSearchDocSuccess() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.matchQuery("age", 11111))
                // 分页
                .withPageable(PageRequest.of(10, 20))
                .build();
        SearchHits<Stu> search = restTemplate.search(query, Stu.class);
        // 通过`SearchHitSupport`获取到分页信息
        SearchPage<Stu> searchPage = SearchHitSupport.searchPageFor(search, query.getPageable());
        // 取出查询结果的Stu部分
        List<Stu> result = searchPage.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        search.stream().forEach(System.out::println);
        log.info("总条数: {}", search.getTotalHits());
        log.info("分页信息: {}", searchPage.getPageable());
    }

    @Test
    @DisplayName("通过repository查询并高亮")
    public void whenRepositoryQuerySuccess() {
        SearchPage<Stu> searchPage = stuRepository.findByNameOrAge("spider", 18, PageRequest.of(0, 10));
        Assertions.assertNotNull(searchPage);
    }

    private Stu createStu() {
        return Stu.builder().age(RandomUtils.nextInt(12, 40))
                .name("spider Man " + RandomStringUtils.randomAlphabetic(7))
                .stuId(RandomUtils.nextLong(1000, 999999))
                .money(RandomUtils.nextFloat(10f, 2000f))
                .sign("i am spider man " + RandomStringUtils.randomAlphabetic(7))
                .desc("i am save man " + RandomStringUtils.randomAlphabetic(7))
                .birthday(LocalDateTime.now().minusYears(RandomUtils.nextInt(10, 40)))
                .build();
    }
}