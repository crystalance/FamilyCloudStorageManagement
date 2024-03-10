package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UploadTaskDetail;

import java.util.List;

public interface UploadTaskDetailMapper extends BaseMapper<UploadTaskDetail> {
    List<Integer> selectUploadedChunkNumList(String identifier);
}
