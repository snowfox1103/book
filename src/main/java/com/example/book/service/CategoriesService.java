package com.example.book.service;

import com.example.book.domain.Categories;
import com.example.book.dto.CategoriesDTO;

import java.util.List;

public interface CategoriesService{
    Long registerCategories(CategoriesDTO categoriesDTO);
    void removeCategories(Long catId);
    List<Categories> categoriesList();
}
