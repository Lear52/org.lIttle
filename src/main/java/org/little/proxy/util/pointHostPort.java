package org.little.proxy.util;

import java.net.InetSocketAddress;

//import org.little.util.Logger;
//import org.little.util.LoggerFactory;


public class pointHostPort{

    //private static final Logger LOG = LoggerFactory.getLogger(pointHostPort.class);

    private String  host_port;
    private String  host;
    private int     port;

    public        pointHostPort(){
                  setHostPort("");
    }
    public        pointHostPort(String _host){
                  setHostPort(_host);
    }
    public        pointHostPort(String _host,int port){
                  setHostPort(_host+":"+port);
    }

    public String  getHostPort(){return host_port;}
    public String  getHost(){return host;}
    public int     getPort(){return port;}
    public boolean isEquals(String h){return host_port.equals(h);}

    public void    setHostPort(String h){
           host_port=h;
           if(h==null){host=h;port=0;}
           if(h.length()==0){host=h;port=0;}
           String key[] = h.split(":");
           host=key[0];
           if(key.length>1)try {port=Integer.parseInt(key[1],10); } catch (Exception e) {port = 0;}
    }
    public void    setHostPort(InetSocketAddress addr){
           String  _addr=addr.getAddress().toString();
           String [] arr=_addr.split("/"); if(arr!=null)if(arr.length>1)_addr=arr[1];            
           host=_addr;
           port=addr.getPort();
           host_port=host+":"+port;
    }

}

