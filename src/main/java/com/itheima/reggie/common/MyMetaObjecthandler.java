package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {

    /**
     * 插入操作：自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 执行insert方法时执行
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        // 在该类中是不能获得HttpSession对象的，因此获取不到当前的用户信息，通过ThreadLocal来解决
        // 通过在LoginCheckFilter的doFilter方法中获取当前登录用户的id
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作：自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 执行update方法时执行
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为：{}", id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
