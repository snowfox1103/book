package com.example.book.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PwGen {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("admin123"));
    }
}