package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;

import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 请求地址：{@code http://localhost:7090/api/recycle}
 * <br/>
 * 回收站相关控制
 *
 * @date 2024/7/30 17:13
 * @author LiMengYuan
 */
@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 接口：/loadRecycleList
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 获取回收站文件列表 根据条件分页查询
     *
     * @date 2024/7/30 17:15
     * @param session
     * @param pageNo
     * @param pageSize
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        query.setPageSize(pageSize);
        query.setPageNo(pageNo);
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("recovery_time desc");
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        PaginationResultVO result = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * 接口：/recoverFile
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 恢复文件
     *
     * @date 2024/7/30 17:17
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.recoverFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/delFile
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 彻底删除文件
     *
     * @date 2024/7/30 17:39
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds,false);
        return getSuccessResponseVO(null);
    }
}
