package com.ezen.service;

import com.ezen.entity.Board;
import com.ezen.entity.QBoard;
import com.ezen.dto.Search;
import com.ezen.persistence.BoardRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class BoardServiceImpl implements BoardService{

    @Autowired
    BoardRepository boardRepository;

    @Override
    public void insertBoard(Board board) {
        boardRepository.save(board);
    }

    @Override
    public void updateBoard(Board board) {
        Board findBoard = boardRepository.findById(board.getBoardSeq()).get();

        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());

        boardRepository.save(board);
    }

    @Override
    public void deleteBoard(Board board) {

        boardRepository.deleteById(board.getBoardSeq());
    }

    @Override
    public Board getBoard(Board board) {

        return boardRepository.findById(board.getBoardSeq()).get();
    }

    @Override
    public Page<Board> getBoardList(int page, Search search) {
        BooleanBuilder builder = new BooleanBuilder();

        QBoard qBoard = QBoard.board;

        if(search.getSearchCondition().equals("TITLE")) {
            builder.and(qBoard.title.like("%" + search.getSearchKeyword() + "%"));
        } else if (search.getSearchCondition().equals("CONTENT")) {
            builder.and(qBoard.content.like("%" + search.getSearchKeyword() + "%"));
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "boardSeq");

        return boardRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Board> findByCategory(int page, String category) {

        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "boardSeq");
        return boardRepository.findAllByCategory(category, pageable);
    }

    @Override
    public Page<Board> getMyBoardList(int page, String username) {

        Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "boardSeq");
        return boardRepository.getMyBoardList(username, pageable);
    }
}
