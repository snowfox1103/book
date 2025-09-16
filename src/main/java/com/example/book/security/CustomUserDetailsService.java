package com.example.book.security;

import com.example.book.domain.Users;
import com.example.book.repository.UsersRepository;
import com.example.book.security.dto.UsersSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UsersRepository usersRepository;
  private PasswordEncoder passwordEncoder;

//  public CustomUserDetailsService() {
//    this.passwordEncoder = new BCryptPasswordEncoder();
//  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername: " + username);
    Optional<Users> result = usersRepository.getWithRoles(username);

    if(result.isEmpty()) {
      throw new UsernameNotFoundException("username not found.........");
    }

    Users users = result.get();
    UsersSecurityDTO usersSecurityDTO = new UsersSecurityDTO(
      users.getRealName(),
      users.getUserId(),
      users.getPassword(),
      users.getEmail(),
      false,
      java.util.List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()))
    );
    log.info("userSecurityDTO");
    log.info(usersSecurityDTO);

    return usersSecurityDTO;
  }
}
