package com.tianji.promotion.controller;


import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.service.ICouponService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public void saveCoupon(@RequestBody @Validated CouponFormDTO couponFormDTO) {
        couponService.saveCoupon(couponFormDTO);
    }
}
