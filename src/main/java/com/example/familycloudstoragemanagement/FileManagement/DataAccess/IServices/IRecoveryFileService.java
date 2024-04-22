package com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.RecoveryFile;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.RecoveryFileListVo;

import java.util.List;

public interface IRecoveryFileService extends IService<RecoveryFile> {
    void deleteUserFileByDeleteBatchNum(String deleteBatchNum);
    void restorefile(String deleteBatchNum, String filePath, Long sessionUserId);
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}
