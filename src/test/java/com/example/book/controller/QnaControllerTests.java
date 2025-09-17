package com.example.book.controller;

import com.example.book.domain.Qna;
import com.example.book.service.QnaReplyService;
import com.example.book.service.QnaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QnaController.class)
class QnaControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    QnaService qnaService;
    @MockitoBean
    QnaReplyService qnaReplyService;

    /** 뷰 파일 없어도 렌더링 오류 안 나게 더미 ViewResolver 등록 */
    @Configuration
    static class DummyViewConfig {
        @Bean
        ViewResolver viewResolver() {
            InternalResourceViewResolver r = new InternalResourceViewResolver();
            r.setPrefix("/templates/");
            r.setSuffix(".html");
            return r;
        }
    }

    @Test
    void testListUnauthorized() throws Exception {
        mvc.perform(get("/qna"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testList() throws Exception {
        Mockito.when(qnaService.listForUser(anyLong(), anyBoolean(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0,10), 0));

        mvc.perform(get("/qna"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testReadPublic() throws Exception {
        Qna q = Qna.builder().qBId(1L).userNo(1L).qBTitle("t").qBContent("c").qBBlind(false).build();
        Mockito.when(qnaService.getForRead(eq(1L), anyLong(), anyBoolean())).thenReturn(q);
        Mockito.when(qnaReplyService.list(1L)).thenReturn(List.of());

        mvc.perform(get("/qna/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("q","replies"));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testReadBlindByOtherNotFound() throws Exception {
        Mockito.when(qnaService.getForRead(eq(1L), anyLong(), anyBoolean()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mvc.perform(get("/qna/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testCreate() throws Exception {
        Mockito.when(qnaService.create(anyLong(), anyString(), anyString(), anyBoolean()))
                .thenReturn(10L);

        mvc.perform(post("/qna/new")
                        .param("title","t")
                        .param("content","c"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna/10"));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testUpdateByOwner() throws Exception {
        Mockito.doNothing().when(qnaService).update(eq(1L), anyLong(), anyString(), anyString(), anyBoolean());

        mvc.perform(post("/qna/1/edit")
                        .param("title","t2")
                        .param("content","c2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna/1"));
    }

    @Test
    @WithMockUser(username="user2", roles="USER")
    void testUpdateForbiddenByOther() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(qnaService).update(eq(1L), anyLong(), anyString(), anyString(), anyBoolean());

        mvc.perform(post("/qna/1/edit")
                        .param("title","t2")
                        .param("content","c2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void testDeleteByOwner() throws Exception {
        Mockito.doNothing().when(qnaService).delete(eq(1L), anyLong());

        mvc.perform(post("/qna/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna"));
    }
}
