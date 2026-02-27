package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

/**
 * 根据openId查询用户信息
 * @param openId 用户的唯一标识openId
 * @return 返回对应用户信息的User对象
 */
    @Select("select * from user where openid = #{openid}")
    User getByopenId(String openId);


    void insert(User newuser);
}
