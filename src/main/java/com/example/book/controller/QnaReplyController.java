package com.example.book.controller;

import com.example.book.service.QnaReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/qna")
public class QnaReplyController {

    private final QnaReplyService qnaReplyService;

    private Long currentUserNo(UserDetails ud) { return 1L; }
    private boolean isAdmin(UserDetails ud) {
        return ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping("/{qBId}/reply")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @PathVariable Long qBId,
                         @RequestParam String content) {
        if (!isAdmin(ud)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        qnaReplyService.create(qBId, currentUserNo(ud), content);
        return "redirect:/qna/" + qBId;
    }

    @PostMapping("/reply/{qRId}/edit")
    public String edit(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable Long qRId,
                       @RequestParam Long qBId,
                       @RequestParam String content) {
        if (!isAdmin(ud)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        qnaReplyService.edit(qRId, currentUserNo(ud), content);
        return "redirect:/qna/" + qBId;
    }

    @PostMapping("/reply/{qRId}/delete")
    public String delete(@AuthenticationPrincipal UserDetails ud,
                         @PathVariable Long qRId,
                         @RequestParam Long qBId) {
        if (!isAdmin(ud)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        qnaReplyService.deleteByAdmin(qRId);
        return "redirect:/qna/" + qBId;
    }
}
