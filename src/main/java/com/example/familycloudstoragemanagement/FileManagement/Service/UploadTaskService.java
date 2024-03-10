package com.example.familycloudstoragemanagement.FileManagement.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UploadTask;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IUploadTaskService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.UploadTaskMapper;
import org.springframework.stereotype.Service;

@Service
public class UploadTaskService extends ServiceImpl<UploadTaskMapper, UploadTask> implements IUploadTaskService {


}