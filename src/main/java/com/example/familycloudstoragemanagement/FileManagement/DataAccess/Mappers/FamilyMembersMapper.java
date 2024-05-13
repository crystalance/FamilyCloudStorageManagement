package com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FamilyMember;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FamilyMemberListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FamilyMembersMapper extends BaseMapper<FamilyMember> {
    List<FamilyMemberListVO> selectFamilyMemberList(@Param("FSSId")String FSSId);
}
