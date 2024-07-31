package com.easypan.entity.dto;

import java.util.Date;

/**
 * 外部分享的session类
 *
 * @date 2024/7/31 9:47
 * @author LiMengYuan
 */
public class SessionShareDto {
    private String shareId;
    private String shareUserId;
    private Date expireTime;
    private String fileId;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(String shareUserId) {
        this.shareUserId = shareUserId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
