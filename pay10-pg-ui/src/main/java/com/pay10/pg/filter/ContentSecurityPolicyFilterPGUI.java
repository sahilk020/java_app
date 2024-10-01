package com.pay10.pg.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class ContentSecurityPolicyFilterPGUI implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
       // httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'");
        httpResponse.setHeader("Cache-Control",	"no-store, no-cache, must-revalidate,pre-check=0, post-check=0, max-age=0, s-maxage=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000;includeSubDomains");
        httpResponse.setHeader("X-XSS-Protection","1;mode=block");
        httpResponse.setDateHeader("Expires", -1);

        
        httpResponse.setHeader("X-FRAME-OPTIONS", "DENY");
        httpResponse.setHeader("X-Content-Type-Option", "nosniff");
        httpResponse.setHeader("Referrer-Policy", "no-referrer");
        httpResponse.setHeader("Permission-Policy", "");
       // res.setHeader("Permissions-Policy", "geolocation=(), microphone=()");
        //res.setHeader(" X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("Pragma","no-cache");
        httpResponse.setHeader("Content-Security-Policy","script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';object-src 'self'");

        
        chain.doFilter(request, response);  /* Let request continue chain filter */
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}