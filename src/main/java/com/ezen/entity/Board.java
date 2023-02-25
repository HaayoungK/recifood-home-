package com.ezen.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long boardSeq;

	private String title;
//	private String writer;
	private String content;

	@Column(columnDefinition = "char default '0'")
	private String category; // 1: 공지사항, 2: 자유게시판

	@Column(insertable=false, updatable=false, columnDefinition="date default sysdate")
	private Date regdate;

	@ManyToOne
	@JoinColumn(name="username", nullable=false, updatable=false)
	private Member member;

	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<BoardReply> boardReplyList = new ArrayList<BoardReply>();

}
