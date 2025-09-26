package com.example.book.controller;

import com.example.book.dto.QnaReplyDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.QnaReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/qna")
public class QnaReplyController {

    private final QnaReplyService qnaReplyService;

    private Long currentUserNo(UsersSecurityDTO auth) {
        return (auth != null) ? auth.getUserNo() : null;
    }
    private boolean isAdmin(UsersSecurityDTO auth) {
        return auth != null && auth.hasRole("ADMIN");
    }

    /** 관리자 답변 등록 */
    @PostMapping("/{qBId}/reply")
    public String create(@AuthenticationPrincipal UsersSecurityDTO auth,
                         @PathVariable Long qBId,
                         @RequestParam String content) {
        if (!isAdmin(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        QnaReplyDTO dto = QnaReplyDTO.builder()
                .bno(qBId)
                .replyText(content)
                .build();

        qnaReplyService.register(dto, qBId, currentUserNo(auth)); // ← admin의 userNo로 저장
        return "redirect:/qna/" + qBId;
    }

    /** 관리자 답변 수정 */
    @PostMapping("/reply/{qRId}/edit")
    public String edit(@AuthenticationPrincipal UsersSecurityDTO auth,
                       @PathVariable Long qRId,
                       @RequestParam Long qBId,
                       @RequestParam String content) {
        if (!isAdmin(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        QnaReplyDTO dto = QnaReplyDTO.builder()
                .rno(qRId)
                .replyText(content)
                .build();

        qnaReplyService.modify(dto, currentUserNo(auth));
        return "redirect:/qna/" + qBId;
    }

    /** 관리자 답변 삭제 */
    @PostMapping("/reply/{qRId}/delete")
    public String delete(@AuthenticationPrincipal UsersSecurityDTO auth,
                         @PathVariable Long qRId,
                         @RequestParam Long qBId) {
        if (!isAdmin(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        qnaReplyService.removeByAdmin(qRId);
        return "redirect:/qna/" + qBId;
    }
}
