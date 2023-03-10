package com.ezen.test;

import com.ezen.entity.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ezen.entity.Board;
import com.ezen.persistence.BoardRepository;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BoardRepositoryTest {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Test
	public void saveBoard() {

		for(int i=0; i<50; i++) {

			Board board = new Board();
			Member member = new Member();
			member.setUsername("member12");

			board.setTitle("멤버 게시글 테스트😉" + i);
			board.setMember(member);
			board.setCategory("2");
			board.setContent("게시글 내용........" + i);
			board.setRegdate(new Date());
			boardRepository.save(board);
		}

		for(int i=0; i<30; i++) {

			Board board = new Board();
			Member member = new Member();
			member.setUsername("admin123");

			board.setTitle("관리자 게시글 테스트😊" + i);
			board.setMember(member);
			board.setCategory("1");
			board.setContent("게시글 내용........" + i);
			board.setRegdate(new Date());
			boardRepository.save(board);
		}
	}
}
