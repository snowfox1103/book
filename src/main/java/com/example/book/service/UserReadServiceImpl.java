package com.example.book.service;

import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class UserReadServiceImpl implements UserReadService {

    private final UsersRepository usersRepository;

    @Override
    public List<UserLite> listAllUsers() {
        // Users 엔티티의 필드명이 다르면 아래 두 부분만 맞춰주면 됨
        return usersRepository.findAll().stream()
                .map(u -> new UserLite(u.getUserNo(), u.getRealName()))
                .toList();
    }
}