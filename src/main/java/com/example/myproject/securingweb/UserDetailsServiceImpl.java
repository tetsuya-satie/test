package com.example.myproject.securingweb;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
    JdbcTemplate jdbcTemplate;

    private final String sql = "select user.id, user.password, role.role_name "+
                                "from user left outer join user_role on user.id = user_role.user_id "+
                                "left outer join role on user_role.role_id = role.id where user.id = ?";

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    List<Map<String,Object>> searched_user_data = jdbcTemplate.queryForList(sql, username);

    if (username == null || searched_user_data.size() == 0) {
      throw new UsernameNotFoundException("Not found username : " + username);
    }

    //パスワードの設定
    String password = (String)(searched_user_data.get(0).get("password"));

    //権限の設定
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    for(Map<String,Object> data : searched_user_data){
      authorities.add(new SimpleGrantedAuthority("ROLE_"+(String)data.get("role_name")));
    }

    //ユーザー情報を作成
    User user = new User(username, password, authorities);
    return user;
  }
}
