package com.ezen.service;

import com.ezen.entity.Purchase;
import com.ezen.entity.QPurchase;
import com.ezen.dto.Search;
import com.ezen.persistence.PurchaseRepository;
import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional // 메서드가 포함하고 있는 작업 중에 하나라도 실패할 경우 전체 작업을 취소한다.
    @Override
    public void insertPurchase(Purchase purchase) {
        if(purchase.getP_comment().isBlank()) {
            purchase.setP_comment("없음");
        }
        if(purchase.getPayment() == 1) {
            purchase.setPayer("없음");
        }
        purchase.setResult(1);
        purchaseRepository.save(purchase);
    }

    @Override
    public void updatePurchase(Purchase purchase) {

    }

    @Override
    public void deletePurchase(Purchase purchase) {

    }

    @Override
    public Purchase getPurchase(Purchase purchase) {

        return purchaseRepository.findById(purchase.getPurchaseSeq()).get();
    }

    @Override
    public Page<Purchase> findByMember_Username(int page, String username) {

        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "purchaseSeq");
        return purchaseRepository.findAllByMember_Username(username, pageable);
    }

    @Override
    public Page<Purchase> getPurchaseList(int page, Search search) {
        BooleanBuilder builder = new BooleanBuilder();

        QPurchase qPurchase = QPurchase.purchase;

        if(search.getSearchCondition().equals("USERNAME")) {
            builder.and(qPurchase.member.username.like("%" + search.getSearchKeyword() + "%"));
        } else if (search.getSearchCondition().equals("FUNDING_NAME")) {
            builder.and(qPurchase.funding.funding_name.like("%" + search.getSearchKeyword() + "%"));
        }
        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "purchaseSeq");
        return purchaseRepository.findAll(builder, pageable);
    }
}
