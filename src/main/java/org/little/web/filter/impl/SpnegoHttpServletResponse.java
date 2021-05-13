package org.little.web.filter.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Class adds capability to track/determine if the HTTP Status 
 * code has been set.
 * 
 * <p>
 * Also allows the ability to set the content length to zero 
 * and flush the buffer immediately after setting the HTTP 
 * Status code.
 * </p>
 * 
 * @author Darwin V. Felix
 * 
 */
public class SpnegoHttpServletResponse extends HttpServletResponseWrapper {

       private transient boolean statusSet = false;
      
       /**
        * 
        * @param response
        */
       public SpnegoHttpServletResponse(final HttpServletResponse response) {
           super(response);
       }
      
       /**
        * Tells if setStatus has been called.
        * 
        * @return true if HTTP Status code has been set
        */
       public boolean isStatusSet() {
           return this.statusSet;
       }
      
       @Override
       public void setStatus(final int status) {
           super.setStatus(status);
           this.statusSet = true;
       }
      
       /**
        * Sets the HTTP Status Code and optionally set the the content 
        * length to zero and flush the buffer.
        * 
        * @param status http status code
        * @param immediate set to true to set content len to zero and flush
        * @throws IOException 
        * 
        * @see #setStatus(int)
        */
       public void setStatus(final int status, final boolean immediate) throws IOException {
           setStatus(status);
           if (immediate) {
               setContentLength(0);
               flushBuffer();
           }
       }
}     
