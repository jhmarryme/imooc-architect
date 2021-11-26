package com.jhmaryme.es;


import com.jhmaryme.es.pojo.Stu;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author JiaHao Wang
 * @date 2021/5/21 16:56
 */
public interface StuRepository extends ElasticsearchRepository<Stu, Long> {
    /**
     * 高亮关键字查询
     *
     * @author Jiahao Wang
     * @date 2021/5/25 12:19
     * @param name name
     * @param age age
     * @param of
     * @return java.util.List<org.springframework.data.elasticsearch.core.SearchHit < com.jhmaryme.test.Stu>>
     */
    @Highlight(
            fields = {
                    @HighlightField(name = "name"),
                    @HighlightField(name = "age")
            },
            // 可以不指定, 默认是<em></em>
            parameters = @HighlightParameters(
                    preTags = "<strong><font style='color:red'>",
                    postTags = "</font></strong>"
            )

    )
    SearchPage<Stu> findByNameOrAge(String name, int age, PageRequest of);

}
