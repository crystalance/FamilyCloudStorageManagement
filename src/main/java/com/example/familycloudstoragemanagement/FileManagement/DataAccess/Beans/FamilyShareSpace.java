package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "familysharespace")
@Entity
@TableName("familysharespace")
public class FamilyShareSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String FamilyShareSpaceId;

    @Column(name = "FamilyName", nullable = false, length = 255)
    private String FamilyName;

    @Column(columnDefinition = "bigint comment '管理员id'")
    private Long AdminId;
}
