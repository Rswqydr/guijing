package com.xiannvzuo.guijing.controller.admin;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.entity.Carousel;
import com.xiannvzuo.guijing.service.GuijingCarouselService;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class GuiJingCarouselController {

    @Autowired
    private GuijingCarouselService guijingCarouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request) {
        request.setAttribute("path", "mall_carousel");
        return "admin/mall_carousel";
    }
    /*
    轮播图分页列表
     */
    @GetMapping("/carousels/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty("limit")) {
            return new Result(Constants.RESULT_CODE_FAIL, "缺失数据", null);
        }
        PageQueryUtil p = new PageQueryUtil(params);
        return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",
                guijingCarouselService.getCarouselPage(p));
    }
    /*
    添加轮播图
     */
    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel) {
        // 先判断参数是否正常
        if (StringUtils.isEmpty(carousel.getCarouselUrl()) || StringUtils.isEmpty(carousel.getCarouselRank())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = guijingCarouselService.saveCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",
                    null);
        }
        return new Result(Constants.RESULT_CODE_FAIL, result, null);
        //
    }

    /*
    根据id修改轮播图
     */
    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update(@RequestBody Carousel carousel) {
        // 根据id修改，所以要求id必须存在
        if (Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        String result = guijingCarouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",
                    null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }
    /*
    获取单条轮播图
     */

    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@RequestParam("id") Integer id) {
        Carousel carousel = guijingCarouselService.getCarouselById(id);
        if (carousel == null) {
            return new Result(Constants.RESULT_CODE_FAIL, ServiceResultEnum.DATA_NOT_EXIST.getResult(), null);
        }
        return new Result(Constants.RESULT_CODE_FAIL, "SUCCESS", carousel);
    }

    /*
    批量删除轮播图
     */
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数异常", null);
        }
        if (guijingCarouselService.deleteBatch(ids)) {
            return new Result(Constants.RESULT_CODE_FAIL, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, "删除失败", null);
        }
    }
}
