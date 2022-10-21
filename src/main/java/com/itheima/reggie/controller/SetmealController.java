package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){

        log.info("setmeal:{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐信息分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 执行分页查询
        setmealService.page(pageInfo, queryWrapper);

        // 对象拷贝，思考为何是这两个对象进行拷贝
        // 暂时先忽略到records
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            // 分类id
            Long categoryId = item.getCategoryId();

            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);

    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    // 将一条或多条数据从缓存中删除
    // allEntitries：表示清除setmealCache的所有缓存数据
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R delete(@RequestParam List<Long> ids){
        log.info("删除套餐，id为:{}", ids);

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    // value-缓存的名称
    // 在方法执行前spring会先查看花村中是否有数据，如果有数据，则直接返回缓存数据；
    // 若没有数据，调用方法并将方法返回值存放到缓存中
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+ #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("setmeal:{}", setmeal);

        // 条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());

        // 排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(queryWrapper));

    }

}
