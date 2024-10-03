package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-09-18
 */
@Api(tags = "My lessons related interface")
@RestController
@Slf4j
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LearningLessonController {

    private final ILearningLessonService lessonService;

    @GetMapping("/page")
    @ApiOperation("page query my lessons")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery pageQuery) {
        return lessonService.queryMyLessons(pageQuery);
    }

    @GetMapping("/now")
    @ApiOperation("query my current studying lesson")
    public LearningLessonVO queryMyCurrentLesson() {
        return lessonService.queryMyCurrentLesson();
    }

    @GetMapping("/{courseId}/valid")
    @ApiOperation("check if user can study the course")
    public Long isLessonValid(@PathVariable("courseId") Long courseId) {
        return lessonService.isLessonValid(courseId);
    }

    @GetMapping("/{courseId}")
    @ApiOperation("query course status of the user")
    public LearningLessonVO queryLessonByCourseId(@PathVariable("courseId") Long courseId){
        return  lessonService.queryLessonByCourseId(courseId);
    }

    @ApiOperation("用户手动删除当前课程")
    @DeleteMapping("/{courseId}")
    public void deleteCourseFromLesson(@PathVariable("courseId") Long couseId){
        log.info("Delete lesson");
        Long user = UserContext.getUser();
        lessonService.deleteCourseFromLesson(user, couseId);
    }

    @ApiOperation("create learning plans")
    @PostMapping("/plans")
    public void createLearningPlans(@Valid @RequestBody LearningPlanDTO learningPlanDTO){
        lessonService.createLearningPlan(learningPlanDTO);
    }

    @ApiOperation("query my learning plan")
    @GetMapping("/plans")
    public LearningPlanPageVO queryMyPlans(PageQuery query){
        return lessonService.queryMyPlans(query);
    }

}
