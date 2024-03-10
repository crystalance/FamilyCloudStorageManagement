package com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserFileService extends IService<UserFile> {
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId);

    IPage<FileListVO> userFileList(Long userId, String filePath, Long beginCount, Long pageCount);
    void updateFilepathByUserFileId(String userFileId, String newfilePath, Long userId);
    void userFileCopy(Long userId, String userFileId, String newfilePath);

    IPage<FileListVO> getFileByFileType(Integer fileTypeId, Long currentPage, Long pageCount, Long userId);
    List<UserFile> selectUserFileListByPath(String filePath, Long userId);
    List<UserFile> selectFilePathTreeByUserId(Long userId);
    void deleteUserFile(String userFileId, Long sessionUserId);

    List<UserFile> selectUserFileByLikeRightFilePath(@Param("filePath") String filePath, @Param("userId") Long userId);

}
