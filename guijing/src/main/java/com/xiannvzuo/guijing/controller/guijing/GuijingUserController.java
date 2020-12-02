package com.xiannvzuo.guijing.controller.guijing;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.entity.GuijingUser;
import com.xiannvzuo.guijing.service.GuijingUserService;
import com.xiannvzuo.guijing.util.MD5Util;
import com.xiannvzuo.guijing.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class GuijingUserController {

    @Autowired
    private GuijingUserService guijingUserService;

    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request) {
        request.setAttribute("path", "personal");
        return "Lmall/personal";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "Lmall/login";
    }


    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "Lmall/login";
    }

    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "Lmall/register";
    }

    @GetMapping("/personal/addresses")
    public String addressesPage() {
        return "Lmall/addresses";
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        // 验证用户名和密码
        if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password) ) {
            return new Result(Constants.RESULT_CODE_FAIL, "用户名或者密码错误", null);
        }
        String result = guijingUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }


    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("password") String password) {
        if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password) ) {
            return new Result(Constants.RESULT_CODE_FAIL, "用户名或者密码为空", null);
        }
        String result = guijingUserService.registerUser(loginName, password);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        } else {
            return new Result(Constants.RESULT_CODE_FAIL, result, null);
        }
    }

    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody GuijingUser guijingUser, HttpSession httpSession) {
        MallUserVO mallUserTemp = guijingUserService.updateUserInfo(guijingUser, httpSession);
        if (mallUserTemp == null) {
            return new Result(Constants.RESULT_CODE_FAIL, "修改失败", null);
        } else {
            return new Result(Constants.RESULT_CODE_SUCCESS, "SUCCESS", null);
        }
    }
}
