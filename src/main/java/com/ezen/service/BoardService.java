package com.ezen.service;

import com.ezen.entity.Board;
import com.ezen.entity.Search;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {

    void insertBoard(Board board); // 게시판 등록

    void updateBoard(Board board); // 수정

    void deleteBoard(Board board); // 삭제

    Board getBoard(Board board);

    Page<Board> getBoardList(Search search);
}
