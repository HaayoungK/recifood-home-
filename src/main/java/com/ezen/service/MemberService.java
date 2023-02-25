package com.ezen.service;

import com.ezen.dto.Email;
import com.ezen.entity.Member;
import com.ezen.dto.Search;
import org.springframework.data.domain.Page;

public interface MemberService {

	void insertMember(Member member); // 회원 가입
	
	void updateMember(Member member); // 회원 수정

	void updatePassword(String username, String tempPwd); // 임시 비밀번호 변경
	
	void deleteMember(Member member); // 회원 탈퇴
	
	Member getMember(Member member);

	Page<Member> getMemberList(int page, Search search);

	Member findMemberId(String name, String email);

	Member findMemberPwd(String username, String email);

	Email sendEmailAndChangePassword(String memberEmail);

	void sendEmail(Email email);
}
