package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.entity.GuijingUser;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;

import javax.servlet.http.HttpSession;

public interface GuijingUserService {
    /**
     * 查看用户列表
     */
    PageResult searchUserList(PageQueryUtil pageQueryUtil);

    /**
     * 查看单个用户
     */
 /*   GuijingUser getUserByPrimaryKey(Long id);
*/
    //查看用户是否存在
  /*  String getUserByUserNameAndPassword(String userName, String password);
*/
    /*// 冻结用户
    Boolean lockUser(Long id);*/
    // 批量冻结/解冻用户
    Boolean batchLockUser(Long[] ids, int lockStatus);

    // 注册用户信息
    String registerUser(String loginName, String password);

    // 修改用户信息并返回最新的用户信息
    MallUserVO updateUserInfo(GuijingUser guijingUser, HttpSession session);

    /**
     * 补充
     */
    String login(String loginName, String passwordMd5, HttpSession session);


}

