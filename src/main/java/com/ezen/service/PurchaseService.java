package com.ezen.service;

import com.ezen.entity.Purchase;
import com.ezen.dto.Search;
import org.springframework.data.domain.Page;

public interface PurchaseService {

    void insertPurchase(Purchase purchase); // 구매

    void updatePurchase(Purchase purchase);

    void deletePurchase(Purchase purchase);

    Purchase getPurchase(Purchase purchase);

    Page<Purchase> findByMember_Username(int page, String username);

    Page<Purchase> getPurchaseList(int page, Search search);

}
