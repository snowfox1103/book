package com.example.book.service;

import com.example.book.dto.MemberJoinDTO;

public interface MemberService {
    static class MidExistException extends Exception{

    }
    void join(MemberJoinDTO memberJoinDTO)throws MidExistException;
}
