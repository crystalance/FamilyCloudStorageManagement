package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class FamilyFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String familyFileId;

    @Column(columnDefinition="varchar(20) comment '文件id'")
    private String FSSId;

    @Column(columnDefinition = "varchar(20) comment '用户文件id'")
    private String userFileId;

    @Column(columnDefinition = "bigint comment '上传文件的用户的id'")
    private Long userId;

}
