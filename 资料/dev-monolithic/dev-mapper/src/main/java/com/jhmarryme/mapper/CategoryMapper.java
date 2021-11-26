package com.jhmarryme.mapper;

import com.jhmarryme.my.mapper.MyMapper;
import com.jhmarryme.pojo.Category;
import com.jhmarryme.pojo.vo.CategoryVO;
import com.jhmarryme.pojo.vo.SubCategoryVO;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends MyMapper<Category> {

    @Select("SELECT id, name, type, father_id FROM category WHERE father_id=#{rootCatId}")
    @Results(
            id = "subCatMap",
            value = {
                @Result(id = true, column = "id", property = "id"),
                @Result(column = "name", property = "name"),
                @Result(column = "type", property = "type"),
                @Result(column = "father_id", property = "fatherId"),
                @Result(column = "id", property = "subCatList", many = @Many(select = "com.jhmarryme.mapper.CategoryMapper.selectById"))
            }
    )
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    @Select("SELECT id, name, type, father_id FROM category WHERE father_id=#{id}")
    @Results(
            id = "seleceByIdMap",
            value = {
                    @Result(id = true, column = "id", property = "subId"),
                    @Result(column = "name", property = "subName"),
                    @Result(column = "type", property = "subType"),
                    @Result(column = "father_id", property = "subFatherId")
            }
    )
    public List<SubCategoryVO> selectById(Integer id);

}