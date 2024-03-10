package com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FileBean;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FileDetailVO;

public interface IFileService  extends IService<FileBean> {

    Long getFilePointCount(String fileId);
    void unzipFile(String userFileId, int unzipMode, String filePath);

    void updateFileDetail(String userFileId, String identifier, long fileSize);

    FileDetailVO getFileDetail(String userFileId);

}
