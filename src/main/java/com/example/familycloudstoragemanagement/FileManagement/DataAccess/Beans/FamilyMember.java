package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 家庭共享空间与成员关联表
 */
@Data
@Table(name = "familymembers")
@Entity
@TableName("familymembers")
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String familyMemberId;

    @Column(columnDefinition="varchar(20) comment '文件id'")
    private String FSSId;

    @Column(columnDefinition = "bigint comment '用户id'")
    private Long userId;

    @Column(columnDefinition = "boolean comment '是否为管理员'")
    private Boolean isAdmin;

}
