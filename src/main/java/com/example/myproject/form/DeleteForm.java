package com.example.myproject.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class DeleteForm {
	@NotNull
	@Size(min=5, max=20, message="ユーザ名は{min}文字以上{max}文字以下です")
	@Pattern(regexp="[a-zA-Z0-9]*", message="ユーザ名は英数字のみ使用できます")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
