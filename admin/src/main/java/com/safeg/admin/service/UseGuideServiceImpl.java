package com.safeg.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.UseGuideMapper;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UseGuideVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UseGuideServiceImpl implements UseGuideService {
    
    @Autowired
    UseGuideMapper useGuideMapper;

    @Override
    public AdminContentVO useGuideSelect() throws Exception {
        // TODO Auto-generated method stub
        log.info("UseGuideServiceImpl useGuide02 호출");
        AdminContentVO useGuideSelect = useGuideMapper.useGuideSelect();
        return useGuideSelect; // 임시로 성공 반환
    }

    @Override
    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception {
        // DB에 content를 저장하는 로직을 여기에 구현
        // 예: DAO/Repository를 통해 INSERT 쿼리 실행
        // 성공 시 1 반환, 실패 시 0 반환 (예시)
        log.info("UseGuideServiceImpl useGuide02 호출" + adminContentVO.getContent());
        int result = useGuideMapper.useGuideInsert(adminContentVO);
        return result; // 임시로 성공 반환
    }

    @Override
    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception {
        // DB에 content를 저장하는 로직을 여기에 구현
        // 예: DAO/Repository를 통해 INSERT 쿼리 실행
        // 성공 시 1 반환, 실패 시 0 반환 (예시)
        int result = useGuideMapper.useGuideUpdate(adminContentVO);
        return result; // 임시로 성공 반환
    }

}
