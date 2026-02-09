package com.safeg.admin.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safeg.admin.vo.SmsVO;

@Repository
public interface SmsRepository extends JpaRepository<SmsVO, Long> {
    // 여기에 이름을 SmsService라고 지으면 기존 Service와 충돌납니다!
}
