package com.example.familycloudstoragemanagement.FileManagement.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.familycloudstoragemanagement.FileManagement.Component.FileDealComp;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.RecoveryFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IRecoveryFileService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.RecoveryFileMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.UserFileMapper;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.RecoveryFileListVo;
import com.example.familycloudstoragemanagement.FileManagement.io.QiwenFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileDealComp fileDealComp;


    @Override
    public void deleteUserFileByDeleteBatchNum(String deleteBatchNum) {

        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.delete(userFileLambdaQueryWrapper);

    }

    @Override
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {

        List<UserFile> restoreUserFileList = userFileMapper.selectList(new QueryWrapper<UserFile>().lambda().eq(UserFile::getDeleteBatchNum, deleteBatchNum));
        for (UserFile restoreUserFile : restoreUserFileList) {
            restoreUserFile.setDeleteFlag(0);
            restoreUserFile.setDeleteBatchNum(deleteBatchNum);
            String fileName = fileDealComp.getRepeatFileName(restoreUserFile, restoreUserFile.getFilePath());
            if (restoreUserFile.isDirectory()) {
                if (!StringUtils.equals(fileName, restoreUserFile.getFileName())) {
                    userFileMapper.deleteById(restoreUserFile);
                } else {
                    userFileMapper.updateById(restoreUserFile);
                }
            } else if (restoreUserFile.isFile()) {
                restoreUserFile.setFileName(fileName);
                userFileMapper.updateById(restoreUserFile);
            }
        }

        QiwenFile qiwenFile = new QiwenFile(filePath, true);
        fileDealComp.restoreParentFilePath(qiwenFile, sessionUserId);

        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        recoveryFileMapper.delete(recoveryFileServiceLambdaQueryWrapper);
    }

    @Override
    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }
}
