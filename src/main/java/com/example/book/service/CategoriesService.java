package com.example.book.service;

import com.example.book.domain.finance.Categories;
import com.example.book.domain.user.Users;
import com.example.book.dto.CategoriesDTO;

import java.util.List;

public interface CategoriesService {
  static class overExistsException extends Exception { }
  static class sameCategoryExistsException extends Exception { }
  void addCategory(Long userId, String catName);
  Long registerCategories(CategoriesDTO categoriesDTO);
  void removeCategories(Long catId);
  List<Categories> categoriesList(Users users);
  boolean existsByUserNoAndCatName(Long userNo, String catName);
  void deleteCategory(Long userNo, Long catId);
  void updateCategory(Long catId, String catName);
  List<Categories> getCategoriesForUser(Long userNo);
  }
