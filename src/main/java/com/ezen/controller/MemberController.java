package com.ezen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ezen.dto.Email;
import com.ezen.dto.Search;
import com.ezen.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.ezen.persistence.MemberRepository;
import com.ezen.service.FundingService;
import com.ezen.service.MemberService;
import com.ezen.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@Controller
@SessionAttributes("member")
@Log4j2
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private FundingService fundingService;

    @GetMapping("/home")
    public String home(Recipe recipe, Funding funding, Model model) {
    	// 베스트 펀딩
    	List<Funding> fundingList = fundingService.getBestFundingList(funding);
    	model.addAttribute("funding", fundingList);
    	// 베스트 레시피
    	List<Recipe> recipeList = recipeService.getBestRecipeList(recipe);
    	model.addAttribute("recipe", recipeList);
        return "home";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "sign/login";
    }


    @PostMapping("/login")
    public String login(HttpServletRequest request, Member loginMember, Model model) {
        Member findMember = (Member) memberService.getMember(loginMember);

        if (findMember != null && findMember.getPassword().equals(loginMember.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", findMember);
            model.addAttribute("loginMember", findMember);
            return "redirect:/home";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping("/join")
    public String joinForm(Member member, Model model) {
        model.addAttribute("member", member);
        return "sign/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("member") Member member,
                       BindingResult result) {

        Optional<Member> memberId = memberRepository.findById(member.getUsername());

        if(memberId.isPresent()) {
            FieldError fieldError = new FieldError("member", "username", "이미 존재하는 아이디입니다.");
            result.addError(fieldError);
        }
        if(!member.getPassword().equals(member.getPasswordCheck())) {
            FieldError fieldError = new FieldError("member", "passwordCheck", "비밀번호가 일치하지 않습니다.");
            result.addError(fieldError);
        }
        if(!member.getAgree().equals("y")) {
            FieldError fieldError = new FieldError("member", "agree", "약관에 동의하셔야 가입이 가능합니다.");
            result.addError(fieldError);
        }
        if(result.hasErrors()) {
            return "sign/join";
        } else {
            member.setRole(Role.valueOf("MEMBER"));
            member.setDegree(Degree.valueOf("BRONZE"));
            memberService.insertMember(member);
            return "redirect:login";
        }
    }

    @GetMapping("/member")
    public String getMember(Member member, Model model) {
        Map<String, String> addrMap = new HashMap<>();
        String[] addressArr = null;

        Member member1 = memberService.getMember(member);
        addressArr = member1.getAddress().split(",");

        addrMap.put("addr1", addressArr[0]);
        addrMap.put("addr2", addressArr[1]);
        addrMap.put("addr3", addressArr[2]);

        model.addAttribute("member", member1);
        model.addAttribute("address", addrMap);

        return "sign/getMember";
    }

    @GetMapping("/updateMember")
    public String updateMemberForm(Member member, Model model) {
        Map<String, String> addrMap = new HashMap<>();
        String[] addressArr = null;

        Member member1 = memberService.getMember(member);
        addressArr = member1.getAddress().split(",");

        addrMap.put("addr1", addressArr[0]);
        addrMap.put("addr2", addressArr[1]);
        addrMap.put("addr3", addressArr[2]);

        model.addAttribute("member", member1);
        model.addAttribute("address", addrMap);

        return "sign/updateMember";
    }

    @PostMapping("/updateMember")
    public String updateMember(@Valid @ModelAttribute("member") Member member, Model model
                               ,BindingResult result) {

        if(!member.getPassword().equals(member.getPasswordCheck())) {
            FieldError fieldError = new FieldError("member", "passwordCheck", "비밀번호가 일치하지 않습니다.");
            result.addError(fieldError);
        }
        model.addAttribute("member", memberRepository.save(member));
        return "redirect:/member";
    }

    @GetMapping("/deleteMember")
    public String deleteMember(@ModelAttribute("member") Member member) {
        memberService.deleteMember(member);
        return "redirect:/home";
    }

    @GetMapping("/findMember")
    public String memberCheck() {
        return "sign/findMember";
    }

    @PostMapping("/findId")
    public String findId(Member member, Model model) {
        Member memberId = memberService.findMemberId(member.getName(), member.getEmail());

        if(memberId == null) {
            model.addAttribute("find", 0);
        } else {
            model.addAttribute("find", 1);
            model.addAttribute("member", memberId);
        }
        return "/sign/findId";
    }

    @PostMapping("/sendEmail")
    public String findPwdAndSendEmail(@RequestParam("email") String memberEmail, Member member, Model model) {
        Member memberPwd = memberService.findMemberPwd(member.getUsername(), member.getEmail());

        if(memberPwd == null) {
            model.addAttribute("find", 0);
        } else {
            Email email = memberService.sendEmailAndChangePassword(memberEmail);
            memberService.sendEmail(email);
            model.addAttribute("find", 1);
        }
        return "sign/findPwd";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Member member) {
    	Member loginMember = (Member)session.getAttribute("loginMember");
    	
    	if (loginMember == null) {
    		return "sign/login";
    	} else {
    		return "sign/mypage";
    	}
    }

    @GetMapping("/adminPage")
    public String adminPage(HttpSession session) {
        Member loginMember = (Member)session.getAttribute("loginMember");

        if (loginMember == null) {
            return "sign/login";
        } else {
            return "admin/adminPage";
        }
    }

    @RequestMapping("/allMemberList")
    public String allMemberList(@RequestParam(value = "page", defaultValue = "1") int page, Search search, Model model) {
        if(search.getSearchCondition() == null) {
            search.setSearchCondition("USERNAME");
        }
        if(search.getSearchKeyword() == null) {
            search.setSearchKeyword("");
        }
        Page<Member> memberList = memberService.getMemberList(page, search);

        model.addAttribute("memberList", memberList);

        return "admin/memberList";
    }

}
