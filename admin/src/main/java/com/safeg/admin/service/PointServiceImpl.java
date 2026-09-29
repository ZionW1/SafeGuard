package com.safeg.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.PointMapper;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.PointHistoryVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService {
    // Implement the methods defined in the PointService interface

    private final PointMapper pointMapper;

    @Override
    public List<PointHistoryVO> getPointList(Option option, Page page, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPointList'");
        int total = pointMapper.pointCount(option, page, selectedMonth); // 페이지 수 계산
        log.info("Selected Month: {}", selectedMonth);
        log.info("Total Points: {}", total);
        page.setTotal(total);
        List<PointHistoryVO> pointList = pointMapper.getPointList(option, page, selectedMonth);

        // return pointList.stream().map(PointHistoryVO::new).collect(Collectors.toList());
        return pointList;
    }

    @Override
    public int pointCount(Option option, Page page, String selectedMonth) throws Exception {
        return pointMapper.pointCount(option, page, selectedMonth);
    }

    @Override
    public int getTotalPoint(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getTotalPoints'");
        int totalPoints = pointMapper.getTotalPoint(userNo, selectedMonth);
        return totalPoints;
    }

    @Override
    public List<PointHistoryVO> pointSelect(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.pointSelect(userNo, selectedMonth);
    }

    @Override
    public List<PointHistoryVO> getWorkList(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.getWorkList(userNo, selectedMonth);
    }

    @Override
    public List<PointHistoryVO> getLeaderList(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.getLeaderList(userNo, selectedMonth);
    }

    @Override
    public List<PointHistoryVO> getReferrerList(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.getReferrerList(userNo, selectedMonth);
    }

    @Override
    public List<PointHistoryVO> getOverPayList(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.getOverPayList(userNo, selectedMonth);
    }

    @Override
    public List<PointHistoryVO> getAttendList(String userNo, String selectedMonth) throws Exception {
        // TODO Auto-generated method stub
        return pointMapper.getAttendList(userNo, selectedMonth);
    }

    @Override
    public int workAmount(String userNo, String selectedMonth) throws Exception {
        return pointMapper.workAmount(userNo, selectedMonth);
    }

    @Override
    public int leaderAmount(String userNo, String selectedMonth) throws Exception {
        return pointMapper.leaderAmount(userNo, selectedMonth);
    }

    @Override
    public int referrerAmount(String userNo, String selectedMonth) throws Exception {
        return pointMapper.referrerAmount(userNo, selectedMonth);
    }

    @Override
    public int attendAmount(String userNo, String selectedMonth) throws Exception {
        return pointMapper.attendAmount(userNo, selectedMonth);
    }

    @Override
    public int overPayAmount(String userNo, String selectedMonth) throws Exception {
        return pointMapper.overPayAmount(userNo, selectedMonth);
    }

}
