package com.example.book.controller;

import com.example.book.domain.qna.Qna;
import com.example.book.dto.PageRequestDTO;
import com.example.book.repository.QnaReplyRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.QnaReplyService;
import com.example.book.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private Long currentUserNo(UsersSecurityDTO auth) {
        return (auth != null) ? auth.getUserNo() : null;
    }

    private boolean isAdmin(UsersSecurityDTO auth) {
        return auth != null && auth.hasRole("ADMIN");
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UsersSecurityDTO auth,
                       @PageableDefault(size = 10) Pageable pageable,
                       Model model) {

        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "regDate"));
        Page<Qna> page = qnaService.listForUser(currentUserNo(auth), isAdmin(auth), sorted);
        model.addAttribute("page", page);

        // 이하 그대로 (답변 수 집계 등)
        List<Long> ids = page.getContent().stream().map(Qna::getQBId).toList();
        Map<Long, Long> counts = ids.isEmpty()
                ? java.util.Map.of()
                : qnaReplyRepository.countByQbIdIn(ids).stream()
                .collect(Collectors.toMap(
                        QnaReplyRepository.ReplyCountRow::getQbId,
                        QnaReplyRepository.ReplyCountRow::getCnt));
        model.addAttribute("counts", counts);

        return "qna/list";
    }

    @GetMapping("/{qBId}")
    public String read(@PathVariable Long qBId,
                       @AuthenticationPrincipal UsersSecurityDTO auth,
                       Model model) {

        Long userNo = (auth != null ? auth.getUserNo() : null);
        boolean admin = (auth != null && auth.hasRole("ADMIN"));

        Qna q = qnaService.getForRead(qBId, userNo, admin);
        model.addAttribute("q", q);

        PageRequestDTO pr = PageRequestDTO.builder()
                .page(1)
                .size(50)
                .build();

        boolean owner = (userNo != null && userNo.equals(q.getUserNo()));

        model.addAttribute("replies", qnaReplyService.list(qBId, pr).getDtoList());

        model.addAttribute("canEdit", admin || owner);
        return "qna/read";
    }

    @GetMapping("/write")
    public String createForm(Model model) {
        model.addAttribute("q", new Qna());
        return "qna/form";
    }

    @PostMapping("/write")
    public String create(@AuthenticationPrincipal UsersSecurityDTO auth,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(name="blind", required=false) String blind) {
        Long id = qnaService.create(currentUserNo(auth), title, content, blind != null);
        return "redirect:/qna/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal UsersSecurityDTO auth,
                           @PathVariable Long id,
                           Model model) {
        Qna q = qnaService.getForRead(id, currentUserNo(auth), isAdmin(auth));

        if (!isAdmin(auth) && !q.getUserNo().equals(currentUserNo(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("q", q);
        return "qna/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@AuthenticationPrincipal UsersSecurityDTO auth,
                       @PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam String content,
                       @RequestParam(name = "blind", required = false) String blind) {
        qnaService.update(id, currentUserNo(auth), title, content, blind != null);
        return "redirect:/qna/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UsersSecurityDTO auth,
                         @PathVariable Long id) {
        qnaService.delete(id, currentUserNo(auth));
        return "redirect:/qna";
    }
}
