// src/main/java/com/example/book/controller/AdminController.java
package com.example.book.controller;

import com.example.book.dto.PendingPointDTO;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import com.example.book.domain.point.RewardType;
import com.example.book.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 공통 모델 세팅: 설정 + 전체 pending(리스트 섹션용) + year/month
    private void injectCommon(Model model, Integer year, Integer month, String msg) {
        YearMonth now = YearMonth.now();
        int y = (year  == null) ? now.getYear()       : year;
        int m = (month == null) ? now.getMonthValue() : month;

        PointSettingsDTO settings = adminService.getSettings();
        // 전체 Pending(최근 10개 표시는 템플릿에서 index < 10으로 제한)
        List<PendingPointDTO> pendings = adminService.listPendings();

        model.addAttribute("settings", settings);
        model.addAttribute("pendings", pendings);
        model.addAttribute("pendingCount", pendings != null ? pendings.size() : 0);
        model.addAttribute("year", y);
        model.addAttribute("month", m);
        if (msg != null && !msg.isBlank()) model.addAttribute("msg", msg);
    }

    // 관리자 홈 (설정 + 최근 10건 섹션)
    @GetMapping({ "", "/" })
    public String adminHome(@RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer month,
                            @RequestParam(required = false) String msg,
                            Model model) {
        injectCommon(model, year, month, msg);
        return "admin/list";
    }

    // 스캔 페이지 (20개/페이지 페이지네이션)
    @GetMapping("/scan")
    public String scanPage(@RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(required = false) String msg,
                           Model model) {
        injectCommon(model, year, month, msg);

        int y = (Integer) model.getAttribute("year");
        int m = (Integer) model.getAttribute("month");
        String ym = String.format("%04d-%02d", y, m);

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), 20, Sort.by(Sort.Direction.DESC, "id"));
        Page<PendingPointDTO> pendingPage = adminService.findPendingsPage(ym, pageable);

        model.addAttribute("page", pendingPage);
        return "admin/scan";
    }

    // 303 See Other 리다이렉트 헬퍼
    private ResponseEntity<Void> seeOther(String url) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(url)).build();
    }

    // 스캔 실행
    @PostMapping("/scan/run")
    public String runScan(@RequestParam int year,
                          @RequestParam int month,
                          RedirectAttributes ra) {
        // 1) 항상 청소 후
        adminService.clearPendings(year, month);

        // 2) 새 스캔(해당 월 승인대기 생성)
        int created = adminService.scanMonthlyAccruals(year, month);

        ra.addFlashAttribute("msg", year + "-" + String.format("%02d", month) + " 스캔: " + created + "건 생성");
        return "redirect:/admin/scan?year=" + year + "&month=" + month;
    }

    // 월 대기 비우기
    @PostMapping("/scan/clear")
    public String clearScan(@RequestParam int year,
                            @RequestParam int month,
                            RedirectAttributes ra) {
        adminService.clearPendings(year, month);
        ra.addFlashAttribute("msg", year + "-" + String.format("%02d", month) + " 비우기 완료");
        return "redirect:/admin/scan?year=" + year + "&month=" + month;
    }

    // 승인/반려 (현재 페이지 유지)
    @PostMapping("/scan/approve/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id,
                                        @RequestParam int year, @RequestParam int month,
                                        @RequestParam(defaultValue = "1") int page) {
        adminService.approvePending(id);
        return seeOther("/admin/scan?year=" + year + "&month=" + month + "&page=" + page);
    }

    @PostMapping("/scan/reject/{id}")
    public ResponseEntity<Void> reject(@PathVariable Long id,
                                       @RequestParam int year, @RequestParam int month,
                                       @RequestParam(defaultValue = "1") int page) {
        adminService.rejectPending(id);
        return seeOther("/admin/scan?year=" + year + "&month=" + month + "&page=" + page);
    }

    // 결제(승인된 항목 일괄 반영)
    @PostMapping("/scan/commit")
    public String commitMonthly(
            @RequestParam int year,
            @RequestParam int month,
            RedirectAttributes ra) {

        adminService.commitApproved(year, month);

        ra.addFlashAttribute("msg", year + "-" + String.format("%02d", month) + " 결제 완료");
        return "redirect:/admin";
    }

    // 규칙/제외/상한
    @PostMapping("/rules")
    public String upsertRule(@RequestParam Integer threshold,
                             @RequestParam Integer reward,
                             @RequestParam RewardType rewardType) {
        adminService.addOrUpdateRule(new RuleDTO(threshold, reward, rewardType));
        return "redirect:/admin";
    }

    @PostMapping("/rules/{threshold}/delete")
    public String deleteRule(@PathVariable Integer threshold) {
        adminService.deleteRule(threshold);
        return "redirect:/admin";
    }

    @PostMapping("/exclusions")
    public String saveExclusions(@RequestParam String excludedCategories) {
        adminService.saveExcludedCategories(excludedCategories);
        return "redirect:/admin";
    }

    @PostMapping("/cap")
    public String saveCap(@RequestParam Integer monthlyCap) {
        adminService.saveMonthlyCap(monthlyCap);
        return "redirect:/admin";
    }
}
