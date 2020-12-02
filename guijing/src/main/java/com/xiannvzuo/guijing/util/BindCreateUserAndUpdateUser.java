package com.xiannvzuo.guijing.util;

import com.xiannvzuo.guijing.entity.GuijingGoods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class BindCreateUserAndUpdateUser {
    public static void bingGoods(GuijingGoods guijingGoods, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // 获取在登录时候存储的用户id，创建者和修改者都设定为自己的id
        guijingGoods.setCreateUser((Integer) session.getAttribute("primaryId"));
        guijingGoods.setUpdateUser((Integer) session.getAttribute("primaryId"));
    }
}
