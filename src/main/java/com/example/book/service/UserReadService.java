package com.example.book.service;

import java.util.List;

public interface UserReadService {
    List<UserLite> listAllUsers();
    record UserLite(Long userNo, String username) {}
}