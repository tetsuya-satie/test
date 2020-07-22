package com.example.myproject.servingwebcontent;

import java.util.*;

import com.example.myproject.registrationform.RegistrationForm;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;


@Service
public class UserService{

  @Autowired
    JdbcTemplate jdbcTemplate;

  public Map<String, String> registration(){
    Map<String,String> result_roles = new HashMap<String,String>();
    String sql = "select * from role";
    List<Map<String,Object>> searched_roles = jdbcTemplate.queryForList(sql);
    for(Map<String,Object> role : searched_roles){
      result_roles.put((String)role.get("id"), (String)role.get("role_name"));
    }
    return result_roles;
  }

  private final String countSql = "select count(*) from user where id = ?";
  private final String user_role_insert_Sql = "insert into user_role(user_id, role_id) values (?, ?)";
  private final String user_insert_Sql = "insert into user(id, password) values (?, ?)";

  public RegistrationResult registrationSubmit(RegistrationForm form){
    RegistrationResult result = new RegistrationResult();
    if(!StringUtils.equals(form.getPassword1(), form.getPassword2())){
      result.setRegistration_success(false);
      result.setErr_msg("2つのパスワードが一致しません");
      return result;
    }
    if(jdbcTemplate.queryForObject(countSql,Integer.class,form.getName()) != 0){
      result.setRegistration_success(false);
      result.setErr_msg("該当ユーザは既登録です");
      return result;
    }
    /*
    * ユーザ登録処理(userとuser_role)
    */
    /*user_roleテーブル*/
    for(String role : form.getAuthorities()){
      jdbcTemplate.update(user_role_insert_Sql, form.getName(), role);
    }
    /*userテーブル*/
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    String password = encoder.encode(form.getPassword1());
    jdbcTemplate.update(user_insert_Sql, form.getName(), password);
    result.setRegistration_success(true);
    return result;
  }
}
