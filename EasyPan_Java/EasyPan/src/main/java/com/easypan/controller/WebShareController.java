package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.ShareInfoVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.FileInfoService;
import com.easypan.service.FileShareService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 请求地址：{@code http://localhost:1024/api/showShare}
 * <br/>
 * 外部分享 Controller
 *
 * @date 2024/7/31 9:32
 * @author LiMengYuan
 */
@RestController("webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {

    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;


    /**
     * 接口：/getShareLoginInfo POST
     * <br/>
     * 请求参数：shareId
     * <br/>
     * 获取用户登录信息
     *
     * @date 2024/7/31 9:33
     * @param session
     * @param shareId
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareLoginInfo(HttpSession session, @VerifyParam(required = true) String shareId) {
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);

        //判断是否是当前用户分享的文件
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        shareInfoVO.setCurrentUser(userDto != null && userDto.getUserId ().equals (shareSessionDto.getShareUserId ()));
        return getSuccessResponseVO(shareInfoVO);
    }

    /**
     * 接口：/getShareInfo POST
     * <br/>
     * 请求参数：shareId
     * <br/>
     * 获取分享信息
     *
     * @date 2024/7/31 9:44
     * @param shareId
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    /**
     * 获取外部分享的公共信息
     *
     * @date 2024/7/31 9:35
     * @param shareId
     * @return ShareInfoVO
     * @throws BusinessException 分享链接不存在，或者已失效
     */
    private ShareInfoVO getShareInfoCommon(String shareId) {
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {//用户已删除该分享文件
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(share.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    /**
     * 接口：/checkShareCode POST
     * <br/>
     * 请求参数：shareId code
     * <br/>
     * 校验分享码
     *
     * @date 2024/7/31 9:50
     * @param session
     * @param shareId
     * @param code
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        SessionShareDto shareSessionDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, shareSessionDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/loadFileList POST
     * <br/>
     * 请求参数：pageNo pageSize shareId filePid
     * <br/>
     * 获取文件列表
     *
     * @date 2024/7/31 9:57
     * @param session
     * @param shareId
     * @param filePid
     * @return ResponseVO
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId,
                                   String filePid) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        FileInfoQuery query = new FileInfoQuery();
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            fileInfoService.checkRootFilePid(shareSessionDto.getFileId(), shareSessionDto.getShareUserId(), filePid);
            query.setFilePid(filePid);
        } else {
            query.setFileId(shareSessionDto.getFileId());
        }
        query.setUserId(shareSessionDto.getShareUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResultVO<FileInfo> resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
    }


    /**
     * 校验分享是否失效
     *
     * @date 2024/7/31 9:59
     * @param session
     * @param shareId
     * @return SessionShareDto
     * @throws BusinessException 分享验证失效，请重新验证 分享链接不存在，或者已失效
     */
    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        if (shareSessionDto.getExpireTime() != null && new Date().after(shareSessionDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return shareSessionDto;
    }


    /**
     * 接口：/getFolderInfo POST
     * <br/>
     * 请求参数：path shareId
     * <br/>
     * 获取目录信息
     *
     * @date 2024/7/31 10:13
     * @param session
     * @param shareId
     * @param path
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId,
                                    @VerifyParam(required = true) String path) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.getFolderInfo(path, shareSessionDto.getShareUserId());
    }

    /**
     * 接口：/getFile/{shareId}/{fileId} POST
     * <br/>
     * 请求参数：
     * <br/>
     * 获取文件信息
     *
     * @date 2024/7/31 10:15
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     * @return
     * @throws
     */
    @RequestMapping("/getFile/{shareId}/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session,
                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 接口：/ts/getVideoInfo/{shareId}/{fileId} GET
     * <br/>
     * 请求参数：
     * <br/>
     * 预览视频文件
     *
     * @date 2024/7/31 10:57
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     * @return
     * @throws
     */
    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 接口：/createDownloadUrl/{shareId}/{fileId} POST
     * <br/>
     * 请求参数：
     * <br/>
     * 创建下载链接
     *
     * @date 2024/7/31 10:21
     * @param session
     * @param shareId
     * @param fileId
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.createDownloadUrl(fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 接口：/download/{code} GET
     * <br/>
     * 请求参数：
     * <br/>
     * 下载
     *
     * @date 2024/7/31 10:23
     * @param request
     * @param response
     * @param code
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * 接口：/saveShare POST
     * <br/>
     * 请求参数：shareId shareFileIds myFolderId
     * <br/>
     * 保存分享
     *
     * @date 2024/7/31 10:28
     * @param session
     * @param shareId
     * @param shareFileIds
     * @param myFolderId
     * @return ResponseVO
     * @throws BusinessException 自己分享的文件无法保存到自己的网盘
     */
    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveShare(HttpSession session,
                                @VerifyParam(required = true) String shareId,
                                @VerifyParam(required = true) String shareFileIds,
                                @VerifyParam(required = true) String myFolderId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }
        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId, shareSessionDto.getShareUserId(), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
