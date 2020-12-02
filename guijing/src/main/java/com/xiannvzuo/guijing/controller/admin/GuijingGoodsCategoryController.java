package com.xiannvzuo.guijing.controller.admin;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallCategoryLevelEnum;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.service.GoodsCategoryService;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.Result;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class GuijingGoodsCategoryController {

    private static final Logger LOG = LoggerFactory.getLogger(GuijingGoodsCategoryController.class);
    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @GetMapping("/coupling-test")
    public String couplingTest(HttpServletRequest request) {
        LOG.info("couplingTest");
        request.setAttribute("path", "coupling-test");
        // 查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L));
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            /*for (GoodsCategory firstLevelCategory : firstLevelCategories) {*/
                List<GoodsCategory> secondLevelCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(firstLevelCategories.get(0).getCategoryId()));
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    /*for (GoodsCategory secondLevelCategory : secondLevelCategories) {*/
                    List<GoodsCategory> thirdLevelCategories = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), Collections.singletonList(secondLevelCategories.get(0).getCategoryId()));
                    /*}*/
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                    return "admin/coupling-test";
                }
            /*}*/
        }
        return "error/error_5xx";
    }

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request, @RequestParam("categoryLevel") Byte categoryLevel, @RequestParam("parentId") Long parentId, @RequestParam("backParentId") Long backParentId) {
        LOG.info("categoriesPage");
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            return "error/error_5xx";
        }
        request.setAttribute("path", "mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/mall_category";
    }

    /**
     *商品类目分页列表接口
     */
    @GetMapping("/categories/listForSelect")
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId) {
        LOG.info("listForSelect");
        // 查看id是否符合规范
        if (categoryId < 1 || categoryId == null) {
            return new Result(Constants.RESULT_CODE_FAIL, "缺少参数", null);
        }
        GoodsCategory goodsCategory = goodsCategoryService.getCaategoryById(categoryId);
        // 查看id是否为第三层的，如果为第三层，没有下一层，则参数异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数错误", null);
        }
        Map<String, Object> categoryResult = new HashMap<>(2);
        // 如果id是第一层的，则展开第一层的下一层列表
        if (goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            List<GoodsCategory> secondCategotiesList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(categoryId));
            // 如果第一层的下一层不为空，则依次展开第三层的所有分类，这里先仅展开第一个实体的第三层所有分类
            if (!CollectionUtils.isEmpty(secondCategotiesList)){
                // 获取第二层第一个实体下面的所有分类
                List<GoodsCategory> thirdCategoriesList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), Collections.singletonList(secondCategotiesList.get(0).getCategoryId()));
                categoryResult.put("secondLevelCategories", secondCategotiesList);
                categoryResult.put("thirdLevelCategories", thirdCategoriesList);
            }
        }
        // 如果id是第二层的，则展开所有第二层的所有下层分类
        if (goodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            List<GoodsCategory> thirdCategoriesList = goodsCategoryService.getCategoriesByLevelAndParentId(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), Collections.singletonList(categoryId));
            categoryResult.put("thirdLevelCategories", thirdCategoriesList);
        }
        return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", categoryResult);
    }

    /**
     *商品类目分页列表接口
     */
    @GetMapping("/categories/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        LOG.info("list");
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            LOG.error("缺少参数");
            return new Result(Constants.RESULT_CODE_FAIL, "缺少参数", null);
        }
        LOG.info("list - 正确");
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        LOG.info("获取--pageQueryUtil--" +pageQueryUtil.toString());
        LOG.info("获取--getCategoriesPage--" +goodsCategoryService.getCategoriesPage(pageQueryUtil).toString());
        return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", goodsCategoryService.getCategoriesPage(pageQueryUtil));
    }

    /**
     * 添加商品类类目接口
     */
    @PostMapping("/categories/save")
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory) {
        LOG.info("save");
        // 添加类目需要保证该类目存在：level、parentId,name,rank
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getCategoryRank())
                || Objects.isNull(goodsCategory.getParentId())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = goodsCategoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }

    /**
     * 修改商品类目接口
     */
    @PostMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory) {
        LOG.info("update");
        if (Objects.isNull(goodsCategory.getCategoryId())
                ||Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getCategoryRank())
                || Objects.isNull(goodsCategory.getParentId())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = goodsCategoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }

    /**
     * 详情页
     */
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long categoryId) {
        LOG.info("info");
        GoodsCategory goodsCategory =goodsCategoryService.getCaategoryById(categoryId);
        if (goodsCategory != null) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", goodsCategory);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "未查询到数据", null);
        }
    }

    /**
     * 删除商品类名
     * 也可批量删除商品类目接口
     */
    ////////////////////////////////////////////////////////////
    // 此处可能问题，如果删除的类目是第二层或者第一层的，那么应该附带将该类目下所有类目都删除
    // 错误：未执行此操作！！！！！！！！！！！！！！！！！！
    ///////////////////////////////////////////////////////////
    @PostMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        LOG.info("delete");
        if(ids.length < 1) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        if (goodsCategoryService.deleteCategoriesBatch(ids)) {
            LOG.info("删除成功");
            return new Result(Constants.RESULT_CODE_SUCCESS, "删除成功", null);
        } else {
            LOG.info("删除失败");
            return new Result(Constants.RESULT_CODE_FAIL, "删除失败", null);
        }

    }

}
