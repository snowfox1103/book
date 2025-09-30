package com.example.book.controller;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.example.book.domain.finance.Categories;
import com.example.book.dto.BudgetAlertDTO;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.security.dto.UsersSecurityDTO;
import com.example.book.service.BudgetsService;
import com.example.book.service.CategoriesService;
import com.example.book.service.TransactionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
@Log4j2
public class BudgetsController {
    private final TransactionsService transactionsService;
    private final BudgetsService budgetsService;
    private final CategoriesService categoriesService;
    @GetMapping("/currentList")
    public void currentList(@AuthenticationPrincipal UsersSecurityDTO users, PageRequestDTO pageRequestDTO, Model model){
        Long userNo = users.getUserNo();
        Long sumBudgets = budgetsService.wholeSetBudgetAmount(userNo);
        Long sumUses = budgetsService.budgetUses(userNo);
        PageResponseDTO<BudgetsDTO> pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        int nowY = LocalDate.now().getYear();
        int nowM = LocalDate.now().getMonthValue();
        List<BudgetsDTO> rows = pageResponseDTO.getDtoList().stream().filter(b -> b.getBudYear() == nowY && b.getBudMonth() == nowM).toList();
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("sumBudgets",sumBudgets);
        model.addAttribute("sumUses",sumUses);
        model.addAttribute("users",userNo);
        model.addAttribute("response",pageResponseDTO);
        model.addAttribute("rows", rows);
        model.addAttribute("hasData", !rows.isEmpty());
        model.addAttribute("categories",categories);
        log.info("-----------get currentList-----------");
    }
    @GetMapping("/budgetList")
    public void budgetList(@AuthenticationPrincipal UsersSecurityDTO users,PageRequestDTO pageRequestDTO, Model model){
        Long userNo = users.getUserNo();
        PageResponseDTO pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("users",userNo);
        model.addAttribute("bList",pageResponseDTO);
        model.addAttribute("categories",categories);
       log.info("---------get budgetList---------");
    }
    @GetMapping("/budgetRegister")
    public void budgetRegister1(@AuthenticationPrincipal UsersSecurityDTO users,Model model){
        log.info("----------get register----------");
        Long userNo = users.getUserNo();
        model.addAttribute("users",userNo);
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("categories",categories);
    }
    @PostMapping("/budgetRegister")
    public String postBudgetReg(@Valid BudgetsDTO budgetsDTO , BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            log.info("has errors...................");
            bindingResult.getFieldErrors().forEach(err -> {
                if(err.getField().equals("budAmount")) {
                    redirectAttributes.addFlashAttribute("budAmountError", "금액을 입력하세요");
                } else {
                    redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
                }
            });
            // 다시 등록 화면으로
            return "redirect:/budget/budgetRegister";
        }
        try{
            Long bno = budgetsService.registerBudget(budgetsDTO); //예산 등록
            // 등록된 예산의 입출금 내역 가져오기
            transactionsService.autoUpdateBudgetCurrent(budgetsDTO.getUserNo(),budgetsDTO.getBudCategory(),budgetsDTO.getBudYear(),budgetsDTO.getBudMonth());
            redirectAttributes.addFlashAttribute("result",bno);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("duplicateError", e.getMessage());
            return "redirect:/budget/budgetRegister";
        }catch (Exception e) {
            log.error("예산 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "예산 등록 중 오류가 발생했습니다.");
            return "redirect:/budget/budgetRegister";
        }
        log.info(budgetsDTO);
        log.info("post budget register...................");
        return "redirect:/budget/currentList";
    }

    @GetMapping({"/budgetRead","/budgetModify"})
    public void budgetRead(@AuthenticationPrincipal UsersSecurityDTO users,Long bno, PageRequestDTO pageRequestDTO,Model model){
        Long userNo = users.getUserNo();
        PageResponseDTO pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        BudgetsDTO budgetsDTO = budgetsService.readOneBudget(bno);
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("users",userNo);
        model.addAttribute("categories",categories);
        model.addAttribute("responseDTO",pageResponseDTO);
        model.addAttribute("budgetDTO",budgetsDTO);
        log.info("------------get read and modify-----------------");
    }
    @PostMapping("/budgetModify")
    public String budgetModify(@Valid BudgetsDTO budgetsDTO, PageRequestDTO pageRequestDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("budget modify post------------------");
        if (bindingResult.hasErrors()){
            log.info("has errors............");
            log.info("errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno",budgetsDTO.getBudgetId());
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            String link = pageRequestDTO.getLink();
            return "redirect:/budget/budgetModify";
        }
        budgetsService.modifyBudget(budgetsDTO);
        redirectAttributes.addAttribute("bno",budgetsDTO.getBudgetId());
        redirectAttributes.addFlashAttribute("result","modified");
        return "redirect:/budget/budgetRead";
    }
    @PostMapping("/budgetRemove")
    public String budgetRemove(Long budgetId, RedirectAttributes redirectAttributes){
        log.info("remove post...."+budgetId);
        budgetsService.removeBudget(budgetId);
        redirectAttributes.addFlashAttribute("result","removed");
        return "redirect:/budget/currentList";
    }

    //0926 조덕진 예산 알림용
    @GetMapping("/alerts")
    @ResponseBody
    public List<BudgetAlertDTO> getBudgetAlerts(@AuthenticationPrincipal UsersSecurityDTO authUser) {
        return budgetsService.getBudgetAlerts(authUser.getUserNo());
    }
}
