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
   <script language="javascript" type="text/javascript" src="inc/jquery-3.5.1.min.js"></script>
   <title>RCMD Дионис</title>

<script type="text/javascript">

        function getList(){
            var req_url='syslog/list';
            rec_data=[];
            jQuery.ajax({
                   url:  req_url, 
                   cache: false,
                   type: "GET",
                   dataType: "JSON",
                   contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            
                   success: function(data1){
                                          //--------------------------------------------
                                     var list_msg =data1.list_msg;

                                     var txt='<table id="table_state" border=1 width="100%">';
                                         txt+='<tr class="head_t">';
                                         txt+='<td width="3%">№</td>';
                                         txt+='<td width="12%">Дата</td>';
                                         txt+='<td width="5%">Level</td>';
                                         txt+='<td width="5%">Хост</td>';
                                         txt+='<td>Сообщение</td>';
                                         txt+='</tr>';
                                     for(var i=0;i<list_msg.length;i++){
                                         txt+='<tr>';
                                         txt+='<td align="center">'+(i+1)+'</td>';
                                         txt+='<td>'+list_msg[i].date+'</td>';
                                         txt+='<td align="center">'+list_msg[i].level+'</td>';
                                         txt+='<td>'+list_msg[i].host+'</td>';
                                         txt+='<td>'+list_msg[i].msg+'</td>';
                                         txt+='</tr>';
                                     }

                                     txt+='</table>';
                                     $("#table_state").remove();                                               
                                     $("#list_msg").empty().html(txt);                                               
                                     txt=null;
                                     oper_data= new Date();
                                     $("#CLK_ID").remove();                                               
                                     $("#CLK").empty().html('<div id="CLK_ID">'+oper_data+'</div>');                                               
                                     $("#ERR_ID").remove();                                               
                                     $("#ERR").empty().html('<div id="ERR_ID"></div>');                                               

                            },
                   error: function(err){
                           $("#ERR_ID").remove();                                               
                           $("#ERR").empty().html('<div id="ERR_ID">Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')</div>');                                               

                   },
                   timeout: 5000
            });
            
            req=null;

        }
        $(document).ready(function() { 
                             getList();
                 setInterval(getList,5000);
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
            <div class="head_t" id="id_header">ДИОНИС</div>
          </td>
        </tr>
        <tr>
          <td class="head_f">&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
</table>


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
       <div id="list_msg"  ></div>
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


