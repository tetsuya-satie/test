package com.example.myproject.securingweb;
import java.util.Collection;
import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    //adminだけ認証する
    if (username == null || !"admin".equals(username)) {
      throw new UsernameNotFoundException("Not found username : " + username);
    }

    //パスワードの設定
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    String password = encoder.encode("adminpassword");

    //権限の設定
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

    //ユーザー情報を作成
    User user = new User(username, password, authorities);
    return user;
  }
}
