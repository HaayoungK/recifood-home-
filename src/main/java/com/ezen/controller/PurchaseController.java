package com.ezen.controller;

import com.ezen.entity.Search;
import com.ezen.entity.*;
import com.ezen.service.CartService;
import com.ezen.service.FundingService;
import com.ezen.service.MemberService;
import com.ezen.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FundingService fundingService;
    @Autowired
    private CartService cartService;

    @PostMapping("/fundingPurchase") // 상품페이지 결제
    public String fundingPurchaseForm(Funding funding, @RequestParam("quantity") int quantity,
                                      Model model, HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if(loginMember == null) {
            return "sign/login";
        } else {

            Map<String, String> addrMap = new HashMap<>();
            String[] addressArr = null;

            Funding findFunding = fundingService.getFunding(funding);
            Member findMember = memberService.getMember(loginMember);
            addressArr = findMember.getAddress().split(",");

            addrMap.put("addr1", addressArr[0]);
            addrMap.put("addr2", addressArr[1]);
            addrMap.put("addr3", addressArr[2]);

            model.addAttribute("funding", findFunding);
            model.addAttribute("quantity", quantity);
            model.addAttribute("member", findMember);
            model.addAttribute("address", addrMap);

            return "purchase/insertPurchase";
        }
    }

    @GetMapping("/cartPurchase")
    public String cartPurchaseView(HttpSession session, Model model,
                                   @RequestParam("cart_seq") Long cart_seq,
                                   @RequestParam("funding_seq") Long funding_seq,
                                   @RequestParam("quantity") int quantity) {
        System.out.println("cart_seq"+cart_seq);
        System.out.println("funding_seq"+funding_seq);
        System.out.println("quantity"+quantity);

        Member loginMember = (Member) session.getAttribute("loginMember");

        if(loginMember == null) {
            return "sign/login";
        } else {

            Map<String, String> addrMap = new HashMap<>();
            String[] addressArr = null;

            Funding funding = new Funding();
            funding.setFunding_seq(funding_seq);
            Funding findFunding = fundingService.getFunding(funding);
            Member findMember = memberService.getMember(loginMember);
            addressArr = findMember.getAddress().split(",");

            addrMap.put("addr1", addressArr[0]);
            addrMap.put("addr2", addressArr[1]);
            addrMap.put("addr3", addressArr[2]);

            model.addAttribute("funding", findFunding);
            model.addAttribute("cart_seq", cart_seq);
            model.addAttribute("quantity", quantity);
            model.addAttribute("member", findMember);
            model.addAttribute("address", addrMap);

            return "purchase/insertPurchase";
        }
    }

    /*
    @PostMapping("/cartPurchase") // 장바구니 결제
    public ModelAndView cartPurchase(@RequestBody Map<String, String> map, HttpSession session, ModelAndView modelAndView) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        Map<String, String> addrMap = new HashMap<>();
        String[] addressArr = null;

        Member findMember = memberService.getMember(loginMember);
        addressArr = findMember.getAddress().split(",");

        addrMap.put("addr1", addressArr[0]);
        addrMap.put("addr2", addressArr[1]);
        addrMap.put("addr3", addressArr[2]);

        Funding funding = new Funding();
        funding.setFunding_seq(Long.valueOf(map.get("funding_seq")));
        Funding findFunding = fundingService.getFunding(funding);
        System.out.println("찾은 펀딩: " + findFunding);

        modelAndView.addObject("member", findMember);
        modelAndView.addObject("address", addrMap);
        modelAndView.addObject("funding", findFunding);
        modelAndView.addObject("cart_seq", map.get("cart_seq"));
        modelAndView.addObject("quantity", Integer.valueOf(map.get("quantity")));
        modelAndView.setViewName("purchase/insertPurchase");

        return modelAndView;
    }
*/
    @PostMapping("/insertPurchase")
    public @ResponseBody void insertPurchase(@RequestBody Map<String, String> map, Purchase purchase) {
        Funding funding = new Funding();
        funding.setFunding_seq(Long.valueOf(map.get("funding_seq")));
        Member member = new Member();
        member.setUsername(map.get("username"));

        if(map.get("cart_seq") != null) {
            Cart cart = new Cart();
            cart.setCart_seq(Long.valueOf(map.get("cart_seq")));
            cartService.deleteCart(cart);
        }

        purchase.setName(map.get("name"));
        purchase.setPhone(map.get("phone"));
        purchase.setEmail(map.get("email"));
        purchase.setZip_num(map.get("zip_num"));
        purchase.setAddress(map.get("address"));
        purchase.setPurchase_uid(map.get("purchase_uid"));
        purchase.setPrice(Integer.parseInt(map.get("price")));
        purchase.setQuantity(Integer.parseInt(map.get("quantity")));
        purchase.setPayment(Integer.parseInt(map.get("payment")));
        purchase.setPayer(map.get("payer"));
        purchase.setP_comment(map.get("p_comment"));
        purchase.setAgree(map.get("agree"));
        purchase.setFunding(funding);
        purchase.setMember(member);

        purchaseService.insertPurchase(purchase);
    }

    @GetMapping("/success")
    public String successPurchase(@ModelAttribute("purchase") Purchase purchase, Model model) {
        model.addAttribute("purchase", purchase);

        return "purchase/successPurchase";
    }

    @GetMapping("/fail")
    public String failPurchase() {
        return "purchase/failPurchase";
    }

    @RequestMapping("/purchaseList")
    public String myPurchaseList(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @SessionAttribute("member") Member member, Model model) {

        Page<Purchase> purchaseList = purchaseService.findByMember_Username(page, member.getUsername());

        model.addAttribute("purchaseList", purchaseList);

        return "purchase/myPurchaseList";
    }

    @RequestMapping("/allPurchaseList")
    public String allPurchaseList(@RequestParam(value = "page", defaultValue = "1") int page, Search search, Model model) {
        if(search.getSearchCondition() == null) {
            search.setSearchCondition("USERNAME");
        }
        if(search.getSearchKeyword() == null) {
            search.setSearchKeyword("");
        }

        Page<Purchase> purchaseList = purchaseService.getPurchaseList(page, search);

        model.addAttribute("purchaseList", purchaseList);

        return "admin/purchaseList";
    }

    @GetMapping("/getPurchase")
    public String getPurchase(Purchase purchase, Model model) {

        model.addAttribute("purchase", purchaseService.getPurchase(purchase));
        return "purchase/getPurchase";
    }
}
