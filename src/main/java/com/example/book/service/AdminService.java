package com.example.book.service;

import com.example.book.dto.PointSettingsDTO;
import com.example.book.dto.RuleDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    // --- 임시 인메모리 저장소 (추후 JPA로 교체) ---
    private final Map<Integer, RuleDTO> rules = new TreeMap<>();
    @Getter
    private List<String> excluded = new ArrayList<>();
    @Getter
    private Integer monthlyCap = 500; // 기본값

    public PointSettingsDTO getSettings() {
        return new PointSettingsDTO(
                monthlyCap,
                new ArrayList<>(rules.values()),
                new ArrayList<>(excluded)
        );
    }

    public void addOrUpdateRule(RuleDTO dto) {
        rules.put(dto.threshold(), dto);
    }

    public void deleteRule(Integer threshold) {
        rules.remove(threshold);
    }

    public void saveExcludedCategories(String csv) {
        if (csv == null || csv.isBlank()) {
            excluded = new ArrayList<>();
        } else {
            excluded = Arrays.stream(csv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    public void saveMonthlyCap(Integer cap) {
        if (cap == null || cap < 0) cap = 0;
        monthlyCap = cap;
    }
}
