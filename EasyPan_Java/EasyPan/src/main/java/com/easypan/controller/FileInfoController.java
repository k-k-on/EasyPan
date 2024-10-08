package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadResultDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.enums.FileFolderTypeEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 请求地址：{@code http://easypan.lmycoding.com/file}
 * <br/>
 * 文件信息 Controller
 *
 * @date 2024/7/18 18:01
 * @author LiMengYuan
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    /**
     * 接口：/loadDataList POST
     * <br/>
     * 请求参数：category filePid fileNameFuzzy pageNo pageSize
     * <br/>
     * 根据条件分页查询文件列表
     *
     * @date 2024/7/18 18:09
     * @param session
     * @param query 文件信息
     * @param category 分类
     * @return ResponseVO
     * @throws
     */
    @RequestMapping ("/loadDataList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
        FileCategoryEnums fileCategory = FileCategoryEnums.getByCode(category);
        if (null != fileCategory) {
            query.setFileCategory(fileCategory.getCategory());
        }
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResultVO<FileInfo> result = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * 接口：/uploadFile
     * <br/>
     * 请求参数：fileId file fileName filePid fileMd5 chunkIndex chunks
     * <br/>
     * 文件分片上传
     *
     * @date 2024/7/18 19:47
     * @param session
     * @param fileId        文件ID
     * @param file          文件流
     * @param fileName      文件名
     * @param filePid       文件父id
     * @param fileMd5       文件MD5值
     * @param chunkIndex    当前分片索引
     * @param chunks        总分片数
     * @param fileSize      文件大小
     * @return ResponseVO
     */
    @RequestMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO uploadFile(HttpSession session, String fileId, MultipartFile file,
                                 @VerifyParam(required = true) String fileName,
                                 @VerifyParam(required = true) String filePid,
                                 @VerifyParam(required = true) String fileMd5,
                                 @VerifyParam(required = true) Integer chunkIndex,
                                 @VerifyParam(required = true) Integer chunks,
                                 @VerifyParam(required = true) Integer fileSize) {

        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks, fileSize);
        return getSuccessResponseVO(resultDto);
    }


    /**
     * 接口：/getImage/{imageFolder}/{imageName} GET
     * <br/>
     * 请求参数：
     * <br/>
     * 显示封面
     *
     * @date 2024/7/19 11:42
     * @param response
     * @param imageFolder
     * @param imageName
     * @return
     * @throws
     */
    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageFolder") String imageFolder,
                         @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    /**
     * 接口：/ts/getVideoInfo/{fileId}
     * <br/>
     * 请求参数：
     * <br/>
     * 获取视频文件信息
     *
     * @date 2024/7/19 11:50
     * @param response
     * @param session
     * @param fileId
     * @return
     * @throws
     */
    @RequestMapping("/ts/getVideoInfo/{fileId}")
    public void getVideoInfo(HttpServletResponse response, HttpSession session,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    /**
     * 接口：/getFile/{fileId}
     * <br/>
     * 请求参数：
     * <br/>
     * 获取文件信息
     *
     * @date 2024/7/19 15:50
     * @param response
     * @param session
     * @param fileId
     * @return
     * @throws
     */
    @RequestMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    /**
     * 接口：/newFolder POST
     * <br/>
     * 请求参数：filePid fileName
     * <br/>
     * 新建目录
     *
     * @date 2024/7/19 16:44
     * @param session
     * @param filePid 文件父id
     * @param fileName 目录名
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/newFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO newFolder(HttpSession session,
                                @VerifyParam(required = true) String filePid,
                                @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(fileInfo);
    }

    /**
     * 接口：/getFolderInfo
     * <br/>
     * 请求参数：filePid fileName
     * <br/>
     * 获取当前目录
     *
     * @date 2024/7/19 17:23
     * @param session
     * @param path
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, getUserInfoFromSession(session).getUserId());
    }

    /**
     * 接口：/rename
     * <br/>
     * 请求参数：filePid fileName
     * <br/>
     * 文件重命名
     *
     * @date 2024/7/30 16:07
     * @param session
     * @param fileId
     * @param fileName
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/rename")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO rename(HttpSession session,
                             @VerifyParam(required = true) String fileId,
                             @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
    }

    /**
     * 接口：/loadAllFolder
     * <br/>
     * 请求参数：filePid currentFileIds
     * <br/>
     * 获取所有目录
     *
     * @date 2024/7/30 16:20
     * @param session
     * @param filePid
     * @param currentFileIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/loadAllFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadAllFolder(HttpSession session,
                                    @VerifyParam(required = true) String filePid,
                                    String currentFileIds) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setFilePid(filePid);
        query.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        if (!StringTools.isEmpty(currentFileIds)) {
            query.setExcludeFileIdArray(currentFileIds.split(","));
        }
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        query.setOrderBy("create_time desc");
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(query);
        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVO.class));
    }

    /**
     * 接口：/loadAllFolder
     * <br/>
     * 请求参数：fileIds filePid
     * <br/>
     * 修改文件目录、移动文件
     *
     * @date 2024/7/30 16:27
     * @param session
     * @param fileIds
     * @param filePid
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/changeFileFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO changeFileFolder(HttpSession session,
                                       @VerifyParam(required = true) String fileIds,
                                       @VerifyParam(required = true) String filePid) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.changeFileFolder(fileIds, filePid, webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 接口：/createDownloadUrl/{fileId}
     * <br/>
     * 请求参数：fileId
     * <br/>
     * 创建下载链接
     *
     * @date 2024/7/30 16:38
     * @param session
     * @param fileId
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/createDownloadUrl/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        return super.createDownloadUrl(fileId, getUserInfoFromSession(session).getUserId());
    }

    /**
     * 接口：/download/{code}
     * <br/>
     * 请求参数：code
     * <br/>
     * 下载文件
     *
     * @date 2024/7/30 16:49
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
     * 接口：/delFile
     * <br/>
     * 请求参数：code
     * <br/>
     * 删除文件
     *
     * @date 2024/7/30 16:53
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @throws
     */
    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.removeFile2RecycleBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }
}
