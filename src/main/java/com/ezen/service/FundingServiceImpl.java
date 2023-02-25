package com.ezen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ezen.entity.Funding;
import com.ezen.entity.QFunding;
import com.ezen.dto.Search;
import com.ezen.persistence.FundingRepository;
import com.querydsl.core.BooleanBuilder;

@Service
public class FundingServiceImpl implements FundingService {

	@Autowired
	private FundingRepository fundingRepo;
	
	@Override
	public void insertFunding(Funding funding) {
		fundingRepo.save(funding);
	}

	@Override
	public void updateFunding(Funding funding) {
		Funding findFunding = fundingRepo.findById(funding.getFunding_seq()).get();
		findFunding.setKind(funding.getKind());
		findFunding.setFunding_name(funding.getFunding_name());
		findFunding.setFunding_subname(funding.getFunding_subname());
		findFunding.setContent(funding.getContent());
		findFunding.setPrice(funding.getPrice());
		findFunding.setStartdate(funding.getStartdate());
		findFunding.setEnddate(funding.getEnddate());
		findFunding.setFilename(funding.getFilename());
		findFunding.setFilepath(funding.getFilepath());
		
		fundingRepo.save(findFunding);
	}

	@Override
	public void deleteFunding(Funding funding) {
		fundingRepo.deleteById(funding.getFunding_seq());
	}

	@Override
	public Page<Funding> getAllFundingList(Funding funding, int page) {
		Pageable pageable = PageRequest.of(page-1, 6, Sort.Direction.DESC, "funding_seq");
		return fundingRepo.getAllFundingList(funding, pageable);
	}

	@Override
	public Page<Funding> getAllFundingListByKind(String kind, int page) {
		Pageable pageable = PageRequest.of(page-1, 6, Sort.Direction.DESC, "funding_seq");
		return fundingRepo.getAllFundingListByKind(kind, pageable);
	}

	@Override
	public Page<Funding> getMyFundingList(String username, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "funding_seq");
		return fundingRepo.getMyFundingList(username, pageable);
	}

	@Override
	public Page<Funding> getMyFundingListByKind(String username, String kind, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "funding_seq");
		return fundingRepo.getMyFundingListByKind(username, kind, pageable);
	}

	@Override
	public Page<Funding> getAllFundingListByPrice(Funding funding, int page) {
		Pageable pageable = PageRequest.of(page-1, 6);
		return fundingRepo.getAllFundingListByPrice(funding, pageable);
	}

	@Override
	public Page<Funding> getAllFundingListByVC(Funding funding, int page) {
		Pageable pageable = PageRequest.of(page-1, 6);
		return fundingRepo.getAllFundingListByVC(funding, pageable);
	}

	@Override
	public Funding getFunding(Funding funding) {
		return fundingRepo.findById(funding.getFunding_seq()).get();
	}

	@Override
	public void updateResult(Long recipe_seq) {
		fundingRepo.updateResult(recipe_seq);
	}

	@Override
	public void updateResultTwo(Long recipe_seq) {
		fundingRepo.updateResultTwo(recipe_seq);
	}
	
	@Override
	public int updateViewCount(Long funding_seq) {
		return fundingRepo.updateViewCount(funding_seq);
	}

	@Override
	public List<Funding> getBestFundingList(Funding funding) {
		return fundingRepo.getBestFundingList(funding);
	}

	@Override
	public List<Funding> getAllFundingListCart(Funding funding) {
		return fundingRepo.findAll();
	}

	@Override
	public Page<Funding> getFundingList(int page, Search search) {
		BooleanBuilder builder = new BooleanBuilder();

		QFunding qFunding = QFunding.funding;

		if(search.getSearchCondition().equals("USERNAME")) {
			builder.and(qFunding.member.username.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("FUNDING_NAME")) {
			builder.and(qFunding.funding_name.like("%" + search.getSearchKeyword() + "%"));
		} 
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "regdate");
		return fundingRepo.findAll(builder, pageable);
	}
}
