package com.example.book.controller;

import com.example.book.domain.Qna;
import com.example.book.service.QnaReplyService;
import com.example.book.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;
    private final QnaReplyService qnaReplyService;

    // 임시: 로그인 사용자 번호 매핑 (DB 스키마 정리 전까지 1L로 고정)
    private Long currentUserNo(UserDetails ud) { return 1L; }
    private boolean isAdmin(UserDetails ud) {
        return ud != null && ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails ud,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        var page = qnaService.listForUser(currentUserNo(ud), isAdmin(ud), pageable);
        model.addAttribute("page", page);
        return "qna/list";
    }

    @GetMapping("/{id}")
    public String read(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable Long id,
                       Model model) {
        Qna q = qnaService.getForRead(id, currentUserNo(ud), isAdmin(ud));
        model.addAttribute("q", q);
        model.addAttribute("replies", qnaReplyService.list(id));
        return "qna/read";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("q", new Qna());
        return "qna/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(name = "blind", required = false) String blind) {
        Long id = qnaService.create(currentUserNo(ud), title, content, blind != null);
        return "redirect:/qna/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal UserDetails ud,
                           @PathVariable Long id,
                           Model model) {
        Qna q = qnaService.getForRead(id, currentUserNo(ud), isAdmin(ud));

        if (!isAdmin(ud) && !q.getUserNo().equals(currentUserNo(ud))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("q", q);
        return "qna/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam String content,
                       @RequestParam(name = "blind", required = false) String blind) {
        qnaService.update(id, currentUserNo(ud), title, content, blind != null);
        return "redirect:/qna/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails ud,
                         @PathVariable Long id) {
        qnaService.delete(id, currentUserNo(ud));
        return "redirect:/qna";
    }

    @GetMapping("/ping") @ResponseBody String ping(){ return "qna-ok"; }
}
