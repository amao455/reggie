package com.itheima.reggie.common;


/**
* 全局异常处理
* */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

// 拦截类上加了@RestController的异常，指定拦截哪些类型的控制器
@ControllerAdvice(annotations = {RestController.class, Controller.class})
// 将方法的返回值R对象转换为json格式的数据，响应给页面
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /*
    异常处理方法，捕获指定异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        // 获取具体异常信息
        if(ex.getMessage().contains("Duplicate entry")){
            // TODO：可以查看一下ex的详细信息
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    // 处理哪种异常
    // 处理自定义的异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
