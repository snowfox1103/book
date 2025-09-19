package com.example.book.dto;

import java.util.List;

public record PointSettingsDTO(
        Integer monthlyCap,
        List<RuleDTO> rules,
        List<String> excludedCategories
) {}
