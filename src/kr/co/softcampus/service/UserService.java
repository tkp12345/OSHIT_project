package kr.co.softcampus.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.softcampus.beans.UserBean;
import kr.co.softcampus.dao.UserDao;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Resource(name ="loginUserBean")
	private UserBean loginUserBean;
	//로그인한 사용자의 정보가 담겨있는 loginUserBean을 주입받음 
	
	public boolean checkuserIdExist(String user_id) {
		String user_name = userDao.checkUserIdExist(user_id);
		
		if(user_name == null) {
			return true;
		}else {
			return false;
		}
	}
	
	public void addUserInfo(UserBean joinUserBean) {
		userDao.addUserInfo(joinUserBean);
		
	}
	
	public void getLoginUserInfo(UserBean tempLoginUserBean) {
		//userDAO 정보 받아옴 
		UserBean tempLoginUserBean2 = userDao.getLoginUserInfo(tempLoginUserBean);
		
		//가져온 데이터가 있다면 
		if(tempLoginUserBean2 != null) {
			loginUserBean.setUser_idx(tempLoginUserBean2.getUser_idx());
			loginUserBean.setUser_name(tempLoginUserBean2.getUser_name());
			loginUserBean.setUserLogin(true);
			//=> 세션 영역에 저장된 빈에서 userlogin 값이 true 면 로그인 되있는거 
			
		}
		
		
		}
		public void getModifyUserInfo(UserBean modifyUserBean) { //컨트롤러 에서 준값 
			UserBean tempModifyUserBean = userDao.getModifyUserInfo(loginUserBean.getUser_idx());
		
			//매개변수로 들어오는 빈객체 (modifyUserBean) 에  데이터 정보 (tempModifyUserBean) 로 다넌다 
			modifyUserBean.setUser_id(tempModifyUserBean.getUser_id());
			modifyUserBean.setUser_name(tempModifyUserBean.getUser_name());
			modifyUserBean.setUser_idx(tempModifyUserBean.getUser_idx());
			
	
		
			
	}
		public void modifyUserInfo(UserBean modifyUserBean) {
			
			modifyUserBean.setUser_id(loginUserBean.getUser_id());
			userDao.modifyUserInfo(modifyUserBean);
		}
		
		


}
