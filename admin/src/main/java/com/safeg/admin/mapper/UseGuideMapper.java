package com.safeg.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UseGuideVO;

@Mapper
public interface UseGuideMapper {

    public AdminContentVO useGuideSelect() throws Exception;

    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception;

    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception;

}
