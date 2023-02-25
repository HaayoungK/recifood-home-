package com.ezen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ezen.entity.QRecipe;
import com.ezen.entity.Recipe;
import com.ezen.entity.RecipeProcedure;
import com.ezen.dto.Search;
import com.ezen.persistence.RecipeProcedureRepository;
import com.ezen.persistence.RecipeRepository;
import com.querydsl.core.BooleanBuilder;

@Service
public class RecipeServiceImpl implements RecipeService {

	@Autowired
	private RecipeRepository recipeRepo;
	@Autowired
	private RecipeProcedureRepository recipeProcedureRepo;
	@Autowired
	private RecipeProcedureService recipeProcedureService;
	
	@Override
	public void insertRecipe(Recipe recipe, List<RecipeProcedure> listProcedure) {
		
		// 레시피를 레포지토리에 저장
    	Recipe result = recipeRepo.saveAndFlush(recipe);
    	
    	// 레시피 과정 저장
    	for(RecipeProcedure recipeProcedure : listProcedure) {
    		recipeProcedure.setRecipe(result);
    		recipeProcedureRepo.save(recipeProcedure);
    	}
	}

	@Override
	public void updateRecipe(Recipe recipe, List<RecipeProcedure> listProcedure) {
		
		// 레시피를 레포지토리에 저장
    	recipeRepo.save(recipe);
    	
    	// 기존 레시피 과정 삭제
    	recipeProcedureService.deleteRecipeProcedure(recipe.getRecipe_seq());
    	
    	// 레시피 과정 수정 저장
    	for(RecipeProcedure recipeProcedure : listProcedure) {
    		recipeProcedure.setRecipe(recipe);
    		recipeProcedureRepo.save(recipeProcedure);
    	}
	}
	@Override
	public void updateGood(Recipe recipe) {
		Recipe findRecipe = recipeRepo.findById(recipe.getRecipe_seq()).get();
		findRecipe.setGood(recipe.getGood());

		recipeRepo.save(findRecipe);
	}
	@Override
	public void deleteRecipe(Recipe recipe) {
		recipeRepo.deleteById(recipe.getRecipe_seq());
	}

	@Override
	public Page<Recipe> getRecipeList(String username, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "recipe_seq");
		return recipeRepo.getRecipeList(username, pageable);
	}

	@Override
	public Page<Recipe> getRecipeListByKind(String username, String kind, int page) {
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "recipe_seq");
		return recipeRepo.getRecipeListByKind(username, kind, pageable);
	}

	@Override
	public Recipe getRecipe(Recipe recipe) {
		return recipeRepo.findById(recipe.getRecipe_seq()).get();
	}

	@Override
	public Page<Recipe> getAllRecipeList(Recipe recipe, int page) {
		Pageable pageable = PageRequest.of(page-1, 6, Sort.Direction.DESC, "recipe_seq");
		return recipeRepo.getAllRecipeList(recipe, pageable);
	}

	@Override
	public Page<Recipe> getAllRecipeListByKind(String kind, int page) {
		Pageable pageable = PageRequest.of(page-1, 6, Sort.Direction.DESC, "recipe_seq");
		return recipeRepo.getAllRecipeListByKind(kind, pageable);
	}

	@Override
	public Page<Recipe> getRecipeListDESC(Recipe recipe, int page) {
		Pageable pageable = PageRequest.of(page-1, 6);
		return recipeRepo.getRecipeListDESC(recipe, pageable);
	}

	@Override
	public Page<Recipe> getRecipeListGood(Recipe recipe, int page) {
		Pageable pageable = PageRequest.of(page-1, 6);
		return recipeRepo.getRecipeListGood(recipe, pageable);
	}

	@Override
	public List<Recipe> getBestRecipeList(Recipe recipe) {
		return recipeRepo.getBestRecipeList(recipe);
	}

	@Override
	public Page<Recipe> getRecipeList(int page, Search search) {
		BooleanBuilder builder = new BooleanBuilder();

		QRecipe qRecipe = QRecipe.recipe;

		if(search.getSearchCondition().equals("USERNAME")) {
			builder.and(qRecipe.member.username.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("RECIPE_NAME")) {
			builder.and(qRecipe.recipe_name.like("%" + search.getSearchKeyword() + "%"));
		}
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "regdate");
		return recipeRepo.findAll(builder, pageable);
	}
}
