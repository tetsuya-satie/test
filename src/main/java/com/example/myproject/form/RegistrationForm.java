package com.example.myproject.form;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

public class RegistrationForm {

	@NotNull
	@Size(min=5, max=20, message="ユーザ名は{min}文字以上{max}文字以下です")
  @Pattern(regexp="[a-zA-Z0-9]*", message="ユーザ名は英数字のみ使用できます")
	private String name;

	@NotNull
  @Size(min=5, max=20, message="パスワードは{min}文字以上{max}文字以下です")
  @Pattern(regexp="[a-zA-Z0-9]*", message="パスワードは英数字のみ使用できます")
	private String password1;

  @NotNull
  @Size(min=5, max=20, message="パスワードは{min}文字以上{max}文字以下です")
  @Pattern(regexp="[a-zA-Z0-9]*", message="パスワードは英数字のみ使用できます")
	private String password2;

  private String[] authorities;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password) {
		this.password1 = password;
	}

  public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password) {
		this.password2 = password;
	}

  public String[] getAuthorities(){
    return this.authorities;
  }

  public void setAuthorities(String[] authorities){
    this.authorities = authorities;
  }

  @Override
  public String toString(){
    String aut_str = "";
    for(String authority : authorities){
      aut_str += authority;
    }
    return this.name + "," + this.password1 + "," + this.password2 + "," + aut_str;
  }

}
