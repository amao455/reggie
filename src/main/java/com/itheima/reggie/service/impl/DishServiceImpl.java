package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        // 保存菜品口味数据到口味表dish_flavor
        List<DishFlavor> flavors =  dishDto.getFlavors();


        flavors = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 批量保存菜品口味数据，向dish_flavor表插入数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 根据id查询菜品
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 条件构造器，对dish_flavor这个数据库进行操作
        LambdaQueryWrapper<DishFlavor> queryWrapper =  new LambdaQueryWrapper<>();
        // 添加查询条件，根据菜品id查询口味
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        // 设置菜品对应的口味信息
        dishDto.setFlavors(list);

        return dishDto;

    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param
     */
    @Override
    @Transactional // 加事务注解
    public void updateWithFlavor(DishDto dishDto) {
        // 修改菜品信息，因为DishDto是Dish的子类，所以这样写没问题
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> removeWrapper = new LambdaQueryWrapper<>();
        removeWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        // 先删除当前菜品关联的口味信息---dish_flavor表的delete操作
        dishFlavorService.remove(removeWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 重新保存当前菜品关联的口味信息
        dishFlavorService.saveBatch(flavors);
    }
}
