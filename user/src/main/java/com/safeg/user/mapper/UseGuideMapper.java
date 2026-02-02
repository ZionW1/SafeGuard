package com.safeg.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.UseGuideVO;

@Mapper
public interface UseGuideMapper {

    public UseGuideVO useGuide01() throws Exception;
    
}
