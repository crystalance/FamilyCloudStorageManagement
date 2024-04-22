package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.RecoveryFile;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.RecoveryFileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    List<RecoveryFileListVo> selectRecoveryFileList(@Param("userId") Long userId);
}

