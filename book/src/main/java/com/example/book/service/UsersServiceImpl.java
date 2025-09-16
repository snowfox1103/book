package com.example.book.service;

import com.example.book.domain.MemberRole;
import com.example.book.domain.Users;
import com.example.book.dto.UsersDTO;
import com.example.book.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
  private final ModelMapper modelMapper;
  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Users register(UsersDTO usersDTO) throws userIdExistsException, emailExistsException {
    String userId = usersDTO.getUserId();
    String email = usersDTO.getEmail();
    boolean existId = usersRepository.existsByUserId(userId);
    boolean existEmail = usersRepository.existsByEmail(email);

    if(existId) {
      throw new userIdExistsException();
    } else if(existEmail) {
      throw new emailExistsException();
    }

//    Users users = modelMapper.map(usersDTO, Users.class);
    Users users = Users.builder()
      .role(MemberRole.USER)
      .realName(usersDTO.getRealName())
      .userId(usersDTO.getUserId())
      .email(usersDTO.getEmail())
      .password(passwordEncoder.encode(usersDTO.getPassword()))
      .build();

//    users.changePassword(passwordEncoder.encode(usersDTO.getPassword()));
//    users.setRole(MemberRole.USER);
    log.info("============================");
    log.info(users);
    log.info(users.getRole());

    usersRepository.save(users);

    return users;
  }

  @Override
  public void unRegister(UsersDTO usersDTO) {
    String userId = usersDTO.getUserId();

    log.info("unRegister userId: " + userId);
    usersRepository.deleteByUserId(userId);
  }

  @Override
  public void passwordModify(UsersDTO usersDTO) {
    String userId = usersDTO.getUserId();
    Users users = modelMapper.map(usersDTO, Users.class);

    log.info("changePassword userId: " + userId);
    users.changePassword(passwordEncoder.encode(usersDTO.getPassword()));
    usersRepository.save(users);
  }

  @Override
  public void emailModify(UsersDTO usersDTO) throws emailExistsException {
    String userId = usersDTO.getUserId();
    Users users = modelMapper.map(usersDTO, Users.class);

    log.info("changeEmail userId: " + userId);
    users.changeEmail(usersDTO.getEmail());
    usersRepository.save(users);
  }
}
