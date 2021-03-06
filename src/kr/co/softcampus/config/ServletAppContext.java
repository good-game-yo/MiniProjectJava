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
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.softcampus.beans.UserBean;
import kr.co.softcampus.interceptor.CheckLogininterceptor;
import kr.co.softcampus.interceptor.CheckWriterInterceptor;
import kr.co.softcampus.interceptor.TopMenuInterceptor;
import kr.co.softcampus.mapper.BoardMapper;
import kr.co.softcampus.mapper.TopMenuMapper;
import kr.co.softcampus.mapper.UserMapper;
import kr.co.softcampus.service.BoardService;
import kr.co.softcampus.service.TopMenuService;

// Spring MVC 프로젝트에 관련된 설정을 하는 클래스
@Configuration
// Controller 어노테이션이 셋팅되어 있는 클래스를 Controller로 등록한다.
@EnableWebMvc
// 스캔할 패키지를 지정한다.
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
	
	//TopMenuInterceptor에서는 autowired를 사용할 수 없으니 여기서 만들어서 넘겨준다.
	@Autowired
	private TopMenuService topMenuService;
	
	@Resource(name="loginUserBean")
	private UserBean loginUserBean;
	
	@Autowired
	private BoardService boardService;
	
	// Controller의 메서드가 반환하는 jsp의 이름 앞뒤에 경로와 확장자를 붙혀주도록 설정한다.
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.configureViewResolvers(registry);
		registry.jsp("/WEB-INF/views/", ".jsp");
	}
	
	// 정적 파일의 경로를 매핑한다.
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);
		registry.addResourceHandler("/**").addResourceLocations("/resources/");
	}
	
	//데이터베이스 접속 정보를 관리하는 Bean
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource source=new BasicDataSource();
		source.setDriverClassName(db_classname);
		source.setUrl(db_url);
		source.setUsername(db_username);
		source.setPassword(db_password);
		return source;
	}
	
	//쿼리문과 접속 정보를 관리하는 객체
	//위에서 생선한 데이터베이스 접속정보를 관리하는 Bean을 매개변수로 받는다.
	@Bean
	public SqlSessionFactory factory(BasicDataSource source)throws Exception{
		SqlSessionFactoryBean factoryBean=new SqlSessionFactoryBean();
		factoryBean.setDataSource(source);
		SqlSessionFactory factory=factoryBean.getObject();
		return factory;
	}
	
	//쿼리문 실행을 위한 객체(Mapper 관리)
	//위에서 생선한 쿼리문과접속정보를 관리하는 bean을 매개변수로 받는다.
	@Bean
	public MapperFactoryBean<BoardMapper>getBoardMapper(SqlSessionFactory factory)throws Exception{
		MapperFactoryBean<BoardMapper>factoryBean=new MapperFactoryBean<BoardMapper>(BoardMapper.class);
		factoryBean.setSqlSessionFactory(factory);
		return factoryBean;
	}

	@Bean
	public MapperFactoryBean<TopMenuMapper>getTopMenuMapper(SqlSessionFactory factory)throws Exception{
		MapperFactoryBean<TopMenuMapper>factoryBean=new MapperFactoryBean<TopMenuMapper>(TopMenuMapper.class);
		factoryBean.setSqlSessionFactory(factory);
		return factoryBean;
	}
	
	@Bean
	public MapperFactoryBean<UserMapper>getUserMapper(SqlSessionFactory factory)throws Exception{
		MapperFactoryBean<UserMapper>factoryBean=new MapperFactoryBean<UserMapper>(UserMapper.class);
		factoryBean.setSqlSessionFactory(factory);
		return factoryBean;
	}
	
	//인터셉터 등록
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addInterceptors(registry);
	
		TopMenuInterceptor topMenuInterceptor=new TopMenuInterceptor(topMenuService,loginUserBean);
		InterceptorRegistration reg1= registry.addInterceptor(topMenuInterceptor);
		reg1.addPathPatterns("/**");
		
		CheckLogininterceptor checkLogininterceptor=new CheckLogininterceptor(loginUserBean);
		InterceptorRegistration reg2=registry.addInterceptor(checkLogininterceptor);
		reg2.addPathPatterns("/user/modify","/user/logout","/board/*");
		reg2.excludePathPatterns("/board/main");
		
		CheckWriterInterceptor checkWriterInterceptor=new CheckWriterInterceptor(loginUserBean, boardService);
		InterceptorRegistration reg3=registry.addInterceptor(checkWriterInterceptor);
		reg3.addPathPatterns("/board/modify","/board/delete");
	}
	
	//property로 등록한 db.properties와 밑에 error_mesage.properties를 별도로 관리하기 위한 빈
	//이렇게 해주지 않으면 dp, error_message가 서로 충돌이 나서 에러 발생
	@Bean
	public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource res=new ReloadableResourceBundleMessageSource();
		res.setBasenames("/WEB-INF/properties/error_message");
		return res;
	}
	
	//파일데이터가 넘어오면 얘를 무조건 통과시켜서 알아서 설정해준다. XML로 프로젝트를 진행하면 우리가 직접 설정해줘야한다
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
}










