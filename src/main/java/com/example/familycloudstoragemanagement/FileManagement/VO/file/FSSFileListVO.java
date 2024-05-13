package com.example.familycloudstoragemanagement.FileManagement.VO.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FSS文件列表Vo",required = true)
public class FSSFileListVO {
    @Schema(description="文件id")
    private String fileId;
    @Schema(description="文件url")
    private String fileUrl;
    @Schema(description="文件大小")
    private Long fileSize;
    @Schema(description="存储类型")
    private Integer storageType;
    @Schema(description="用户文件id")
    private String userFileId;



    @Schema(description="文件名")
    private String fileName;
    @Schema(description="文件路径")
    private String filePath;
    @Schema(description="文件扩展名")
    private String extendName;
    @Schema(description="是否是目录 0-否， 1-是")
    private Integer isDir;
    @Schema(description="上传时间")
    private String uploadTime;
    @Schema(description="分享文件路径")
    private String shareFilePath;

}
