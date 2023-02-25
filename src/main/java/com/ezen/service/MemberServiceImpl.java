package com.ezen.service;

import java.util.Optional;
import java.util.UUID;

import com.ezen.dto.Email;
import com.ezen.entity.QMember;
import com.ezen.dto.Search;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.ezen.entity.Member;
import com.ezen.persistence.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void insertMember(Member member) {
		memberRepository.save(member);
	}

	@Override
	public void updateMember(Member member) {
		Member findMember = memberRepository.findById(member.getUsername()).get();

		findMember.setPassword(member.getPassword());
		findMember.setPhone(member.getPhone());
		findMember.setEmail(member.getEmail());
		findMember.setZip_num(member.getZip_num());
		findMember.setAddress(member.getAddress());

		memberRepository.save(member);
	}

	@Override
	public void updatePassword(String tempPwd, String memberEmail) {

		String newPassword = tempPwd;
		Member findMember = memberRepository.findEmailMember(memberEmail);

		findMember.setPassword(newPassword);

		memberRepository.save(findMember);
	}

	@Override
	public void deleteMember(Member member) {
		memberRepository.deleteById(member.getUsername());
	}

	@Override
	public Member getMember(Member member) {
		Optional<Member> findMember = memberRepository.findById(member.getUsername());

		if(findMember.isPresent()) {
			return findMember.get();
		} else {
			return null;
		}
	}

	@Override
	public Page<Member> getMemberList(int page, Search search) {
		BooleanBuilder builder = new BooleanBuilder();

		QMember qMember = QMember.member;

		if(search.getSearchCondition().equals("USERNAME")) {
			builder.and(qMember.member.username.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("NAME")) {
			builder.and(qMember.name.like("%" + search.getSearchKeyword() + "%"));
		} else if (search.getSearchCondition().equals("EMAIL")) {
			builder.and(qMember.email.like("%" + search.getSearchKeyword() + "%"));
		}
		Pageable pageable = PageRequest.of(page-1, 10, Sort.Direction.DESC, "regdate");
		return memberRepository.findAll(builder, pageable);
	}

	@Override
	public Member findMemberId(String name, String email) {
		return memberRepository.findMemberId(name, email);
	}

	@Override
	public Member findMemberPwd(String username, String email) {
		return memberRepository.findMemberPwd(username, email);
	}

	@Override
	public Email sendEmailAndChangePassword(String memberEmail) {
		String tempPwd = UUID.randomUUID().toString().replace("-", "");
		tempPwd = tempPwd.substring(0, 10);

		Email email = new Email();
		email.setAddress(memberEmail);
		email.setTitle("[Recifood] 임시비밀번호가 발급되었습니다.");
		email.setMessage("Recifood에서 요청하신 임시 비밀번호는" + tempPwd + "입니다." +
				"임시비밀번호로 로그인 후에는 반드시 새로운 비밀번호로  변경해 주시기 바랍니다.");

		updatePassword(tempPwd, memberEmail);
		return email;
	}

	@Override
	public void sendEmail(Email email) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();

		mailMessage.setTo(email.getAddress());
		mailMessage.setSubject(email.getTitle());
		mailMessage.setText(email.getMessage());
		mailMessage.setFrom("recifood@naver.com");
		mailMessage.setReplyTo("recifood@naver.com");
		javaMailSender.send(mailMessage);
	}


}
