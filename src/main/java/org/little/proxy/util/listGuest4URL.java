package org.little.proxy.util;

import org.w3c.dom.Node;

public interface listGuest4URL {
    public void      init(Node node_cfg);
    public String    getUser4IP(String ip);
    public String    getUser4URL(String url);
    public void      setDefaultUser(String s);
    public String    getDefaultUser();
  
}

