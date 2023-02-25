package com.ezen.controller;

import com.ezen.entity.Board;
import com.ezen.entity.BoardReply;
import com.ezen.entity.Member;
import com.ezen.service.BoardReplyService;
import com.ezen.service.BoardService;
import com.ezen.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
public class BoardController {

    @Autowired
    BoardService boardService;
    @Autowired
    BoardReplyService boardReplyService;
    @Autowired
    MemberService memberService;


    @GetMapping("/board")
    public String getBoard(Board board, Model model) {
        List<BoardReply> boardReplyList =boardReplyService.getBoardReplyList(board);

        if(boardReplyList != null && !boardReplyList.isEmpty()) {
            model.addAttribute("boardReplyList", boardReplyList);
        }

        model.addAttribute("board", boardService.getBoard(board));
        return "board/getBoard";
    }

    @GetMapping("/insertBoard")
    public String insertBoardForm(@SessionAttribute("member") Member member, Model model) {
        model.addAttribute("member", member);
        return "board/insertBoard";
    }

    @PostMapping("/insertBoard")
    public String insertBoard(Board board, @SessionAttribute("member") Member member,
                              @RequestParam("role") String role) {

        Member findMember = memberService.getMember(member);

        if(role.equals("ADMIN")) {
            board.setCategory("1");
            board.setMember(findMember);
            boardService.insertBoard(board);
            return "redirect:/boardList?category=1";
        } else {
            board.setCategory("2");
            board.setMember(findMember);
            boardService.insertBoard(board);
            return "redirect:/boardList?category=2";
        }
    }

    @GetMapping("/updateBoard")
    public String updateBoardForm(Board board, Model model) {
        model.addAttribute("board", boardService.getBoard(board));
        return "board/updateBoard";
    }

    @PostMapping("/updateBoard")
    public String updateBoard(Board board) {

        if(board.getCategory().toString().equals("1")) {
            boardService.updateBoard(board);
            return "redirect:/boardList?category=1";
        } else {
            boardService.updateBoard(board);
            return "redirect:/boardList?category=2";
        }
    }

    @GetMapping("/deleteBoard")
    public String deleteBoard(Board board) {
        if(board.getCategory().toString().equals("1")) {
            boardService.deleteBoard(board);
            return "redirect:/boardList?category=1";
        } else {
            boardService.deleteBoard(board);
            return "redirect:/boardList?category=2";
        }
    }

    @RequestMapping("/boardList")
    public String getFreeBoardList(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "category", defaultValue = "0") int category, Model model) {

        Page<Board> boardList = boardService.findByCategory(page, String.valueOf(category));
        model.addAttribute("boardList", boardList);
        model.addAttribute("category", category);

        return "board/freeBoardList";
    }

    @RequestMapping("/myBoardList")
    public String getFreeBoardList(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @SessionAttribute("member") String username, Model model) {

        Page<Board> boardList = boardService.getMyBoardList(page, username);
        model.addAttribute("boardList", boardList);

        return "board/myBoardList";
    }
}
