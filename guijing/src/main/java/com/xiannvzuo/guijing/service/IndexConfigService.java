package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.controller.vo.MallIndexConfigGoodsVO;
import com.xiannvzuo.guijing.entity.IndexConfig;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;

import java.util.List;

public interface IndexConfigService {
    /**
     *常见服务类型
     */
    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigByPrimaryKey(Long id);

    PageResult findIndexConfigList(PageQueryUtil pageQueryUtil);

    Boolean batchDelete(Long[] ids);

    List<MallIndexConfigGoodsVO> getConfigGoods(int configType, int number);


}
