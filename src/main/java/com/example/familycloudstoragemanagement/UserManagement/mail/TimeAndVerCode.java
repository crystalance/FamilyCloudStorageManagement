package com.example.familycloudstoragemanagement.UserManagement.mail;

import java.util.Date;
import java.util.HashMap;

public class TimeAndVerCode {

    public static HashMap<String, Date> currentTimeMap = new HashMap<>();
    //通过邮箱key对应一个验证码valus，在验证时从静态nap获取进行验证
    public static HashMap <String,String> verCodeMap = new HashMap<>();
}
