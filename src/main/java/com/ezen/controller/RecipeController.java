package com.ezen.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ezen.entity.Heart;
import com.ezen.entity.Member;
import com.ezen.entity.Recipe;
import com.ezen.entity.RecipeProcedure;
import com.ezen.entity.RecipeReply;
import com.ezen.dto.Search;
import com.ezen.service.HeartService;
import com.ezen.service.RecipeProcedureService;
import com.ezen.service.RecipeReplyService;
import com.ezen.service.RecipeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RecipeController {
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private RecipeReplyService recipeReplyService;
	@Autowired
	private HeartService heartService;
	@Autowired
	private RecipeProcedureService recipeProcedureService;
	
	@GetMapping("/recipeList")
	public String recipeList(@RequestParam(value="page", defaultValue="1") int page, Recipe recipe, Model model) {
		Page<Recipe> rp = recipeService.getAllRecipeList(recipe, page);
		model.addAttribute("recipe", rp);
		return "recipe/recipeList";
	}
	
	@GetMapping("/recipeListKind")
	public String recipeListByKind(@RequestParam(value="page", defaultValue="1") int page, 
									@RequestParam(value = "kind", defaultValue = "") String kind,
									Recipe recipe, Model model) {
		Page<Recipe> rp = recipeService.getAllRecipeListByKind(recipe.getKind(), page);
		model.addAttribute("recipe", rp);
		return "recipe/recipeListKind";
	}
	
	@GetMapping("/recipeListDESC")
	public String recipeListDESC(@RequestParam(value="page", defaultValue="1") int page, Recipe recipe, Model model) {
		Page<Recipe> rp = recipeService.getRecipeListDESC(recipe, page);
		model.addAttribute("recipe", rp);
		return "recipe/recipeListDESC";
	}
	
	@GetMapping("/recipeListGood")
	public String recipeListGood(@RequestParam(value="page", defaultValue="1") int page, Recipe recipe, Model model) {
		Page<Recipe> rp = recipeService.getRecipeListGood(recipe, page);
		model.addAttribute("recipe", rp);
		return "recipe/recipeListGood";
	}
	
	
	@GetMapping("/getRecipe")
	public String getRecipe(Recipe recipe, Model model, RecipeReply recipeReply, Heart heart, HttpSession session, RecipeProcedure recipeProcedure) {
		Recipe rp = recipeService.getRecipe(recipe);
		recipeReply.setRecipe(rp);
		List<RecipeReply> rrp = recipeReplyService.getRecipeReplyList(recipeReply);
		//recipeReplyService.replyCount(recipeReply.getRecipe().getRecipe_seq());
		model.addAttribute("recipe", rp);
		model.addAttribute("recipeReply", rrp);
		model.addAttribute("cnt", recipeReplyService.replyCount(recipeReply.getRecipe().getRecipe_seq())); // ?????? ??? ??????
		model.addAttribute("rp", recipeProcedureService.getRecipeProcedureList(recipe.getRecipe_seq()));
		
		Member loginMember = (Member) session.getAttribute("loginMember");

		if(loginMember != null) { // ???????????? ????????? ????????? ?????? ????????? ????????? ????????????
			heart.setRecipe(rp);
			heart.setMember(loginMember);
			model.addAttribute("heart", heartService.getHeart(heart));
		}
		return "recipe/getRecipe";
		
	}
	
	// ?????? ????????? ?????? ??????
	@GetMapping("/myRecipeList")
	public String myRecipeList(@RequestParam(value="page", defaultValue="1") int page, HttpSession session, Model model, Recipe recipe) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			recipe.setMember(loginMember);
			Page<Recipe> recipeList = recipeService.getRecipeList(recipe.getMember().getUsername(), page);
			model.addAttribute("recipe", recipeList);
			return "recipe/myRecipeList";
		}
	}
	
	// ??????????????? ????????? ?????? ??????
	@GetMapping("/getMyRecipeListKind")
	public String myRecipeListKind(@RequestParam(value="page", defaultValue="1") int page, 
									@RequestParam(value = "kind", defaultValue = "") String kind,
									HttpSession session, Model model, Recipe recipe) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			recipe.setMember(loginMember);
			Page<Recipe> recipeList = recipeService.getRecipeListByKind(recipe.getMember().getUsername(), recipe.getKind(), page);
			model.addAttribute("recipe", recipeList);
			return "recipe/myRecipeListKind";
		}
	}
	
	// ????????? ?????? ??????
	@GetMapping("/getMyRecipe")
	public String getMyRecipe(HttpSession session, Model model, Recipe recipe, RecipeReply recipeReply, RecipeProcedure recipeProcedure) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			recipe.setMember(loginMember);
			Recipe rp = recipeService.getRecipe(recipe);
			recipeReply.setRecipe(rp);
			List<RecipeReply> rrp = recipeReplyService.getRecipeReplyList(recipeReply);
			model.addAttribute("recipe", rp);
			model.addAttribute("recipeReply", rrp);
			model.addAttribute("cnt", recipeReplyService.replyCount(recipeReply.getRecipe().getRecipe_seq())); // ?????? ??? ??????
			model.addAttribute("rp", recipeProcedureService.getRecipeProcedureList(recipe.getRecipe_seq()));
			return "recipe/getMyRecipe";
		}
	}
	
	@GetMapping("/insertRecipe")
	public String insertRecipe() {
		
		return "recipe/insertRecipe";
	}
	
	@PostMapping(value="/insertRecipe", consumes= {MediaType.MULTIPART_FORM_DATA_VALUE}) 
	public String insertRecipe(
			@RequestPart(value="recipe_img") MultipartFile recipe_img,
			@RequestPart(value="kind") String kind,
			@RequestPart(value="recipe_name") String recipe_name,
			@RequestPart(value="content") String content,
			@RequestPart(value="cooking_time") String cooking_time,
			@RequestPart(value="amount") String amount,
			@RequestPart(value="degree") String degree,
			@RequestPart(value="ingredient") String ingredient,
			@RequestPart(value="process") String desc,
			@RequestPart(value="file", required=false) List<MultipartFile> fileList,
			HttpSession session, RecipeProcedure recipeProcedure, MultipartFile file) throws Exception {
		
		Member loginMember = (Member)session.getAttribute("loginMember");
		File saveFile = null;
		String fileName = null;
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			System.out.println("kind=" + kind);
			System.out.println("recipe_name=" + recipe_name);
			System.out.println("content=" + content);
			System.out.println("cooking_time=" + cooking_time);
			System.out.println("amount=" + amount);
			System.out.println("degree=" + degree);
			System.out.println("ingredient=" + ingredient);
			
			String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\image"; // ?????? ?????? ??????
			
			// ????????? ?????? ??????
	    	// -- ????????? ????????? ??????
	    	UUID uuid = UUID.randomUUID(); // ?????????, ???????????? ?????? ??????
	    	fileName = uuid + "_" + recipe_img.getOriginalFilename(); // ???????????????_?????????????????? = ????????? ???????????? ??????
	    	saveFile = new File(projectPath, fileName); // ?????? name, projectPath ????????? ??????	
	    	recipe_img.transferTo(saveFile);
			
			Recipe recipe = new Recipe();
			recipe.setFilename(fileName); // DB??? ?????? ??????
	    	recipe.setFilepath("/files/" + fileName); // ?????? ??????
	    	recipe.setMember(loginMember);
	    	recipe.setKind(kind);
	    	recipe.setRecipe_name(recipe_name);
	    	recipe.setContent(content);
	    	recipe.setCooking_time(Integer.parseInt(cooking_time));
	    	recipe.setAmount(Integer.parseInt(amount));
	    	recipe.setDegree(degree);
	    	recipe.setIngredient(ingredient);
	    	
	    	// ?????? ??????(Recipe Procedure) ?????? ?????????
	    	List<RecipeProcedure> listRecipeProcedure = new ArrayList<>();
	    	
	    	System.out.println(desc);
	    	String[] remark = desc.split("@");
	    	
	    	File tempFile = null;
	    	String processFileName = null;
	    	for(int i=0; i<remark.length; i++) {
	    		System.out.println(remark[i]);
	    		
	    		uuid = UUID.randomUUID(); // ?????????, ???????????? ?????? ??????
	    		MultipartFile multiFile = fileList.get(i);
	    		if (fileList.get(i) != null) {
	    			processFileName = uuid + "_" + multiFile.getOriginalFilename();
	    			tempFile = new File(projectPath, processFileName); // ?????? name, projectPath ????????? ??????
	    	    	
	    	    	multiFile.transferTo(tempFile);
	    		}
	    		
	    		RecipeProcedure rProcedure = new RecipeProcedure();
	    		rProcedure = RecipeProcedure.builder()
				    			.member(loginMember)
				    			.procedure(remark[i])
				    			.filename(processFileName)
				    			.filepath(projectPath)
				    			.build();
				
				listRecipeProcedure.add(rProcedure);
	    	}
	    	
			recipeService.insertRecipe(recipe, listRecipeProcedure);
   	
			System.out.println("????????????");

	    	return "redirect:myRecipeList";
	    	
		}
	}
	
	@GetMapping("/updateRecipe")
	public String updateRecipeView(HttpSession session, Recipe recipe, Model model, RecipeProcedure recipeProcedure) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			recipe.setMember(loginMember);
			Recipe rp = recipeService.getRecipe(recipe);
			model.addAttribute("recipe", rp);
			model.addAttribute("rp", recipeProcedureService.getRecipeProcedureList(recipe.getRecipe_seq()));
			
			return "recipe/updateRecipe";
		}
	}
	
	
	@PostMapping(value="/updateRecipe", consumes= {MediaType.MULTIPART_FORM_DATA_VALUE})
	public String updateRecipe(
			@RequestPart(value="recipe_img") MultipartFile recipe_img,
			@RequestPart(value="recipe_seq") String recipe_seq,
			@RequestPart(value="kind") String kind,
			@RequestPart(value="recipe_name") String recipe_name,
			@RequestPart(value="content") String content,
			@RequestPart(value="cooking_time") String cooking_time,
			@RequestPart(value="amount") String amount,
			@RequestPart(value="degree") String degree,
			@RequestPart(value="ingredient") String ingredient,
			@RequestPart(value="process") String desc,
			@RequestPart(value="file", required=false) List<MultipartFile> fileList,
			HttpSession session, RecipeProcedure recipeProcedure, MultipartFile file) throws Exception {
		
		Member loginMember = (Member)session.getAttribute("loginMember");
		File saveFile = null;
		String fileName = null;
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			System.out.println("recipe_seq=" + recipe_seq);
			System.out.println("kind=" + kind);
			System.out.println("recipe_name=" + recipe_name);
			System.out.println("content=" + content);
			System.out.println("cooking_time=" + cooking_time);
			System.out.println("amount=" + amount);
			System.out.println("degree=" + degree);
			System.out.println("ingredient=" + ingredient);
			
			String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\image"; // ?????? ?????? ??????
			
			// ????????? ?????? ??????
	    	// -- ????????? ????????? ??????
	    	UUID uuid = UUID.randomUUID(); // ?????????, ???????????? ?????? ??????
	    	fileName = uuid + "_" + recipe_img.getOriginalFilename(); // ???????????????_?????????????????? = ????????? ???????????? ??????
	    	saveFile = new File(projectPath, fileName); // ?????? name, projectPath ????????? ??????	
	    	recipe_img.transferTo(saveFile);
			
			Recipe recipe = new Recipe();
			recipe.setRecipe_seq(Long.parseLong(recipe_seq));
			recipe.setFilename(fileName); // DB??? ?????? ??????
	    	recipe.setFilepath("/files/" + fileName); // ?????? ??????
	    	recipe.setMember(loginMember);
	    	recipe.setKind(kind);
	    	recipe.setRecipe_name(recipe_name);
	    	recipe.setContent(content);
	    	recipe.setCooking_time(Integer.parseInt(cooking_time));
	    	recipe.setAmount(Integer.parseInt(amount));
	    	recipe.setDegree(degree);
	    	recipe.setIngredient(ingredient);
	    	
	    	// ?????? ??????(Recipe Procedure) ?????? ?????????
	    	List<RecipeProcedure> listRecipeProcedure = new ArrayList<>();
	    	
	    	System.out.println(desc);
	    	String[] remark = desc.split("@");
	    	
	    	File tempFile = null;
	    	String processFileName = null;
	    	for(int i=0; i<remark.length; i++) {
	    		System.out.println(remark[i]);
	    		
	    		uuid = UUID.randomUUID(); // ?????????, ???????????? ?????? ??????
	    		MultipartFile multiFile = fileList.get(i);
	    		if (fileList.get(i) != null) {
	    			processFileName = uuid + "_" + multiFile.getOriginalFilename();
	    			tempFile = new File(projectPath, processFileName); // ?????? name, projectPath ????????? ??????
	    	    	
	    	    	multiFile.transferTo(tempFile);
	    		}
	    		
	    		RecipeProcedure rProcedure = new RecipeProcedure();
	    		rProcedure = RecipeProcedure.builder()
				    			.member(loginMember)
				    			.procedure(remark[i])
				    			.filename(processFileName)
				    			.filepath(projectPath)
				    			.recipe(recipe)
				    			.build();
				
				listRecipeProcedure.add(rProcedure);
	    	}
	    	
			recipeService.updateRecipe(recipe, listRecipeProcedure);

			System.out.println("????????????");

	    	return "redirect:myRecipeList";
	    	
		}
	}
	
	@GetMapping("/deleteRecipe")
	public String deleteRecipe(HttpSession session, Recipe recipe) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			recipe.setMember(loginMember);
			recipeService.deleteRecipe(recipe);
			return "redirect:myRecipeList";
		}
	}

	@RequestMapping("/allRecipeList")
    public String allRecipeList(@RequestParam(value = "page", defaultValue = "1") int page, Search search, Model model) {
        if(search.getSearchCondition() == null) {
            search.setSearchCondition("USERNAME");
        }
        if(search.getSearchKeyword() == null) {
            search.setSearchKeyword("");
        }
        Page<Recipe> recipeList = recipeService.getRecipeList(page, search);

        model.addAttribute("recipeList", recipeList);

        return "admin/recipeList";
    }
}
