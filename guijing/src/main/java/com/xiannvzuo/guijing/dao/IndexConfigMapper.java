package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.IndexConfig;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface IndexConfigMapper {
    /**
     * 配置项的增删改查系列操作
     */
    int insert(IndexConfig indexConfig);

    int insertSelective(IndexConfig indexConfig);

    int deleteByPrimaryKey(Long configId);

    List<IndexConfig> findIndexConfigList(PageQueryUtil pageQueryUtil);
    int totalIndexConfigs(PageQueryUtil pageQueryUtil);

    int batchDelete(Long[] ids);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig indexConfig);

    List<IndexConfig> findIndexConfigByTypeAndNumber(@Param("configType") int configType, @Param("number") int number );

}
