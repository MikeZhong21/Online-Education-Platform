package com.tianji.promotion.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.dto.CouponIssueFormDTO;
import com.tianji.promotion.domain.query.CouponQuery;
import com.tianji.promotion.domain.vo.CouponPageVO;
import com.tianji.promotion.domain.vo.CouponVO;
import com.tianji.promotion.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 前端控制器
 * </p>
 *
 * @author Mike
 * @since 2024-10-29
 */
@RestController
@Api("Coupon related interface")
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final ICouponService couponService;

    @ApiOperation("add coupon")
    @PostMapping
    public void saveCoupon(@RequestBody @Validated CouponFormDTO couponFormDTO) {
        couponService.saveCoupon(couponFormDTO);
    }

    @ApiOperation("page query coupons")
    @GetMapping("/page")
    public PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query){
        return couponService.queryCouponByPage(query);
    }

    @ApiOperation("issue coupon")
    @PutMapping("/{id}/issue")
    public void beginIssue(@RequestBody @Valid CouponIssueFormDTO dto) {
        couponService.beginIssue(dto);
    }

    @ApiOperation("query issuing coupons")
    @GetMapping("/list")
    public List<CouponVO> queryIssuingCoupons(){
        return couponService.queryIssuingCoupons();
    }
}
