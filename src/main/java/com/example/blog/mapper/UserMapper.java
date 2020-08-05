package com.example.blog.mapper;

import com.example.blog.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from USER where id = #{id}")
    User getUserById(@Param("id") Integer id);

    @Select("select * from USER where username = #{username}")
    User getUserByUsername(@Param("username") String username);

    @Insert("insert into USER (username, password, avatar, created_at, updated_at) values" +
            " (#{username}, #{password}, #{avatar}, now(), now())")
    void saveUser(@Param("username") String username,
                  @Param("password") String password,
                  @Param("avatar") String avatar);
}
