package com.ezen.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ezen.entity.Funding;
import com.ezen.entity.Member;
import com.ezen.entity.Recipe;
import com.ezen.dto.Search;
import com.ezen.service.FundingService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class FundingController {
	@Autowired
	private FundingService fundingService;
	
	@GetMapping("/fundingList")
	public String fundingList(@RequestParam(value="page", defaultValue="1") int page, Funding funding, Model model) {
		Page<Funding> fundingList = fundingService.getAllFundingList(funding, page);
		model.addAttribute("funding", fundingList);
		return "funding/fundingList";
	}
	
	@GetMapping("/fundingListKind")
	public String fundingListKind(@RequestParam(value="page", defaultValue="1") int page, 
									@RequestParam(value = "kind", defaultValue = "") String kind,
									Funding funding, Model model) {
		Page<Funding> fundingList = fundingService.getAllFundingListByKind(funding.getKind(), page);
		model.addAttribute("funding", fundingList);
		return "funding/fundingListKind";
	}
	
	@GetMapping("/fundingListPrice")
	public String fundingListByPrice(@RequestParam(value="page", defaultValue="1") int page, Funding funding, Model model) {
		Page<Funding> fundingList = fundingService.getAllFundingListByPrice(funding, page);
		model.addAttribute("funding", fundingList);
		return "funding/fundingListPrice";
	}
	
	@GetMapping("/fundingListViewCount")
	public String fundingListViewCount(@RequestParam(value="page", defaultValue="1") int page, Funding funding, Model model) {
		Page<Funding> fundingList = fundingService.getAllFundingListByVC(funding, page);
		model.addAttribute("funding", fundingList);
		return "funding/fundingListViewCount";
	}
	
	@GetMapping("/getFunding")
	public String getFunding(Funding funding, Model model, HttpServletRequest request, HttpServletResponse response) {
		Funding fd = fundingService.getFunding(funding);
		model.addAttribute("funding", fd);
		
		// 조회수 중복방지
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("fundingView")) {
					oldCookie = cookie;
				}
			}
		}	
	
		if (oldCookie != null) {
			if (!oldCookie.getValue().contains("[" + funding.getFunding_seq().toString() + "]")) {
				fundingService.updateViewCount(funding.getFunding_seq());
				oldCookie.setValue(oldCookie.getValue() + "[" +funding.getFunding_seq() +"]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);
				response.addCookie(oldCookie);
			}
		} else {
				fundingService.updateViewCount(funding.getFunding_seq());
				Cookie newCookie = new Cookie("fundingView", "[" + funding.getFunding_seq() + "]");
				newCookie.setPath("/");
				newCookie.setMaxAge(60*60*24);
				response.addCookie(newCookie);
		}
		return "funding/getFunding";
	}
	
	@GetMapping("/getMyFunding")
	public String getMyFunding(HttpSession session, Funding funding, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			Funding fd = fundingService.getFunding(funding);
			model.addAttribute("funding", fd);
			return "funding/getMyFunding";
		}
	}
	
	@GetMapping("/myFundingList")
	public String myfundingList(@RequestParam(value="page", defaultValue="1") int page, HttpSession session, Model model, Funding funding) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			funding.setMember(loginMember);
			Page<Funding> fundingList = fundingService.getMyFundingList(funding.getMember().getUsername(), page);
			model.addAttribute("funding", fundingList);
			return "funding/myFundingList";
		}
	}
	
	@GetMapping("/myFundingListKind")
	public String myfundingListKind(@RequestParam(value="page", defaultValue="1") int page, 
									@RequestParam(value = "kind", defaultValue = "") String kind,
									HttpSession session, Model model, Funding funding) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			funding.setMember(loginMember);
			Page<Funding> fundingList = fundingService.getMyFundingListByKind(funding.getMember().getUsername(), funding.getKind(), page);
			model.addAttribute("funding", fundingList);
			return "funding/myFundingListKind";
		}
	}
	
	@GetMapping("/insertFunding")
	public String insertFunding(Recipe vo, Model model) {
		
		model.addAttribute("recipe_seq", vo.getRecipe_seq());
		
		return "funding/insertFunding";
	}
	
	@PostMapping("/insertFunding")
	public String insertFunding(@RequestParam(value="recipe_seq") long recipe_seq,
								HttpSession session, Funding funding, MultipartFile file) throws IllegalStateException, IOException {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\image";
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			File saveFile = new File(projectPath, fileName);
	    	file.transferTo(saveFile);
	    	funding.setFilename(fileName);
	    	funding.setFilepath("/files/" + fileName);
			funding.setMember(loginMember);
			
			Recipe recipe = new Recipe();
			recipe.setRecipe_seq(recipe_seq);
			funding.setRecipe(recipe);
				
			fundingService.insertFunding(funding);
			fundingService.updateResult(funding.getRecipe().getRecipe_seq()); // 레시피상태 신청완료로 업데이트
			return "redirect:myFundingList";
		}
	}
	
	@GetMapping("/updateFunding")
	public String updateFunding(HttpSession session, Funding funding, Model model) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			funding.setMember(loginMember);
			Funding fd = fundingService.getFunding(funding);
			model.addAttribute("funding", fd);
			return "funding/updateFunding";
		}
	}
	
	@PostMapping("/updateFunding")
	public String updateunding(HttpSession session, Funding funding, MultipartFile file) throws IllegalStateException, IOException {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else {
			String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\image";
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			File saveFile = new File(projectPath, fileName);
			file.transferTo(saveFile);
			funding.setFilename(fileName);
			funding.setFilepath("/files/" + fileName);
			funding.setMember(loginMember);
			
			fundingService.updateFunding(funding);
			return "redirect:myFundingList";
		}
	}
	
	@GetMapping("/deleteFunding")
	public String deleteFunding(@RequestParam(value="recipe_seq") long recipe_seq, HttpSession session, Funding funding) {
		Member loginMember = (Member)session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "sign/login";
		} else  {
			Recipe recipe = new Recipe();
			recipe.setRecipe_seq(recipe_seq);
			funding.setRecipe(recipe);
			
			funding.setMember(loginMember);
			fundingService.updateResultTwo(funding.getRecipe().getRecipe_seq());
			fundingService.deleteFunding(funding);
			return "redirect:myFundingList";
		}
	}
	
	@RequestMapping("/allFundingList")
    public String allFundingList(@RequestParam(value = "page", defaultValue = "1") int page, Search search, Model model) {
        if(search.getSearchCondition() == null) {
            search.setSearchCondition("USERNAME");
        }
        if(search.getSearchKeyword() == null) {
            search.setSearchKeyword("");
        }
        Page<Funding> fundingList = fundingService.getFundingList(page, search);

        model.addAttribute("fundingList", fundingList);

        return "admin/fundingList";
    }
}
