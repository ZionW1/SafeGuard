package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.PointHistoryVO;

public interface PointService {
    public List<PointHistoryVO> getPointList(Option option, Page page, String selectedMonth) throws Exception;

    public int getTotalPoint(String userNo, String selectedMonth) throws Exception;

	public List<PointHistoryVO> pointSelect(String userNo, String selectedMonth) throws Exception;

    public List<PointHistoryVO> getWorkList(String userNo, String selectedMonth) throws Exception;

    public List<PointHistoryVO> getLeaderList(String userNo, String selectedMonth) throws Exception;

    public List<PointHistoryVO> getReferrerList(String userNo, String selectedMonth) throws Exception;

    public List<PointHistoryVO> getOverPayList(String userNo, String selectedMonth) throws Exception;

    public List<PointHistoryVO> getAttendList(String userNo, String selectedMonth) throws Exception;

    public int workAmount(String userNo, String selectedMonth) throws Exception;

    public int leaderAmount(String userNo, String selectedMonth) throws Exception;

    public int referrerAmount(String userNo, String selectedMonth) throws Exception;

    public int attendAmount(String userNo, String selectedMonth) throws Exception;

    public int overPayAmount(String userNo, String selectedMonth) throws Exception;

    public int pointCount(Option option, Page page, String selectedMonth) throws Exception;

}
