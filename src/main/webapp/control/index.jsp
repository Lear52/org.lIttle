<%@ page language="java" 
%>

<%@ page contentType="text/html; charset=UTF-8"%>    

<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
   <meta name="Author" content="Andrey Shadrin">
   <link   rel="stylesheet"      type="text/css"       href="inc/main.css">
   <link   rel="stylesheet"      type="text/css"       href="inc/clean.css">
   <script language="javascript" type="text/javascript" src="inc/jquery-3.6.0.js"></script>
   <title>CONTROL MQ FLOW</title>
<script type="text/javascript">
        var txt;
        var cur_timeout=5000;       
        var is_alarm   =false; // sssh
        var last_alarm =false;
        var type_page  ="0";
        //var timeoutid  ;
        var audio      =new Audio('inc/alarm.mp3');
        var is_expand_q=false;

        function set_auto(_set_auto){

        }
        function set_timeout(_timeout){

        }
        function clear_q(group_id,flow_id,mngr_id,q_id){
                 var req_url='cmd/clr?group='+group_id+'&flow='+flow_id+'&mngr='+mngr_id+'&q='+q_id;
                 jQuery.ajax({
                        url:  req_url, 
                        cache: false,
                        type: "GET",
                        dataType: "JSON",
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                 
                        success: function(data1){
                                 getList();
                        },
                        error: function(err){
                           alert('Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')');          
                        },
                        timeout: 5000
                 });

        }

        function set_flag(group_id,flow_id,is_flag){
                 var req_url='cmd/cntrl?group='+group_id+'&flow='+flow_id+'&state='+is_flag;
                 jQuery.ajax({
                        url:  req_url, 
                        cache: false,
                        type: "GET",
                        dataType: "JSON",
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                 
                        success: function(data1){
                                         getList();
                        },
                        error: function(err){
                           alert('Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')');          

                        },
                        timeout: 5000
                 });
        }
        var handler_control_flow=function() { 
            var v=this.getAttribute('VALUE');
            var gr_id=this.getAttribute('gr_id');
            var fl_id=this.getAttribute('fl_id');
            if(v==='STOP')set_flag(gr_id,fl_id,true);
            if(v==='START')set_flag(gr_id,fl_id,false);

        };
        var handler_control_clear=function() { 
            var v=this.getAttribute('VALUE');
            var gr_id=this.getAttribute('gr_id');
            var fl_id=this.getAttribute('fl_id');
            var mngr_id=this.getAttribute('mngr_id');
            var q_id=this.getAttribute('q_id');
            clear_q(gr_id,fl_id,mngr_id,q_id);
        };
        function make_table(data1){
                 var list_group =data1.list;
                 txt='<table id="table_state" border=1 width="100%" cellspacing="0" cellpadding="0">';
                 txt+='<tr class="head_t" ><td>№</td><td>имя группы</td><td colspan="5">контролируемые потоки</td></tr>';
                 for(var i=0;i<list_group.length;i++){
                     var grp=list_group[i];
                     var rows=1+grp.list.length;
                     //------------------------------------------------------------------------------------------------------------------------
                     txt+='<tr align=center><td rowspan="'+rows+'">'+grp.id+'</td><td rowspan="'+rows+'">'+grp.name+'<br><i><font size="-2">статус:'+grp.state+'</font></i></td>';
                     txt+='<td class="head_t">№</td><td class="head_t">имя потока</td><td class="head_t">очереди</td><td class="head_t">Управление потоком</td><td class="head_t">Flag</td></tr>';
                     var list_flow =grp.list;
                     for(var j=0;j<list_flow.length;j++){
                         var flow=list_flow[j];
                         var _cl='';
                         if(flow.alarm)_cl='class="alarm"';

                         //------------------------------------------------------------------------------------------------------------------------
                         txt+='<tr align=center>';
                         txt+='<td>'+flow.id+'</td><td  '+_cl+'>'+flow.name+'</td>';
                         txt+='<td align="left">';
                         var list_q =flow.list;
                         //------------------------------------------------------------------------------------------------------------------------
                         txt+='<table id="flow_'+i+'_'+j+'" border=0 cellspacing="0" cellpadding="0">';
                         for(var jj=0;jj<list_q.length;jj++){
                             var q=list_q[jj];
                             var alrm='';
                             var cl='';
                             if(q.alarm){
                                alrm='        !!!!!!!!!!!!!!!!!!';cl='class="alarm"';is_alarm=true;
                                txt+='<tr><td '+cl+'>'+q.mngr+'</td><td>@</td><td '+cl+'>'+q.queue+'</td><td>=</td><td '+cl+'>'+q.len+'</td><td '+cl+'>'+alrm+'</td>';
                                txt+='<td> -> </td>';
                                txt+='<td><input type="submit" name="CONTROL_CLEAR" VALUE="CLEAR" id="clear_q" gr_id="'+grp.id+'" fl_id="'+flow.id+'" mngr_id="'+q.mngr+'" q_id="'+q.queue+'"></input></td>';
                                txt+='</tr>';
                             }
                             else {
                                if(is_expand_q){
                                   alrm=' ';
                                   txt+='<tr><td '+cl+'>'+q.mngr+'</td><td>@</td><td '+cl+'>'+q.queue+'</td><td>=</td><td '+cl+'>'+q.len+'</td><td '+cl+'>'+alrm+'</td>';
                                   txt+='<td> </td>';
                                   txt+='<td> </td>';
                                   txt+='</tr>';
                                }
                             }
                             q=null;
                             alrm=null;
                             cl=null;
                         }
                         txt+='</table>';
                         //------------------------------------------------------------------------------------------------------------------------
                         txt+='</td><td>';
                         if(flow.control.is_manual){
                            txt+='manual';
                         }
                         else{
                             if(type_page==="0"){
                                if(flow.control.is_flag){
                                   txt+='<input type="submit" VALUE="START" name="CONTROL_FLOW" id="contrl_flow01" gr_id="'+grp.id+'" fl_id="'+flow.id+'"> </input>';
                                   //txt+='<form name="net_record_save" action="javascript:set_flag(\''+grp.id+'\',\''+flow.id+'\',false)"><input type="submit" VALUE="START" id="save_net_rec"></input></form>';
                                }
                                else{
                                   txt+='<input type="submit" VALUE="STOP" name="CONTROL_FLOW" id="contrl_flow01" gr_id="'+grp.id+'" fl_id="'+flow.id+'"> </input>';
                                   //txt+='<form name="net_record_save" action="javascript:set_flag(\''+grp.id+'\',\''+flow.id+'\',true)"><input type="submit" VALUE="STOP" id="save_net_rec"></input></form>';
                                }
                             }
                         }
                         txt+='</td>';
                         txt+='<td '+_cl+'> alarm:'+flow.alarm+'<br>flag:'+flow.control.is_flag+"<br>manual:"+flow.control.is_manual+'</td>';
                         txt+='</tr>';
                         //------------------------------------------------------------------------------------------------------------------------
                         flow=null;
                         _cl=null;
                         list_q=null;
                     }
                     list_flow=null;
                     grp      =null;
                     rows     =null;
                     txt+='</td></tr>';
                     //------------------------------------------------------------------------------------------------------------------------
                 }
                 list_group=null;

                 txt+='</table>';

        }
        function getList(){
 
            var req_url='cmd/list';
            var is_alarm=false; 
            jQuery.ajax({
                   url:  req_url, 
                   cache: false,
                   type: "GET",
                   dataType: "JSON",
                   contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            
                   success: function(data1){
                                     //--------------------------------------------
                                     set_auto(data1.auto);
                                     set_timeout(data1.timeout);
                                     make_table(data1);
                                     //$("#txt_list").replaceWith('<div id="txt_list">'+txt+'</div>');                                               
                                     //$('input[name="CONTROL_FLOW"]').unbind('click',handler_control_flow);
                                     //$('input[name="CONTROL_CLEAR"]').unbind('click',handler_control_clear);
                                     $('input[name="CONTROL_FLOW"]').unbind();
                                     $('input[name="CONTROL_CLEAR"]').unbind();
                                     $("#table_state").remove();                                               
                                     $("#txt_list").empty().html(txt);                                               
                                     $('input[name="CONTROL_FLOW"]').bind('click',handler_control_flow);
                                     $('input[name="CONTROL_CLEAR"]').bind('click',handler_control_clear);

                                     //document.getElementById('txt_list').innerHTML=txt;
                                     txt  =null;
                                     data1=null;
                                     
                                     if(!last_alarm && is_alarm) {
                                       audio.play();
                                     }
                                     last_alarm=is_alarm;

                                     oper_data= new Date();
                                     $("#CLK_ID").remove();                                               
                                     $("#CLK").empty().html('<div id="CLK_ID">'+oper_data+'</div>');                                               
                                     $("#ERR_ID").remove();                                               
                                     $("#ERR").empty().html('<div id="ERR_ID"></div>');                                               
                                     oper_data=null;


                            },
                   error: function(err){
                           $("#ERR_ID").remove();                                               
                           $("#ERR").empty().html('<div id="ERR_ID">Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')</div>');                                               
                           // document.getElementById('ERR').innerHTML='Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')';
                           //alert('Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')');          

                   },
                   timeout: 5000
         });
        }
        $(document).ready(function() { 
                 getList();
                 setInterval(getList,cur_timeout);
        });

