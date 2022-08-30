package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppongCart:{}", shoppingCart);

        // 得到当前用户的id,指定当前是哪个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 构造条件构造器
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        // 查询当前菜品或者套餐是否在购物车中
        if(shoppingCart.getDishId() != null){
            // 添加到购物车中的是菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else{
            // 添加到购物车中的是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 查询当前菜品或者套餐是否在购物车
        ShoppingCart shoppingCartDb = shoppingCartService.getOne(wrapper);
        // 如果不存在，则添加到购物车，数量默认就是1
        if(shoppingCartDb == null){
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartDb = shoppingCart;
        }else{
            // 如果存在，就在原来数量基础上加1
            shoppingCartDb.setNumber(shoppingCartDb.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartDb);
        }
        return R.success(shoppingCartDb);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
       log.info("查看购物车...");

       LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
       queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

       List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

       return R.success(list);

    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R clean(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清理购物车成功");
    }
}
