package com.easypan.entity.dto;

import java.io.Serializable;

/**
 * 用户空间信息
 *
 * @date 2024/7/17 20:50
 * @author LiMengYuan
 */
public class UserSpaceDto implements Serializable {
    private Long useSpace;
    private Long totalSpace;

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }
}
