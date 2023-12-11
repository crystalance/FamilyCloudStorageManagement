package com.example.familycloudstoragemanagement.UserManagement.authorizeSaToken;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.RoleauthMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.UserroleMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.roleauth;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.userrole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private UserroleMapper userroleMapper;

    @Autowired
    private RoleauthMapper roleauthMapper;
    /**
     * 返回一个账号所拥有的权限码集合
     * 从数据库中查询这个账号的所有的权限码
     */

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();

        QueryWrapper<userrole> queryWrapper = new QueryWrapper<userrole>();
        queryWrapper.select().eq("userid",loginId);
        List<userrole> userroleList = userroleMapper.selectList(queryWrapper);

        for(userrole ur:userroleList){
            QueryWrapper<roleauth>  roleauthQueryWrapper = new QueryWrapper<roleauth>();
            roleauthQueryWrapper.select().eq("roleid",ur.getRoleid());
            List<roleauth> roleauthList = roleauthMapper.selectList(roleauthQueryWrapper);
            for(roleauth ra:roleauthList) {
                if(!list.contains(ra.getAuthid())){ //防止重复
                    list.add(ra.getAuthid());
                }
            }
        }
        // System.out.println("hello!" );

        return list;
    }


    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     * 从数据库中查询一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();

        //查询条件
        QueryWrapper<userrole> queryWrapper = new QueryWrapper<userrole>();
        queryWrapper.select().eq("userid",loginId);
        List<userrole> userroleList = userroleMapper.selectList(queryWrapper);

        for(userrole ur:userroleList){
            list.add(ur.getRoleid());
        }
        return list;
    }



}