package org.little.proxy.util;

import java.util.ArrayList;

public interface  statProxyMBean {


    public ArrayList<String> getPointHost();
    public ArrayList<String> getAllStatChannel();
    public int               getAllCountChannel();
    public int               getFrontCountChannel();
    public int               getBackCountChannel();

    //public ArrayList<String> getState();
    /*public void      setState(String s);*/
    //public int       getNbChanges();

    public void      reset();

}
