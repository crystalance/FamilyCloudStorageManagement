package com.example.familycloudstoragemanagement.FileManagement.VO.file;

import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.Image;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.Music;
import lombok.Data;

@Data
public class FileDetailVO {
    private String fileId;

    private String timeStampName;

    private String fileUrl;

    private Long fileSize;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;

    private String userFileId;

    private Long userId;


    private String fileName;

    private String filePath;

    private String extendName;

    private Integer isDir;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;

    private Image image;

    private Music music;
}
