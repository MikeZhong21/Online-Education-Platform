package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.ReplyDTO;
import com.tianji.learning.domain.query.ReplyPageQuery;
import com.tianji.learning.domain.vo.ReplyVO;
import com.tianji.learning.service.IInteractionReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 互动问题的回答或评论 前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-10-05
 */
@RestController
@Api(tags = "互动问答回复相关接口")
@RequestMapping("/replies")
@RequiredArgsConstructor
public class InteractionReplyController {
    private final IInteractionReplyService interactionReplyService;
    @ApiOperation("Submit reply or comment")
    @PostMapping
    public void addNewComment(@RequestBody @Validated ReplyDTO replyDTO){
        interactionReplyService.addNewComment(replyDTO);
    }

    @ApiOperation("Page query reply and comment list")
    @GetMapping("/page")
    public PageDTO<ReplyVO> queryComment(ReplyPageQuery replyPageQuery){
        return interactionReplyService.queryComment(replyPageQuery);
    }
}
