package com.example.myproject.servingwebcontent;

import java.util.ArrayList;
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

import com.example.myproject.data.UserInfo;
import com.example.myproject.form.DeleteForm;
import com.example.myproject.form.RegistrationForm;
import com.example.myproject.form.SearchForm;
import com.example.myproject.result.DeleteResult;
import com.example.myproject.result.RegistrationResult;
import com.example.myproject.result.SearchResult;


@Service
public class UserService{

  @Autowired
    JdbcTemplate jdbcTemplate;

  public Map<String, String> initAuthorities(){
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


  private static final String[] alphabets = {"a", "b", "c", "d", "e", "f", "g", "h"};
  public SearchResult search(SearchForm searchForm) {
	  SearchResult result = new SearchResult();
	  /*検索の条件を作る。ここから*/
	  String search_sql = "select user.id, user.password, role.role_name "+
              "from user left outer join user_role on user.id = user_role.user_id "+
              "left outer join role on user_role.role_id = role.id ";
	  List<Object> placeholder = new ArrayList<Object>();
	  //検索条件として権限が入力されている
	  if(searchForm.getAuthorities().length != 0) {
		  String[] searchAuthorities = searchForm.getAuthorities();
		  int k = 0;
		  search_sql += ("join (" +
		  		"	select " + alphabets[k] + ".user_id from (" +
		  		"		select user.id as user_id from user left outer join user_role on user.id = user_role.user_id" +
		  		"		where user_role.role_id = ?) as " + alphabets[k]);
		  placeholder.add(searchAuthorities[k]);
		  k++;
		  for(; k < searchForm.getAuthorities().length; k++) {
			  search_sql += (" join (" +
			  		"			select user.id as user_id from user left outer join user_role on user.id = user_role.user_id" +
			  		"			where user_role.role_id = ?) as " + alphabets[k] + " " +
			  		"		on "+ alphabets[k-1] +".user_id = "+ alphabets[k] +".user_id");
			  placeholder.add(searchAuthorities[k]);
		  }
		  search_sql += ") as aut on user.id = aut.user_id ";
	  }
	  //検索条件としてnameが入力されている
	  if(!searchForm.getName().isEmpty()) {
		  placeholder.add(searchForm.getName());
		  search_sql += " where user.id = ? ";
	  }
	  search_sql += " order by user.id asc";
	  Object[] placeholderArray = placeholder.toArray();
	  List<Map<String,Object>> searched_users = jdbcTemplate.queryForList(search_sql, placeholderArray);
	  /*ここまで*/

	  List<UserInfo> result_users = new ArrayList<UserInfo>();
	  int j = 1;
	  for(int i = 0 ; i < searched_users.size(); i++) {
		  UserInfo user = new UserInfo();
		  Map<String, Object> user_map = searched_users.get(i);
		  user.setNo(String.valueOf(j++));
		  user.setId((String)user_map.get("id"));
		  user.setAuthorities((String)user_map.get("role_name"));
		  while(i+1 < searched_users.size()
				  && StringUtils.equals(user.getId(), (String)searched_users.get(i+1).get("id"))) {
			  user.addAuthorities((String)searched_users.get(i+1).get("role_name"));
			  i++;
		  }
		  result_users.add(user);
	  }
	  result.setUsers(result_users);
	  return result;
  }

}
