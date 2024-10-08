package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;

import com.easypan.entity.dto.CreateImageCode;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.VerifyRegexEnum;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;

import com.easypan.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求地址：{@code http://easypan.lmycoding.com}
 * <br/>
 * 账号 Controller
 *
 * @date 2024/7/18 18:01
 * @author LiMengYuan
 */
@RestController("accountController")
//@RequestMapping("userInfo")
public class AccountController extends ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;


    /**
     * 接口：/checkCode?type=0&time=1721453022292 GET
     * <br/>
     * 请求参数：type
     * <br/>
     * 获取验证码
     *
     * @param response
     * @param session
     * @param type 表示验证码的作用，0:登录注册 1:邮箱验证码发送 默认0
     * @return
     * @throws
     */
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
     * 接口：/sendEmailCode POST
     * <br/>
     * 请求参数：<li/>email<li/>checkCode<li/>type
     * <br/>
     * 发送邮箱验证码
     *
     * @param session
     * @param email 邮箱
     * @param checkCode 图片验证码
     * @param type 0:注册 1:找回密码
     * @return ResponseVO
     * @throws
     */
    @RequestMapping ("/sendEmailCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)//未登录，有传递参数
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
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

    /**
     * 接口：/register POST
     * <br/>
     * 请求参数：email checkCode type
     * <br/>
     * 注册
     *
     * @param session
     * @param email 邮箱，校验邮箱正则规则和最大值
     * @param nickName 昵称，校验最大值
     * @param password 密码，校验密码正则规则和最大值、最小值 只能是数字，字母，特殊字符 8-18位
     * @param checkCode 图片验证码
     * @param emailCode 邮箱验证码
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/register")
    @GlobalInterceptor(checkLogin = false, checkParams = true)//未登录，有传递参数
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, max = 20) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 接口：/login
     * <br/>
     * 请求参数：email password checkCode
     * <br/>
     * 登录
     *
     * @param session
     * @param request
     * @param email 邮箱
     * @param password md5加密传输
     * @param checkCode 图片验证码
     * @return ResponseVO
     * @throws BusinessException 图片验证码不正确
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkLogin = false, checkParams = true)//未登录，有传递参数
    public ResponseVO<SessionWebUserDto> login(HttpSession session, HttpServletRequest request,
                            @VerifyParam(required = true) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return getSuccessResponseVO(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 接口：/resetPwd
     * <br/>
     * 请求参数：email password checkCode
     * <br/>
     * 重置密码
     *
     * @param session
     * @param email 邮箱
     * @param password md5加密传输
     * @param checkCode 图片验证码
     * @param emailCode 邮箱验证码
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkLogin = false, checkParams = true)//未登录，有传递参数
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * 接口：/getAvatar/{userId}  GET
     * <br/>
     * 请求参数：email password checkCode
     * <br/>
     * 获取用户头像
     *
     * @param response
     * @param userId
     * @return
     * @throws
     */
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)//未登录，有传递参数
    public void getAvatar(HttpServletResponse response,
                          @VerifyParam(required = true) @PathVariable("userId") String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;

        //判断头像目录是否存在
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //获取头像存储的完整路径
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
                printNoDefaultImage(response);
                return;
            }
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
        }
        response.setContentType("image/jpg");
        readFile(response, avatarPath);
    }

    /**
     * 提示放置默认头像
     *
     * @date 2024/7/18 10:18
     * @param response
     * @return
     * @throws
     */
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        try (PrintWriter writer = response.getWriter ()) {
            writer.print ("请在头像目录下放置默认头像default_avatar.jpg");
        } catch (Exception e) {
            logger.error ("输出无默认图失败", e);
        }
    }


    /**
     * 接口：/getUseSpace POST
     * <br/>
     * 请求参数：
     * <br/>
     * 获取用户空间
     *
     * @date 2024/7/18 11:17
     * @param session
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVO(redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId()));
    }

    /**
     * 接口：/logout
     * <br/>
     * 请求参数：
     * <br/>
     * 退出登录
     *
     * @date 2024/7/18 11:27
     * @param session
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/updateUserAvatar
     * <br/>
     * 请求参数：avatar
     * <br/>
     * 更新用户头像
     *
     * @date 2024/7/18 11:29
     * @param session
     * @param avatar 头像文件路径
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/updateUserAvatar")
    @GlobalInterceptor
    public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);

        //判断头像目录是否存在
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }

        //获取用户id对应的头像存储完整路径
        File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
        try {
            avatar.transferTo(targetFile);//上传头像到指定路径
        } catch (Exception e) {
            logger.error("上传头像失败", e);
        }

        //更新用户id对应的头像，更新mysql数据库。如果本地存在用户头像，则显示本地头像，否则显示qq头像，都不存在则显示默认头像
        UserInfo userInfo = new UserInfo();
        userInfo.setQqAvatar("");
        userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
        webUserDto.setAvatar(null);
        session.setAttribute(Constants.SESSION_KEY, webUserDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/updatePassword
     * <br/>
     * 请求参数：password
     * <br/>
     * 修改密码
     *
     * @date 2024/7/18 19:41
     * @param session
     * @param password 密码 只能是数字，字母，特殊字符 8-18位
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                     @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        //TODO 修改 修改密码的逻辑，需要输入当前密码进行验证（前后端都需要更改）
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/qqlogin
     * <br/>
     * 请求参数：callbackUrl
     * <br/>
     * QQ登录
     *
     * @date 2024/7/18 16:18
     * @param session
     * @param callbackUrl 登录成功后回调地址
     * @return ResponseVO
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/qqlogin")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO qqlogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
        String state = StringTools.getRandomString(Constants.LENGTH_30);
        if (!StringTools.isEmpty(callbackUrl)) {
            session.setAttribute(state, callbackUrl);
        }
        String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(), URLEncoder.encode(appConfig.getQqUrlRedirect(), "utf-8"), state);
        return getSuccessResponseVO(url);
    }

    /**
     * 接口：/qqlogin/callback
     * <br/>
     * 请求参数：code state
     * <br/>
     * QQ登录回调
     *
     * @date 2024/7/18 16:28
     * @param session
     * @param code qq官方回调的code
     * @param state 获取登录信息后端传入的状态码
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/qqlogin/callback")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO qqLoginCallback(HttpSession session,
                                      @VerifyParam(required = true) String code,
                                      @VerifyParam(required = true) String state) {
        SessionWebUserDto sessionWebUserDto = userInfoService.qqLogin(code);
        session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
        Map<String, Object> result = new HashMap<> ();
        result.put("callbackUrl", session.getAttribute(state));
        result.put("userInfo", sessionWebUserDto);
        return getSuccessResponseVO(result);
    }

}
