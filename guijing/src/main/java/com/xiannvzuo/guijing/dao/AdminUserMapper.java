package com.xiannvzuo.guijing.dao;

import com.sun.org.glassfish.gmbal.ParameterNames;
import com.xiannvzuo.guijing.entity.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.type.Alias;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

@Component
public interface AdminUserMapper {
/*
    // 添加一条记录adminuser
    int insertAdminUser(AdminUser record);
*/

    // 插入一条随意登录的用户信息，此功能仅用于检测
    int insertUser(@Param("userName") String userName, @Param("password") String password);

    //登录验证方法
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    // 通过id查看admin信息
    AdminUser selectByAdminUserId(Integer id);

    //通过id查找admin用户
    AdminUser selectByUserId(@Param("id") Integer id);
    // 修改密码
    int updatePassword(AdminUser record);
    // 修改昵称
    int updateNiceName(AdminUser record);
}
