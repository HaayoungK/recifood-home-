package com.ezen.service;

import java.util.List;

import com.ezen.entity.Member;
import com.ezen.entity.Search;
import org.springframework.data.domain.Page;

public interface MemberService {

	void insertMember(Member member); // 회원 가입
	
	void updateMember(Member member); // 회원 수정
	
	void deleteMember(Member member); // 회원 탈퇴
	
	Member getMember(Member member);

	Page<Member> getMemberList(int page, Search search);

	Member findMemberId(String name, String email);

	Member findMemberPwd(String username, String email);
}
