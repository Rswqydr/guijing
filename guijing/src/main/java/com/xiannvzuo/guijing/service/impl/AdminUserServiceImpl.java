package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.dao.AdminUserMapper;
import com.xiannvzuo.guijing.entity.AdminUser;
import com.xiannvzuo.guijing.service.AdminUserService;
import com.xiannvzuo.guijing.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String userName, String password) {
        // 需要对密码进行MD5加密
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        // 直接返回查询到的admin User，如果不存在，会返回null，以此判断账号存在否
        return adminUserMapper.login(userName, passwordMD5);
    }

    @Override
    public int insertUser(String userName, String password) {
        // 需要对密码进行MD5加密
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserMapper.insertUser(userName, passwordMD5);
    }

    @Override
    public AdminUser getUserDetailById(Integer id) {
        return adminUserMapper.selectByAdminUserId(id);
    }

    @Override
    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        // 获取admin信息
        AdminUser adminUser = adminUserMapper.selectByAdminUserId(loginUserId);
        // 如果该管理员信息存在，才能修改密码
        if (adminUser != null) {
            // 将密码转换为MD5
            String originalMD5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPassMD5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            // 判断旧密码是否和数据库中密码一致
            // 如果一致，修改密码
            if (originalMD5.equals(adminUser.getAdminPassword())) {
                adminUser.setAdminPassword(newPassword);
                if (adminUserMapper.updatePassword(adminUser) > 0) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        // 获取admin用户信息
        AdminUser adminUser = adminUserMapper.selectByAdminUserId(loginUserId);
        // 如果用户信息存在，才修改昵称
        if (adminUser != null) {
            adminUser.setAdminName(loginUserName);
            adminUser.setNiceName(nickName);
            if (adminUserMapper.updateNiceName(adminUser) > 0) {
                return true;
            }
        }
       return false;
    }


}
