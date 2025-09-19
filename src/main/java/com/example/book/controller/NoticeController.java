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
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "nBId") String sort,
            @RequestParam(required = false) String dir,          // null 허용
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // ---- 콤마 방어: sort=nBTitle,asc 처럼 들어와도 안전하게 분리 ----
        if (sort != null && sort.contains(",")) {
            String[] p = sort.split(",", 2);
            sort = p[0].trim();
            if (dir == null && p.length > 1) dir = p[1].trim();
        }
        if (dir == null || (!dir.equalsIgnoreCase("asc") && !dir.equalsIgnoreCase("desc"))) {
            dir = "desc"; // 기본값
        }
        // -------------------------------------------------------------------

        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sort));

        Page<Notice> page = noticeService.list(p);
        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
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
