package org.little.web.filter;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.ietf.jgss.GSSCredential;
import org.little.web.filter.SpnegoHttpFilter.Constants;

/**
 * Wrap ServletRequest so we can do our own handling of the 
 * principal and auth types.
 * 
 * <p>Also, see the documentation on the {@link DelegateServletRequest} class.</p>
 * 
 * <p>Finally, a credential delegation example can be found on 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 *
 */
final class SpnegoHttpServletRequest extends HttpServletRequestWrapper 
    implements DelegateServletRequest, SpnegoAccessControl {
    
    private static final String MESSAGE_UNSUPPORTED = 
            "User Access Control has NOT been defined or is NOT supported.";
    
    /** Client Principal. */
    private final transient SpnegoPrincipal principal;
    
    /** authZ framework interface. */
    private final transient UserAccessControl accessControl;
    
    /**
     * Creates Servlet Request specifying KerberosPrincipal of util.
     * 
     * @param request
     * @param spnegoPrincipal 
     */
    SpnegoHttpServletRequest(final HttpServletRequest request
        , final SpnegoPrincipal spnegoPrincipal) {
        
        this(request, spnegoPrincipal, null);
    }
    
    /**
     * Creates Servlet Request specifying KerberosPrincipal of util
     * and a specified User Access Control (authZ).
     * @param request
     * @param spnegoPrincipal
     * @param userAccessControl
     */
    SpnegoHttpServletRequest(final HttpServletRequest request
        , final SpnegoPrincipal spnegoPrincipal
        , final UserAccessControl userAccessControl) {
        
        super(request);
        
        this.principal = spnegoPrincipal;
        this.accessControl = userAccessControl;
    }
    
    /**
     * Returns "Negotiate" or "Basic" else default auth type.
     * 
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    @Override
    public String getAuthType() {
        
        final String authType;
        final String header = this.getHeader(Constants.AUTHZ_HEADER);
        
        if (null == header) {
            authType = super.getAuthType();
            
        } else if (header.startsWith(Constants.NEGOTIATE_HEADER)) {
            authType = Constants.NEGOTIATE_HEADER;

        } else if (header.startsWith(Constants.BASIC_HEADER)) {
            authType = Constants.BASIC_HEADER;
            
        } else {
            authType = super.getAuthType();
        }
        
        return authType;
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.DelegateServletRequest#getDelegatedCredential()
     */
    @Override
    public GSSCredential getDelegatedCredential() {
        return this.principal.getDelegatedCredential();
    }
    
    /**
     * Returns authenticated username (sans domain/realm) else default username.
     * 
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        
        if (null == this.principal) {
            return super.getRemoteUser();
            
        } else {
            final String[] username = this.principal.getName().split("@", 2);
            return username[0];
        }
    }
    
    /**
     * Returns KerberosPrincipal of util.
     * 
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#anyRole(java.lang.String[])
     */
    @Override
    public boolean anyRole(final String... roles) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.anyRole(this.getRemoteUser(), roles);
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasRole(java.lang.String)
     */
    @Override
    public boolean hasRole(final String role) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), role);        
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasRole(java.lang.String, java.lang.String[])
     */
    @Override
    public boolean hasRole(final String featureX, final String... featureYs) {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), featureX, featureYs);
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#anyAccess(java.lang.String[])
     */
    @Override
    public boolean anyAccess(final String... resources) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.anyAccess(this.getRemoteUser(), resources);        
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasAccess(java.lang.String)
     */
    @Override
    public boolean hasAccess(final String resource) {
     // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasAccess(this.getRemoteUser(), resource);
    }
    
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasAccess(java.lang.String, java.lang.String[])
     */
    @Override
    public boolean hasAccess(final String resourceX, final String... resourceYs) {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasAccess(this.getRemoteUser(), resourceX, resourceYs);        
    }

    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#getUserInfo()
     */
    @Override
    public UserInfo getUserInfo() {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        final UserInfo userInfo = this.accessControl.getUserInfo(this.getRemoteUser());
        
        if (null == userInfo) {
            throw new UnsupportedOperationException("UserInfo was NULL and/or not configured");
        } else {
            return userInfo;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(final String role) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), role);
    }
}
