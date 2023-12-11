package com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("roleauth")
public class roleauth {

    @TableId(type = IdType.AUTO)
    private Long raid;
    private String roleid;
    private String authid;

}