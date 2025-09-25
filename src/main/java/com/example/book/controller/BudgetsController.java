package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.service.BudgetsService;
import com.example.book.service.CategoriesService;
import com.example.book.service.TransactionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
@Log4j2
public class BudgetsController {
    private final TransactionsService transactionsService;
    private final BudgetsService budgetsService;
    private final CategoriesService categoriesService;
    @GetMapping("/currentList")
    public void currentList(Users users, PageRequestDTO pageRequestDTO, Model model){
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        PageResponseDTO pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        List<Categories> categories = categoriesService.categoriesList(users);
        model.addAttribute("users",userNo);
        model.addAttribute("response",pageResponseDTO);
        model.addAttribute("categories",categories);
        log.info("-----------get currentList-----------");
    }
    @GetMapping("/budgetList")
    public void budgetList(Users users, PageRequestDTO pageRequestDTO, Model model){
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        PageResponseDTO pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        List<Categories> categories = categoriesService.categoriesList(users);
        model.addAttribute("users",userNo);
        model.addAttribute("bList",pageResponseDTO);
        model.addAttribute("categories",categories);
       log.info("---------get budgetList---------");
    }
    @GetMapping("/budgetRegister")
    public void budgetRegister1(Users users,Model model){
        log.info("----------get register----------");
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        model.addAttribute("users",userNo);
        List<Categories> categories = categoriesService.categoriesList(users);
        model.addAttribute("categories",categories);
    }
    @PostMapping("/budgetRegister")
    public String postBudgetReg(@Valid BudgetsDTO budgetsDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.info("has errors...................");
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/budget/budgetRegister";
        }
        try{
            Long bno = budgetsService.registerBudget(budgetsDTO);
            transactionsService.autoUpdateBudgetCurrentByCategory(budgetsDTO.getBudCategory(),budgetsDTO.getUserNo());
            redirectAttributes.addFlashAttribute("result",bno);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/budget/budgetRegister";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "예산 등록 중 오류가 발생했습니다.");
            return "redirect:/budget/budgetRegister";
        }
        log.info(budgetsDTO);
        log.info("post budget register...................");
        return "redirect:/budget/currentList";
    }

    @GetMapping({"/budgetRead","/budgetModify"})
    public void budgetRead(Users users,Long bno, PageRequestDTO pageRequestDTO,Model model){
//        Long userNo = users.getUserNo();
        Long userNo = 1L;
        PageResponseDTO pageResponseDTO = budgetsService.budgetListByUser(userNo,pageRequestDTO);
        BudgetsDTO budgetsDTO = budgetsService.readOneBudget(bno);
        List<Categories> categories = categoriesService.categoriesList(users);
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
}
