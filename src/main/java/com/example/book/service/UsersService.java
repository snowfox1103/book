package com.example.book.service;

import com.example.book.domain.Users;
import com.example.book.dto.UsersDTO;

public interface UsersService {
  static class userIdExistsException extends Exception { }
  static class emailExistsException extends Exception { }
  Users register(UsersDTO usersDTO) throws userIdExistsException, emailExistsException;
  void unRegister(UsersDTO usersDTO);
  void passwordModify(UsersDTO usersDTO);
  void emailModify(UsersDTO usersDTO) throws emailExistsException;
}
