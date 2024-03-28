package com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.StorageBean;

public interface IStorageService extends IService<StorageBean> {
    Long getTotalStorageSize(Long userId);
    boolean checkStorage(Long userId, Long fileSize);
}
