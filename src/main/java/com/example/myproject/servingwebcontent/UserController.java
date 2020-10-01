package com.example.myproject.servingwebcontent;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myproject.data.UserInfo;
import com.example.myproject.form.DeleteForm;
import com.example.myproject.form.RegistrationForm;
import com.example.myproject.form.SearchForm;
import com.example.myproject.result.DeleteResult;
import com.example.myproject.result.RegistrationResult;


@Controller
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/home")
	public String home(){
		return "home";
	}

	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal User user, Model model){
		model.addAttribute("name", user.getUsername());
		model.addAttribute("authorities", user.getAuthorities());
		model.addAttribute("isADMIN", user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		return "mypage";
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}

	@GetMapping("/registration")
	public String registration(RegistrationForm registrationForm, Model model){
		Map<String,String> roles = userService.initAuthorities();
		model.addAttribute("checkAuthorities", roles);
		return "registration";
	}

	@PostMapping("/registration")
	public String registrationSubmit(@Valid RegistrationForm registrationForm, BindingResult bindingResult, Model model){
		System.out.println(registrationForm);
		Map<String,String> roles = userService.initAuthorities();
		model.addAttribute("checkAuthorities", roles);

		//入力チェックによるエラー
		if (bindingResult.hasErrors()) {
			model.addAttribute("sof","登録失敗");
			return "registration";
		}
		/*ここでサービス層registrationSubmitを呼んで必要な処理をさせたい
		* registrationFormを渡す
		* 登録の成否、否ならその理由を返して欲しい
		*/
		RegistrationResult result = userService.registrationSubmit(registrationForm);
		//フォーム入力形式に問題はないが登録できない場合
		if(!result.getRegistration_success()){
			model.addAttribute("sof","登録失敗");
			model.addAttribute("msg", result.getErr_msg());
		}
		else{
			model.addAttribute("sof","登録成功");
		}
		return "registration";
	}

	@GetMapping("/delete")
	public String delete(DeleteForm deleteForm) {
		return "delete";
	}

	@PostMapping("/delete")
	public String deleteSubmit(@Valid DeleteForm deleteForm, BindingResult bindingResult,
			@AuthenticationPrincipal User user, Model model) {
		//入力チェックによるエラー
		if (bindingResult.hasErrors()) {
			model.addAttribute("sof","削除失敗");
			return "delete";
		}
		DeleteResult result = userService.deleteSubmit(deleteForm,user);
		if(result.isDelete_success()) {
			model.addAttribute("sof", "削除成功");
		}
		else {
			model.addAttribute("sof", "削除失敗");
			model.addAttribute("msg", result.getErr_msg());
		}
		return "delete";
	}

	@GetMapping("/search")
	public String search(SearchForm searchForm, Model model) {
		System.out.println("HereHere");
		Map<String, String> authorities = userService.initAuthorities();
		model.addAttribute("checkAuthorities", authorities);
		return "search";
	}

	@PostMapping("/search")
	public String searchSubmit(SearchForm searchForm, Model model) {
		List<UserInfo> resultUsers = userService.search(searchForm).getUsers();
		Map<String, String> authorities = userService.initAuthorities();
		model.addAttribute("users", resultUsers);
		model.addAttribute("search_called", true);
		model.addAttribute("checkAuthorities", authorities);
		return "search";
	}

	@GetMapping("/403")
	public String accessDenied(){
		return "403";
	}
}
