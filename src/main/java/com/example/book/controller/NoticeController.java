package com.example.book.controller;

import com.example.book.domain.notice.Notice;
import com.example.book.domain.user.Users;
import com.example.book.repository.NoticeRepository;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.NoticeService;
import com.example.book.service.UsersService;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    // NoticeController
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "nBCreatedAt") String sort,
                       @RequestParam(defaultValue = "desc") String dir,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Sort sortSpec = Sort.by("asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC, sort);

        Page<Notice> p = noticeService.list(pageable, sortSpec);

        // --- 블록 페이지네이션 계산 ---
        int now = p.getNumber() + 1;
        int total = Math.max(1, p.getTotalPages());
        int blockSize = 10;
        int startPage = ((now - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, total);
        boolean hasPrevBlock = startPage > 1;
        boolean hasNextBlock = endPage < total;
        int prevPage = Math.max(1, startPage - 1);
        int nextPage = Math.min(total, endPage + 1);

        // --- 작성자 userNo → userId 매핑 ---
        Map<Long, String> authorNames = new HashMap<>();
        for (Notice n : p.getContent()) {
            Long no = n.getUserNo();
            authorNames.put(no, usersService.getUserIdByUserNo(no));
        }

        model.addAttribute("page", p);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        model.addAttribute("now", now);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        model.addAttribute("authorNames", authorNames);

        return "notice/list";
    }


    @GetMapping("/{id:\\d+}")
    public String read(@PathVariable Long id, Model model) {
        model.addAttribute("n", noticeService.read(id));
        return "notice/read";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("n", new Notice());
        return "notice/write";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@AuthenticationPrincipal UsersSecurityDTO me,
                         @RequestParam("nBTitle") String nBTitle,
                         @RequestParam("nBContent") String nBContent) {

        Long newId = noticeService.write(me.getUserNo(), nBTitle, nBContent);

        return "redirect:/notice";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id:\\d+}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("n", noticeService.read(id));
        return "notice/write";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id:\\d+}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam String content) {
        noticeService.edit(id, title, content);
        return "redirect:/notice/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable Long id) {
        noticeService.delete(id);
        return "redirect:/notice";
    }
}
