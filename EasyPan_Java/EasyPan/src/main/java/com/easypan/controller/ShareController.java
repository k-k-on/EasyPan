package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileShareService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 请求地址：{@code http://localhost:1024/api/share}
 * <br/>
 * 文件分享 Controller
 *
 * @date 2024/7/30 18:19
 * @author LiMengYuan
 */
@RestController("shareController")
@RequestMapping("/share")
public class ShareController extends ABaseController {
    @Resource
    private FileShareService fileShareService;

    /**
     * 接口：/loadShareList POST
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 获取分享文件列表
     *
     * @date 2024/7/30 18:21
     * @param session
     * @param query
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/loadShareList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadShareList(HttpSession session, FileShareQuery query) {
        query.setOrderBy("share_time desc");
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        query.setUserId(userDto.getUserId());
        query.setQueryFileName(true);
        PaginationResultVO<FileShare> resultVO = this.fileShareService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 接口：/shareFile POST
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 分享文件
     *
     * @date 2024/7/30 18:25
     * @param session
     * @param fileId
     * @param validType
     * @param code
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO shareFile(HttpSession session,
                                @VerifyParam(required = true) String fileId,
                                @VerifyParam(required = true) Integer validType,
                                String code) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        FileShare share = new FileShare();
        share.setFileId(fileId);
        share.setValidType(validType);
        share.setCode(code);
        share.setUserId(userDto.getUserId());
        fileShareService.saveShare(share);
        return getSuccessResponseVO(share);
    }

    /**
     * 接口：/cancelShare POST
     * <br/>
     * 请求参数：shareIds
     * <br/>
     * 取消分享
     *
     * @date 2024/7/30 18:31
     * @param session
     * @param shareIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO cancelShare(HttpSession session, @VerifyParam(required = true) String shareIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        fileShareService.deleteFileShareBatch(shareIds.split(","), userDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
