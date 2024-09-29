package com.tianji.learning.service;

import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;

import java.util.List;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author Mike
 * @since 2024-09-18
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    void addUserLessons(Long userId, List<Long> courseIds);

    PageDTO<LearningLessonVO> queryMyLessons(PageQuery pageQuery);

    LearningLessonVO queryMyCurrentLesson();

    Long isLessonValid(Long courseId);

    LearningLessonVO queryLessonByCourseId(Long courseId);

    void createLearningPlan(LearningPlanDTO learningPlanDTO);
<<<<<<< HEAD

    LearningPlanPageVO queryMyPlans(PageQuery query);
=======
>>>>>>> 499f842726ec189e68c9ab32fff5001dc8521e94
}
