package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.SupportVO;

@Mapper
public interface SupportMapper {

    public List<AdminContentVO> supportList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public int insertSupport(SupportVO supportVO) throws Exception;
    
}
