package com.itheima.reggie;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@SpringBootApplication
//TODO 在springboot项目中，在引导类/配置类加上该注解之后，会自动扫描（当前包及其子包下）的@WebServlet,@WebFilter,@WebListener注解，自动注册Servlet相关组件
@ServletComponentScan  // 这样才会扫描过滤器
@EnableTransactionManagement // 开启事务管理的支持
@EnableCaching // 开启 Spring Cache注解方式的缓存功能
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功");

    }
}
