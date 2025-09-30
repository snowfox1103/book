package com.example.book.controller;

import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    @GetMapping
    public String pointList(@AuthenticationPrincipal UsersSecurityDTO me,
                            @RequestParam(required = false) String ym,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "date") String sort,
                            @RequestParam(defaultValue = "desc") String dir,
                            Model model) {

        // 0) 로그인 가드
        if (me == null) return "redirect:/login";

        // 1) 파라미터 가드
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 1);
        int pageIndex = safePage - 1;

        // 2) UI sort 키 화이트리스트 및 DB 컬럼 매핑
        //    - date  -> pointStartDate (DB 정렬)
        //    - type  -> pointType      (DB 정렬)
        //    - delta -> pointStartDate (DB는 날짜로 정렬, 실제 delta 정렬은 Service에서 '페이지 내부' 정렬)
        String sortKey = switch (sort) {
            case "date", "type", "delta" -> sort;
            default -> "date";
        };
        String sortProp = switch (sortKey) {
            case "date" -> "pointStartDate";
            case "type" -> "pointType";
            case "delta" -> "pointStartDate"; // DB 정렬용 대체
            default -> "pointStartDate";
        };
        String dirKey = "asc".equalsIgnoreCase(dir) ? "asc" : "desc";

        Sort sortSpec = "asc".equals(dirKey)
                ? Sort.by(sortProp).ascending()
                : Sort.by(sortProp).descending();
        Pageable pageable = PageRequest.of(pageIndex, safeSize, sortSpec);

        // 3) 서비스 호출 (delta 정렬은 서비스에서 처리)
        Page<PointListDTO> list = pointService.findPointPage(me.getUserNo(), pageable, sortKey, dirKey, ym);

        // 4) 요약(연/월 파싱)
        Integer year = null, month = null;
        if (ym != null && ym.length() >= 7) {
            try {
                year = Integer.parseInt(ym.substring(0, 4));
                month = Integer.parseInt(ym.substring(5, 7));
                if (month < 1 || month > 12) { year = null; month = null; }
            } catch (Exception ignore) {}
        }
        PointSummaryDTO summary = pointService.getSummary(me.getUserNo(), year, month);

        // 5) 블록 페이지네이션 계산 (1-based)
        int now = pageIndex + 1;
        int totalPages = Math.max(1, list.getTotalPages());
        int blockSize = 10;
        int startPage = ((now - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);
        boolean hasPrevBlock = startPage > 1;
        boolean hasNextBlock = endPage < totalPages;
        int prevPage = Math.max(1, startPage - blockSize);
        int nextPage = Math.min(totalPages, startPage + blockSize);

        // 6) 모델
        model.addAttribute("page", list);
        model.addAttribute("summary", summary);
        model.addAttribute("ym", ym);
        model.addAttribute("sort", sortKey);  // ← UI가 쓰는 키 그대로 보냄
        model.addAttribute("dir", dirKey);
        model.addAttribute("size", safeSize);
        model.addAttribute("pageNum", now);
        model.addAttribute("now", now);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "point/list";
    }

    @GetMapping("/setting")
    public String setting() {
        return "point/setting";
    }
}
