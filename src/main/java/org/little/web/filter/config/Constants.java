package org.little.web.filter.config;

import org.little.web.filter.impl.DelegateServletRequest;

/**
 * Defines constants and parameter names that are used in the  
 * web.xml file, and HTTP request headers, etc.
 * 
 * <p>
 * This class is primarily used internally or by implementers of 
 * custom http clients and by {@link SpnegoFilterConfig}.
 * </p>
 * 
 */
public class Constants {

       private Constants() {
           // default private
       }
       
       /** 
        * Servlet init param name in web.xml <b>spnego.allow.basic</b>.
        * 
        * <p>Set this value to <code>true</code> in web.xml if the filter 
        * should allow Basic Authentication.</p>
        * 
        * <p>It is recommended that you only allow Basic Authentication 
        * if you have clients that cannot perform Kerberos authentication. 
        * Also, you should consider requiring SSL/TLS by setting 
        * <code>spnego.allow.unsecure.basic</code> to <code>false</code>.</p>
        */
       public static final String ALLOW_BASIC = "spnego.allow.basic";
      
       /**
        * Servlet init param name in web.xml <b>spnego.allow.delegation</b>.
        * 
        * <p>Set this value to <code>true</code> if server should support 
        * credential delegation requests.</p>
        * 
        * <p>Take a look at the {@link DelegateServletRequest} for more 
        * information about other pre-requisites.</p>
        */
       public static final String ALLOW_DELEGATION = "spnego.allow.delegation";
       
       /**
        * Servlet init param name in web.xml <b>spnego.allow.localhost</b>.
        * 
        * <p>Flag to indicate if requests coming from http://localhost 
        * or http://127.0.0.1 should not be authenticated using 
        * Kerberos.</p>
        * 
        * <p>This feature helps to obviate the requirement of 
        * creating an SPN for developer machines.</p>
        * 
        */
       public static final String ALLOW_LOCALHOST = "spnego.allow.localhost";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.allow.unsecure.basic</b>.
        * 
        * <p>Set this value to <code>false</code> in web.xml if the filter 
        * should reject connections that do not use SSL/TLS.</p>
        */
       public static final String ALLOW_UNSEC_BASIC = "spnego.allow.unsecure.basic";
       
       /** 
        * HTTP Response Header <b>WWW-Authenticate</b>. 
        * 
        * <p>The filter will respond with this header with a value of "Basic" 
        * and/or "Negotiate" (based on web.xml file).</p>
        */
       public static final String AUTHN_HEADER = "WWW-Authenticate";
       
       /** 
        * HTTP Request Header <b>Authorization</b>. 
        * 
        * <p>Clients should send this header where the value is the 
        * authentication token(s).</p>
        */
       public static final String AUTHZ_HEADER = "Authorization";
       
       /** 
        * HTTP Response Header <b>Basic</b>. 
        * 
        * <p>The filter will set this as the value for the "WWW-Authenticate" 
        * header if "Basic" auth is allowed (based on web.xml file).</p>
        */
       public static final String BASIC_HEADER = "Basic";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.login.client.module</b>. 
        * 
        * <p>The LoginModule name that exists in the login.conf file.</p>
        */
       public static final String CLIENT_MODULE = "spnego.login.client.module";
      
       /** 
        * HTTP Request Header <b>Content-Type</b>. 
        * 
        */
       public static final String CONTENT_TYPE = "Content-Type";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.exclude.dirs</b>.
        * 
        * <p>
        * A List of URL paths, starting at the context root, 
        * that should NOT undergo authentication (authN). 
        * </p>
        */
       public static final String EXCLUDE_DIRS = "spnego.exclude.dirs";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.krb5.conf</b>. 
        * 
        * <p>The location of the krb5.conf file. On Windows, this file will 
        * sometimes be named krb5.ini and reside <code>%WINDOWS_ROOT%/krb5.ini</code> 
        * here.</p>
        * 
        * <p>By default, Java looks for the file in these locations and order:
        * <li>System Property (java.security.krb5.conf)</li>
        * <li>%JAVA_HOME%/lib/security/krb5.conf</li>
        * <li>%WINDOWS_ROOT%/krb5.ini</li>
        * </p>
        */
       public static final String KRB5_CONF = "spnego.krb5.conf";
       
       /**
        * Specify logging level.
      
        * <pre>
        * 1 = FINEST
        * 2 = FINER
        * 3 = FINE
        * 4 = CONFIG
        * 5 = INFO
        * 6 = WARNING
        * 7 = SEVERE
        * </pre>
        * 
        */
       public static final String LOGGER_LEVEL = "spnego.logger.level";
       
       /**
        * Name of Spnego Logger.
        * 
        * <p>Example: <code>Logger.getLogger(Constants.LOGGER_NAME)</code></p>
        */
       public static final String LOGGER_NAME = "SpnegoHttpFilter"; 
       
       /** 
        * Servlet init param name in web.xml <b>spnego.login.conf</b>. 
        * 
        * <p>The location of the login.conf file.</p>
        */
       public static final String LOGIN_CONF = "spnego.login.conf";
       
       /** 
        * HTTP Response Header <b>Negotiate</b>. 
        * 
        * <p>The filter will set this as the value for the "WWW-Authenticate" 
        * header. Note that the filter may also add another header with 
        * a value of "Basic" (if allowed by the web.xml file).</p>
        */
       public static final String NEGOTIATE_HEADER = "Negotiate";
       
       /**
        * NTLM base64-encoded token start value.
        */
       public static final String NTLM_PROLOG = "TlRMTVNT";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.preauth.password</b>. 
        * 
        * <p>Network Domain password. For Windows, this is sometimes known 
        * as the Windows NT password.</p>
        */
       public static final String PREAUTH_PASSWORD = "spnego.preauth.password";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.preauth.username</b>. 
        * 
        * <p>Network Domain username. For Windows, this is sometimes known 
        * as the Windows NT username.</p>
        */
       public static final String PREAUTH_USERNAME = "spnego.preauth.username";
       
       /**
        * If server receives an NTLM token, the filter will return with a 401 
        * and with Basic as the only option (no Negotiate) <b>spnego.prompt.ntlm</b>. 
        */
       public static final String PROMPT_NTLM = "spnego.prompt.ntlm";
       
       /** 
        * Servlet init param name in web.xml <b>spnego.login.server.module</b>. 
        * 
        * <p>The LoginModule name that exists in the login.conf file.</p>
        */
       public static final String SERVER_MODULE = "spnego.login.server.module";
       
       /** 
        * HTTP Request Header <b>SOAPAction</b>. 
        * 
        */
       public static final String SOAP_ACTION = "SOAPAction";
}
