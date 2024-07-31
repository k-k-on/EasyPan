package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.UserInfoVO;
import com.easypan.service.FileInfoService;
import com.easypan.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求地址：{@code http://localhost:1024/api/admin}
 * <br/>
 * 管理员 Controller
 *
 * @date 2024/7/30 18:48
 * @author LiMengYuan
 */
@RestController("adminController")
@RequestMapping("/admin")
public class AdminController extends CommonFileController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 接口：/getSysSettings POST
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 获取系统设置
     *
     * @date 2024/7/30 18:49
     * @param
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getSysSettings() {
        return getSuccessResponseVO(redisComponent.getSysSettingsDto());
    }

    /**
     * 接口：/saveSysSettings POST
     * <br/>
     * 请求参数：registerEmailTitle registerEmailContent userInitUseSpace nickName
     * <br/>
     * 保存系统设置
     *
     * @date 2024/7/30 19:00
     * @param registerEmailTitle
     * @param registerEmailContent
     * @param userInitUseSpace
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO saveSysSettings(
            @VerifyParam(required = true) String registerEmailTitle,
            @VerifyParam(required = true) String registerEmailContent,
            @VerifyParam(required = true) Integer userInitUseSpace) {
        SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterEmailContent(registerEmailContent);
        sysSettingsDto.setUserInitUseSpace(userInitUseSpace);
        redisComponent.saveSysSettingsDto(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/loadUserList POST
     * <br/>
     * 请求参数：pageNo pageSize
     * <br/>
     * 获取用户列表
     *
     * @date 2024/7/30 19:36
     * @param userInfoQuery
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadUser(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        PaginationResultVO resultVO = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, UserInfoVO.class));
    }

    /**
     * 接口：/updateUserStatus POST
     * <br/>
     * 请求参数：userId status
     * <br/>
     * 修改用户状态
     *
     * @date 2024/7/30 19:39
     * @param userId
     * @param status
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserStatus(@VerifyParam(required = true) String userId, @VerifyParam(required = true) Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/updateUserSpace POST
     * <br/>
     * 请求参数：userId nickName email qqAvatar joinTime lastLoginTime status useSpace totalSpace changeSpace
     * <br/>
     * 修改用户空间
     *
     * @date 2024/7/30 19:43
     * @param userId
     * @param changeSpace
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserSpace(@VerifyParam(required = true) String userId, @VerifyParam(required = true) Integer changeSpace) {
        userInfoService.changeUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/loadFileList POST
     * <br/>
     * 请求参数：pageNo pageSize fileNameFuzzy filePid
     * <br/>
     * 查询所有文件
     *
     * @date 2024/7/30 19:49
     * @param query
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true,checkAdmin = true)
    public ResponseVO loadDataList(FileInfoQuery query) {
        query.setOrderBy("last_update_time desc");
        query.setQueryNickName(true);
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 接口：/getFolderInfo POST
     * <br/>
     * 请求参数：pageNo pageSize fileNameFuzzy filePid
     * <br/>
     * 获取当前目录信息
     *
     * @date 2024/7/30 19:55
     * @param path
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false,checkAdmin = true, checkParams = true)
    public ResponseVO getFolderInfo(@VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, null);
    }

    /**
     * 接口：/getFolderInfo POST
     * <br/>
     * 请求参数：pageNo pageSize fileNameFuzzy filePid
     * <br/>
     * 获取文件信息
     *
     * @date 2024/7/30 19:56
     * @param response
     * @param userId
     * @param fileId
     * @return
     * @throws
     */
    @RequestMapping("/getFile/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getFile(HttpServletResponse response,
                        @PathVariable("userId") @VerifyParam(required = true) String userId,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }


    @RequestMapping("/ts/getVideoInfo/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getVideoInfo(HttpServletResponse response,
                             @PathVariable("userId") @VerifyParam(required = true) String userId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }

    /**
     * 接口：/createDownloadUrl/{userId}/{fileId} POST
     * <br/>
     * 请求参数：pageNo pageSize fileNameFuzzy filePid
     * <br/>
     * 创建下载链接
     *
     * @date 2024/7/30 19:58
     * @param userId
     * @param fileId
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/createDownloadUrl/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO createDownloadUrl(@PathVariable("userId") @VerifyParam(required = true) String userId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        return super.createDownloadUrl(fileId, userId);
    }

    /**
     * 接口：/download GET
     * <br/>
     * 请求参数：code
     * <br/>
     * 下载文件
     *
     * @date 2024/7/30 19:59
     * @param request
     * @param response
     * @param code
     * @return
     * @throws
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * 接口：/delFile POST
     * <br/>
     * 请求参数：fileIdAndUserIds
     * <br/>
     * 删除文件
     *
     * @date 2024/7/31 9:25
     * @param fileIdAndUserIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO delFile(@VerifyParam(required = true) String fileIdAndUserIds) {
        String[] fileIdAndUserIdArray = fileIdAndUserIds.split(",");
        for (String fileIdAndUserId : fileIdAndUserIdArray) {
            String[] itemArray = fileIdAndUserId.split("_");
            fileInfoService.delFileBatch(itemArray[0], itemArray[1], true);
        }
        return getSuccessResponseVO(null);
    }
}
