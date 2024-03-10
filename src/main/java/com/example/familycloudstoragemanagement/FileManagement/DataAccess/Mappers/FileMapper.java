package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FileBean;

import java.util.List;

public interface FileMapper extends BaseMapper<FileBean> {


    void batchInsertFile(List<FileBean> fileBeanList);


}
