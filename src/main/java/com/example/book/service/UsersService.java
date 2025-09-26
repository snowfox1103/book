package com.example.book.service;

import com.example.book.domain.user.Users;
import com.example.book.dto.*;

public interface UsersService {
  static class userIdExistsException extends Exception { }
  static class emailExistsException extends Exception { }
  Users register(UsersDTO usersDTO) throws userIdExistsException, emailExistsException;
  void unRegister(UsersDTO usersDTO);
  void passwordModify(UsersDTO usersDTO);
  void emailModify(UsersDTO usersDTO) throws emailExistsException;
  void changePassword(String userId, PasswordChangeRequestDTO req);
  void changeEmail(String userId, EmailChangeRequestDTO req);
  void resend(ResendRequestDTO req);
  void idSearch(IdSearchRequestDTO req);
  void pwSearch(PwSearchRequestDTO req);
  String getUserIdByUserNo(Long userNo);
}
