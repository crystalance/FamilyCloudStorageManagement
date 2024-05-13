package com.example.familycloudstoragemanagement.FileManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.familycloudstoragemanagement.FileManagement.Component.FileDealComp;
import com.example.familycloudstoragemanagement.FileManagement.DTO.CreateFSSDTO;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyMember;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyShareSpace;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.FamilyFilesMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.FamilyMembersMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.FamilyShareSpaceMapper;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FSSFileListVO;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FamilyMemberListVO;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.RecoveryFileListVo;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.mapper.UserMapper;
import com.example.familycloudstoragemanagement.UserManagement.dataAccess.pojo.User;
import com.example.familycloudstoragemanagement.UserManagement.mail.SendEmail;
import com.qiwenshare.common.result.RestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    @Resource
    UserMapper userMapper;

    @Resource
    private SendEmail emailsender;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FamilyFilesMapper familyFilesMapper;

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

            //绑定家庭共享空间
            LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userLambdaUpdateWrapper.eq(User::getUserid,userId).set(User::getFamilysharespaceid,fssId);
            userMapper.update(null,userLambdaUpdateWrapper);

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

    @Operation(summary = "发送邮件邀请新成员加入", description = "发送邮件邀请新成员加入", tags = {"familySpaceShare"})
    @RequestMapping(value = "/SendMail2NewFamilyMember", method = RequestMethod.POST)
    public RestResult SendMail2NewFamilyMember(String email){
        String message = "";
        Long uesrId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserid, uesrId);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        String request_email = user.getUseremailaddr();

        if(!emailsender.isEmail(email)){
            message = "邮箱格式不正确，请重新输入！";
            return RestResult.fail().message(message);
        }
        //查找系统中是否有这个新用户
        LambdaQueryWrapper<User> lambdaQueryWrapper_findemail = new LambdaQueryWrapper<>();
        lambdaQueryWrapper_findemail.eq(User::getUseremailaddr,email);
        if(userMapper.selectCount(lambdaQueryWrapper_findemail)<1){
            message = "该邮箱未注册账号";
            return RestResult.fail().message(message);
        }
        User newMember = userMapper.selectOne(lambdaQueryWrapper_findemail);
        Long newMember_userId = newMember.getUserid();
        if(fileDealComp.isFSSExist(newMember_userId)){
            message = "该邮箱已经绑定了一个FSS,无法加入!";
            return RestResult.fail().message(message);
        }

        emailsender.sendInviteMail(email,request_email);
        message = "邮箱验证码已发送，有效时间为5分钟";
        return RestResult.success().message(message);
    }

    @Operation(summary = "注册新成员", description = "根据新成员返回的验证码注册新成员", tags = {"familySpaceShare"})
    @RequestMapping(value="/RegisterNewFamilyMember",method = RequestMethod.POST)
    public RestResult RegisterNewFamilyMember(String email, String verifyCode){
        //先查询email是否存在
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(email))) {
            return RestResult.fail().message("邮箱号不存在或者验证码已过期");
        }

        //查询value,比较
        String TrueCode;
        TrueCode = stringRedisTemplate.opsForValue().get(email);

        if (Objects.equals(TrueCode, verifyCode)) {
            FamilyMember familyMember = new FamilyMember();
            LambdaQueryWrapper<User> lambdaQueryWrapper_findemail = new LambdaQueryWrapper<>();
            lambdaQueryWrapper_findemail.eq(User::getUseremailaddr,email);
            User familyMemberuser = userMapper.selectOne(lambdaQueryWrapper_findemail);

            Long userid = familyMemberuser.getUserid();
            familyMember.setUserId(userid);
            familyMember.setFamilyMemberId(IdUtil.getSnowflakeNextIdStr());
            familyMember.setIsAdmin(false);

            Long current_user_id = StpUtil.getLoginIdAsLong();
            LambdaQueryWrapper<FamilyShareSpace> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FamilyShareSpace::getAdminId,current_user_id);
            FamilyShareSpace familyShareSpace = familyShareSpaceMapper.selectOne(lambdaQueryWrapper);
            String fssid = familyShareSpace.getFamilyShareSpaceId();

            familyMember.setFSSId(fssid);
            familyMembersMapper.insert(familyMember);

            LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userLambdaUpdateWrapper.eq(User::getUserid,userid).set(User::getFamilysharespaceid,fssid);
            userMapper.update(null,userLambdaUpdateWrapper);



            return RestResult.success().message("家庭成员："+email+"注册成功");
        } else {
            return RestResult.fail().message("验证码不正确！");
        }
    }

    @Operation(summary = "上传家庭共享文件", description = "上传家庭共享文件", tags = {"familySpaceShare"})
    @RequestMapping(value="/UploadFSSFile",method = RequestMethod.POST)
    public RestResult UploadFSSFile(String userFileId){

        Long userId = StpUtil.getLoginIdAsLong();
        if(!fileDealComp.isFSSExist(userId)){
            return RestResult.fail().message("请先绑定家庭共享空间");
        }
        FamilyFile familyFile = new FamilyFile();
        familyFile.setFamilyFileId(IdUtil.getSnowflakeNextIdStr());
        familyFile.setUserFileId(userFileId);
        familyFile.setUserId(userId);

        LambdaQueryWrapper<FamilyMember> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FamilyMember::getUserId,userId);
        FamilyMember familyMember = familyMembersMapper.selectOne(lambdaQueryWrapper);
        familyFile.setFSSId(familyMember.getFSSId());

        familyFilesMapper.insert(familyFile);
        return RestResult.success().message("上传成功");
    }

    @Operation(summary = "家庭共享文件列表", description = "家庭共享文件列表", tags = {"FSSfile"})
    @RequestMapping(value = "/FSSFilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<FSSFileListVO> getFamilyFileList() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserid,userId);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if(user.getFamilysharespaceid()!=null) {
            String FSSid = user.getFamilysharespaceid();
            List<FSSFileListVO> fssFileListVO = familyFilesMapper.selectFSSFileList(FSSid);
            return RestResult.success().dataList(fssFileListVO, fssFileListVO.size());
        }
        return RestResult.fail().message("请先绑定FSS！");

//        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(StpUtil.getLoginIdAsLong());
//        return RestResult.success().dataList(recoveryFileList, recoveryFileList.size());
    }

    @Operation(summary = "家庭共享空间成员列表", description = "家庭共享文件列表", tags = {"recoveryfile"})
    @RequestMapping(value = "/getFamilyMemberList", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<FamilyMemberListVO> getFamilyMemberList() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserid,userId);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if(user.getFamilysharespaceid()!=null) {
            String FSSid = user.getFamilysharespaceid();
            List<FamilyMemberListVO> familyMemberListVO = familyMembersMapper.selectFamilyMemberList(FSSid);
            return RestResult.success().dataList(familyMemberListVO, familyMemberListVO.size());
        }
        return RestResult.fail().message("请先绑定FSS！");


    }




}
