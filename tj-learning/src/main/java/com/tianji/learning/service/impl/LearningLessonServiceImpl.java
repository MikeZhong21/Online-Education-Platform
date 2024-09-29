package com.tianji.learning.service.impl;

<<<<<<< HEAD
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
=======
>>>>>>> 499f842726ec189e68c9ab32fff5001dc8521e94
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.AssertUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.PlanStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 *
 * @author Mike
 * @since 2024-09-18
 */
@Service
@SuppressWarnings("ALL")
@RequiredArgsConstructor
@Slf4j
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    private final CourseClient courseClient;
    private final CatalogueClient catalogueClient;

    private final LearningRecordMapper learningRecordMapper;

    @Override
    @Transactional
    public void addUserLessons(Long userId, List<Long> courseIds) {
        // 1.查询课程有效期
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(courseIds);
        if (CollUtils.isEmpty(cInfoList)) {
            // 课程不存在，无法添加
            log.error("课程信息不存在，无法添加到课表");
            return;
        }
        // 2.循环遍历，处理LearningLesson数据
        List<LearningLesson> list = new ArrayList<>(cInfoList.size());
        for (CourseSimpleInfoDTO cInfo : cInfoList) {
            LearningLesson lesson = new LearningLesson();
            // 2.1.获取过期时间
            Integer validDuration = cInfo.getValidDuration();
            if (validDuration != null && validDuration > 0) {
                LocalDateTime now = LocalDateTime.now();
                lesson.setCreateTime(now);
                lesson.setExpireTime(now.plusMonths(validDuration));
            }
            // 2.2.填充userId和courseId
            lesson.setUserId(userId);
            lesson.setCourseId(cInfo.getId());
            list.add(lesson);
        }
        // 3.批量新增
        this.saveBatch(list);
    }

    @Override
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery pageQuery) {
        Long userID = UserContext.getUser();

        Page<LearningLesson> page = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userID)
                .page(pageQuery.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if(CollUtils.isEmpty(records)){
            return PageDTO.empty(page);
        }

        Set<Long> courseIds = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        List<CourseSimpleInfoDTO> cinfos = courseClient.getSimpleInfoList(courseIds);
        if(CollUtils.isEmpty(cinfos)){
            throw new BizIllegalException("course not exist");
        }

        Map<Long, CourseSimpleInfoDTO> infoDTOMap = cinfos.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        List<LearningLessonVO> voList = new ArrayList<>();
        for (LearningLesson record : records) {
            LearningLessonVO lessonVO = new LearningLessonVO();
            BeanUtils.copyProperties(record, lessonVO);
            CourseSimpleInfoDTO infoDTO = infoDTOMap.get(record.getCourseId());
            if(infoDTO!=null){
                lessonVO.setCourseName(infoDTO.getName());
                lessonVO.setCourseCoverUrl(infoDTO.getCoverUrl());
                lessonVO.setSections(infoDTO.getSectionNum());
            }
            voList.add(lessonVO);
        }

        return PageDTO.of(page, voList);
    }

    @Override
    public LearningLessonVO queryMyCurrentLesson() {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询正在学习的课程 select * from xx where user_id = #{userId} AND status = 1 order by latest_learn_time limit 1
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if (lesson == null) {
            return null;
        }
        // 3.拷贝PO基础属性到VO
        LearningLessonVO vo = new LearningLessonVO();
        BeanUtils.copyProperties(lesson, vo);
        // 4.查询课程信息
        CourseFullInfoDTO cInfo = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (cInfo == null) {
            throw new BadRequestException("课程不存在");
        }
        vo.setCourseName(cInfo.getName());
        vo.setCourseCoverUrl(cInfo.getCoverUrl());
        vo.setSections(cInfo.getSectionNum());
        // 5.统计课表中的课程数量 select count(1) from xxx where user_id = #{userId}
        Integer courseAmount = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .count();
        vo.setCourseAmount(courseAmount);
        // 6.查询小节信息
        List<CataSimpleInfoDTO> cataInfos =
                catalogueClient.batchQueryCatalogue(CollUtils.singletonList(lesson.getLatestSectionId()));
        if (!CollUtils.isEmpty(cataInfos)) {
            CataSimpleInfoDTO cataInfo = cataInfos.get(0);
            vo.setLatestSectionName(cataInfo.getName());
            vo.setLatestSectionIndex(cataInfo.getCIndex());
        }
        return vo;
    }

    @Override
    public Long isLessonValid(Long courseId) {
        Long userId = UserContext.getUser();

        LearningLesson lesson = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();
        if(lesson==null){
            return null;
        }

        LocalDateTime expireTime = lesson.getExpireTime();
        LocalDateTime now = LocalDateTime.now();
        if(expireTime!=null && now.isAfter(expireTime)){
            return null;
        }

        return lesson.getId();
    }

    @Override
    public LearningLessonVO queryLessonByCourseId(Long courseId) {
        Long userId = UserContext.getUser();

        LearningLesson lesson = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();
        if(lesson==null){
            return null;
        }

        LearningLessonVO lessonVO = new LearningLessonVO();
        BeanUtils.copyProperties(lesson, lessonVO);
        return lessonVO;
    }

    @Override
    public void createLearningPlan(LearningPlanDTO learningPlanDTO) {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询课表中的指定课程有关的数据
        LearningLesson lesson = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, learningPlanDTO.getCourseId())
                .one();
        if(lesson==null){
            throw new BizIllegalException("Course information not exist");
        }
        // 3.修改数据
        this.lambdaUpdate()
                .set(LearningLesson::getWeekFreq, learningPlanDTO.getFreq())
                .set(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)
                .eq(LearningLesson::getId, lesson.getId())
                .update();
    }
