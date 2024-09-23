package com.tianji.learning.controller;


import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.learning.service.ILearningLessonService;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-09-21
 */
@Api(tags = "Learning records related interface")
@RestController
@RequestMapping("/learning-records")
@RequiredArgsConstructor
public class LearningRecordController {

    private final ILearningRecordService recordService;

    @GetMapping("/course/{courseId}")
    @ApiOperation("query current user's learning progress of the corresponding course")
    public LearningLessonDTO queryLearningRecordByCourse(@PathVariable("courseId") Long courseId){
        return recordService.queryLearningRecordByCourse(courseId);
    }
}
