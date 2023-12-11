package com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
