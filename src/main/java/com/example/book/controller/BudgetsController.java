package com.example.book.controller;

import com.example.book.domain.Categories;
import com.example.book.dto.BudgetsDTO;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.service.BudgetsService;
import com.example.book.service.CategoriesService;
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
    private final BudgetsService budgetsService;
    private final CategoriesService categoriesService;
    @GetMapping("/currentList")
    public void currentList(PageRequestDTO pageRequestDTO,Model model){
        PageResponseDTO pageResponseDTO = budgetsService.budgetList(pageRequestDTO);
        List<Categories> categories = categoriesService.categoriesList();
        model.addAttribute("response",pageResponseDTO);
        model.addAttribute("categories",categories);
        log.info("-----------get currentList-----------");
    }
    @GetMapping("/budgetList")
    public void budgetList(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO pageResponseDTO = budgetsService.budgetList(pageRequestDTO);
        List<Categories> categories = categoriesService.categoriesList();
        model.addAttribute("bList",pageResponseDTO);
        model.addAttribute("categories",categories);
       log.info("---------get budgetList---------");
    }
    @GetMapping("/budgetRegister")
    public void budgetRegister1(Model model){
        log.info("----------get register----------");
        List<Categories> categories = categoriesService.categoriesList();
        model.addAttribute("categories",categories);
    }
    @PostMapping("/budgetRegister")
    public String postBudgetReg(@Valid BudgetsDTO budgetsDTO, RedirectAttributes redirectAttributes, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.info("has errors...................");
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/budget/budgetRegister";
        }
        log.info(budgetsDTO);
        Long bno = budgetsService.registerBudget(budgetsDTO);
        redirectAttributes.addFlashAttribute("result",bno);
        log.info("post budget register...................");
        return "redirect:/budget/currentList";
    }

    @GetMapping({"/budgetRead","/budgetModify"})
    public void budgetRead(PageRequestDTO pageRequestDTO,Model model){
        log.info("------------get read and modify-----------------");
    }

}
