package org.little.monitor.snmp;

import java.io.IOException;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.run.tfork;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.asn1.BER;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class snmpCln{ 

       public final static int    SNMP_RETRIES   = 3;
       public final static long   SNMP_TIMEOUT   = 5000L;
       public final static int    BULK_SIZE      = 100;

       protected TransportMapping <? extends Address> transport= null;
       protected Snmp                                 snmp     = null; 
       protected Target                               _target  = null;

       protected ResponseEvent                        event    = null;
       protected OID                                  r_oid    = null;

       protected boolean          is_start = false;
       protected String           address  = null;
       protected String           community= null;

       public snmpCln(String _address,String _community)  throws IOException { 
                        SNMP4JSettings.setForwardRuntimeExceptions(true);
                        SNMP4JSettings.setExtensibilityEnabled(true);
                        BER.setCheckSequenceLength(false);
                        new BER().setCheckValueLength(false);

                        address=_address;
                        community=_community;

       Address          targetAddress = GenericAddress.parse(address); 
       CommunityTarget  target        = new CommunityTarget(); 

                        target.setCommunity(new OctetString(community)); 
                        target.setAddress(targetAddress); 
                        target.setRetries(SNMP_RETRIES); 
                        target.setTimeout(SNMP_TIMEOUT); 
                        //target.setVersion(SnmpConstants.version2c); /**/
                        target.setVersion(SnmpConstants.version1); 
                        _target       = target;
       //---------------------------------------------------------------------------
                        transport     = new DefaultUdpTransportMapping(); 
                        snmp          = new Snmp(transport); 
       } 
              

       public String getAddress(){return address;}
       public String getCommunity(){return community;}

      
       public boolean isStart(){ 
              return is_start;
       } 
       public void start() throws IOException { 
              if(is_start==true)return;
              transport.listen(); 
              is_start=true;
       } 
       public void stop() throws IOException { 
              TransportMapping<? extends Address> t=transport;
              Snmp                                s=snmp; 
              snmp              = null; 
              transport         = null; 
              if(is_start==false)return;
              is_start   = false;
              //System.out.println(">>>>");
              while(t.isListening()){}

              t.close(); 

              //System.out.println("transport close .....");

              while(t.isListening()){}

              //System.out.println("transport close");


              s.close(); 
              //System.out.println("snmp close");
       } 
       protected void sendGET(String _oid) throws IOException, Except { 
                 
                 VariableBinding  req_var_oid;
                 PDU              req_pdu;
                 //ResponseEvent    event;

                 if(is_start==false) throw new IOException("Transport is null");

                 r_oid = new OID(_oid);
                 if(r_oid==null) throw new IOException("OID is null");
                 req_var_oid = new VariableBinding(r_oid);
                 req_pdu     = new PDU(); 
                 if(req_pdu==null || req_var_oid==null){
                         throw new IOException("PDU is null");
                 }
                 req_pdu.add(req_var_oid); 
                 req_pdu.setType(PDU.GET); 

                 _target.setVersion(SnmpConstants.version1); 

                 event = snmp.send(req_pdu, _target); 
                 if(event==null){
                    throw new Except("Response event is null (timeout)");
                 }
                 //Exception ex_event=
                 event.getError();/**/
                 
       }
       protected void sendWalk(String oids) throws IOException, Except {}

       protected void sendBULK(String[] oids) throws IOException, Except {
                 PDU req_pdu;
                 
                 req_pdu = new PDU();
                 for (int i=0;i<oids.length;i++){
                     String oid=oids[i];
                     //System.out.println("#"+oid);
                     r_oid=new OID(oid);
                     VariableBinding  req_var_oid=new VariableBinding(r_oid);
                     req_pdu.add(req_var_oid);
                 }
                 
                 req_pdu.setType(PDU.GETBULK);
                 req_pdu.setMaxRepetitions(BULK_SIZE);
                 //pdu.setNonRepeaters(1);
                 _target.setVersion(SnmpConstants.version2c); /**/
                 
                 //event = snmp.send(req_pdu, _target, null);
                 event = snmp.send(req_pdu, _target);
                 if(event==null){
                    throw new Except("Response event is null (timeout)");
                 }
                 //Exception ex_event=
                		 event.getError();/**/

      }
      protected long [] getArrey() throws IOException, Except {
                //Integer32 requestId = event.getRequest().getRequestID();
                PDU       response  = event.getResponse();
                int index=0;
                long [] var=null;

                if(response==null){
                   Exception ex_event=null;
                   ex_event=event.getError();
                   String msg="Response PDU is null";
                   if(ex_event!=null)msg+="ex:"+ex_event.toString();
                   else msg+="(timeout) oid:"+r_oid.toString()+" address:"+address + " community:"+community + " is_start:"+is_start;

                   throw new Except(msg);
                }

                if(response.getErrorStatus()!=PDU.noError){
                   throw new IOException("Response PDU status:"+response.getErrorStatusText()+" error_index:"+response.getErrorIndex());
                }

                var=new long [response.size()];
                
                for(index=0;index<response.size();index++){
                   VariableBinding vb=null;
                   Variable        v=null;
                   OID             o=null;
                   var[index]=0;
                   vb=response.get(index);
                   if(vb==null)break;
                   o=vb.getOid();
                
                   if(o == null){
                       throw new Except("getOid() is null (timeout)");
                   }
                   if(o.size() < r_oid.size()){
                      //System.out.println("o.size < r_oid.size");
                      break;
                   }
                   if(r_oid.leftMostCompare(r_oid.size(), o) != 0){
                      //System.out.println("leftMostCompare "+r_oid.size());
                      //System.out.println(o.toString()+" ############### "+r_oid.toString());
                      break;
                   }
                   //System.out.println(vb.toString());
                
                   //System.out.println(o.toString()+" ############### "+r_oid.toString());
                   v=vb.getVariable();
                   if(v==null)break;
                   try{
                      var[index]=v.toLong();
                   }catch(Exception e){
                       throw new IOException("toLong() is null ex:"+e);
                
                   }
                }
                
                
                return var;
      }    
           
           
       /**  
       * M ethod which takes a single OID and returns the response from the agent as a Long. 
       * @ param oid 
       * @ return 
       * @ throws IOException 
       */  
       protected Variable getAs(String _oid) throws IOException,Except { 
           
                  if(is_start==false) throw new IOException("Transport is null");

                  sendGET(_oid);

                  PDU             res_pdu;
                  VariableBinding res_var_b;
                  Variable        res_var;
           
                  res_pdu   = event.getResponse();
           
                  if(res_pdu==null){
                     Exception ex_event=null;
                     ex_event=event.getError();
                     String msg="Response PDU is null";
                     if(ex_event!=null)msg+=" ex:"+ex_event.toString();
                     else              msg+=" (timeout) oid:"+_oid+" address:"+address + " community:"+community + " is_start:"+is_start;

                     throw new Except(msg);
                  }

                  if(res_pdu.getErrorStatus()!=PDU.noError){
                     Exception ex_event=null;
                     ex_event=event.getError();

                     String msg="Response PDU status:"+res_pdu.getErrorStatusText()+" oid:"+_oid+" address:"+address + " community:"+community + " is_start:"+is_start +" error_index:"+res_pdu.getErrorIndex();
                     if(ex_event!=null)msg+=" ex:"+ex_event.toString();
                     if(res_pdu.getErrorStatus()==PDU.noAccess)  throw new Except(msg);
                     if(res_pdu.getErrorStatus()==PDU.noSuchName)throw new Except(msg); 
                     else throw new IOException(msg);
                  }


                  res_var_b = res_pdu.get(0);


                  if(res_var_b==null) throw new IOException("Response VAR is null");

                  res_var   = res_var_b.getVariable();
                  if(res_var==null) throw new IOException("VAR is null");

             
                  return res_var;
       } 
       /** 
       * Method which takes a single OID and returns the response from the agent as a String. 
       * @param oid 
       * @return 
       * @throws IOException 
       */ 
       private long getAsNumber(String _oid) throws IOException,Except { return getAs(_oid).toLong(); }
       /** 
       * Method which takes a single OID and returns the response from the agent as a String. 
       * @param oid 
       * @return 
       * @throws IOException 
       */ 
       public String getAsString(String _oid) throws IOException,Except {  return getAs(_oid).toString(); }

       public long getLong(String oid,int index,String ext_index) throws IOException,Except { 
              long out_put;
              String work_oid=oid;

              if(ext_index.equals("-")==false)work_oid=new String(work_oid+"."+ext_index);
              if(index>=0)work_oid=new String(work_oid+"."+index);
              
              out_put=getAsNumber(work_oid);

              Logger.getLogger(getClass().getName()).trace("get oid:"+work_oid+" value:"+out_put);

              return out_put;
       }
       public long getSum(String oid) throws IOException,Except { 
              long    out_put=0;
              long [] out;

              out=getArrey(oid);

              for(int index=0;index<out.length;index++){
                  out_put+=out[index];
                  //Logger.getLogger(getClass().getName()).trace(">>value["+index+"]="+out[index]);
              }

              Logger.getLogger(getClass().getName()).trace("getSum oid:"+oid+" value:"+out_put);

              return out_put;
       }
       public long[] getArrey(String oid) throws IOException,Except { 
              long [] out;
              String[] oids=new String[1];
              oids[0]=oid;

              sendBULK(oids);

              out=getArrey();

              Logger.getLogger(getClass().getName()).trace("getArrey oid:"+oid+" size:"+out.length);

              return out;
       }
       public long getAver(String oid) throws IOException,Except { 
              long    out_put=0;
              long [] out;
              String[] oids=new String[1];
              oids[0]=oid;

              sendBULK(oids);

              out=getArrey();

              for(int index=0;index<out.length;index++){
                  out_put+=out[index];
              }
              out_put/=out.length;

              Logger.getLogger(getClass().getName()).trace("getAver oid:"+oid+" value:"+out_put);

              return out_put;
       }



       public static void main(String[] args) throws IOException,Except { 
              snmpCln cln;

              cln=new snmpCln("udp:10.93.128.11/161","public");
              cln.start();
              /*
              String data1=cln.getAsNumber(".1.3.6.1.2.1.2.2.1.10.2")+"";
              System.out.println("V1:"+ data1);
              String data2=cln.getAsNumber(".1.3.6.1.2.1.25.2.3.1.5.2")+"";
              System.out.println("V2:"+ data2);
              */
              long old_data=0;
              long cur_data=0;
              long old_data1=0;
              long cur_data1=0;
              long old_data4=0;
              long cur_data4=0;
              //while(cln!=null){
                    java.util.Date date=new java.util.Date();
                    cur_data1=cln.getAsNumber(".1.3.6.1.2.1.25.5.1.1.1.1");
                    cur_data4=cln.getAsNumber(".1.3.6.1.2.1.25.5.1.1.1.4");
                    cur_data=cln.getSum(      ".1.3.6.1.2.1.25.5.1.1.1");
                    long d=cur_data-old_data;
                    long d1=cur_data1-old_data1;
                    long d4=cur_data4-old_data4;
                    old_data=cur_data;
                    old_data1=cur_data1;
                    old_data4=cur_data4;
                    System.out.println("S[]="+ d +" S[4-n]="+(d-d1)+" S[4-n]/S[1]="+((d-d1)*100.0)/d + "%   "+ date.toString()+" " + d4);
                    tfork.delay(120);

              //}
              cln.stop(); 
                  
              
       } 


} 
