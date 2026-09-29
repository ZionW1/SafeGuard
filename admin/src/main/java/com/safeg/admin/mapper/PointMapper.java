package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.PointHistoryVO;

@Mapper
public interface PointMapper {

    public List<PointHistoryVO> getPointList(@Param("option") Option option, @Param("page") Page page, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int getTotalPoints() throws Exception;

    public List<PointHistoryVO> pointSelect(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public List<PointHistoryVO> getWorkList(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public List<PointHistoryVO> getLeaderList(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public List<PointHistoryVO> getReferrerList(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public List<PointHistoryVO> getAttendList(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public List<PointHistoryVO> getOverPayList(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int workAmount(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int leaderAmount(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int referrerAmount(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int attendAmount(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int overPayAmount(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int getTotalPoint(@Param("userNo") String userNo, @Param("selectedMonth") String selectedMonth) throws Exception;

    public int pointCount(@Param("option") Option option, @Param("page") Page page, @Param("selectedMonth") String selectedMonth);

}
