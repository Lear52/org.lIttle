package org.little.web;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.auth.authUserLDAP;

/**
* Some credits to http://stackoverflow.com/a/18363307/771431
*/
public class BasicAuthFilter implements Filter {

       private static authUserLDAP auth=null;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
           if(auth==null){
              String _ldap_url=filterConfig.getInitParameter("LDAP_URL");
              String _realm   =filterConfig.getInitParameter("REALM");
              String _domain  =filterConfig.getInitParameter("DOMAIN");;
              if(_ldap_url==null)return;
              auth=new authUserLDAP(_ldap_url,_realm,_domain);
           }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean authorized = false;

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {

            String[] authHeaderSplit = authHeader.split("\\s");

            for (int i = 0; i < authHeaderSplit.length; i++) {
                String token = authHeaderSplit[i];
                if (token.equalsIgnoreCase("Basic")) {

                    String credentials = new String(Base64.getDecoder().decode(authHeaderSplit[i + 1]));
                    int index = credentials.indexOf(":");
                    if (index != -1) {
                        String username = credentials.substring(0, index).trim();
                        String password = credentials.substring(index + 1).trim();
                        if(auth==null)authorized = false;
                        else  authorized = auth.checkUser(username,password);
                        //authorized = username.equals("<username>") && password.equals("<password>");
                    }
                }
            }
        }

        if (!authorized) {
            res.setHeader("WWW-Authenticate", "Basic realm=\"Insert credentials\"");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }
}