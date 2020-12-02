package com.xiannvzuo.guijing.controller.admin;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallCategoryLevelEnum;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.service.GoodsCategoryService;
import com.xiannvzuo.guijing.service.GuijingGoodsService;
import com.xiannvzuo.guijing.util.BindCreateUserAndUpdateUser;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.jws.Oneway;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class GuijingGoodsController {
    private static final Logger LOG = LoggerFactory.getLogger(GuijingGoodsController.class);

    @Autowired
    private GuijingGoodsService guijingGoodsService;
    @Autowired
    private GoodsCategoryService goodsCategoryService;

    /**
     * 进如具体页面接口
     * @param request
     * @return
     */
    @GetMapping("/goods")
    public String goods(HttpServletRequest request) {
        LOG.info("进入商品页");
        request.setAttribute("path", "moll_goods");
        return "admin/mall_goods";
    }


    /**
     * 默认展开第一个实体的级联处理接口
     */
    // 注意，返回视图的方法，可以通过设置HttpServletRequest来设置返回数据！
    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        LOG.info("--edit默认展开层级服务中--");
        request.setAttribute("path", "edit");
        // 通过仿造一个list 内放数据0 来获取父ParentId=0的第一层类目
        // 仿造方法：Collections.singletonList(0L) list<Long>
        List<GoodsCategory> firstGoodsCategoryList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L));
        LOG.info("第一层级" +firstGoodsCategoryList.toString());
        if (!CollectionUtils.isEmpty(firstGoodsCategoryList)) {
            LOG.info("第一层级第0" + firstGoodsCategoryList.get(0).getParentId().toString());
            List<GoodsCategory> secondGoodsCategoryList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(firstGoodsCategoryList.get(0).getCategoryId()));
            if (!CollectionUtils.isEmpty(secondGoodsCategoryList)) {
                List<GoodsCategory> thirdGoodsCategoryList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), Collections.singletonList(secondGoodsCategoryList.get(0).getCategoryId()));
                request.setAttribute("firstLevelCategories", firstGoodsCategoryList);
                request.setAttribute("secondLevelCategories", secondGoodsCategoryList);
                request.setAttribute("thirdLevelCategories", thirdGoodsCategoryList);
                request.setAttribute("path", "goods-edit");
                LOG.info("--edit结果返回--");
                return "admin/mall_goods_edit";
            }
        }
        LOG.info("--edit错误视图--");
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        LOG.info("--指定id层级展开中--");
        request.setAttribute("path", "edit");
        GuijingGoods guijingGoods = guijingGoodsService.getGuijingGoods(goodsId);
        if (guijingGoods == null ) {
            return "error/error_400";
        }
        if (guijingGoods.getGoodsCategoryId() > 0) {
            if (guijingGoods.getGoodsCategoryId() != null || guijingGoods.getGoodsCategoryId() > 0) {
                // 获取对应类别的对象
                GoodsCategory goodsCategory = goodsCategoryService.getCaategoryById(guijingGoods.getGoodsCategoryId());
                // 如果该类别不为空，且未第三层，才有意义
                if (goodsCategory != null && goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    // 查询所有的一级分类
                    List<GoodsCategory> firstGoodsCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L));
                    // 根据parentId查看当前的parentId下的所有category
                    List<GoodsCategory> thirdGoodsCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(),Collections.singletonList(goodsCategory.getCategoryId()));
                    // 查看当前parentId下的父级分类
                    GoodsCategory secondGoodsCategory = goodsCategoryService.getCaategoryById(goodsCategory.getParentId());
                    if (secondGoodsCategory != null) {
                        // 找到和此parentId同级的所有类目
                        List<GoodsCategory> secondGoodsCategoryList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(secondGoodsCategory.getParentId()));
                        // 然后顺势找到自己的顶头上司层
                        GoodsCategory firstGoodsCategory = goodsCategoryService.getCaategoryById(secondGoodsCategory.getParentId());
                        if (firstGoodsCategory != null) {
                            LOG.info("此时未出方法：后接6个request的set方法");
                            // 如果存在第一层的话，那么就可以开始输出数据了，将所有获取到的数据放到request中供前端进行选择，为此：
                            request.setAttribute("firstLevelCategories", firstGoodsCategories);
                            request.setAttribute("secondLevelCategories", secondGoodsCategoryList);
                            request.setAttribute("thirdLevelCategories", thirdGoodsCategories);
                            request.setAttribute("firstLevelCategoryId", firstGoodsCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondGoodsCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", goodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        // 说明此时展开的是第一层，那么只需要把后面的层展开即可，默认为展开的层均展开第一个实体
        LOG.info("层级判断为：0");
        if (guijingGoods.getGoodsCategoryId() == 0 ) {
             // 查询所有的一级分类
             List<GoodsCategory> firstGoodsCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L));
             if (!CollectionUtils.isEmpty(firstGoodsCategories)) {
                 // 展示第二分类 默认展开第一个实体的
                 List<GoodsCategory> secondGoodsCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(firstGoodsCategories.get(0).getCategoryId()));
                 // 如果第二个实体也不为空，则展开第三个实体
                 if (!CollectionUtils.isEmpty(secondGoodsCategories)) {
                    List<GoodsCategory> thirdGoodsCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), Collections.singletonList(secondGoodsCategories.get(0).getCategoryId()));
                    request.setAttribute("thirdLevelCategories", thirdGoodsCategories);
                     request.setAttribute("secondLevelCategories",secondGoodsCategories);
                     request.setAttribute("firstLevelCategories",firstGoodsCategories);
                 }

            }
        }
        request.setAttribute("goods", guijingGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/mall_goods_edit";
    }

    /**
     * 列表
     */
    // 回忆：可以将数组转化为json接收哦
    @GetMapping("/goods/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> param) {
        LOG.info("--list--");
        // 分页一律先判断参数是否存在
        if (StringUtils.isEmpty(param.get("limit")) || StringUtils.isEmpty(param.get("page"))) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", guijingGoodsService.getGuijingGoodsPage(new PageQueryUtil(param)));
    }

    /**
     * 添加
     */
    @PostMapping("/goods/save")
    @ResponseBody
    public Result save(@RequestBody GuijingGoods guijingGoods, HttpServletRequest request) {
        LOG.info("--save--");
        if (StringUtils.isEmpty(guijingGoods.getGoodsName())
                || StringUtils.isEmpty(guijingGoods.getgoodsIntro())
                || StringUtils.isEmpty(guijingGoods.getTag())
                || Objects.isNull(guijingGoods.getOriginalPrice())
                || Objects.isNull(guijingGoods.getGoodsCategoryId())
                || Objects.isNull(guijingGoods.getSellingPrice())
                || Objects.isNull(guijingGoods.getStockNum())
                || Objects.isNull(guijingGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(guijingGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(guijingGoods.getGoodsDetailContent())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        BindCreateUserAndUpdateUser.bingGoods(guijingGoods, request);
        String result = guijingGoodsService.saveGuijingGoods(guijingGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "数据库异常", result);
        }
    }

    @PostMapping("/goods/update")
    @ResponseBody
    public Result update(@RequestBody GuijingGoods guijingGoods) {
        LOG.info("--update--");
        if (Objects.isNull(guijingGoods.getGoodsId())
                || StringUtils.isEmpty(guijingGoods.getGoodsName())
                || StringUtils.isEmpty(guijingGoods.getgoodsIntro())
                || StringUtils.isEmpty(guijingGoods.getTag())
                || Objects.isNull(guijingGoods.getOriginalPrice())
                || Objects.isNull(guijingGoods.getSellingPrice())
                || Objects.isNull(guijingGoods.getGoodsCategoryId())
                || Objects.isNull(guijingGoods.getStockNum())
                || Objects.isNull(guijingGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(guijingGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(guijingGoods.getGoodsDetailContent())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = guijingGoodsService.updateGuijingGoods(guijingGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "SUCCESS", result);
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        LOG.info("--info--");
        GuijingGoods guijingGoods = guijingGoodsService.getGuijingGoods(id);
        if (guijingGoods == null) {
            return new Result(Constants.RESULT_CODE_FAIL, ServiceResultEnum.DATA_NOT_EXIST.getResult(), null);
        } else {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", guijingGoods);
        }
    }

    /**
     * 批量修改状态
     */
    @PutMapping("/goods/status/{sellStatus}")
    @ResponseBody
    public Result put(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        LOG.info("--put--");
        if (ids.length < 1) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        LOG.info(sellStatus+"");
        if (sellStatus != Constants.SELL_STATUS_DOWN && sellStatus != Constants.SELL_STATUS_UP) {
            return new Result(Constants.RESULT_CODE_FAIL, "状态异常", null);
        }
        if (guijingGoodsService.batchUpdateGuijingGoodsStatus(ids, sellStatus)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "更新失败", null);
        }
    }



}
