package com.pay10.crm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;



/**
 * Servlet Filter implementation class NoEtagFilter
 */
public final class NoEtagFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(NoEtagFilter.class.getName());
   
	  @Override
	public void doFilter(ServletRequest request, ServletResponse response,
	            FilterChain chain) throws IOException, ServletException {
	        chain.doFilter(request, new HttpServletResponseWrapper(
	                (HttpServletResponse) response) {
	            @Override
				public void setHeader(String name, String value) {
	                if (!"etag".equalsIgnoreCase(name)) {
	                    super.setHeader(name, value);
	                } else {
	                	logger.debug("Ignoring etag header: " + name + " " + value);
	                }
	            }
	        });
	    }
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

	@Override
	public void destroy() {
		
		
	}

}
