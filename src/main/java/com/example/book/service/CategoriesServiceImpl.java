package com.example.book.service;

import com.example.book.domain.Categories;
import com.example.book.dto.CategoriesDTO;
import com.example.book.repository.CategoriesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CategoriesServiceImpl implements CategoriesService{
    private final CategoriesRepository categoriesRepository;
    private final ModelMapper modelMapper;
    @Override
    public Long registerCategories(CategoriesDTO categoriesDTO){
        Categories categories = modelMapper.map(categoriesDTO,Categories.class);
        Long catId = categoriesRepository.save(categories).getCatId();
        return catId;
    }
    @Override
    public void removeCategories(Long catId){
        categoriesRepository.deleteById(catId);
    }
    @Override
    public List<Categories> categoriesList(){
        return categoriesRepository.findAll();
    }
}
