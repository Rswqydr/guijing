package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.entity.AdminUser;

public interface AdminUserService {

    AdminUser login(String userName, String password);

    int insertUser(String userName, String password);

    AdminUser getUserDetailById(Integer id);

    Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    Boolean updateName(Integer loginUserId, String loginUserName, String nickName);

}
