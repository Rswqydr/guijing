package com.xiannvzuo.guijing.controller.guijing;

import com.sun.org.apache.regexp.internal.RE;
import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallException;
import com.xiannvzuo.guijing.common.MallOrderStatusEnum;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.entity.GuijingOrder;
import com.xiannvzuo.guijing.entity.GuijingShoppingCart;
import com.xiannvzuo.guijing.service.GuijingOrderService;
import com.xiannvzuo.guijing.service.GuijingShoppingCartService;
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
import sun.print.PSPrinterJob;

import javax.jws.Oneway;
import javax.naming.MalformedLinkException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
public class GuijingOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(GuijingOrderController.class);

    @Autowired
    private GuijingOrderService guijingOrderService;
    @Autowired
    private GuijingShoppingCartService guijingShoppingCartService;

    @PostMapping("/saveOrder")
    @ResponseBody
    public Result saveOrder(Long[] cartItemIds, HttpSession httpSession) {
        // 获取订单的用户
        MallUserVO mallUserVO = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 判断购物车ids参数是否异常
        if (cartItemIds.length < 1) {
            return new Result(Constants.RESULT_CODE_FAIL, "参数错误", null);
        }
        // 从购物车获取表现层信息
        List<MallShoppingCartItemVO> mallShoppingCartItemVOList = guijingShoppingCartService.getCartByUserIdsAndCartId(Arrays.asList(cartItemIds), mallUserVO.getUserId());
        if (!CollectionUtils.isEmpty(mallShoppingCartItemVOList)) {
            // 如果购物车中确实有可用正确信息，则生成订单
            String orderNo = guijingOrderService.saveOrder(mallUserVO, mallShoppingCartItemVOList);
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", orderNo);
        }
        return new Result(Constants.RESULT_CODE_FAIL, "生成订单失败", null);
    }

    @GetMapping("/selectPayType")
    public String payPage(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        // 从session中获取用户信息
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 从订单号获取订单
        GuijingOrder guijingOrder= guijingOrderService.getOrderByOrderNo(orderNo);
        // 检测订单状态，判断当前是否可以进行支付
        if (guijingOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            MallException.fail("非待支付状态不可进行支付操作");
        }
        // 设置request信息，如订单号，订单总额
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", guijingOrder.getTotalPrice());
        // 根据支付方式调用支付API 其内部正确实现应该是工具类的分装。
        if (payType == 1) {
            return "Lmall/alipay";
        } else {
            return "Lmall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = guijingOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, payResult, null);
        }
    }

    @GetMapping("/successPage/{orderNo}")
    public String successfulPage(HttpServletRequest request, @PathVariable("orderNo")String orderNo, HttpSession httpSession) {
        // 获取用户信息
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 获取订单对象
        GuijingOrder guijingOrder = guijingOrderService.getOrderByOrderNo(orderNo);
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("userAddress", guijingOrder.getUserAddress());
        request.setAttribute("totalPrice",guijingOrder.getTotalPrice());
        return "Lmall/success";
    }

    @GetMapping("/orders")
    public String orderList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        // 获取用户信息
        LOG.info("进入orderList");
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", guijingOrderService.getGuijingOrderPage(pageQueryUtil));
        request.setAttribute("path", "orders");
        return "Lmall/my-orders";
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = guijingOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = guijingOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }







}
