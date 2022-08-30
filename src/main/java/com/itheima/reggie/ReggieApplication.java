package com.itheima.reggie;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.LinkedList;
import java.util.Stack;


@Slf4j
@SpringBootApplication
@ServletComponentScan  // 这样才会扫描过滤器
@EnableTransactionManagement // 开启事务管理的支持
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功");

    }
}
