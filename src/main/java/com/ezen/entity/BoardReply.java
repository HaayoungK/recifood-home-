package com.ezen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@ToString(exclude = {"member", "board"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardReplySeq;
    private String content;

    @Column(insertable=false, updatable=false, columnDefinition="date default sysdate")
    private Date regdate; // 등록일

    @LastModifiedDate
    private Date updateDate;

    @ManyToOne
    @JoinColumn(name="username", nullable=false, updatable=false)
    private Member member; // 댓글 작성자

    @ManyToOne
    @JoinColumn(name="boardSeq", nullable = false, updatable = false)
    private Board board; // 댓글 작성할 게시글

}
