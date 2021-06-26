package org.little.mq.stream.config;

import java.util.ArrayList;

import org.little.util.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class cfg_point extends cfg_addr{
       final private static String CLASS_NAME="org.little.mq.stream.config.cfg_point";
       final private static int    CLASS_ID  =1802;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);
   

       private String  reportQMName    ;
       private String  reportQueueName ;
       private String  report          ;
       private String  mask            ;
       private String  last            ;
       private String  expiry          ;
       private boolean is_last         ;
       private boolean is_default      ;
     
       public cfg_point(){clear();}
       
       public void clear() {
              super.clear();
              reportQMName    = null;
              reportQueueName = null;
              report          = null;
              mask            = null;
              last            = null;
              expiry          = null;
              is_last         = false;
              is_default      = false;
       }
       public String  getReportQMName   (){return reportQMName;   }
       public String  getReportQueueName(){return reportQueueName;}
       public String  getReport         (){return report;         }
       public String  getMask           (){return mask;           }
       public String  getExpiry         (){return expiry;         }
       public boolean isLast            (){return is_last;        }
       public boolean isDefault         (){return is_default;     }

       public void    isDefault         (boolean is)  {is_default     =is;}
       public void    setReportQMName   (String  n)   {reportQMName   =n;}
       public void    setReportQueueName(String  n)   {reportQueueName=n;}
       public void    setReport         (String  n)   {report         =n;}
       public void    setMask           (String  n)   {mask           =n;}
       public void    setExpiry         (String  n)   {expiry         =n;}
       public void    setLast           (String  n)   {last=n;if(last!=null)if(last.equalsIgnoreCase(def.CNFG_TAG_YES))is_last=true;;}
        
       public int parse(Node node) {
              return parse(node.getChildNodes());
       }
       private int parse(NodeList nodeList) {
               int n  = nodeList.getLength();
               int k;

               for(k = 0; k < n; k++) {
                    Node nd=nodeList.item(k);
                    String tag =nd.getNodeName();
                    String cntx=nd.getTextContent();
                    //System.out.println("k:"+k+" node:"+tag+" text:"+cntx);

                    if(tag==null){
                        continue;
                    }
                     
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_REPLY_QM_NAME    ))setReportQMName   (cntx);
                    else                                                                                
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_REPLY_QUEUE_NAME ))setReportQueueName(cntx);
                    else                                                                                
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_POINT            ))setMask           (cntx);
                    else
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_REPORT           ))setReport         (cntx);
                    else                                                                              
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_TARGET_QM_NAME   ))setQMName         (cntx);
                    else                                                                              
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_TARGET_QUEUE_NAME))setQueueName      (cntx);
                    else                                                                          
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_LAST             ))setLast           (cntx);
                    else                                                                              
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_EXPIRY           ))setExpiry         (cntx);
                    else continue;

                      
               }

               
               if(mask==null){ 
                  log.error("config interface:"+getID()+" "+def.CNFG_TAG_POINT+" is empty" );
                  return -1;
               }
               if(getQueueName()==null){ 
                  log.error("config interface:"+getID()+" "+def.CNFG_TAG_TARGET_QM_NAME+" is empty" );
                  return -1;
               }
               
               
               return 0;
      }

      public boolean is(ArrayList<cfg_point> outqueue){
             if(getQMName()==null   || getQueueName()==null)return false;
             int n  = outqueue.size();
             for (int i = 0; i < n; i++) { 
                  cfg_point p=outqueue.get(i);
                  if(this.equals(p))return true;
             }

             return false;                        
      }
      public String toString(){
             return super.toString()+
                    " reportQMName:"      +reportQMName   +
                    " reportQueueName:"   +reportQueueName+
                    " report:"            +report         + 
                    " mask:"              +mask           + 
                    " last:"              +last           + 
                    " expiry:"            +expiry         + 
                    " is_last:"           +is_last        + 
                    " is_default:"        +is_default     + 
                    "";
      }

}
