package com.tianji.learning.controller;


import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.learning.domain.dto.LearningRecordFormDTO;
import com.tianji.learning.service.ILearningLessonService;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    @ApiOperation("submit learning records")
    public void addLearningRecord(@RequestBody @Validated LearningRecordFormDTO learningRecordFormDTO){
        recordService.addLearningRecord(learningRecordFormDTO);
    }
}
