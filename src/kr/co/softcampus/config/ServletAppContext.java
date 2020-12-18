package kr.co.softcampus.config;

import javax.annotation.Resource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.softcampus.beans.UserBean;
import kr.co.softcampus.interceptor.CheckLoginInterceptor;
import kr.co.softcampus.interceptor.TopMenuInterceptor;
import kr.co.softcampus.mapper.BoardMapper;
import kr.co.softcampus.mapper.TopMenuMapper;
import kr.co.softcampus.mapper.UserMapper;
import kr.co.softcampus.service.TopMenuService;
/*
 *  1. 예제처럼 @EnableWebMvc 어노테이션과 WebMvcConfigurer 인터페이스를 구현하는 방식.

 2. @Configuration과 WebMvcConfigurationSupport 클래스를 상속하는 방식
  -> 일반 @Configuration 우선순위가 구분되지 않는 경우에 사용한다고함.
 */


//Spring MVC 프로젝트에 관련된 설정을 하는 클래스
@Configuration
//Controller 어노테이션이 셋팅되어 있는 클래스를 Controller로 등록한다.
@EnableWebMvc
//스캔할 패키지를 지정한다. 
@ComponentScan("kr.co.softcampus.controller")
@ComponentScan("kr.co.softcampus.dao")
@ComponentScan("kr.co.softcampus.service")


@PropertySource("/WEB-INF/properties/db.properties")
public class ServletAppContext implements WebMvcConfigurer{
	
	
	@Value("${db.classname}")
	private String db_classname;
	
	@Value("${db.url}")
	private String db_url;
	
	@Value("${db.username}")
	private String db_username;
	
	@Value("${db.password}")
	private String db_password;
	
	@Autowired
	private TopMenuService topMenuService;
	
	@Resource(name = "loginUserBean")
	private UserBean loginUserBean; 
	
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		WebMvcConfigurer.super.configureViewResolvers(registry);
		registry.jsp("/WEB-INF/views/", ".jsp");
	}
	
	// 정적 파일의 경로를 매핑한다 
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);
		registry.addResourceHandler("/**").addResourceLocations("/resources/");
	}
	
	//데이터베이스 접속 정보를 관리하는 Bean
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName(db_classname);
		source.setUrl(db_url);
		source.setUsername(db_username);
		source.setPassword(db_password);
		
		return source;
	}
	
	// SqlSessionFactory : 쿼리문과 접속 정보를 관리하는 객체 
	// 쿼리실행시 SqlSessionFactory  주입받아서 사용 
	// return 값 (factory) 를  MapperFactoryBean 으로 주입 
	@Bean
	public SqlSessionFactory factory(BasicDataSource source) throws Exception{
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(source);
		SqlSessionFactory factory = factoryBean.getObject();
		return factory;
	}
	
	//쿼리문실행 (Mapper 관리)
	@Bean
	public MapperFactoryBean<BoardMapper> getBoardMapper(SqlSessionFactory factory) throws Exception{
		MapperFactoryBean<BoardMapper> factoryBean = new MapperFactoryBean<BoardMapper>(BoardMapper.class);
		factoryBean.setSqlSessionFactory(factory);
		return factoryBean;
 
	}
	
	@Bean
	public MapperFactoryBean<TopMenuMapper> getTopMenuMapper(SqlSessionFactory factory) throws Exception{
		MapperFactoryBean<TopMenuMapper> factoryBean = new MapperFactoryBean<TopMenuMapper>(TopMenuMapper.class);
		factoryBean.setSqlSessionFactory(factory);
		return factoryBean;
 
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addInterceptors(registry);
		
		TopMenuInterceptor topMenuInterceptor = new TopMenuInterceptor(topMenuService ,loginUserBean);
		
		InterceptorRegistration reg1 = registry.addInterceptor(topMenuInterceptor);
		reg1.addPathPatterns("/**"); //모든요청주소에  인터셉터 통과해야함 
		
		//*로그인 해야지만 접근가능하게 인터셉터 등록 
		
		
		CheckLoginInterceptor checkLoginInterceptor = new CheckLoginInterceptor(loginUserBean);
		InterceptorRegistration reg2 = registry.addInterceptor(checkLoginInterceptor);
		reg2.addPathPatterns("/user/modify" , "/user/logout", "/board/*"); //접근금지 페이지 
		reg2.excludePathPatterns("/board/main");//접근금지 예외처리 
		
	}
	//@value 사용하기 위한 설정 
	@Bean 
	public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	//위아래가 따로 관리됨 
	
	//에러메세지 등록
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource res = new ReloadableResourceBundleMessageSource();
		res.setBasename("/WEB-INF/properties/error_message");
		return res; 
	}
	
	@Bean
	public MapperFactoryBean<UserMapper> getUserMapper(SqlSessionFactory factory) throws Exception{
		MapperFactoryBean<UserMapper> FactoryBean = new MapperFactoryBean<UserMapper>(UserMapper.class);
		FactoryBean.setSqlSessionFactory(factory);
		return FactoryBean;
 
	} 
	
	@Bean
	public StandardServletMultipartResolver multipartResolver() { 
		return new StandardServletMultipartResolver();
	}
	
}







