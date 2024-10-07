package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.query.QuestionPageQuery;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
@Api(tags = "Interaction Q&A related interface")
@RequestMapping("/questions")
public class InteractionQuestionController {

    private final IInteractionQuestionService iInteractionQuestionService;

    @ApiOperation("Add question")
    @PostMapping
    public void saveQuestion(@Validated @RequestBody QuestionFormDTO questionFormDTO){
        iInteractionQuestionService.saveQuestion(questionFormDTO);
    }

    @ApiOperation("Update question")
    @PutMapping("{id}")
    public void updateQuestion(@PathVariable Long id, @RequestBody QuestionFormDTO questionFormDTO){
        iInteractionQuestionService.updateQuestion(id, questionFormDTO);
    }

    @ApiOperation("Page query question -- user side")
    @GetMapping("/page")
    public PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery questionPageQuery){
        return iInteractionQuestionService.queryQuestionPage(questionPageQuery);
    }

}
