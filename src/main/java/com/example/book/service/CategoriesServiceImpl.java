package com.example.book.service;

import com.example.book.domain.finance.Budgets;
import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.example.book.dto.CategoriesDTO;
import com.example.book.dto.CategoryThresholdDTO;
import com.example.book.repository.BudgetsRepository;
import com.example.book.repository.CategoriesRepository;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
  private final UsersRepository usersRepository;
  private final CategoriesRepository categoriesRepository;
  private final ModelMapper modelMapper;
  private final BudgetsRepository budgetsRepository;

  @Override
  public void addCategory(Long userNo, String catName){
    log.info("addCategory ................");
    Users users = usersRepository.findByUserNo(userNo)
      .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    Categories categories = Categories.builder()
      .catName(catName)
      .users(users)
      .isSystemDefault(false)
      .build();

    categoriesRepository.save(categories);
  }

  @Override
  public Long registerCategories(CategoriesDTO categoriesDTO){
    Categories categories = modelMapper.map(categoriesDTO,Categories.class);
    Long catId = categoriesRepository.save(categories).getCatId();

    return catId;
  }

  @Override
  public void removeCategories(Long catId){
    categoriesRepository.deleteByCatId(catId);
  }

  @Override
  public List<Categories> categoriesList(Users users){
    return categoriesRepository.findAllByUsers_UserNo(users.getUserNo());
  }

  @Override
  public boolean existsByUserNoAndCatName(Long userNo, String catName) {
    return categoriesRepository.existsByUsers_UserNoAndCatName(userNo, catName);
  }

  @Override
  public void deleteCategory(Long userNo, Long catId) {
    categoriesRepository.deleteByCatIdAndUsers_UserNo(catId, userNo);
  }

  @Override
  public void updateCategory(Long catId, String catName) {
    categoriesRepository.updateCatNameByCatId(catId, catName);
  }

  @Override
  public List<Categories> getCategoriesForUser(Long userNo) {
    // 유저 카테고리
    List<Categories> userCategories = categoriesRepository.findByUsers_UserNo(userNo);
    // 시스템 기본 카테고리
    List<Categories> defaultCategories = categoriesRepository.findByIsSystemDefaultTrue();

    // 두 리스트 합치기
    List<Categories> all = new ArrayList<>();
    all.addAll(defaultCategories);
    all.addAll(userCategories);

    return all;
  }

  @Override
  public String getCatNameByCatId(Long catId) {
    Categories categories = categoriesRepository.findByCatId(catId)
      .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

    return categories.getCatName();
  }

  @Override
  public List<CategoryThresholdDTO> getCategoriesWithThreshold(Long userNo, int year, int month) {
    List<Budgets> budgets = budgetsRepository
      .findByUserNoAndBudYearAndBudMonthAndBudNoticeTrue(userNo, year, month);

    return budgets.stream()
      .map(budget -> new CategoryThresholdDTO(
        budget.getBudCategory(),      // 카테고리 ID
        getCategoryName(budget.getBudCategory()), // 카테고리 이름
        budget.getBudThreshold() != null ? budget.getBudThreshold() : 90 // 기본값 90
      ))
      .collect(Collectors.toList());
  }

  // 🔹 카테고리 이름 가져오기 (예시: Categories 테이블에서 조회)
  private String getCategoryName(Long catId) {
    return categoriesRepository.findById(catId)
      .map(Categories::getCatName)
      .orElse("알 수 없음");
  }
}
