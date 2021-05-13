
package org.little.web.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.web.filter.config.Constants;
import org.little.web.filter.config.SpnegoFilterConfig;
import org.little.web.filter.impl.SpnegoHttpServletRequest;
import org.little.web.filter.impl.SpnegoHttpServletResponse;
import org.little.web.filter.impl.SpnegoPrincipal;

import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ietf.jgss.GSSException;

/**
 * Http Servlet Filter that provides <a
 * href="http://en.wikipedia.org/wiki/SPNEGO" target="_blank">SPNEGO</a> authentication.
 * It allows servlet containers like Tomcat and JBoss to transparently/silently
 * authenticate HTTP clients like Microsoft Internet Explorer (MSIE).
 * 
 * <p>
 * This feature in MSIE is sometimes referred to as single sign-on and/or 
 * Integrated Windows Authentication. In general, there are at least two 
 * authentication mechanisms that allow an HTTP server and an HTTP client 
 * to achieve single sign-on: <b>NTLM</b> and <b>Kerberos/SPNEGO</b>.
 * </p>
 * 
 * <p>
 * <b>NTLM</b><br />
 * MSIE has the ability to negotiate NTLM password hashes over an HTTP session 
 * using Base 64 encoded NTLMSSP messages. This is a staple feature of Microsoft's 
 * Internet Information Server (IIS). Open source libraries exists (ie. jCIFS) that 
 * provide NTLM-based authentication capabilities to Servlet Containers. jCIFS uses 
 * NTLM and Microsoft's Active Directory (AD) to authenticate MSIE clients.
 * </p>
 * 
 * <p>
 * <b>{@code SpnegoHttpFilter} does NOT support NTLM (tokens).</b>
 * </p>
 * 
 * <p>
 * <b>Kerberos/SPNEGO</b><br />
 * Kerberos is an authentication protocol that is implemented in AD. The protocol 
 * does not negotiate passwords between a client and a server but rather uses tokens 
 * to securely prove/authenticate to one another over an un-secure network.
 * </p>
 * 
 * <p>
 * <b><code>SpnegoHttpFilter</code> does support Kerberos but through the 
 * pseudo-mechanism <code>SPNEGO</code></b>.
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/SPNEGO" target="_blank">Wikipedia: SPNEGO</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc4178.txt" target="_blank">IETF RFC: 4178</a></li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Localhost Support</b><br />
 * The Kerberos protocol requires that a service must have a Principal Name (SPN) 
 * specified. However, there are some use-cases where it may not be practical to 
 * specify an SPN (ie. Tomcat running on a developer's machine). The DNS 
 * http://localhost is supported but must be configured in the servlet filter's 
 * init params in the web.xml file. 
 * </p>
 * 
 * <p><b>Modifying the web.xml file</b></p>
 * 
 * <p>Here's an example configuration:</p>
 * 
 * <p>
 * <pre><code>  &lt;filter&gt;
 *      &lt;filter-name&gt;SpnegoHttpFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;net.sourceforge.spnego.SpnegoHttpFilter&lt;/filter-class&gt;
 *      
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.basic&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.localhost&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.allow.unsecure.basic&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.client.module&lt;/param-name&gt;
 *          &lt;param-value&gt;spnego-client&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.krb5.conf&lt;/param-name&gt;
 *          &lt;param-value&gt;krb5.conf&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.conf&lt;/param-name&gt;
 *          &lt;param-value&gt;login.conf&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.preauth.username&lt;/param-name&gt;
 *          &lt;param-value&gt;Zeus&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.preauth.password&lt;/param-name&gt;
 *          &lt;param-value&gt;Zeus_Password&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.login.server.module&lt;/param-name&gt;
 *          &lt;param-value&gt;spnego-server&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.prompt.ntlm&lt;/param-name&gt;
 *          &lt;param-value&gt;true&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *          
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;spnego.logger.level&lt;/param-name&gt;
 *          &lt;param-value&gt;1&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *  &lt;/filter&gt;
 *</code></pre>
 * </p>
 * 
 * <p><b>Example usage on web page</b></p>
 * 
 * <p><pre>  &lt;html&gt;
 *  &lt;head&gt;
 *      &lt;title&gt;Hello SPNEGO Example&lt;/title&gt;
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *  Hello &lt;%= request.getRemoteUser() %&gt; !
 *  &lt;/body&gt;
 *  &lt;/html&gt;
 *  </pre>
 * </p>
 *
 * <p>
 * Take a look at the <a href="http://spnego.sourceforge.net/reference_docs.html" 
 * target="_blank">reference docs</a> for other configuration parameters.
 * </p>
 * 
 * <p>See more usage examples at 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
public class SpnegoHttpFilter implements Filter {
       private static final Logger logger = LoggerFactory.getLogger(SpnegoHttpFilter.class);
      
       private transient SpnegoAuthenticator authenticator;/** Object for performing Basic and SPNEGO authentication. */
       private transient UserAccessControl   accessControl;/** Object for performing User Authorization. */
       private transient String              sitewide; /** AuthZ required for every page. */
       private transient String              page403;/** Landing page if util is denied authZ access. */
       private transient List<String>        excludeDirs = new ArrayList<String>();/** directories which should not be authenticated irrespective of filter-mapping. */
       
       @Override
       public void init(final FilterConfig filterConfig) throws ServletException {
      
              try {
                  // set some System properties
                  final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance(filterConfig);
                  this.excludeDirs.addAll(config.getExcludeDirs());
                  
                  logger.info("excludeDirs=" + this.excludeDirs);
                  
                  // pre-authenticate
                  this.authenticator = new SpnegoAuthenticator(config);
                  
                  // authorization
                  final Properties props = SpnegoHttpFilter.toProperties(filterConfig);
                  if (!props.getProperty("spnego.authz.class", "").isEmpty()) {
                      props.put("spnego.server.realm", this.authenticator.getServerRealm());
                      this.page403 = props.getProperty("spnego.authz.403", "").trim();
                      this.sitewide = props.getProperty("spnego.authz.sitewide", "").trim();
                      this.sitewide = (this.sitewide.isEmpty()) ? null : this.sitewide;
                      this.accessControl = (UserAccessControl) Class.forName(props.getProperty("spnego.authz.class")).newInstance();
                      this.accessControl.init(props);                
                  }
                  
              } 
              catch (final LoginException            lex ) { throw new ServletException(lex);}
              catch (final GSSException              gsse) { throw new ServletException(gsse);}
              catch (final PrivilegedActionException pae ) { throw new ServletException(pae); }
              catch (final FileNotFoundException     fnfe) { throw new ServletException(fnfe);}
              catch (final URISyntaxException        uri ) { throw new ServletException(uri); }
              catch (InstantiationException          iex ) { throw new ServletException(iex); }
              catch (IllegalAccessException          iae ) { throw new ServletException(iae); }
              catch (ClassNotFoundException          cnfe) { throw new ServletException(cnfe);}
              
       }
      
       @Override
       public void destroy() {
              this.page403 = null;
              this.sitewide = null;
              if(null != this.excludeDirs)  this.excludeDirs.clear();
              if(null != this.accessControl)this.accessControl.destroy();
              if(null != this.authenticator)this.authenticator.dispose();
              this.accessControl = null;
              this.authenticator = null;
       }
      
       @Override
       public void doFilter(final ServletRequest request, final ServletResponse response,final FilterChain chain) throws IOException, ServletException {
      
           final HttpServletRequest        httpRequest    = (HttpServletRequest) request;
           final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse((HttpServletResponse) response);
           
           // skip authentication if resource is in the list of directories to exclude
           if (exclude(httpRequest.getContextPath(), httpRequest.getServletPath())) {
               chain.doFilter(request, response);
               return;
           }
           
           // client/caller principal
           final SpnegoPrincipal principal;
           try {
               principal = this.authenticator.authenticate(httpRequest, spnegoResponse);
           } catch (GSSException gsse) {
               logger.error("HTTP Authorization Header="+ httpRequest.getHeader(Constants.AUTHZ_HEADER));
               throw new ServletException(gsse);
           }
      
           // context/auth loop not yet complete
           if (spnegoResponse.isStatusSet()) {
               return;
           }
      
           // assert
           if (null == principal) {
               logger.error("Principal was null.");
               spnegoResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true);
               return;
           }
      
           logger.trace("principal=" + principal);
           
           final SpnegoHttpServletRequest spnegoRequest = new SpnegoHttpServletRequest(httpRequest, principal, this.accessControl);
                   
           // site wide authZ check (if enabled)
           if (!isAuthorized((HttpServletRequest) spnegoRequest)) {
               logger.info("Principal Not AuthoriZed: " + principal);
               if (this.page403.isEmpty()) spnegoResponse.setStatus(HttpServletResponse.SC_FORBIDDEN, true);  
               else                        request.getRequestDispatcher(this.page403).forward(spnegoRequest, response);

               return;            
           }
      
           chain.doFilter(spnegoRequest, response);
       }
       
       private boolean isAuthorized(final HttpServletRequest request) {
               if(null != this.sitewide && null != this.accessControl && !this.accessControl.hasAccess(request.getRemoteUser(), this.sitewide)) return false;
               return true;
       }
       
       private boolean exclude(final String contextPath, final String servletPath) {
               // each item in excludeDirs ends with a slash
               final String path = contextPath + servletPath + (servletPath.endsWith("/") ? "" : "/");
               
               for (String dir : this.excludeDirs) {
                   if (path.startsWith(dir)) {
                       return true;
                   }
               }
               
               return false;
       }
       
       private static Properties toProperties(final FilterConfig filterConfig) {
               final Properties props = new Properties();
               //@SuppressWarnings("unchecked")
               final Enumeration<String> it = filterConfig.getInitParameterNames();
               
               while (it.hasMoreElements()) {
                   final String key = it.nextElement();
                   props.put(key, filterConfig.getInitParameter(key));
               }
               
               return props;
       }
       
}
