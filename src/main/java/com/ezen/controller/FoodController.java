package com.ezen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ezen.entity.Food;
import com.ezen.entity.Member;
import com.ezen.dto.Search;
import com.ezen.service.FoodService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FoodController {
	@Autowired
	private FoodService foodService;
	
	@GetMapping("/insertFood")
	public String insertFoodView() {
		return "food/insertFood";
	}
	

	@PostMapping("/insertFood")
	public String insertFood(Food food, HttpSession session) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			foodService.insertFood(food);
			return "redirect:foodList";
		}
	}
	
	/*
	@InitBinder // Date 입력 메소드
    protected void initBinder(WebDataBinder binder){
        DateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true));
    }
    */
	
	// 상품 전체 조회
	@GetMapping("/foodList")
	public String foodList(@RequestParam(value="page", defaultValue="1") int page, 
							HttpSession session, Model model, Food food) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		/*
		Member memberDto = new Member();
		memberDto.setUsername(loginMember.getUsername());
		
		food.setMember(memberDto);
		*/
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			Page<Food> foodList = foodService.getFoodList(food.getMember().getUsername(), page);
			model.addAttribute("food", foodList);
			return "food/foodList";
		}
	}
	
	@GetMapping("/getFood")
	public String getFood(HttpSession session, Food food, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			Food fd = foodService.getFood(food);
			model.addAttribute("food", fd);
			return "food/getFood";
		}
	}
	
	@PostMapping("/updateFood")
	public String updateFood(HttpSession session, Food food) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			foodService.updateFood(food);
			return "redirect:foodList";
		}
	}
	
	@GetMapping("/deleteFood")
	public String deleteFood(HttpSession session, Food food) {
		
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			foodService.deleteFood(food);
			return "redirect:foodList";
		}
	}
	
	// 카테고리별 상품 목록 조회
	@GetMapping("/getFoodByCategory")
	public String getFoodByCategory(@RequestParam(value="page", defaultValue="1") int page, 
									@RequestParam(value = "category", defaultValue = "") String kind,
									HttpSession session, Food food, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			Page<Food> foodList = foodService.getFoodListUsernameAndCategory(food.getMember().getUsername(), food.getCategory(), page);
			model.addAttribute("food", foodList);
			return "food/getFoodCate";
		}
	}
	
	// 유통기한 내림차순 목록 조회
	@GetMapping("/getFoodDESC")
	public String getFoodDESC(@RequestParam(value="page", defaultValue="1") int page, 
							HttpSession session, Food food, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			Page<Food> foodList = foodService.getFoodListUsernameOrderByExpDESC(food.getMember().getUsername(), page);
			model.addAttribute("food", foodList);
			return "food/getFoodDESC";
		}
	}
	
	// 유통기한 오름차순 목록 조회
	@GetMapping("/getFoodASC")
	public String getFoodASC(@RequestParam(value="page", defaultValue="1") int page, 
							HttpSession session, Food food, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			food.setMember(loginMember);
			Page<Food> foodList = foodService.getFoodListUsernameOrderByExpASC(food.getMember().getUsername(), page);
			model.addAttribute("food", foodList);
			return "food/getFoodASC";
		}
	}
	
	@RequestMapping("/allFoodList")
    public String allFoodList(@RequestParam(value = "page", defaultValue = "1") int page, Search search, Model model) {
        if(search.getSearchCondition() == null) {
            search.setSearchCondition("USERNAME");
        }
        if(search.getSearchKeyword() == null) {
            search.setSearchKeyword("");
        }
        Page<Food> foodList = foodService.getFoodList(page, search);

        model.addAttribute("foodList", foodList);

        return "admin/foodList";
    }
}
