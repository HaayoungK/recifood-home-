package com.ezen.service;

import org.springframework.data.domain.Page;

import com.ezen.entity.Food;
import com.ezen.dto.Search;

public interface FoodService {
	void insertFood(Food food);
	
	void updateFood(Food food);
	
	void deleteFood(Food food);
	
	Page<Food> getFoodList(String username, int page);
	
	Food getFood(Food food);
	
	Page<Food> getFoodListUsernameAndCategory(String username, String category, int page);
	
	Page<Food> getFoodListUsernameOrderByExpDESC(String username, int page);
	
	Page<Food> getFoodListUsernameOrderByExpASC(String username, int page);
	
	Page<Food> getFoodList(int page, Search search);
}
