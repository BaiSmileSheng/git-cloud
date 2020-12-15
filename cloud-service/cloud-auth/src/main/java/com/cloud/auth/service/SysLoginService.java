package com.cloud.auth.service;

import cn.hutool.core.util.StrUtil;
import com.cloud.auth.form.HucProperties;
import com.cloud.auth.form.LoginProperties;
import com.cloud.auth.form.UucProperties;
import com.cloud.common.constant.Constants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.enums.UserStatus;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.exception.user.*;
import com.cloud.common.log.publish.PublishFactory;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.IpUtils;
import com.cloud.common.utils.MessageUtils;
import com.cloud.common.utils.ServletUtils;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteSupplierInfoService;
import com.cloud.system.feign.RemoteUserService;
import com.cloud.system.util.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SysLoginService {

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private RemoteSupplierInfoService supplierInfoService;

    @Autowired
    private UucLoginCheckService uucLoginCheckService;

    @Autowired
    private HucLoginCheckService hucLoginCheckService;

    @Autowired
    private UucProperties uucProperties;

    @Autowired
    private HucProperties hucProperties;

    @Autowired
    private LoginProperties loginProperties;

    /**
     * 登录
     */
    public SysUser login(String username, String password) throws Exception {
        // 验证码校验
        // if
        // (!StringUtils.isEmpty(ServletUtils.getRequest().getAttribute(ShiroConstants.CURRENT_CAPTCHA)))
        // {
        // AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,
        // Constants.LOGIN_FAIL,
        // MessageUtils.message("user.jcaptcha.error")));
        // throw new CaptchaException();
        // }
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null"));
            throw new UserNotExistsException();
        }


        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.not.match"));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.not.match"));
            throw new UserPasswordNotMatchException();
        }


        // 查询用户信息
        SysUser user = userService.selectSysUserByUsername(username);


        // if (user == null && maybeMobilePhoneNumber(username))
        // {
        // user = userService.selectUserByPhoneNumber(username);
        // }
        // if (user == null && maybeEmail(username))
        // {
        // user = userService.selectUserByEmail(username);
        // }
        if (user == null) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists"));
            throw new UserNotExistsException();
        }
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.delete"));
            throw new UserDeleteException();
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            PublishFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.blocked", user.getRemark()));
            throw new UserBlockedException();
        }
        if (user.getUserId() == 1L) {
            if (!PasswordUtil.matches(user, password)) {
                throw new UserPasswordNotMatchException();
            }
            return user;
        }
        if (!loginProperties.getIsLogin()) {
            throw new BusinessException(loginProperties.getErrMsg());
        }
        //如果是海尔用户  UUC校验
        if(UserConstants.USER_TYPE_HR.equals(user.getUserType())){
            if(uucProperties.getIsCheck()){
                //先从redis中获取accessToken，如果没有，重新获取token。从uuc校验用户名密码
                String accessToken = uucLoginCheckService.getAccessToken();
                if (StrUtil.isBlank(accessToken)) {
                    throw new BusinessException("获取UUC token失败！");
                }
                R r = uucLoginCheckService.checkUucUser(username, password, accessToken);
                if (!r.isSuccess()) {
                    throw new UserPasswordNotMatchException();
                }
            }else{
                //如果不检验UUC则校验本系统数据库用户名，密码
                if (!PasswordUtil.matches(user, password)) {
                    throw new UserPasswordNotMatchException();
                }
            }
        } else if (UserConstants.USER_TYPE_WB.equals(user.getUserType())) {
            if(hucProperties.getIsCheck()){
                //HUC校验外部用户
                //先从redis中获取accessToken，如果没有，重新获取token。从uuc校验用户名密码
                String accessToken = hucLoginCheckService.getAccessToken();
                if (StrUtil.isBlank(accessToken)) {
                    throw new BusinessException("获取HUC token失败！");
                }
                R r = hucLoginCheckService.checkHucUser(username, password, accessToken);
                if (!r.isSuccess()) {
                    throw new UserPasswordNotMatchException();
                }
            }else{
                //如果不检验HUC则校验本系统数据库用户名，密码
                if (!PasswordUtil.matches(user, password)) {
                    throw new UserPasswordNotMatchException();
                }
            }
            //外部用户根据登录名查询供应商V码
            CdSupplierInfo cdSupplierInfo = supplierInfoService.getByNick(user.getLoginName());
            if (cdSupplierInfo != null) {
                user.setSupplierCode(cdSupplierInfo.getSupplierCode());
                user.setSupplierName(cdSupplierInfo.getCorporation());
            }
        }else{
            throw new UserException("user.type.null",null);
        }
        PublishFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user);
        return user;
    }

    // private boolean maybeEmail(String username)
    // {
    // if (!username.matches(UserConstants.EMAIL_PATTERN))
    // {
    // return false;
    // }
    // return true;
    // }
    //
    // private boolean maybeMobilePhoneNumber(String username)
    // {
    // if (!username.matches(UserConstants.MOBILE_PHONE_NUMBER_PATTERN))
    // {
    // return false;
    // }
    // return true;
    // }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(SysUser user) {
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUserLoginRecord(user);
    }

    public void logout(String loginName) {
        PublishFactory.recordLogininfor(loginName, Constants.LOGOUT, MessageUtils.message("user.logout.success"));
    }
}
