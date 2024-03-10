package com.example.familycloudstoragemanagement.FileManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.familycloudstoragemanagement.FileManagement.Component.FileDealComp;
import com.example.familycloudstoragemanagement.FileManagement.DTO.UploadFileDTO;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IFileService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IFiletransferService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IUserFileService;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.UploadFileVo;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.ufop.factory.UFOPFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传、下载和预览")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {
    @Resource
    IFiletransferService filetransferService;

    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;
    @Resource
    FileDealComp fileDealComp;
//    @Resource
//    StorageService storageService;
    @Resource
    UFOPFactory ufopFactory;


    public static final String CURRENT_MODULE = "文件传输接口";

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto) {

        Long userId = StpUtil.getLoginIdAsLong();
        filetransferService.uploadFile(request, uploadFileDto, userId);

        UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success().data(uploadFileVo);

    }



}
