package com.jhmarryme.mapper;

import com.jhmarryme.my.mapper.MyMapper;
import com.jhmarryme.pojo.ItemsComments;
import com.jhmarryme.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}