</script>

</head>

<body>
<table BORDER=0 width="100%" cellspacing="0" cellpadding="0" height="46">
  <tr> 
    <td width=15> 
      <table BORDER=0 width="15" cellspacing="0" cellpadding="0">
        <tr>
          <td class="head_f"><font size="-3">&nbsp;</font></td>
        </tr>
        <tr>
          <td height=36 class="head_t">&nbsp;</td>
        </tr>
        <tr>
          <td class="head_f"><font size="-3">&nbsp;</font></td>
        </tr>
      </table>
    </td>
    <td width=64 class="head_f"><a href="/"><img SRC="inc/logo.gif" BORDER=0 height=46 width=64></a></td>
    <td width="100%"> 
      <table BORDER=0 width="100%" cellspacing="0" cellpadding="0">
        <tr>
          <td class="head_f">&nbsp;</td>
        </tr>
        <tr>
          <td height=32 class="head_t" nowrap> 
            <div class="head_t" id="id_header">
            <div class="head_t" id="id_header">
               <input class="ts" name="radio" type="radio" id="r0" value="0" checked>Ручное управление потоком
               <input class="ts" name="radio" type="radio" id="r1" value="1">Автоматическое управление потоком
            </div>
            
          </td>
        </tr>
        <tr>
          <td class="head_f">&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<script>
        $('input[name="radio"]').click(function() { 
           type_page=$(this).val();
           getList(); 
        });
</script>


<table>
<tr>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="/">Главная</a></font></td>
<td><font color="#FF0000">/</font></td>
<td><font size="-2"><i><div id="CLK"></div></font></td>
<td><font size="-2"><font color="#FF0000"><i><div id="ERR"></div></font></font></td>
</tr>
</table>
<hr>
    <div id="id_list_right">
       <div id="txt_list"  ></div>
    </div>

<hr>
<table>
<tr>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="/">Главная</a></font></td>
<td><font color="#FF0000">/</font></td>
</tr>
</table>

</body>
</html>


