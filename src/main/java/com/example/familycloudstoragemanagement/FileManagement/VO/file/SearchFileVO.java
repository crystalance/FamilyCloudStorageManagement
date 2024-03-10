package com.example.familycloudstoragemanagement.FileManagement.VO.file;

import lombok.Data;

import java.util.List;
import java.util.Map;

//vo是返回给前端的数据打包的集合。
@Data
public class SearchFileVO {
    private String userFileId;
    private String fileName;
    private String filePath;
    private String extendName;
    private Long fileSize;
    private String fileUrl;
    private Map<String, List<String>> highLight;
    private Integer isDir;
}