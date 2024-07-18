package com.easypan.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装copy方法，将一个对象copy为另外一个对象
 *
 * @date 2024/7/18 18:16
 * @author LiMengYuan
 */
public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        List<T> list = new ArrayList<T>();
        for (S s : sList) {
            T t = null;
            try {
                t = classz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s, t);
            list.add(t);
        }
        return list;
    }

    public static <T, S> T copy(S s, Class<T> classz) {
        T t = null;
        try {
            t = classz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;
    }
}
