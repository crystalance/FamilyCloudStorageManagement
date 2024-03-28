package com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.SysParam;

public interface ISysParamService  extends IService<SysParam> {
    String getValue(String key);
}
