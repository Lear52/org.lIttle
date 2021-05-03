package org.little.monitor.snmp;

import java.io.IOException;
import java.util.ArrayList;

import org.little.util.Except;

/**
 класс содержащий пул клиенских snmpCln
*/

public class snmpClnTable{ 

       ArrayList<snmpCln> list;
 
       public snmpClnTable(){ 
              list=new ArrayList<snmpCln>(10);

       } 
              
       public snmpCln getClient(String address,String community) throws IOException{
              for(int i=0;i<list.size();i++){
                  snmpCln c;
                  c=list.get(i);
                  if(address.equals(c.getAddress())==true && community.equals(c.getCommunity())==true)return c;  
              }

              snmpCln client;
              client=new snmpCln(address,community);
              client.start();
              list.add(client);
              return client;
       }

      
       public void start() throws IOException { 
              for(int i=0;i<list.size();i++){
                  snmpCln c;
                  c=list.get(i);
                  c.start();
              }
       } 
       public void stop() throws IOException { 
              for(int i=0;i<list.size();i++){
                  snmpCln c;
                  c=list.get(i);
                  c.stop();
              }
       } 



       public static void main(String[] args) throws IOException, Except { 
              snmpClnTable list=new snmpClnTable();

              snmpCln cln;

              cln=list.getClient("udp:127.0.0.1/161","public");

              list.start();
              String data=null;

              data=cln.getAsString(".1.3.6.1.2.1.1.5.0");

              System.out.println("sysname:"+ data);

              list.stop();
                  
              
       } 


} 
