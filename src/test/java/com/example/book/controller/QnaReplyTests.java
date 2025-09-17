package com.example.book.controller;

import com.example.book.service.QnaReplyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QnaReplyController.class)
class QnaReplyControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    QnaReplyService qnaReplyService;

    @Test
    @WithMockUser(username="admin1", roles="ADMIN")
    void managerCreateReply() throws Exception {
        Mockito.when(qnaReplyService.create(anyLong(), anyLong(), anyString())).thenReturn(101L);

        mvc.perform(post("/admin/qna/1/reply")
                        .param("content","안녕하세요"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna/1"));
    }

    @Test
    @WithMockUser(username="admin1", roles="ADMIN")
    void managerEditReply() throws Exception {
        mvc.perform(post("/admin/qna/reply/10/edit")
                        .param("qBId","1")
                        .param("content","수정합니다"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna/1"));
    }

    @Test
    @WithMockUser(username="admin1", roles="ADMIN")
    void managerDeleteReply() throws Exception {
        mvc.perform(post("/admin/qna/reply/10/delete")
                        .param("qBId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/qna/1"));
    }

    @Test
    @WithMockUser(username="user1", roles="USER")
    void UserForbiddenTest() throws Exception {
        mvc.perform(post("/admin/qna/1/reply")
                        .param("content","허용안됨"))
                .andExpect(status().isForbidden());
    }
}
