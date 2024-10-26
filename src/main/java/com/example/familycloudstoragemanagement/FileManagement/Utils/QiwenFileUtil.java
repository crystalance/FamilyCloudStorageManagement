package com.example.familycloudstoragemanagement.FileManagement.Utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.io.QiwenFile;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.security.SessionUtil;

public class QiwenFileUtil {


    public static UserFile getQiwenDir(Long userId, String filePath, String fileName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(QiwenFile.formatPath(filePath));
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(com.qiwenshare.common.util.DateUtil.getCurrentTime());
        userFile.setCreateUserId(StpUtil.getLoginIdAsLong());
        userFile.setCreateTime(com.qiwenshare.common.util.DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile getQiwenFile(Long userId, String fileId, String filePath, String fileName, String extendName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(fileId);
        userFile.setFileName(fileName);
        userFile.setFilePath(QiwenFile.formatPath(filePath));
        userFile.setExtendName(extendName);
        userFile.setIsDir(0);
        userFile.setUploadTime(com.qiwenshare.common.util.DateUtil.getCurrentTime());
        userFile.setCreateTime(DateUtil.getCurrentTime());
        userFile.setCreateUserId(StpUtil.getLoginIdAsLong());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile searchQiwenFileParam(UserFile userFile) {
        UserFile param = new UserFile();
        param.setFilePath(QiwenFile.formatPath(userFile.getFilePath()));
        param.setFileName(userFile.getFileName());
        param.setExtendName(userFile.getExtendName());
        param.setDeleteFlag(0);
        param.setUserId(userFile.getUserId());
        param.setIsDir(0);
        return param;
    }

    public static String formatLikePath(String filePath) {
        String newFilePath = filePath.replace("'", "\\'");
        newFilePath = newFilePath.replace("%", "\\%");
        newFilePath = newFilePath.replace("_", "\\_");
        return newFilePath;
    }

}
