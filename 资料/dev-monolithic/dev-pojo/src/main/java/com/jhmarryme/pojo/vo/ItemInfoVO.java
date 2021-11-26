package com.jhmarryme.pojo.vo;

import com.jhmarryme.pojo.Items;
import com.jhmarryme.pojo.ItemsImg;
import com.jhmarryme.pojo.ItemsParam;
import com.jhmarryme.pojo.ItemsSpec;
import lombok.Data;

import java.util.List;

/**
 * 商品详情VO
 */
@Data
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;

}
