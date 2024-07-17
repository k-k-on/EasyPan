package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;

import com.easypan.entity.dto.CreateImageCode;
import com.easypan.entity.enums.VerifyRegexEnum;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController("accountController")
//@RequestMapping("userInfo")
public class AccountController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private EmailCodeService emailCodeService;
    /**
     * 接口：/checkCode?type=0
     * <p/>
     * 请求参数：type
     * <p/>
     *
     *
     * @date 2024/7/17 16:17
     * @author LiMengYuan
     * @param response
     * @param session
     * @param type 表示验证码的作用，0:登录注册 1:邮箱验证码发送 默认0
     * @return
     **/
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws
            IOException {
        CreateImageCode vCode = new CreateImageCode (130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();//获取验证码

        if (type == null || type == 0) {//登录注册的验证码
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {//邮箱的验证码
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }


    /**
     * 接口：/sendEmailCode
     * <p/>
     * 请求参数：<li/>email<li/>checkCode<li/>type
     * <p/>
     * 发送邮箱验证码
     *
     * @date 2024/7/17 16:22
     * @author LiMengYuan
     * @param session
     * @param email 邮箱
     * @param checkCode 图片验证码
     * @param type 0:注册 1:找回密码
     * @return ResponseVO
     **/
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO sendEmailCode(HttpSession session, String email, String checkCode, Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {//校验验证码是否正确
                throw new BusinessException ("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

}
