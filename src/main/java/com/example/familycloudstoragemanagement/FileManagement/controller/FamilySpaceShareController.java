package com.example.familycloudstoragemanagement.FileManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.example.familycloudstoragemanagement.FileManagement.Component.FileDealComp;
import com.example.familycloudstoragemanagement.FileManagement.DTO.CreateFSSDTO;
import com.example.familycloudstoragemanagement.FileManagement.DTO.CreateFoldDTO;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyMember;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyShareSpace;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.FamilyMembersMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.FamilyShareSpaceMapper;
import com.example.familycloudstoragemanagement.FileManagement.Utils.QiwenFileUtil;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "familySpaceShare", description = "该接口为家庭文件共享空间接口")
@RestController
@Slf4j
@RequestMapping("/familySpaceShare")
public class FamilySpaceShareController {
    @Resource
    FileDealComp fileDealComp;

    @Resource
    FamilyShareSpaceMapper familyShareSpaceMapper;

    @Resource
    FamilyMembersMapper familyMembersMapper;

    /**
     * 判断用户是否已经创建或加入了一个FSS
     */
    @RequestMapping(value = "/isFSSexist",method = RequestMethod.GET)
    @ResponseBody
    public boolean isFSSexist(){
        Long userId = StpUtil.getLoginIdAsLong();
        return fileDealComp.isFSSExist(userId);
    }

    @Operation(summary = "创建家庭共享空间", description = "当前用户家庭共享空间的创建", tags = {"familySpaceShare"})
    @RequestMapping(value = "/createFSS", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> createFSS(CreateFSSDTO createFSSDTO) {

        Long userId = StpUtil.getLoginIdAsLong();
        String FSSName = createFSSDTO.getFSSName();
        if(FSSName.isEmpty()){FSSName = "Default";}

        boolean isFSSExist = fileDealComp.isFSSExist(userId);
        if (isFSSExist) {
            return RestResult.fail().message("用户已绑定FSS！");
        }else{
            //存储家庭共享空间基本信息
            String fssId = IdUtil.getSnowflakeNextIdStr();
            FamilyShareSpace familyShareSpace = new FamilyShareSpace();
            familyShareSpace.setFamilyShareSpaceId(fssId);
            familyShareSpace.setFamilyName(FSSName);
            familyShareSpace.setAdminId(userId);
            familyShareSpaceMapper.insert(familyShareSpace);
            //存储家庭共享空间与其成员的映射关系
            FamilyMember familyMember = new FamilyMember();
            familyMember.setFamilyMemberId(IdUtil.getSnowflakeNextIdStr());
            familyMember.setFSSId(fssId);
            familyMember.setUserId(userId);
            familyMember.setIsAdmin(true);
            familyMembersMapper.insert(familyMember);
        }
        return RestResult.success();
    }





}
