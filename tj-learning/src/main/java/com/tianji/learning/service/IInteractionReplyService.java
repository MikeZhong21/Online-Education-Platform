package com.tianji.learning.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.ReplyDTO;
import com.tianji.learning.domain.po.InteractionReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.query.ReplyPageQuery;
import com.tianji.learning.domain.vo.ReplyVO;

/**
 * <p>
 * 互动问题的回答或评论 服务类
 * </p>
 *
 * @author Mike
 * @since 2024-10-05
 */
public interface IInteractionReplyService extends IService<InteractionReply> {

    void addNewComment(ReplyDTO replyDTO);

    PageDTO<ReplyVO> queryComment(ReplyPageQuery replyPageQuery);
}
