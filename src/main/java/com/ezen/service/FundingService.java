package com.ezen.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ezen.entity.Funding;
import com.ezen.dto.Search;

public interface FundingService {
	void insertFunding(Funding funding);
	
	void updateFunding(Funding funding);
	
	void deleteFunding(Funding funding);
	
	Page<Funding> getAllFundingList(Funding funding, int page);
	
	List<Funding> getAllFundingListCart(Funding funding);
	
	Page<Funding> getAllFundingListByKind(String kind, int page);
	
	Page<Funding> getAllFundingListByPrice(Funding funding, int page);
	
	Page<Funding> getAllFundingListByVC(Funding funding, int page);
	
	Page<Funding> getMyFundingList(String username, int page);
	
	Page<Funding> getMyFundingListByKind(String username, String kind, int page);
	
	Funding getFunding(Funding funding);
	
	void updateResult(Long recipe_seq);
	
	void updateResultTwo(Long recipe_seq);
	
	int updateViewCount(Long recipe_seq);
	
	List<Funding> getBestFundingList(Funding funding);
	
	Page<Funding> getFundingList(int page, Search search);
}
