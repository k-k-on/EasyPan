package com.easypan.entity.enums;

/**
 * 文件上传状态枚举类
 *
 * @date 2024/7/18 20:02
 * @author LiMengYuan
 */
public enum FileStatusEnums {
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");

    private final Integer status;
    private final String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
