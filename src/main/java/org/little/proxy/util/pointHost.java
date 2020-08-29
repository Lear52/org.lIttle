package org.little.proxy.util;

//import org.little.util.Logger;
//import org.little.util.LoggerFactory;

public class pointHost extends pointHostPort{

    //private static final Logger LOG = LoggerFactory.getLogger(pointHost.class);

    private int     id;
    private boolean is_active;

    public        pointHost(){
                  setHostPort("");
                  isActive(false);
                  setID(0); 
    }
    public        pointHost(String _host,boolean _active,int _id){
                  setHostPort(_host);
                  isActive(_active);
                  setID(_id); 
    }
    public        pointHost(String _host,boolean _active){
                  setHostPort(_host);
                  isActive(_active);
                  setID(0); 
    }
    public        pointHost(String _host){
                  setHostPort(_host);
                  isActive(true);
                  setID(0); 
    }

    public boolean isActive(){return is_active;}
    public void    isActive(boolean a){is_active=a;}
    public int     getID(){return id;}
    public void    setID(int _id){id=_id;}

  
    public String  getString(){return "id:"+getID()+" active:"+isActive()+" ip:"+getHostPort();}

}

