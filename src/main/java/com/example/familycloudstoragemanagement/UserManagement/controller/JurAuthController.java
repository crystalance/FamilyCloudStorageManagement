package com.example.familycloudstoragemanagement.UserManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.RoleauthMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.UserMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.UserroleMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.User;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.roleauth;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.userrole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("jur")
public class JurAuthController {

    /*
     * 前提1：首先调用登录接口进行登录
     *
     *
     * 前提2：项目实现 StpInterface 接口，代码在  com.pj.satoken.StpInterfaceImpl
     * 		Sa-Token 将从此实现类获取 每个账号拥有哪些权限。
     *
     * 然后我们就可以使用下面的代码进行鉴权了
     */
    @Autowired
    private UserroleMapper userroleMapper;

    @Autowired
    private RoleauthMapper roleauthMapper;

    @Autowired
    private UserMapper userMapper;


    // 查询权限   ---- http://localhost:8081/jur/getPermission
    @RequestMapping("getPermission")
    public SaResult getPermission() {
        // 查询权限信息 ，如果当前会话未登录，会返回一个空集合
        List<String> permissionList = StpUtil.getPermissionList();
        System.out.println("当前登录账号拥有的所有权限：" + permissionList);

        // 查询角色信息 ，如果当前会话未登录，会返回一个空集合
        List<String> roleList = StpUtil.getRoleList();
        System.out.println("当前登录账号拥有的所有角色：" + roleList);

        // 返回给前端
        return SaResult.ok()
                .set("roleList", roleList)
                .set("permissionList", permissionList);
    }

    // http://localhost:8081/jur/checkPermission?permission=
    @RequestMapping("checkPermission")
    public SaResult checkPermission(String permissionId) {
        StpUtil.checkPermission(permissionId);
        return SaResult.ok();
    }


    //  http://localhost:8081/jur/checkPermission?roleid=
    @RequestMapping("checkRole")
    public SaResult checkRole(String roleid) {
        StpUtil.checkRole(roleid);
        return SaResult.ok();
    }

    //给某个账号赋予角色（前提是这个账号具有管理员身份）
    // http://localhost:8081/jur/UserAddRoleByUserId?userId=&roleId=
    @RequestMapping("UserAddRoleByUserId")
    public SaResult UserAddRoleByUserId(Long userId, String roleId) {

        //先检查是否为管理员角色
        if (!StpUtil.hasRole("root")) {
            return SaResult.error("权限不足！");
        }


        //先查询userId是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.select().eq("userid", userId);
        User usr = userMapper.selectOne(userQueryWrapper);

        if (usr == null) {
            return SaResult.error("用户不存在!");
        }

        //查询该用户是否已经具有该角色，若有，则退出
        QueryWrapper<userrole> urQW = new QueryWrapper<>();
        urQW.select().eq("userid", userId).eq("roleid", roleId);
        userrole usrole = userroleMapper.selectOne(urQW);

        if (usrole != null) {
            return SaResult.error("用户已经具备" + roleId + "角色");
        }


        userrole ur = new userrole();
        ur.setUserid(userId);
        ur.setRoleid(roleId);

        userroleMapper.insert(ur);

        return SaResult.ok();

    }


    //给某个账号删除某用户权限
    // http://localhost:8081/jur/UserDelRoleByUserId?userId=&roleId=
    @RequestMapping("UserDelRoleByUserId")
    public SaResult UserDelRoleByUserId(Long userId, String roleId) {

        // 先检查是否为管理员角色
        if (!StpUtil.hasRole("root")) {
            return SaResult.error("权限不足！");
        }

        //先查询userId是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.select().eq("userid", userId);
        User usr = userMapper.selectOne(userQueryWrapper);

        if (usr == null) {
            return SaResult.error("用户不存在!");
        }

        //查询该用户是否已经具有该角色，若有，则删除
        QueryWrapper<userrole> urQW = new QueryWrapper<>();
        urQW.select().eq("userid", userId).eq("roleid", roleId);
        userrole usrole = userroleMapper.selectOne(urQW);

        if (usrole != null) {
            userroleMapper.delete(urQW);
            return SaResult.ok("删除成功！");
        }

        return SaResult.error("角色本就不存在！");
    }

    // http://localhost:8081/jur/AddPermToRole?roleId=&authId=
    //给某个角色增加一项权限
    @RequestMapping("AddPermToRole")
    public SaResult AddPermToRole(String roleId, String authId) {

        // 先检查是否为管理员角色
        if (!StpUtil.hasRole("root")) {
            return SaResult.error("权限不足！");
        }

        //先查询roleId是否已经有该权限
        QueryWrapper<roleauth> roleauthQueryWrapper = new QueryWrapper<>();
        roleauthQueryWrapper.select().eq("roleid", roleId).eq("authid", authId);
        roleauth ra = roleauthMapper.selectOne(roleauthQueryWrapper);

        if (ra != null) {
            return SaResult.error("角色：" + roleId + "已经有权限：" + authId);
        }

        roleauth rainsert = new roleauth();

        rainsert.setRoleid(roleId);
        rainsert.setAuthid(authId);
        roleauthMapper.insert(rainsert);

        return SaResult.ok("增加成功！" + "角色：" + roleId + "已经有权限：" + authId);

    }

    // http://localhost:8081/jur/DelPermToRole?roleId=&authId=

    @RequestMapping("DelPermToRole")
    public SaResult DelPermToRole(String roleId, String authId) {
        // 先检查是否为管理员角色
        if (!StpUtil.hasRole("root")) {
            return SaResult.error("权限不足！");
        }

        //先查询roleId是否已经有该权限
        QueryWrapper<roleauth> roleauthQueryWrapper = new QueryWrapper<>();
        roleauthQueryWrapper.select().eq("roleid", roleId).eq("authid", authId);
        roleauth ra = roleauthMapper.selectOne(roleauthQueryWrapper);

        if (ra == null) {
            return SaResult.error("角色：" + roleId + "本没有权限：" + authId);
        }

        roleauthMapper.delete(roleauthQueryWrapper);

        return SaResult.ok("角色：" + roleId + "去除了权限：" + authId);

    }


//    @RequestMapping("AuthorizeRole")
//    public SaResult A


}
