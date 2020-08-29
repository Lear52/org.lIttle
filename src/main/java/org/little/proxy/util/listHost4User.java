package org.little.proxy.util;

import  org.w3c.dom.Node;

public interface listHost4User {

    public void     init(Node node_cfg);

    public String   getHostPort(String user_name);
    public String   getHostPort(String user_name,String url);

    public host4URL getDefault();

    public String   getDefaultHostPort();
    public void     setDefaultHostPort(String h,boolean _is_active_path);
    public String   getDefaultHost();
    public int      getDefaultPort();

  
}

