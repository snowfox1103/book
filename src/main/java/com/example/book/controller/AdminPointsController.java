package com.example.book.controller;

import com.example.book.dto.PointListDTO;
import com.example.book.dto.PointSummaryDTO;
import com.example.book.service.AdminService;
import com.example.book.service.PointService;
import com.example.book.security.dto.UsersSecurityDTO; // ← 프로젝트의 시큐리티 DTO 사용
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminPointsController {

    private final AdminService adminService;
    private final PointService pointService;   // ← 주입 누락 부분 추가

    // 스캔/승인대기 화면
    @GetMapping("/points")
    public String scanPage(@RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           @RequestParam(required = false) String msg,
                           Model model) {

        var now = java.time.YearMonth.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        model.addAttribute("settings",  adminService.getSettings());
        model.addAttribute("pendings",  adminService.listPendings());
        model.addAttribute("year", y);
        model.addAttribute("month", m);
        if (msg != null && !msg.isBlank()) model.addAttribute("msg", msg);

        return "admin/scan";
    }

    // 스캔 실행
    @PostMapping("/points/scan")
    public String runScan(@RequestParam int year,
                          @RequestParam int month,
                          RedirectAttributes ra) {
        int createdOrUpdated = adminService.scanMonthlyAccruals(year, month);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", String.format("%d건 스캔 처리", createdOrUpdated));
        return "redirect:/admin/points";
    }

    // 내 포인트 리스트 (템플릿: templates/point/list.html)
    @GetMapping("/point")
    public String pointList(@AuthenticationPrincipal UsersSecurityDTO me,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "date") String sort,
                            @RequestParam(defaultValue = "DESC") String dir,
                            @RequestParam(required = false, name = "ym") String ym, // "YYYY-MM"
                            Model model) {

        // 1) 정렬 매핑 (템플릿은 sort=date|delta|type 을 씀)  :contentReference[oaicite:1]{index=1}
        String sortProp = switch (sort.toLowerCase()) {
            case "type"  -> "pointType";
            case "delta" -> "pointAmount";        // 변동액(양수/음수는 pointType으로 표시)
            case "date"  -> "pointStartDate";
            default      -> "pointStartDate";
        };
        Sort.Direction direction = "ASC".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, sortProp));

        // 2) 목록
        Page<PointListDTO> list = pointService.findPointPage(me.getUserNo(), pageable, sort, dir);

        // 3) 요약 (summary는 절대 null로 주지 말 것)  :contentReference[oaicite:2]{index=2}
        Integer year = null, month = null;
        if (ym != null && ym.length() >= 7) {
            YearMonth y = YearMonth.parse(ym); // "YYYY-MM"
            year = y.getYear(); month = y.getMonthValue();
        }
        PointSummaryDTO summary = pointService.getSummary(me.getUserNo(), year, month);
        if (summary == null) summary = PointSummaryDTO.zero();

        // 4) 블록 페이지네이션 변수들 (템플릿에서 사용)  :contentReference[oaicite:3]{index=3}
        int now = list.getNumber() + 1;          // 1-based
        int totalPages = Math.max(list.getTotalPages(), 1);
        int blockSize = 10;
        int startPage = ((now - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);
        boolean hasPrevBlock = startPage > 1;
        boolean hasNextBlock = endPage < totalPages;
        int prevPage = Math.max(startPage - 1, 1);
        int nextPage = Math.min(endPage + 1, totalPages);

        // 5) 템플릿이 요구하는 모델 키들 주입  :contentReference[oaicite:4]{index=4}
        model.addAttribute("page", list);
        model.addAttribute("pointList", new PointListView(sort, dir)); // 정렬/방향 표시용
        model.addAttribute("summary", summary);
        model.addAttribute("ym", ym);

        model.addAttribute("now", now);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "point/list";
    }

    /** 템플릿에서 쓰는 정렬/방향 표시용 작은 뷰 모델 */
    public record PointListView(String sort, String dir) {}

    // 해당 월 승인 대기 전부 비우기
    @PostMapping("/point/pending/cleanup")
    public String clearPending(@RequestParam int year,
                               @RequestParam int month,
                               RedirectAttributes ra) {
        int deleted = adminService.clearPendingFor(year, month);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", String.format("%d건 승인 대기 삭제", deleted));
        return "redirect:/admin/points";
    }

    // 단건 승인
    @PostMapping("/points/approve/{id}")
    public String approveOne(@PathVariable Long id,
                             @RequestParam int year,
                             @RequestParam int month,
                             RedirectAttributes ra) {
        adminService.approvePending(id);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", "승인 완료: #" + id);
        return "redirect:/admin/points";
    }

    // 단건 반려
    @PostMapping("/points/reject/{id}")
    public String rejectOne(@PathVariable Long id,
                            @RequestParam int year,
                            @RequestParam int month,
                            RedirectAttributes ra) {
        adminService.rejectPending(id);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", "반려 완료: #" + id);
        return "redirect:/admin/points";
    }

    // 일괄 승인
    @PostMapping("/point/approve/bulk")
    public String approveBulk(@RequestParam("ids") java.util.List<Long> ids,
                              @RequestParam int year,
                              @RequestParam int month,
                              RedirectAttributes ra) {
        int cnt = adminService.approvePendingBulk(ids);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", cnt + "건 승인 완료");
        return "redirect:/admin/points";
    }

    // 일괄 반려
    @PostMapping("/point/reject/bulk")
    public String rejectBulk(@RequestParam("ids") java.util.List<Long> ids,
                             @RequestParam int year,
                             @RequestParam int month,
                             RedirectAttributes ra) {
        int cnt = adminService.rejectPendingBulk(ids);
        ra.addAttribute("year", year);
        ra.addAttribute("month", month);
        ra.addAttribute("msg", cnt + "건 반려 완료");
        return "redirect:/admin/points";
    }
}
