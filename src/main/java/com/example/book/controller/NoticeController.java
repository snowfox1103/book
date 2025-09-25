package com.example.book.controller;

import com.example.book.domain.notice.Notice;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /** 목록 + 정렬 */
    @GetMapping("")
    public String list(
            @PageableDefault(size = 10, sort = "nBId", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        Page<Notice> page = noticeService.list(pageable);

        Sort.Order order = page.getSort().stream()
                .findFirst()
                .orElse(Sort.Order.by("nBId").with(Sort.Direction.DESC));

        model.addAttribute("page", page);
        model.addAttribute("size", page.getSize());
        model.addAttribute("sort", order.getProperty());
        model.addAttribute("dir", order.getDirection().isAscending() ? "asc" : "desc");
        return "notice/list";
    }

    /** 읽기 */
    @GetMapping("/{id:\\d+}")
    public String read(@PathVariable Long id, Model model) {
        model.addAttribute("n", noticeService.read(id));
        return "notice/read";
    }

    /** 작성 폼 (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("n", new Notice());
        return "notice/form";
    }

    /** 등록 (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@AuthenticationPrincipal UsersSecurityDTO me,
                         @ModelAttribute Notice n) {
        n.setUserNo(me.getUserNo());
        noticeService.save(n);
        return "redirect:/notice";
    }

    /** 수정 폼 (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id:\\d+}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("n", noticeService.read(id));
        return "notice/form";
    }

    /** 수정 (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id:\\d+}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam String content) {
        noticeService.edit(id, title, content);
        return "redirect:/notice/" + id;
    }

    /** 삭제 (ADMIN) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable Long id) {
        noticeService.delete(id);
        return "redirect:/notice";
    }
}
