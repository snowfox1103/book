package com.example.book.controller;

import com.example.book.dto.PendingPointDTO;
import com.example.book.service.PointScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/points")
public class AdminPointsController {

    private final PointScanService pointScanService;

    /** 스캔 화면 */
    @GetMapping("/scan")
    public String scanPage(@RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer month,
                           Model model) {

        LocalDate now = LocalDate.now();
        int y = (year  == null ? now.getYear()       : year);
        int m = (month == null ? now.getMonthValue() : month);

        List<PendingPointDTO> pendings = pointScanService.listPending();

        model.addAttribute("year", y);
        model.addAttribute("month", m);
        model.addAttribute("pendings", pendings);

        // 템플릿 경로: src/main/resources/templates/admin/scan.html
        return "admin/scan";
    }

    /** 해당 연/월 거래 스캔 → 승인 대기 생성 */
    @PostMapping("/scan")
    public String runScan(@RequestParam int year,
                          @RequestParam int month,
                          Model model) {

        int created = pointScanService.scanMonth(year, month);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("message", year + "-" + month + " 스캔 완료: " + created + "건 생성");
        model.addAttribute("pendings", pointScanService.listPending());

        return "admin/scan";
    }

    /** 승인 */
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id,
                          @RequestParam int year,
                          @RequestParam int month) {

        pointScanService.approve(id);
        return "redirect:/admin/points/scan?year=" + year + "&month=" + month;
    }

    /** 반려 */
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         @RequestParam int year,
                         @RequestParam int month) {

        pointScanService.reject(id);
        return "redirect:/admin/points/scan?year=" + year + "&month=" + month;
    }
}
