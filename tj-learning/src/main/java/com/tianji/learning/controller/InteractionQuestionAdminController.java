package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.query.QuestionPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 互动提问的问题表 前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-10-05
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "Interaction Q&A admin related interface")
@RequestMapping("/admin/questions")
public class InteractionQuestionAdminController {

    private final IInteractionQuestionService iInteractionQuestionService;

    @ApiOperation("Page Query question -- admin side")
    @GetMapping("page")
    public PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query){
        return iInteractionQuestionService.queryQuestionPageAdmin(query);
    }
}
