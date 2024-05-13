package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyFile;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FSSFileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FamilyFilesMapper extends BaseMapper<FamilyFile> {
    List<FSSFileListVO> selectFSSFileList(@Param("FSSId")String FSSId);
}
