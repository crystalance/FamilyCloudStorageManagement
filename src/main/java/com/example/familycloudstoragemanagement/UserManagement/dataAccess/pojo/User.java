package com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    @TableId //指定 userId为主键，参考（https://baomidou.com/pages/223848/#tableid）
    private Long userid;
    private String Usernickname;
    private String Userpassword;
    private String Username;
    private String Userphonenumber;
    private String Useremailaddr;
    private String Aliid;
    private String Giteeid;
    private String union_id;
    private String familysharespaceid;
}
