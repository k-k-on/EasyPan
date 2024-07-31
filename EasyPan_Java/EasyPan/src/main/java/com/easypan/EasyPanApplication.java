package com.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author LiMengYuan
 * @version 1.0
 * @date 2024/7/16 15:25
 */
@SpringBootApplication(scanBasePackages = {"com.easypan"})
@MapperScan(basePackages = {"com.easypan.mappers"})
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class EasyPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyPanApplication.class, args);
    }
}
