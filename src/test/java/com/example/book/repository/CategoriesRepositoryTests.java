package com.example.book.repository;

import com.example.nyjbook.domain.Categories;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class CategoriesRepositoryTests {
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Test
    public void testinsertcat(){
        Categories categories = Categories.builder()
                .userNo(1L)
                .catName("모임 통장")
                .isSystemDefault(false)
                .build();
        categoriesRepository.save(categories);
    }
    @Test
    public void testremovecat(){
        categoriesRepository.deleteById(14L);
    }
}