package com.tianji.learning.controller;


import com.tianji.learning.domain.po.PointsBoardSeason;
import com.tianji.learning.service.IPointsBoardSeasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-10-15
 */
@RestController
@RequestMapping("/boards/seasons")
@Api(tags = "Points related interface")
@RequiredArgsConstructor
public class PointsBoardSeasonController {
    private IPointsBoardSeasonService pointsBoardSeasonService;

    @GetMapping("/list")
    @ApiOperation("query seasons list")
    public List<PointsBoardSeason> queryPointsBoardSeasons(){
        return pointsBoardSeasonService.list();
    }

}
