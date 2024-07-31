package com.easypan.utils;


import com.easypan.entity.constants.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class StringTools {

    /**
     * MD5加密密码
     *
     * @param originString 原始未加密密码
     * @return String 加密后密码
     */
    public static String encodeByMD5(String originString) {
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    public static boolean isEmpty(String str) {

        if (null == str || str.isEmpty () || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else return str.trim ().isEmpty ();
    }

    public static String getFileSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index);
    }


    public static String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    /**
     * 重命名
     *
     * @date 2024/7/18 20:25
     * @param fileName
     * @return String
     */
    public static String rename(String fileName) {
        String fileNameReal = getFileNameNoSuffix(fileName);
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Constants.LENGTH_5) + suffix;
    }

    /**
     * 生成随机字符串
     *
     * @date 2024/7/19 9:50
     * @param count
     * @return String
     * @throws
     */
    public static String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    /**
     * 生成随机数
     *
     * @date 2024/7/17 16:28
     * @author LiMengYuan
     * @param count 随机数的长度
     * @return String
     **/
    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }


    public static String escapeTitle(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;");
        return content;
    }


    public static String escapeHtml(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;");
        content = content.replace(" ", "&nbsp;");
        content = content.replace("\n", "<br>");
        return content;
    }

    /**
     * 确认文件路径是否正确，不能包含"../"、"..\\"
     *
     * @param path
     * @return boolean {@code true 正确} {@code false 存在非法字符}
     */
    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        return !path.contains ("../") && !path.contains ("..\\");
    }
}
