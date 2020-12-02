package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.GuijingUser;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface GuijingUserMapper {
    /**
     * 添加新用户
     */
    int insert(GuijingUser guijingUser);
    int insertSelective(GuijingUser guijingUser);

    /**
     * 删除用户
     */
    int deleteByPrimaryKey(Long userId);

    // 冻结用户
    int lockUserByPrimaryKey(Long userId);

    /**
     * 查询相关
     */
    GuijingUser selectByPrimaryKey(Long userId);
    // 验证用户名密码
    GuijingUser login(@Param("loginName") String userName, @Param("loginPassword") String password);
    // 通过登录名查找
    GuijingUser selectByLoginName(@Param("loginName") String userName);
    /**
     * 修改用户信息
     */
    int updateSelective(GuijingUser guijingUser);
    /**
     * 修改收货地址
     */
    int updateUserAddressByPrimaryKey(@Param("address") String address, @Param("id") Long id);

    // 找用户分页
    List<GuijingUser> findGuijingUserList(PageQueryUtil pageQueryUtil);
    // 配套使用
    int getTotalGuijingUser(PageQueryUtil pageQueryUtil);

    // 批量修改用户状态
    int batchChangeLock(@Param("ids") Long[] ids, @Param("lockStatus") int lockStatus);
}
