package com.pay10.crm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import com.opensymphony.module.sitemesh.filter.PageFilter;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.crm.action.CaptchaServlet;
import com.pay10.crm.filter.NoEtagFilter;
import com.pay10.crm.filter.SetHeaderFilter;

@SpringBootApplication
@ComponentScan({"com.pay10.commons","com.pay10.crm","com.pay10.pg"})
public class Pay10CrmApplication {

	private static Logger logger = LoggerFactory.getLogger(Pay10CrmApplication.class.getName());

	{
		logger.info("static block called upon invocation");
		UserDao userDao = new UserDao();
		userDao.getUserActiveList();
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(StaticDataProvider::updateMapValues, 0, 300, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Pay10CrmApplication.class, args);
	}
	
	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath("classpath:/WEB-INF/classes/");
		return bean;
	}

	@Bean
	public ServletContextInitializer initializer() {
	    return new ServletContextInitializer() {

	        @Override
	        public void onStartup(ServletContext servletContext) throws ServletException {
	            servletContext.setInitParameter("contextConfigLocation", "classpath*:all-beans-spring.xml");
	        }
	    };
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean captchaServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new CaptchaServlet(), "/Captcha.jpg/*");
		registration.addInitParameter("height", "30");
		registration.addInitParameter("width", "120");
		return registration;
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Bean
//	public FilterRegistrationBean siteMeshFilter() {
//		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//		filterRegistrationBean.setFilter(new CustomSiteMeshFilter()); // adding sitemesh filter ??
//		filterRegistrationBean.addUrlPatterns("*.jsp");
//		filterRegistrationBean.setName("CustomSiteMeshFilter");
//		return filterRegistrationBean;
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean strutsPrepareAndExecuteFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		StrutsPrepareAndExecuteFilter struts = new StrutsPrepareAndExecuteFilter();
		filterRegistrationBean.setFilter(struts);
		filterRegistrationBean.addUrlPatterns("/jsp/*", "/help/*");
		filterRegistrationBean.setName("StrutsPrepareAndExecuteFilter");
		return filterRegistrationBean;
	}

	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean pageFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		PageFilter sitemesh = new PageFilter();
		filterRegistrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
		filterRegistrationBean.setFilter(sitemesh);
		filterRegistrationBean.addUrlPatterns("*.jsp", "/help/*");
		filterRegistrationBean.setName("sitemesh");
		return filterRegistrationBean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean setHeaderFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		SetHeaderFilter headerFilter = new SetHeaderFilter();
		filterRegistrationBean.addInitParameter("mode", "DENY");
		filterRegistrationBean.setFilter(headerFilter);
		filterRegistrationBean.addUrlPatterns("/jsp/*", "*.js", "*.css", "*.png", "*.jpeg", "*.jpg", "/help/*");
		filterRegistrationBean.setName("header-options");
		return filterRegistrationBean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean noEtagFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		NoEtagFilter noEtagFilter = new NoEtagFilter();
		filterRegistrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
		filterRegistrationBean.addServletNames("default");
		filterRegistrationBean.setFilter(noEtagFilter);
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName("NoEtagFilter");
		return filterRegistrationBean;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean noEtagEncoderFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		NoEtagFilter noEtagFilter = new NoEtagFilter();
		filterRegistrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
		filterRegistrationBean.addServletNames("default");
		filterRegistrationBean.setFilter(noEtagFilter);
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName("encoder");
		return filterRegistrationBean;
	}


}
