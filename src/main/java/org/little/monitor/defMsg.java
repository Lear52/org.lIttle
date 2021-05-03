package org.little.monitor;

/** 
 * class dataMsg
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2021
 * @version 2.0
 */
public class defMsg{

       public static final String  instance_null               = "common.getInstance()==null";
       public static final String  db_null                     = "common.getInstance().getDB()==null";
       public static final String  db_open_null                = "common.getInstance().getDB().open()==null";

       public final static String put_snmp_data                ="Put recData to DB ";
       public final static String get_snmp_data                ="Get recData from DB ";
       public final static String aggr_snmp_data               ="Aggr recData from DB";
       public final static String make_html_snmp_data          ="Make html for recData";
       public final static String make_xml_snmp_data           ="Make XML for recData";
       public final static String make_img_snmp_data           ="Make image for recData";
       public final static String no_snmp_data                 ="Empty recData";
       public final static String list_request_is_null         ="List request is NULL";
       public final static String get_list_request             ="Get list data request";
       public final static String get_list_host                ="Get list request host";
       public final static String get_list_task                ="Get task for request";
                                                           
       public final static String put_snmp_record              ="Put dataRecord to DB ";
       public final static String get_snmp_record              ="Get dataRecord from DB";
       public final static String begin_init                   ="Begin init";
       public final static String end_init                     ="End init";
       public final static String inf_is_null                  ="Interface information is NULL";
       //public final static String scheduller_run             ="Scheduller run";
       public final static String set_task                     ="Set task for run";
      
       public final static String start_task_get_snmp_record   ="Start dataRecord.work()";
       public final static String stop_task_get_snmp_record    ="Stop dataRecord.work()";
       public final static String run_task_get_snmp_record     ="Run dataRecord.work()";

       public final static String start_task_get_snmp_request  ="Start dataRequest.work()";
       public final static String stop_task_get_snmp_request   ="Stop dataRequest.work()";
       public final static String run_task_get_snmp_request    ="Run dataRequest.work()";

       public final static String start_task_get_host          ="Start dataGet.work()";
       public final static String stop_task_get_host           ="Stop dataGet.work()";
       public final static String run_task_get_host            ="Run dataGet.work()";

       public final static String start_task_aggr_snmp_record  ="Start dataRecord.aggr()";
       public final static String stop_task_aggr_snmp_record   ="Stop dataRecord.aggr()";
       public final static String start_task_aggr_snmp_request ="Start dataRequest.aggr()";
       public final static String stop_task_aggr_snmp_request  ="Stop dataRequest.aggr()";

       public final static String create_snmp_client           ="Create snmp client";
       public final static String stop_snmp_client             ="Stop snmp client";
       //public final static String init_snmp_client             ="Init snmp client";
       public final static String open_snmp_client             ="Open snmp client";
       public final static String close_snmp_client            ="Close snmp client";
      
       public final static String config_smnp_aggr_timeout     ="smnp.aggr.timeout";   
       public final static String config_smnp_clear_period     ="smnp.clear.period";   
       public final static String config_smnp_aggr_1_interval  ="smnp.aggr.1.interval";
       public final static String config_smnp_aggr_1_period    ="smnp.aggr.1.period";  
       public final static String config_smnp_aggr_2_interval  ="smnp.aggr.2.interval";
       public final static String config_smnp_aggr_2_period    ="smnp.aggr.2.period";  
       public final static String config_smnp_aggr_delay       ="smnp.aggr.delay";

}
