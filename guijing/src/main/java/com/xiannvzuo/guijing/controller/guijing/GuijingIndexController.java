package com.xiannvzuo.guijing.controller.guijing;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.IndexConfigTypeEnum;
import com.xiannvzuo.guijing.controller.vo.GuijingIndexCarouselVO;
import com.xiannvzuo.guijing.controller.vo.GuijingIndexCategoryVO;
import com.xiannvzuo.guijing.controller.vo.MallIndexConfigGoodsVO;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.service.GoodsCategoryService;
import com.xiannvzuo.guijing.service.GuijingCarouselService;
import com.xiannvzuo.guijing.service.GuijingGoodsService;
import com.xiannvzuo.guijing.service.IndexConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
public class GuijingIndexController {
    private static final Logger LOG = LoggerFactory.getLogger(GuijingIndexController.class);

    @Autowired
    private GoodsCategoryService goodsCategoryService;


    @Autowired
    private GuijingCarouselService guijingCarouselService;

    @Autowired
    private IndexConfigService indexConfigService;

    @GetMapping({"/index","", "/", "/index.html"})
    public String index(HttpServletRequest request) {
        LOG.info("进入商城首页");
        List<GuijingIndexCategoryVO> guijingIndexCategoryVOS  =  goodsCategoryService.getCategoryByIndex();
        if (StringUtils.isEmpty(guijingIndexCategoryVOS)) {
            return "error/error_5xx";
        }
        List<GuijingIndexCarouselVO> guijingIndexCarouselVOS = guijingCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<MallIndexConfigGoodsVO> hotGoods = indexConfigService.getConfigGoods(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsVO> newGoods = indexConfigService.getConfigGoods(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsVO> recommendGoods = indexConfigService.getConfigGoods(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", guijingIndexCategoryVOS);//分类数据
        request.setAttribute("carousels", guijingIndexCarouselVOS);//轮播图
        request.setAttribute("hotGoodses", hotGoods);//热销商品
        request.setAttribute("newGoodses", newGoods);//新品
        request.setAttribute("recommendGoodses", recommendGoods);//推荐商品
        return "Lmall/index";

    }
}
