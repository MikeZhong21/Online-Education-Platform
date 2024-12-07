package com.tianji.promotion.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.dto.CouponIssueFormDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.CouponScope;
import com.tianji.promotion.domain.po.UserCoupon;
import com.tianji.promotion.domain.query.CouponQuery;
import com.tianji.promotion.domain.vo.CouponPageVO;
import com.tianji.promotion.domain.vo.CouponVO;
import com.tianji.promotion.enums.CouponStatus;
import com.tianji.promotion.enums.ObtainType;
import com.tianji.promotion.enums.UserCouponStatus;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.service.ICouponScopeService;
import com.tianji.promotion.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.promotion.service.IExchangeCodeService;
import com.tianji.promotion.service.IUserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.tianji.promotion.enums.CouponStatus.*;

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

    private final IExchangeCodeService codeService;

    private final IUserCouponService userCouponService;

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

    @Override
    public PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query) {
        Integer status = query.getStatus();
        String name = query.getName();
        Integer type = query.getType();
        // 1.分页查询
        Page<Coupon> page = lambdaQuery()
                .eq(type != null, Coupon::getDiscountType, type)
                .eq(status != null, Coupon::getStatus, status)
                .like(StringUtils.isNotBlank(name), Coupon::getName, name)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        // 2.处理VO
        List<Coupon> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        List<CouponPageVO> list = BeanUtils.copyList(records, CouponPageVO.class);
        // 3.返回
        return PageDTO.of(page, list);
    }

    @Transactional
    @Override
    public void beginIssue(CouponIssueFormDTO dto) {
        // 1.查询优惠券
        Coupon coupon = getById(dto.getId());
        if (coupon == null) {
            throw new BadRequestException("Coupon not exist!");
        }
        // 2.判断优惠券状态，是否是暂停或待发放
        if(coupon.getStatus() != CouponStatus.DRAFT && coupon.getStatus() != CouponStatus.PAUSE){
            throw new BizIllegalException("Coupon status error!");
        }
        // 3.判断是否是立刻发放
        LocalDateTime issueBeginTime = dto.getIssueBeginTime();
        LocalDateTime now = LocalDateTime.now();
        boolean isBegin = issueBeginTime == null || !issueBeginTime.isAfter(now);
        // 4.更新优惠券
        // 4.1.拷贝属性到PO
        Coupon c = BeanUtils.copyBean(dto, Coupon.class);
        // 4.2.更新状态
        if (isBegin) {
            c.setStatus(CouponStatus.ISSUING);
            c.setIssueBeginTime(now);
        }else{
            c.setStatus(CouponStatus.UN_ISSUE);
        }
        // 4.3.写入数据库
        this.updateById(c);


        if(coupon.getObtainWay() == ObtainType.ISSUE && coupon.getStatus() == CouponStatus.DRAFT){
            coupon.setIssueEndTime(c.getIssueEndTime());
            codeService.asyncGenerateCode(coupon);
        }
    }


    @Override
    public List<CouponVO> queryIssuingCoupons() {

        List<Coupon> coupons = lambdaQuery()
                .eq(Coupon::getStatus, CouponStatus.ISSUING)
                .eq(Coupon::getObtainWay, ObtainType.PUBLIC)
                .list();
        if (CollUtils.isEmpty(coupons)) {
            return CollUtils.emptyList();
        }

        List<Long> couponIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        List<UserCoupon> userCoupons = userCouponService.lambdaQuery()
                .eq(UserCoupon::getUserId, UserContext.getUser())
                .in(UserCoupon::getCouponId, couponIds)
                .list();

        Map<Long, Long> issuedMap = userCoupons.stream()
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));

        Map<Long, Long> unusedMap = userCoupons.stream()
                .filter(uc -> uc.getStatus() == UserCouponStatus.UNUSED)
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));

        List<CouponVO> list = new ArrayList<>(coupons.size());
        for (Coupon c : coupons) {

            CouponVO vo = BeanUtils.copyBean(c, CouponVO.class);
            list.add(vo);

            vo.setAvailable(
                    c.getIssueNum() < c.getTotalNum()
                            && issuedMap.getOrDefault(c.getId(), 0L) < c.getUserLimit()
            );

            vo.setReceived(unusedMap.getOrDefault(c.getId(),  0L) > 0);
        }
        return list;
    }
}
