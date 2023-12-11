package com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class userrole {

    @TableId(type = IdType.AUTO)
    private Long ruid;
    private Long userid;
    private String roleid;

}