package it.unipd.dei.webapp.filters;

import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.utils.JWTUtils;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.util.ArrayList;
import java.util.List;


public class NavigationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();

        request.setAttribute("currentPage", uri);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}