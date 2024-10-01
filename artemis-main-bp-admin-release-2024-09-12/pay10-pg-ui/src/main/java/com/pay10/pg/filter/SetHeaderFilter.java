package com.pay10.pg.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * @author OWASP
 *
 */
public class SetHeaderFilter implements Filter 
{
    private String mode = "DENY";
    	
    /**
     * Add X-FRAME-OPTIONS response header to tell IE8 (and any other browsers who
     * decide to implement) not to display this content in a frame. For details, please
     * refer to http://blogs.msdn.com/sdl/archive/2009/02/05/clickjacking-defense-in-ie8.aspx.
     */
    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse)response;
       // res.addHeader("X-FRAME-OPTIONS", mode );
		res.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate,pre-check=0, post-check=0, max-age=0, s-maxage=0");
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Strict-Transport-Security", "max-age=31536000;includeSubDomains");
		res.setHeader("X-XSS-Protection","1;mode=block");
	//	res.setHeader("X-Content-Type-Options","nosniff");
		res.setDateHeader("Expires", -1);

        res.setHeader("X-FRAME-OPTIONS", "DENY");
        res.setHeader("X-Content-Type-Option", "nosniff");
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("Permission-Policy", "");
        //res.setHeader("Permissions-Policy", "geolocation=(), microphone=()");
        //res.setHeader(" X-Content-Type-Options", "nosniff");
        res.setHeader("Pragma","no-cache");
        res.setHeader("Content-Security-Policy","script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';object-src 'self'");

        chain.doFilter(request, response);
    }

    @Override
	public void destroy() {
    }

    @Override
	public void init(FilterConfig filterConfig) {
        String configMode = filterConfig.getInitParameter("mode");
        if ( configMode != null ) {
            mode = configMode;
        }
    }    
}
