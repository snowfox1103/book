package com.example.book.controller;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.finance.InOrOut;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.TransactionsDTO;
import com.example.book.security.dto.UsersSecurityDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/trans")
@Log4j2
@RequiredArgsConstructor
public class TransactionsController {
    private final TransactionsService transactionsService;
    private final CategoriesService categoriesService;
    @GetMapping("/transList")
    public void getTransList(@AuthenticationPrincipal UsersSecurityDTO users, PageRequestDTO pageRequestDTO, Model model){
        Long userNo = users.getUserNo();
        PageResponseDTO<TransactionsDTO> responseDTO = transactionsService.listByUser(userNo,pageRequestDTO);
        log.info(responseDTO);
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("users",userNo);
        model.addAttribute("active", "board");
        model.addAttribute("categories", categories);
        model.addAttribute("responseDTO",responseDTO);
        log.info("--------get list---------");
    }
    @GetMapping("/transRegister")
    public void getTransRegister(@AuthenticationPrincipal UsersSecurityDTO users,Model model){
        Long userNo = users.getUserNo();
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("users",userNo);
        model.addAttribute("categories", categories);
        model.addAttribute("inOrOutValues", InOrOut.values());
        log.info("--------------------get register------------------------------");
    }
    @PostMapping("/transRegister")
    public String registerPost(@Valid TransactionsDTO transactionsDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            log.info("has errors.............");
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/trans/transRegister";
        }
        log.info(transactionsDTO);
        Long tno = transactionsService.registerTrans(transactionsDTO);
        LocalDate date = transactionsDTO.getTransDate();
        transactionsService.autoUpdateBudgetCurrent(transactionsDTO.getUserNo(),transactionsDTO.getTransCategory(),date.getYear(),date.getMonthValue());
        //result message 안뜸 495p----------------------------------------------------------
        redirectAttributes.addFlashAttribute("result",tno);
        log.info("------------post register------------------");
        return "redirect:/trans/transList";
    }
    @GetMapping({"/transRead","/transModify"})//get mapping은 url을 쳤을 때 url 속에서 값 받아옴 예)/transRead?tno=3;이면 파라미터 tno에 3 저장
    public void read(@AuthenticationPrincipal UsersSecurityDTO users,Long tno,PageRequestDTO pageRequestDTO,Model model){ //url에서 tno값 받아오기, model 이용해서 view(html,jsp 등)에 전달
        Long userNo = users.getUserNo();
        List<Categories> categories = categoriesService.getCategoriesForUser(users.getUserNo());
        model.addAttribute("categories", categories);
        model.addAttribute("inOrOutValues", InOrOut.values()); //없어도 되는 듯
        TransactionsDTO transactionsDTO = transactionsService.readOneTrans(tno);
        log.info(transactionsDTO);
        model.addAttribute("users",userNo); //빼고 transDTO.userNo해도 될듯
        model.addAttribute("pageRequestDTO",pageRequestDTO);
        model.addAttribute("transDTO",transactionsDTO);
    }
    @PostMapping("/transModify")
    public String transModify(@Valid TransactionsDTO transactionsDTO,PageRequestDTO pageRequestDTO,BindingResult bindingResult,RedirectAttributes redirectAttributes){
        log.info("trans modify post------------------");
        if (bindingResult.hasErrors()){
            log.info("has errors............");
            log.info("errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("tno",transactionsDTO.getTransId());
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            String link = pageRequestDTO.getLink();
            return "redirect:/trans/transModify";
        }
        transactionsService.modifyTrans(transactionsDTO);
        LocalDate date = transactionsDTO.getTransDate();
//        TransactionsDTO updated = transactionsService.readOneTrans(transactionsDTO.getTransId());
        transactionsService.autoUpdateBudgetCurrent(transactionsDTO.getUserNo(),transactionsDTO.getTransCategory(),date.getYear(),date.getMonthValue());
        redirectAttributes.addAttribute("tno",transactionsDTO.getTransId());
        redirectAttributes.addFlashAttribute("result","modified");
        return "redirect:/trans/transRead";
    }
    @PostMapping("/transRemove")
    public String transRemove(Long transId, RedirectAttributes redirectAttributes){
        log.info("remove post...."+transId);
        TransactionsDTO transactionsDTO = transactionsService.readOneTrans(transId);
        transactionsService.removeTrans(transId);
        LocalDate date = transactionsDTO.getTransDate();
        transactionsService.autoUpdateBudgetCurrent(transactionsDTO.getUserNo(),transactionsDTO.getTransCategory(),date.getYear(),date.getMonthValue());
        redirectAttributes.addFlashAttribute("result","removed");
        return "redirect:/trans/transList";
    }
}