<<<<<<< HEAD


    @Override
    public LearningPlanPageVO queryMyPlans(PageQuery query) {
        //1.获取当前登录用户id
        Long userId = UserContext.getUser();
        //todo 2.查询积分

        //3.查询本周学习计划总数据 learning_lesson 条件userId status in(0,1) plan_status=1 ,查询sum(week_freq)

        // 方法1 mybatis查询
        //  Integer num = learningLessonMapper.queryCurrentWeekAllLearningNum(userId);

        //方法2 mybatis-plus
        QueryWrapper<LearningLesson> wrapper = new QueryWrapper<>();
        wrapper.select("SUM(week_freq) as num");//查询哪些列
        wrapper.eq("user_id",userId);
        wrapper.eq("plan_status",PlanStatus.PLAN_RUNNING);//计划正在进行
        wrapper.in("status",LessonStatus.NOT_BEGIN,LessonStatus.LEARNING);//课程未开始学习和正在学习
        Map<String,Object> map = this.getMap(wrapper);
        Integer weekTotalPlan = 0;
        if (map != null && map.get("num") != null){
            weekTotalPlan = Integer.valueOf(map.get("num").toString());
        }
        //4.查询本周已学习的计划总数
        //SELECT COUNT(*) FROM learning_record
        //WHERE user_id = 2
        //AND finished = 1
        //AND finish_time BETWEEN '2022-07-19 10:45:55' AND '2023-07-19 10:45:55';
        //获取本周起始时间
        LocalDate now = LocalDate.now();
        LocalDateTime begin = DateUtils.getWeekBeginTime(now);
        LocalDateTime end = DateUtils.getWeekEndTime(now);
        Integer weekFinished = learningRecordMapper.selectCount(Wrappers.<LearningRecord>lambdaQuery()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished, true)
                .between(LearningRecord::getFinishTime, begin, end));
        //5.查询课表数据
        Page<LearningLesson> page = this.lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)//计划进行中
                .in(LearningLesson::getStatus, LessonStatus.NOT_BEGIN, LessonStatus.LEARNING)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if(CollUtils.isEmpty(records)){//如果查出课表数据为空，则返回空数据
            LearningPlanPageVO vo = new LearningPlanPageVO();
            vo.setTotal(0L);
            vo.setPages(0L);
            vo.setList(new ArrayList<>());
        }
        //6.远程调用课程服务 获取课程信息
        List<Long> ids = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toList());
        List<CourseSimpleInfoDTO> simpleInfoList = courseClient.getSimpleInfoList(ids);
        Map<Long, CourseSimpleInfoDTO> cInfoMap = simpleInfoList.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        if(CollUtils.isEmpty(simpleInfoList)){
            throw new BizIllegalException("课程不存在");
        }
        //7.本周当前用户下每一门课下已学习的小节数量
//        SELECT lesson_id,COUNT(*) FROM learning_record
//        WHERE user_id = 2
//        AND finished = 1
//        AND finish_time BETWEEN '2021-10-19 10:45:55' AND '2023-10-19 10:45:55'
//        GROUP BY lesson_id
        QueryWrapper<LearningRecord> learningRecordQueryWrapper = new QueryWrapper<>();
        learningRecordQueryWrapper.select("lesson_id as lessonId,count(*) as moment");
        learningRecordQueryWrapper.eq("user_id",userId);
        learningRecordQueryWrapper.eq("finished", true);
        learningRecordQueryWrapper.between("finish_time",begin,end);
        learningRecordQueryWrapper.groupBy("lesson_id");
        List<LearningRecord> learningRecords = learningRecordMapper.selectList(learningRecordQueryWrapper);
        Map<Long, Integer> maps = learningRecords.stream().collect(Collectors.toMap(LearningRecord::getLessonId, LearningRecord::getMoment));

        //8.封装vo返回
        LearningPlanPageVO learningPlanPageVO = new LearningPlanPageVO();
        learningPlanPageVO.setWeekFinished(weekFinished);
        learningPlanPageVO.setWeekTotalPlan(weekTotalPlan);
        learningPlanPageVO.setWeekPoints(999);
        List<LearningPlanVO> list = new ArrayList<>();
        for(LearningLesson record:records){
            LearningPlanVO vo = com.tianji.common.utils.BeanUtils.copyBean(record,LearningPlanVO.class);
            CourseSimpleInfoDTO infoDTO = cInfoMap.get(record.getCourseId());
            if(infoDTO != null){
                vo.setCourseName(infoDTO.getName());
                vo.setSections(infoDTO.getSectionNum());
            }
            //如果map为空说明该课程没有学习记录 则置为0
            vo.setWeekLearnedSections(CollUtils.isEmpty(maps) ? 0 : maps.get(record.getId()));
            list.add(vo);
        }
        learningPlanPageVO.setList(list);
        learningPlanPageVO.setTotal(page.getTotal());
        learningPlanPageVO.setPages(page.getPages());
        return learningPlanPageVO;
    }
=======
>>>>>>> 499f842726ec189e68c9ab32fff5001dc8521e94
}
