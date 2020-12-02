package com.xiannvzuo.guijing.controller.guijing;

import com.sun.java.accessibility.util.GUIInitializedListener;
import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallException;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.entity.GuijingShoppingCart;
import com.xiannvzuo.guijing.entity.GuijingUser;
import com.xiannvzuo.guijing.service.GuijingShoppingCartService;
import com.xiannvzuo.guijing.util.Result;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
public class GuijingShoppingCartController {

    private static final Logger Log = LoggerFactory.getLogger(GuijingShoppingCartController.class);
    @Autowired
    private GuijingShoppingCartService guijingShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request, HttpSession session) {
        Log.info("进入--cartListPage--");
        MallUserVO guijingUser = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemSize = 0;
        int priceTotal = 0;
        List<MallShoppingCartItemVO> guijingShoppingCartList = guijingShoppingCartService.getShoppingCartItems(guijingUser.getUserId());
        if (CollectionUtils.isEmpty(guijingShoppingCartList)) {
            // 获取购买的商品总数
            int itemTotal = guijingShoppingCartList.stream().mapToInt(MallShoppingCartItemVO::getGoodsCount).sum();
            if (itemTotal < 1) {
                return "error/error_5xx";
            }
            itemSize = guijingShoppingCartList.size();
            // 总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : guijingShoppingCartList) {
                priceTotal += mallShoppingCartItemVO.getGoodsCount()*mallShoppingCartItemVO.getSellingPrice();
            }
            // 错误预想
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemSize", itemSize);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", guijingShoppingCartList);
        return "Lmall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveShoppingCart(@RequestBody GuijingShoppingCart guijingShoppingCart, HttpSession session) {
        // 首先获取用户状态，如获取用户名
        MallUserVO user = (MallUserVO)session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 将该用户名加到购物车
        guijingShoppingCart.setUserId(user.getUserId());
        // 获取保存结果
        String result = guijingShoppingCartService.saveGoodsToCart(guijingShoppingCart);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result,null);
        }
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateGuijingShoppingCart(@RequestBody GuijingShoppingCart guijingShoppingCart, HttpSession session) {
        // 获取用户状态信息
        MallUserVO user = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        // 添加用户信息到购物车对象
        guijingShoppingCart.setUserId(user.getUserId());
        // 修改购物车数据
        String result = guijingShoppingCartService.updateGuijingShoppingCart(guijingShoppingCart);
        // 返回结果
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result,null);
        }
    }

    @DeleteMapping("/shop-cart/{mallShoppingCartItemId}")
    @ResponseBody
    public Result deleteShoppingCart(@PathVariable("mallShoppingCartItemId") Long id, HttpSession session) {
        Boolean result = guijingShoppingCartService.deleteGoodsById(id);
        if (result) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS",null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, ServiceResultEnum.OPERATE_ERROR.getResult(),null);
        }
    }


    @GetMapping("/shop-cart/settle")
    public String settlePage(Long[] cartItemIds, HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        if (cartItemIds.length < 1) {
            MallException.fail("参数异常");
        }
        List<MallShoppingCartItemVO> itemsForSettle = guijingShoppingCartService.getCartByUserIdsAndCartId(Arrays.asList(cartItemIds), user.getUserId());
        if (CollectionUtils.isEmpty(itemsForSettle)) {
            //无数据则不跳转至结算页
            MallException.fail("参数异常");
        } else {
            //总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : itemsForSettle) {
                priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                MallException.fail("价格异常");
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("cartItems", itemsForSettle);
        request.setAttribute("cartItemIds", Arrays.toString(cartItemIds).replace("[", "").replace("]", ""));
        return "Lmall/order-settle";
    }

}
