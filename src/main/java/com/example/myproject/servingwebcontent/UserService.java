package com.example.myproject.servingwebcontent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myproject.form.DeleteForm;
import com.example.myproject.form.RegistrationForm;


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

  private final String user_delete_Sql = "delete from user where id = ?";
  private final String user_role_delete_Sql = "delete from user_role where user_id = ?";

  public DeleteResult deleteSubmit(DeleteForm form, User loginUser) {
	  DeleteResult result = new DeleteResult();
	  /*自分自身は削除できない*/
	  if(StringUtils.equals(form.getName(), loginUser.getUsername())) {
		  result.setDelete_success(false);
	      result.setErr_msg("自分自身を削除することはできません");
	      return result;
	  }
	  /*存在しないユーザ*/
	  if(jdbcTemplate.queryForObject(countSql,Integer.class,form.getName()) == 0){
	      result.setDelete_success(false);
	      result.setErr_msg("該当ユーザは存在しません");
	      return result;
	  }
	  /*ユーザ削除処理(userとuser_roleテーブル)*/
	  /*userテーブル*/
	  jdbcTemplate.update(user_delete_Sql, form.getName());
	  jdbcTemplate.update(user_role_delete_Sql, form.getName());
	  result.setDelete_success(true);
	  return result;
  }
}
