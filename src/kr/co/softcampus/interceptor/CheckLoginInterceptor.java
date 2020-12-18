package kr.co.softcampus.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import kr.co.softcampus.beans.UserBean;

public class CheckLoginInterceptor implements HandlerInterceptor{
	
	private UserBean loginUserBean;
	
	public CheckLoginInterceptor(UserBean loginUserBean) {
		this.loginUserBean = loginUserBean;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		if(loginUserBean.isUserLogin() == false) {
			String contextPath= request.getContextPath();
			response.sendRedirect(contextPath + "/user/not_login");
			return false; //false 하면 여기서 딱끝난다  로그인안했으므로 여서 끝 
		}
		return true; //로그인 했으므로 통과
	}
}
