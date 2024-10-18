package com.tianji.learning.controller;

import com.tianji.learning.domain.vo.SignResultVO;
import com.tianji.learning.service.SignRecordService;
import com.tianji.learning.service.SignRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Sign in related interface")
@RestController
@RequestMapping("sign-records")
@RequiredArgsConstructor
public class SignRecordsController {

    private final SignRecordService recordService;

    @PostMapping
    @ApiOperation("Sign in")
    public SignResultVO addSignRecords(){
        return recordService.addSignRecords();
    }
}
