package com.tianji.promotion.service.impl;

import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.CouponScope;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.service.ICouponScopeService;
import com.tianji.promotion.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 服务实现类
 * </p>
 *
 * @author Mike
 * @since 2024-10-29
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    private final ICouponScopeService couponScopeService;

    @Override
    public void saveCoupon(CouponFormDTO couponFormDTO) {

        Coupon coupon = BeanUtils.copyBean(couponFormDTO, Coupon.class);
        this.save(coupon);

        if(!couponFormDTO.getSpecific()){
            return;
        }
        List<Long> scopes = couponFormDTO.getScopes();
        if(CollUtils.isEmpty(scopes)){
            throw new BadRequestException("Coupon scopes cannot be empty");
        }

        List<CouponScope> csList = new ArrayList<>();
        for (Long scope : scopes) {
            CouponScope couponScope = new CouponScope();
            couponScope.setCouponId(coupon.getId());
            couponScope.setType(1);
            couponScope.setBizId(scope);
            csList.add(couponScope);
        }
        couponScopeService.saveBatch(csList);
    }
}
