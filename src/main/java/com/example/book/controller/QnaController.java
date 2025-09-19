package com.example.book.controller;

import com.example.book.domain.qna.Qna;
import com.example.book.repository.QnaReplyRepository;
import com.example.book.service.QnaReplyService;
import com.example.book.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;
    private final QnaReplyService qnaReplyService;
    private final QnaReplyRepository qnaReplyRepository;

    // 임시: 로그인 사용자 번호 매핑 (DB 스키마 정리 전까지 1L로 고정)
    private Long currentUserNo(UserDetails ud) { return 1L; }
    private boolean isAdmin(UserDetails ud) {
        return ud != null && ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails ud,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {

        // 목록 페이지
        Page<Qna> page = qnaService.listForUser(currentUserNo(ud), isAdmin(ud), pageable);
        model.addAttribute("page", page);

        // 해당 페이지의 qBId 모아 답변 수 집계
        List<Long> ids = page.getContent().stream()
                .map(Qna::getQBId)   // Qna 필드가 qBId 이므로 getter는 getQBId()
                .toList();

        Map<Long, Long> counts = ids.isEmpty()
                ? java.util.Map.of()
                : qnaReplyRepository.countByQbIdIn(ids).stream()
                .collect(java.util.stream.Collectors.toMap(
                        QnaReplyRepository.ReplyCountRow::getQbId,
                        QnaReplyRepository.ReplyCountRow::getCnt));

        model.addAttribute("counts", counts);

        return "qna/list";
    }


    @GetMapping("/{id:\\d+}")
    public String read(@AuthenticationPrincipal UserDetails ud,
                       @PathVariable Long id,
                       Model model) {
        Qna q = qnaService.getForRead(id, currentUserNo(ud), isAdmin(ud));
        model.addAttribute("q", q);
        model.addAttribute("replies", qnaReplyService.list(id));
        boolean canEdit = isAdmin(ud) || (ud != null && q.getUserNo().equals(currentUserNo(ud)));
        model.addAttribute("canEdit", canEdit);
        return "qna/read";
    }

    @GetMapping("/write")
    public String createForm(Model model) {
        model.addAttribute("q", new Qna());
        return "qna/form";
    }

    @PostMapping("/write")
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

}
