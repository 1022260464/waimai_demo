package com.sky.controller.admin;

import com.sky.config.RedisConfiguration;
import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/shop")
@Slf4j
@RestController("adminShopController")
// 商户管理
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String key="SHOP_STATUS";
//    @Autowired
//    private ShopService shopService;



    @PutMapping("/{status}")
    public Result SetStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set(key,status);
        return Result.success();

    }

    @GetMapping("status")
    public Result<Integer> GetStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("获取商户状态:{}",status ==1?"开启":"关闭");
        return Result.success(status);
    }
}
