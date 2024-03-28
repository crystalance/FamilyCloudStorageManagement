package com.example.familycloudstoragemanagement.FileManagement.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.SysParam;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.ISysParamService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.SysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class SysParamService extends ServiceImpl<SysParamMapper, SysParam> implements ISysParamService {

    @Resource
    SysParamMapper sysParamMapper;

    @Override
    public String getValue(String key) {
        SysParam sysParam = new SysParam();
        sysParam.setSysParamKey(key);
        List<SysParam> list = sysParamMapper.selectList(new QueryWrapper<>(sysParam));
        if (list != null && !list.isEmpty()) {
            return list.get(0).getSysParamValue();
        }
        return null;
    }
}
