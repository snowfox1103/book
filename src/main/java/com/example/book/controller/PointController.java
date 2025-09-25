package com.example.book.controller;

import com.example.book.dto.PointListDTO;
import com.example.book.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping({"/point", "/point/list"})
    public String list(
            @RequestParam(defaultValue = "date") String sort,   // date | type | delta
            @RequestParam(defaultValue = "desc") String dir,    // asc | desc
            @RequestParam(defaultValue = "0")    int page,      // 0-based
            @RequestParam(defaultValue = "10")   int size,      // page size
            Model model
    ) {
        // UI sort→엔티티 필드 매핑 (delta는 서비스에서 전용 쿼리 사용)
        String sortProp = switch (sort == null ? "date" : sort.toLowerCase()) {
            case "type"  -> "pointType";
            case "date"  -> "pointStartDate";
            default      -> "pointStartDate";
        };
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProp));

        // TODO: 실제 로그인 사용자 PK로 교체
        Long userNo = 1L;

        // 페이지 조회(서비스가 delta 정렬/잔액 계산까지 처리)
        Page<PointListDTO> p = pointService.findPointPage(userNo, pageable, sort, dir);

        // ===== 페이지 블록(10개씩) 계산 =====
        int blockSize = 10;
        int totalPages = p.getTotalPages();
        int nowPage0 = p.getNumber(); // 0-based

        int startBlock0 = (nowPage0 / blockSize) * blockSize;                      // 0-based
        int endBlock0Exclusive = Math.min(startBlock0 + blockSize, totalPages);    // 0-based exclusive

        boolean hasPrevBlock = startBlock0 > 0;
        boolean hasNextBlock = endBlock0Exclusive < totalPages;

        // ===== 모델 바인딩 =====
        model.addAttribute("page", p);
        model.addAttribute("pointList", Map.of("sort", sort, "dir", dir));

        // 블록 페이지네이션용 모델 (템플릿은 1-based로 출력)
        model.addAttribute("startPage", startBlock0 + 1);          // 1-based
        model.addAttribute("endPage", endBlock0Exclusive);         // 1-based (inclusive)
        model.addAttribute("now", nowPage0 + 1);                   // 1-based 표시용
        model.addAttribute("hasPrevBlock", hasPrevBlock);
        model.addAttribute("hasNextBlock", hasNextBlock);
        model.addAttribute("prevPage", startBlock0);               // 링크용: 이전 블록 시작(0-based)
        model.addAttribute("nextPage", endBlock0Exclusive + 1);    // 링크용: 다음 블록 첫 페이지(1-based) -> 템플릿에서 -1 처리

        return "point/list";
    }

    @GetMapping("/point/setting")
    public String setting() {
        return "point/setting";
    }
}
