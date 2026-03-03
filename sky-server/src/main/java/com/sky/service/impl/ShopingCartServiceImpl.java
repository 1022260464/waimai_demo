package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShopingCartService;

import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopingCartServiceImpl implements ShopingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shopingcart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shopingcart);
        shopingcart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shopingcart);

        // ✅ 修正 1：先判断 != null，再判断 size，防止 NullPointerException
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateById(cart);
        } else {
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                // 如果是菜品
                Dish dish = dishMapper.selectById(dishId);

                if (dish == null) {
                    throw new ShoppingCartBusinessException("菜品不存在");
                }
                shopingcart.setName(dish.getName());
                shopingcart.setAmount(dish.getPrice());
                shopingcart.setImage(dish.getImage());

            } else {
                // 添加的是套餐，这里没问题
                Long setmealID = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealID);
                if (setmeal == null) {
                    throw new ShoppingCartBusinessException(MessageConstant.SETMEAL_NOT_FOUND);
                }
                shopingcart.setName(setmeal.getName());
                shopingcart.setAmount(setmeal.getPrice());
                shopingcart.setImage(setmeal.getImage());
            }

            shopingcart.setNumber(1);
            shopingcart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shopingcart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.list(ShoppingCart.
                builder().
                userId(BaseContext.getCurrentId()).
                build());
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if (number == 1) {
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateById(shoppingCart);
            }
        }

    }
}
