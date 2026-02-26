package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DishFlavorMapper {




     void insertBatch(List<DishFlavor> flavors);



/**
 * 根据菜品ID列表删除菜品,批量删除
 *
 * @param dishId 菜品ID列表，需要删除的菜品ID集合
 */
     void deleteById(List<Long> dishId);

     List<DishFlavor> selectByDishId(Long id);


     void deleteByDishId(Long id);
}
