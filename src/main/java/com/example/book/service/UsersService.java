package com.example.book.service;

import com.example.book.domain.user.Users;
import com.example.book.dto.EmailChangeRequest;
import com.example.book.dto.PasswordChangeRequest;
import com.example.book.dto.UsersDTO;

public interface UsersService {
  static class userIdExistsException extends Exception { }
  static class emailExistsException extends Exception { }
  Users register(UsersDTO usersDTO) throws userIdExistsException, emailExistsException;
  void unRegister(UsersDTO usersDTO);
  void passwordModify(UsersDTO usersDTO);
  void emailModify(UsersDTO usersDTO) throws emailExistsException;
  void changePassword(String userId, PasswordChangeRequest req);
  void changeEmail(String userId, EmailChangeRequest req);
}
