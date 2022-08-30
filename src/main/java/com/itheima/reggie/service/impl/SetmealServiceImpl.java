package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要关联菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息，操作setmeal表
        this.save(setmealDto);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //  保存套餐和菜品关联信息，操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐，同时需要删除套餐和菜品的关系
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(wrapper);

        // 抛出一个业务异常
        if(count > 0){
            throw new CustomException("套餐正在售卖中，无法删除");
        }

        // 删除套餐数据，操作setmeal表，执行delete操作
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> removeWrapper = new LambdaQueryWrapper<>();
        removeWrapper.in(SetmealDish::getSetmealId, ids);

        // 删除套餐和菜品关联数据，操作setmeal_dish表，执行delete操作
        setmealDishService.remove(removeWrapper);
    }
}
