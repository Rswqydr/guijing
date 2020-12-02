package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallIndexConfigGoodsVO;
import com.xiannvzuo.guijing.dao.GuijingGoodsMapper;
import com.xiannvzuo.guijing.dao.IndexConfigMapper;
import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.entity.IndexConfig;
import com.xiannvzuo.guijing.service.IndexConfigService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class IndexConfigServiceImpl implements IndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;
    @Autowired
    private GuijingGoodsMapper guijingGoodsMapper;

    private static final Logger LOG = LoggerFactory.getLogger(IndexConfigServiceImpl.class);

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        LOG.info("--service:saveIndexConfig");
        /*IndexConfig indexConfig1 = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (indexConfig == null) {*/
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
       /* }*/
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp != null ) {
            if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public IndexConfig getIndexConfigByPrimaryKey(Long id) {
        return indexConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult findIndexConfigList(PageQueryUtil pageQueryUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageQueryUtil);
        int total = indexConfigMapper.totalIndexConfigs(pageQueryUtil);
        return new PageResult(indexConfigs, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public Boolean batchDelete(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return indexConfigMapper.batchDelete(ids) > 0;
    }

    /**
     * 提供给前端展示，所以使用VO是必要地
     * @param configType
     * @param number
     * @return
     */
    @Override
    public List<MallIndexConfigGoodsVO> getConfigGoods(int configType, int number) {
        List<MallIndexConfigGoodsVO> mallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigByTypeAndNumber(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)){
            List<Long> goodsId = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<GuijingGoods> guijingGoods = guijingGoodsMapper.selectByBatchPrimaryKeys(goodsId);
            mallIndexConfigGoodsVOS = BeanUtil.copyList(guijingGoods, MallIndexConfigGoodsVO.class);
            // 对返回的数据大小做限制
            for (MallIndexConfigGoodsVO mallIndexConfigGoodsVO : mallIndexConfigGoodsVOS) {
                String goodsName = mallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = mallIndexConfigGoodsVO.getGoodsIntro();
                if (goodsName.length() > 30 ) {
                    goodsName = goodsName.substring(0, 30);
                    mallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22);
                    mallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return mallIndexConfigGoodsVOS;
    }
}
