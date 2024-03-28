package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "storage", uniqueConstraints = {
        @UniqueConstraint(name = "userid_index", columnNames = {"userId"})
})
@Entity
@TableName("storage")
public class StorageBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="bigint(20)")
    @TableId(type = IdType.AUTO)
    private Long storageId;

    @Column(columnDefinition="bigint")
    private Long userId;

    @Column(columnDefinition="bigint(20) comment '占用存储大小'")
    private Long storageSize;

    @Column(columnDefinition="bigint(20) comment '总存储大小'")
    private Long totalStorageSize;

    @Column(columnDefinition="varchar(25) comment '修改时间'")
    private String modifyTime;
    @Column(columnDefinition="bigint(20) comment '修改用户id'")
    private Long modifyUserId;

    public StorageBean(){

    }
    public StorageBean(Long userId) {
        this.userId = userId;
    }

}