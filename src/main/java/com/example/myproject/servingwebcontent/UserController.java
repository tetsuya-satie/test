package com.example.myproject.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

@Controller
public class UserController {

	@GetMapping("/home")
	public String home(){
		return "home";
	}

	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal User user, Model model){
		model.addAttribute("name", user.getUsername());
		return "mypage";
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}
}
