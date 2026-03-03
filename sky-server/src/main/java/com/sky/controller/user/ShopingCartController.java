package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopingCartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShopingCartController {

    @Autowired
    private ShopingCartService shopingCartService;

    @PostMapping("/add")
    public Result add(@RequestBody  ShoppingCartDTO shoppingCartDTO)  {
        log.info("添加购物车 DTO：{}", shoppingCartDTO);
        shopingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shopingCartService.showShoppingCart());
    }
    /**
     * 清空购物车商品
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车商品")
    public Result<String> clean(){
        shopingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    public Result number(@RequestBody ShoppingCartDTO shoppingCartDTO){

        shopingCartService.sub(shoppingCartDTO);
        return Result.success();

    }
}
