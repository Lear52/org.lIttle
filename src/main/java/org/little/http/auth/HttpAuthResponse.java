package org.little.http.auth;

//import org.little.util.Logger;
//import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpAuthResponse {
       //private static final Logger  looger = LoggerFactory.getLogger(HttpAuthResponse.class);

       HttpResponseStatus status;
       String             response_body_msg;
       String             response_auth_data;
       String             response_auth_header;
       String             user;

       public HttpAuthResponse() {}

       public HttpResponseStatus getStatus() {
              return status;
       }

       public void setStatus(HttpResponseStatus status) {
              this.status = status;
       }

       public String getBodyMsg() {
              return response_body_msg;
       }

       public void setBodyMsg(String msg) {
              this.response_body_msg = msg;
       }

       public String getAuthicationData() {
              return response_auth_data;
       }

       public void setAuthicationData(String auth) {
              this.response_auth_data = auth;
       }
       public String getAuthicationHeader() {
              return response_auth_header;
       }

       public void setAuthicationHeader(String auth) {
              this.response_auth_header = auth;
       }
       public String getUser() {
              return user;
       }

       protected void setUser(String u) {
              user=u;
       }
       public boolean isAuth() {return user!=null;}

}
