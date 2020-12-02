package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.dao.GuijingUserMapper;
import com.xiannvzuo.guijing.entity.GuijingUser;
import com.xiannvzuo.guijing.service.GuijingUserService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.MD5Util;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class GuijingUserServiceImpl implements GuijingUserService {
    // 引入日志
    private static final Logger LOG = LoggerFactory.getLogger(GuijingUserServiceImpl.class);

    @Autowired
    private GuijingUserMapper guijingUserMapper;


    @Override
    public PageResult searchUserList(PageQueryUtil pageQueryUtil) {
        LOG.info("--searchUserList");
        int total = guijingUserMapper.getTotalGuijingUser(pageQueryUtil);
        List<GuijingUser> guijingUserList = guijingUserMapper.findGuijingUserList(pageQueryUtil);
        return new PageResult(guijingUserList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public Boolean batchLockUser(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return guijingUserMapper.batchChangeLock(ids, lockStatus) > 0;
    }

    @Override
    public String registerUser(String loginName, String password) {
        GuijingUser guijingUser = guijingUserMapper.selectByLoginName(loginName);
        if (guijingUser != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        GuijingUser registerUser = new GuijingUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setPasswordMd5(MD5Util.MD5Encode(password, "UTF-8"));
        if (guijingUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public MallUserVO updateUserInfo(GuijingUser guijingUser, HttpSession session) {
        GuijingUser temp = guijingUserMapper.selectByPrimaryKey(guijingUser.getUserId());
        if (temp != null) {
            if(guijingUserMapper.updateSelective(guijingUser) > 0) {
                MallUserVO mallUserVO = new MallUserVO();
                GuijingUser user = guijingUserMapper.selectByPrimaryKey(guijingUser.getUserId());
                BeanUtil.copyProperties(user, mallUserVO);
                session.setAttribute(Constants.MALL_USER_SESSION_KEY, mallUserVO);
                return mallUserVO;
            }
        }
        return null;
    }

    @Override
    public String login(String loginName, String passwordMd5, HttpSession session) {
        GuijingUser user = guijingUserMapper.login(loginName, passwordMd5);
        if (user != null && session != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                user.setNickName(user.getNickName().substring(0, 7) + "..");
            }
            MallUserVO mallUserVO = new MallUserVO();
            BeanUtil.copyProperties(mallUserVO, user);
            session.setAttribute(Constants.MALL_USER_SESSION_KEY, mallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }
}
