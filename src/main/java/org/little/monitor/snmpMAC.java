package org.little.monitor;


import java.io.IOException;
import java.util.List;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.dbExcept;
import org.little.util.run.scheduler;
import org.little.util.run.task;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;


public class snmpMAC extends task{
       private static final Logger    logger = LoggerFactory.getLogger(snmpMAC.class);

       private String                              targetAddr      ="10.6.114.21";
       private String                              oidStr          ="1.3.6.1.2.1.4.22.1.2";
       private String                              commStr         ="kcoi1-ro";//"public";
       private int                                 snmpVersion     =SnmpConstants.version2c;
       private String                              portNum         ="161";  
       private Snmp                                snmp            =null;
       private List<TreeEvent>                     events          =null;
       private Address                             targetAddress=null;
       private TransportMapping<? extends Address> transport=null;
 
 
 
       private dbMAC                  db;

  
       public snmpMAC(String _addr,String _comm,int start,int timeout){
               super(start,timeout);     
               targetAddr=_addr;commStr=_comm;
       }
        

       private void req_start() throws IOException{

               targetAddress = GenericAddress.parse("udp:"+ targetAddr + "/" + portNum);
               transport     = new DefaultUdpTransportMapping();
               snmp          = new Snmp(transport);

               transport.listen();
               // setting up target
               CommunityTarget target = new CommunityTarget();
               target.setCommunity(new OctetString(commStr));
               target.setAddress(targetAddress);
               target.setRetries(3);
               target.setTimeout(1000 * 3);
               target.setVersion(snmpVersion);
               OID oid = new OID(oidStr);

               // Get MIB data.
               TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());      

               events = treeUtils.getSubtree(target, oid);

               if(events == null || events.size() == 0){
                  logger.trace("No result returned.");
                  return;
               }

        }
        private void req_run() throws IOException , dbExcept{
              
                // Handle the snmpwalk result.
                for(TreeEvent event : events) {
                    if(event == null) {
                       continue;  
                    }
                    if(event.isError()) {
                       logger.error("ERROR oid:" + oidStr + " event:" + event.getErrorMessage());
                       continue;
                    }
               
                    VariableBinding[] varBindings = event.getVariableBindings();
                    if(varBindings == null || varBindings.length == 0){
                       continue;
                    }
                    for(VariableBinding varBinding : varBindings) {
                        if(varBinding == null) {
                           continue;
                        }
                        String _addr=varBinding.getOid().toString();
                        String __addr=_addr.substring(oidStr.length()+1);
                        String addr=__addr.substring(__addr.indexOf('.')+1);
               
                        String mac=varBinding.getVariable().toString();
                        //System.out.println(addr+" : " + mac);
                        if(addr==null || mac==null){
                           logger.trace("NUll record addr:"+addr+" mac:"+mac);
                           continue;
                        }
                        db.update(addr,mac);
               
                  }
                }

        }
        private void req_end(){
                 if(snmp!=null){
                    try { 
                       snmp.close();
                    } catch (IOException ex) {}
                    snmp=null;
                 }
        }
   
        @Override
        public void work(){

               System.out.println("request start");
               try{
                    try{
                        req_start();
                    } 
                    catch (IOException ex1) {
                        logger.error("Open snmp ex:"+ex1);
                        return;
                    }
                   
                    try{
                        req_run();
                    } 
                    catch (IOException ex3) {
                        logger.error("Get snmp request ex:"+ex3);
                    }
                    catch (dbExcept ex4) {
                        logger.error("Get snmp request ex:"+ex4);
                    }
                    
                    //try{Thread.sleep(30*1000);}catch(Exception e){}
               }
               finally{
                  req_end();
                  logger.trace("request end"); 
               }
               

        }
  
        public static void main(String[] args){
               snmpMAC snmp1=new snmpMAC("10.6.114.21"  ,"kcoi1-ro",0 ,600);
               snmpMAC snmp2=new snmpMAC("10.6.114.23"  ,"kcoi1-ro",30,600);
               snmpMAC snmp3=new snmpMAC("10.6.112.36"  ,"public"  ,60,600);
               snmpMAC snmp4=new snmpMAC("10.38.116.214","public"  ,90,600);
               snmpMAC snmp5=new snmpMAC("10.38.116.219","public"  ,120,600);
               snmpMAC snmp6=new snmpMAC("10.6.114.253" ,"public"  ,150,600);
               scheduler runner = new scheduler(30);

               runner.add(snmp1);
               runner.add(snmp2);
               runner.add(snmp3);
               runner.add(snmp4);
               runner.add(snmp5);
               runner.add(snmp6);
               System.out.println("run");

               //runner.run();
               runner.fork();

               System.out.println("runner run");

               scheduler.delay(3600*10);
               System.out.println("end");
               runner.stop();
               System.out.println("stop");
               scheduler.delay(10);

               //snmp1.work();
               //snmp2.work();
       }




}
