package kr.co.softcampus.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.catalina.startup.WebAnnotationSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.softcampus.beans.UserBean;
import kr.co.softcampus.service.UserService;
import kr.co.softcampus.validator.UserValidator;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//세션 영역에 있는 userBean  을 주입받는다 
	@Resource(name = "loginUserBean")
	private UserBean loginUserBean; 
	
	@GetMapping("/login")
	public String login(@ModelAttribute("tempLoginUserBean") UserBean tempLoginUserBean,
						@RequestParam(value ="fail",defaultValue = "false") boolean fail,
						Model model) {
		//model 객체 에 넣어준다      value ="fail" => fail 이라는 값이 넘어오지 않으면  기본값 false
		
		model.addAttribute("fail",fail);
		
		return "user/login";
	}
	
	@PostMapping("/login_pro")
	public String login_pro(@Valid @ModelAttribute("tempLoginUserBean") UserBean tempLoginUserBean, BindingResult result) {
		
		if(result.hasErrors()) {
			return "user/login";
		}
		
		userService.getLoginUserInfo(tempLoginUserBean);
		
		//만들어진 객체 주소값 슉슉 가져다쓰기 세션영역 loginUserBean.setUserLogin(true); 정보 
		if(loginUserBean.isUserLogin() == true) {
			return "user/login_success";
		}else {
			return "user/login_fail";
		}
	}
		

	
	
  //joinuserean 주입 받아서 ModelAttribute 로 세팅 
	@GetMapping("/join")
	public String join(@ModelAttribute("joinUserBean") UserBean joinUserBean) {
		return "user/join";
	}
	
	@PostMapping("/join_pro")
	public String join_pro(@Valid @ModelAttribute("joinUserBean") UserBean joinUserBean, BindingResult result) {
		if(result.hasErrors()) {  
			return "user/join";
		}
		
		userService.addUserInfo(joinUserBean);
		
		return "user/join_success"; 
	}
	
	@GetMapping("/modify")
	public String modify(@ModelAttribute("modifyUserBean") UserBean modifyUserBean) {
		
		userService.getModifyUserInfo(modifyUserBean);//객체 주소값 전달 
		
		return "user/modify";
	}
	
	@PostMapping("/modify_pro")
	public String modify_pro(@Valid @ModelAttribute("modifyUserBean") UserBean modifyUserBean, BindingResult result) { 
		
		if(result.hasErrors()) {
			return "user/modify";
		}
		
		userService.modifyUserInfo(modifyUserBean);
		
		return "user/modify_success";
	} 
	
	@GetMapping("/logout")
	public String logout() {
		return "user/logout";
	}
	
	
	@GetMapping("/not_login")
	public String not_login() {
		return "user/not_login";
	}
	
	@InitBinder
	//특정 컨트롤러에서 바인딩 또는 검증 설정을 변경하고 싶을 때 사용
	public void intitBinder(WebDataBinder binder) {
		UserValidator validator1 = new UserValidator(); 
		binder.addValidators(validator1);
	}
	
	
}
