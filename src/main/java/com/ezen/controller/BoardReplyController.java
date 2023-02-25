package com.ezen.controller;

import com.ezen.entity.Board;
import com.ezen.entity.BoardReply;
import com.ezen.entity.Member;
import com.ezen.service.BoardReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class BoardReplyController {

    @Autowired
    BoardReplyService boardReplyService;

    @PostMapping("/insertReply")
    public @ResponseBody void insertReply(@RequestBody Map<String, String> map, BoardReply boardReply) {
        Member member = new Member();
        member.setUsername(map.get("replyUsername"));
        Board board = new Board();
        board.setBoardSeq(Long.parseLong(map.get("boardSeq")));

        boardReply.setContent(map.get("replyContent"));
        boardReply.setMember(member);
        boardReply.setBoard(board);

        boardReplyService.insertBoardReply(boardReply);
    }
}
