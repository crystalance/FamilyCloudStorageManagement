package com.example.familycloudstoragemanagement.UserManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.UserMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.User;
import com.example.familycloudstoragemanagement.UserManagement.mail.SendEmail;
import com.example.familycloudstoragemanagement.UserManagement.mysecurity.IdGenerator;
import com.example.familycloudstoragemanagement.UserManagement.utility.thirdLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密

    @Autowired
    private SendEmail emailsender;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    private userdeviceMapper udMapper;


    //-------------------------------用户注册部分----------------------------------------------//

    /**
     * 向指定邮箱发送验证码，前提条件是要连接上redis
     *
     * @param email 需要发送的邮箱
     * @return 返回给前端的信息
     */
    //http://localhost:8081/user/SendCode?email=lancewaker@gmail.com
    @RequestMapping("SendCode")
    @ResponseBody
    public SaResult sendCode(String email) {
        String message;
//        try {
        message = "";
        //判断邮箱格式是否正确
        if (emailsender.isEmail(email)) {
            emailsender.sendEmail(email);
            message = "邮箱验证码已发送，有效时间为5分钟";
            return SaResult.ok(message);
        } else {
            message = "邮箱格式不正确";
            return SaResult.error(message);
        }
//        } catch (Exception e) {
//            //log.info(String.valueOf(e));
//            message = "出现未知错误";
//        }

    }


    //http://localhost:8081/user/doRegister?username=testname1&password=123456
    @RequestMapping("doRegister")
    public String doRegister(String username, String password, String usernickname, String userphonenumber, String useremailaddr,
                             String aliid, String giteeid, String unionid) {

        //查取用户名
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.select().eq("username", username);
        User usr = userMapper.selectOne(userQueryWrapper);

        if (usr == null) {
            //用户名不存在，注册用户
            //使用雪花算法自动生成一个id
            Long userID;
            IdGenerator idGenerator = new IdGenerator();
            userID = idGenerator.getId();


            User newUser = new User();
            newUser.setUsername(username);
            newUser.setUserpassword(bCryptPasswordEncoder.encode(password)); //加密密码
            newUser.setUserid(userID);
            newUser.setUsernickname(usernickname);
            newUser.setUserphonenumber(userphonenumber);
            newUser.setUseremailaddr(useremailaddr);
            newUser.setAliid(aliid);
            newUser.setGiteeid(giteeid);
            newUser.setUnion_id(unionid);

            userMapper.insert(newUser);
            return "用户注册成功，用户名：" + username;

        } else { //用户名已经存在

            return "用户名已经存在";
        }

    }

    //http://localhost:8081/user/emailRegister?username=emtest1&password=emtest1&usernickname=lala&userphonenumber=123456789&email=lancewaker@gmail.com&verifyCode=
    @RequestMapping("emailRegister")
    public SaResult emailRegister(String username, String password, String usernickname, String userphonenumber, String email, String verifyCode) {

        //先查询email是否存在
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(email))) {
            return SaResult.error("邮箱号不存在或者验证码已过期！");
        }

        //查询value,比较
        String TrueCode;
        TrueCode = stringRedisTemplate.opsForValue().get(email);

        if (Objects.equals(TrueCode, verifyCode)) {

            return SaResult.ok(doRegister(username, password, usernickname, userphonenumber, email, null, null, null) + ", 邮箱号：" + email);

        } else {
            return SaResult.error("验证码错误");
        }

    }


    // 测试登录，浏览器访问： http://localhost:8081/user/Login?username=zhang&password=123456
    //http://localhost:8081/user/doLogin?username=emtest1&password=emtest1
    @RequestMapping("Login")
    public SaResult doLogin(String email, String password) {

        String error_msg;
        // 向数据库中查取用户名，这里要捕获查取失败的异
        try {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
            userQueryWrapper.select().eq("UserEmailAddr", email);
            User usr = userMapper.selectOne(userQueryWrapper);
            String TruePassWord = usr.getUserpassword();
            Long usrId = usr.getUserid();
            String username = usr.getUsername();

            if (bCryptPasswordEncoder.matches(password, TruePassWord)) {
                // SaSession session = StpUtil.getSession();
                //session.set("user",usr);9
                StpUtil.login(usrId);

                return SaResult.data(StpUtil.getTokenValue());
            }else{
                return SaResult.error("密码错误");
            }
        } catch (Exception e){
           error_msg = "邮箱未注册" +e;
        }

        return SaResult.error(error_msg);
    }

    // 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
    @RequestMapping("isLogin")
    public SaResult isLogin() {
        return SaResult.ok("当前会话是否登录：" + StpUtil.isLogin());
    }

    @RequestMapping("checkuserlogininfo")
    public SaResult checkuserlogininfo() {
        if(StpUtil.isLogin()){
            try {
                Long userId = StpUtil.getLoginIdAsLong();
                String id = userId.toString();
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
                userQueryWrapper.select().eq("userId", id);
                User usr = userMapper.selectOne(userQueryWrapper);
                return SaResult.data(usr);
            } catch (Exception e){
                SaResult.error("没找到id");
            }

        }
        return SaResult.error();
    }


    //退出登录
    //  http://localhost:8081/user/logout
    @RequestMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        ;
        return SaResult.ok("当前会话已经退出");
    }

    // 查询 Token 信息  ---- http://localhost:8081/user/tokenInfo
    // 这个token是登录时服务器自动创建的，相当于登录的令牌
    @RequestMapping("tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }


    //查询 Session信息
    // http://localhost:8081/user/getSession
    @RequestMapping("getSession")
    public SaResult getSession() {


        return SaResult.data(StpUtil.getSession());
    }

    /**
     * Gitee登录
     *
     * @param code 编码
     * @return {@code String}
     */
    @RequestMapping("/giteecallback")
    public String GiteeLogin(@RequestParam("code") String code) {
        JSONObject GiteeUser = thirdLoginUtil.getUser(code, 0);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("giteeid", GiteeUser.getString("id"));
        String returnWord = String.valueOf(userMapper.selectOne(queryWrapper));
        if (returnWord == "null") {
            User newUser = new User();
            newUser.setUsernickname(GiteeUser.getString("name"));
            newUser.setGiteeid(GiteeUser.getString("id"));
            IdGenerator Id = new IdGenerator();
            newUser.setUserid(Id.getId());
            log.info("Gitee用户" + newUser.getUserid() + "创建成功");
            userMapper.insert(newUser);
            return "注册成功";  //不存在此用户 为其注册
        } else {
            User giteeuser = userMapper.selectOne(queryWrapper);
            Long GiteeuserId = giteeuser.getUserid();
            StpUtil.login(GiteeuserId);
            log.info("用户" + returnWord + "登录成功");
            return "登录成功";//跳转至登录成功界面
        }
    }



//

//    @RequestMapping("/findAlldevice")
//    public SaResult findAlldevice(String name){
//
//        Object loginId = StpUtil.getLoginId();
//        List<String> list = new ArrayList<String>();
//
//        QueryWrapper<userdevice> queryWrapper = new QueryWrapper<userdevice>();
//        queryWrapper.select().eq("userid",loginId);
//        List<userdevice> userroleList = udMapper.selectList(queryWrapper);
//
//        for(userdevice ud : userroleList){
//            list.add(ud.getDeviceid());
//        }
//        return SaResult.ok("设备id："+list.toString());
//
//    }
//
//
//    //http://120.25.123.85:8081/user/doLogin?username=inzam&password=123456
//    // /http://localhost:8081/user/addDeviceByDeviceID?DeviceID=
//
//
//
//    @RequestMapping("/addDeviceByDeviceID")
//    public SaResult addDeviceByDeviceID(String DeviceID){
//        if(!StpUtil.hasRole("root")){
//            return SaResult.error("权限不足！");
//        }
//        QueryWrapper<userdevice> urQW = new QueryWrapper<>();
//        urQW.select().eq("userid",StpUtil.getLoginId()).eq("deviceid",DeviceID);
//        userdevice usrd = udMapper.selectOne(urQW);
//
//        if(usrd != null){
//            return SaResult.error("设备(设备id："+DeviceID+")已存在");
//        }
//
//        userdevice insertData = new userdevice();
//        Long usrid = StpUtil.getLoginIdAsLong();
//        insertData.setUserid(usrid);
//        insertData.setDeviceid(DeviceID);
//        udMapper.insert(insertData);
//        return SaResult.ok("已添加设备(设备id："+DeviceID+")");
//
//    }
}
