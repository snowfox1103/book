package com.example.book.controller;

import com.example.book.domain.point.PointType;
import com.example.book.domain.point.RewardType;
import com.example.book.service.AdminService;
import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')") 오류 체크를 위한 임시 해제
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("settings", adminService.getSettings());
        return "admin/list";
    }

    // 적립률 규칙 추가/저장
    @PostMapping("/rules")
    public String upsertRule(@RequestParam Integer threshold,
                             @RequestParam Integer reward,
                             @RequestParam RewardType rewardType) {
        adminService.addOrUpdateRule(new RuleDTO(threshold, reward, rewardType));
        return "redirect:/admin";
    }

    // 규칙 삭제
    @PostMapping("/rules/{threshold}/delete")
    public String deleteRule(@PathVariable Integer threshold) {
        adminService.deleteRule(threshold);
        return "redirect:/admin";
    }

    // 제외 카테고리 저장(쉼표 구분)
    @PostMapping("/exclusions")
    public String saveExclusions(@RequestParam String excludedCategories) {
        adminService.saveExcludedCategories(excludedCategories);
        return "redirect:/admin";
    }

    // 월 상한 저장
    @PostMapping("/cap")
    public String saveCap(@RequestParam Integer monthlyCap) {
        adminService.saveMonthlyCap(monthlyCap);
        return "redirect:/admin";
    }

    @GetMapping("/admin/points")
    public String pointsPage(Model model,
                             @RequestParam(required=false) Integer year,
                             @RequestParam(required=false) Integer month){
        model.addAttribute("settings", adminService.getSettings());
        model.addAttribute("pendings", adminService.listPendings());
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "admin/points/list"; // = list.html
    }

    @PostMapping("/admin/points/scan")
    public String scan(@RequestParam int year,
                       @RequestParam int month,
                       RedirectAttributes ra){
        int n = adminService.scanMonthlyAccruals(year, month);
        ra.addFlashAttribute("msg", year + "년 " + month + "월: " + n + "건 생성");
        return "redirect:/admin/points?year=" + year + "&month=" + month;
    }

    @PostMapping("/admin/points/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(required=false) Integer year,
                          @RequestParam(required=false) Integer month){
        adminService.approvePending(id);
        return "redirect:/admin/points" + (year!=null?("?year="+year+"&month="+month):"");
    }

    @PostMapping("/admin/points/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required=false) Integer year,
                         @RequestParam(required=false) Integer month){
        adminService.rejectPending(id);
        return "redirect:/admin/points" + (year!=null?("?year="+year+"&month="+month):"");
    }

}
