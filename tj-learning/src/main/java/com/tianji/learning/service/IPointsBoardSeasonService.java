package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsBoardSeason;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Mike
 * @since 2024-10-15
 */
public interface IPointsBoardSeasonService extends IService<PointsBoardSeason> {

    void createPointBoardOfLastSeason(LocalDate time);

}
