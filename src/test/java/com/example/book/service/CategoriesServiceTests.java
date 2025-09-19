package com.example.book.service;

import com.example.book.dto.CategoriesDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class CategoriesServiceTests {
    @Autowired
    private CategoriesService categoriesService;
    @Test
    public void testregistercat(){
        CategoriesDTO categoriesDTO = CategoriesDTO.builder()
                .catName("삥뽕")
                .userNo(43L)
                .isSystemDefault(false)
                .build();
        categoriesService.registerCategories(categoriesDTO);
    }
    @Test
    public void removecat(){
        categoriesService.removeCategories(16L);
    }
    @Test
    public void catList(){
        log.info(categoriesService.categoriesList());
    }
}