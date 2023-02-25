package com.ezen.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ezen.entity.Recipe;
import com.ezen.entity.RecipeProcedure;
import com.ezen.dto.Search;

public interface RecipeService {
	void insertRecipe(Recipe recipe, List<RecipeProcedure> listProcedure);
	
	void updateRecipe(Recipe recipe, List<RecipeProcedure> listProcedure);

	void updateGood(Recipe recipe);

	void deleteRecipe(Recipe recipe);
	
	Page<Recipe> getRecipeList(String username, int page);
	
	// 카테고리별 나의 레시피 목록 조회
	Page<Recipe> getRecipeListByKind(String username, String kind, int page);
	
	Recipe getRecipe(Recipe recipe);
	
	// 전체 레시피 조회
	Page<Recipe> getAllRecipeList(Recipe recipe, int page);
	
	// 카테고리별 전체 레시피 목록 조회
	Page<Recipe> getAllRecipeListByKind(String kind, int page);
	
	// 최신순 목록 조회
	Page<Recipe> getRecipeListDESC(Recipe recipe, int page);
	
	// 인기순 목록 조회
	Page<Recipe> getRecipeListGood(Recipe recipe, int page);
	
	List<Recipe> getBestRecipeList(Recipe recipe);
	
	Page<Recipe> getRecipeList(int page, Search search);
}
