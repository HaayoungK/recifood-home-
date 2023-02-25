package com.ezen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ezen.entity.Food;
import com.ezen.entity.QFood;
import com.ezen.dto.Search;
import com.ezen.persistence.FoodRepository;
import com.querydsl.core.BooleanBuilder;

@Service
public class FoodServiceImpl implements FoodService {

	@Autowired
	FoodRepository foodRepo;
	
	@Override
	public void insertFood(Food food) {
		foodRepo.save(food);
	}

	@Override
	public void updateFood(Food food) {
		Food findFood = foodRepo.findById(food.getFood_seq()).get();
		findFood.setCategory(food.getCategory());
		findFood.setName(food.getName());
		findFood.setExp(food.getExp());
		
		foodRepo.save(food);
	}

	@Override
	public void deleteFood(Food food) {
		foodRepo.deleteById(food.getFood_seq());
		
	}

	@Override
	public Page<Food> getFoodList(String username, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "food_seq");
		return foodRepo.getFoodList(username, pageable);
	}

	@Override
	public Food getFood(Food food) {
		return foodRepo.findById(food.getFood_seq()).get();
	}
	
	@Override
	public Page<Food> getFoodListUsernameAndCategory(String username, String category, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "food_seq");
		return foodRepo.getFoodListUsernameAndCategory(username, category, pageable);
	}

	
	@Override
	public Page<Food> getFoodListUsernameOrderByExpDESC(String username, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "food_seq");
		return foodRepo.getFoodListUsernameOrderByExpDESC(username, pageable);
	}

	@Override
	public Page<Food> getFoodListUsernameOrderByExpASC(String username, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "food_seq");
		return foodRepo.getFoodListUsernameOrderByExpASC(username, pageable);
	}
	
	@Override
	public Page<Food> getFoodList(int page, Search search) {
		BooleanBuilder builder = new BooleanBuilder();

		QFood qFood = QFood.food;

		if(search.getSearchCondition().equals("USERNAME")) {
			builder.and(qFood.member.username.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("CATEGORY")) {
			builder.and(qFood.category.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("NAME")) {
			builder.and(qFood.name.like("%" + search.getSearchKeyword() + "%"));
		}
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "regdate");
		return foodRepo.findAll(builder, pageable);
	}
}
