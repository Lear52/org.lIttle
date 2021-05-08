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

        function parseLS(_text,_apk,_cmd){
                 //var a_txt=_text.split(/(s+)/).filter( function(e){return e.trim().length>0;} );
                 var a_dir=_cmd.split("ls ");

                 var _dir=a_dir[1];//'log:'
                 var a_txt2=[];
                 var j=0;
                 var a_txt=_text.split(/\s+/);
                 for(var i=0;i<a_txt.length;i++){
                     var t=a_txt[i];
                     if(t==="")continue;
                     if(t===" ")continue;
                     if(j<5)a_txt2[j]=t;
                     else a_txt2[4]+=' '+t;
                     j++;
                 }
                 var txt='<tr>';
                 for(var i=0;i<a_txt2.length;i++){
                     var t=a_txt2[i];
                     var i_txt=t.indexOf('/');
                     if(i_txt!=-1 || i<4){txt+='<td>'+t+'</td>';}
                     else{ txt+='<td><a href="rcmd/rcmd/receive?apk='+_apk+'&file='+_dir+'/'+t+'">'+t+'</a></td>';}
                 }
                 txt+='</tr>';
                 return txt;
        }
        function parseCMD(_text,_apk,_cmd){
                 return _text+'<br>';
        }
        function _getCMD(_command,_apk,_id_div,_fun){
            var req_url='rcmd/rcmd/cmd?type=js&cmd='+_command+'&apk='+_apk;
            var _id=_id_div;
            rec_data=[];
            jQuery.ajax({
                   url:  req_url, 
                   cache: false,
                   type: "GET",
                   dataType: "JSON",
                   contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            
                   success: function(data1){
                                          //--------------------------------------------
                                     var list=data1.list;
                                     var txt='<table><tr><td><table>';
                                     var _cmd=list[0].txt;
                                     txt+='<table border=0>';
                                     for(var i=1;i<list.length-1;i++){
                                         //txt+=list[i].txt+'<br>';
                                         txt+=_fun(list[i].txt,_apk,_cmd);
                                     }
                                     txt+='</table>';
                                     txt+='</td></tr></table>';
                                     $(_id).html(txt);                                               
                                     _id=null;
                                     txt=null;
                                     oper_data= new Date();
                                     document.getElementById('CLK').innerHTML=' '+oper_data;
                                     document.getElementById('ERR').innerHTML=' ';

                            },
                   error: function(err){
                            document.getElementById('ERR').innerHTML='Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')';
                           //alert('Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')');          

                   },
                   timeout: 5000
            });
            
            req=null;

        }
        function getF(run_apk,run_div){
                 var _command='dir_flash1';
                 var _apk=run_apk;
                 var _run_div=run_div;
                 _getCMD(_command,_apk,_run_div,parseLS);
        }
        function getCMD(_command,_apk){
                 var _run_div='#txt_cmd';
                 _getCMD(_command,_apk,_run_div,parseCMD);
        }

        function getList(){
            var req_url='rcmd/rcmd/list?type=js';
            rec_data=[];
            jQuery.ajax({
                   url:  req_url, 
                   cache: false,
                   type: "GET",
                   dataType: "JSON",
                   contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            
                   success: function(data1){
                                          //--------------------------------------------
                                     var list_cmd =data1.list_cmd;
                                     var list_node=data1.list_node;

                                     var txt='<table border=1>';
                                     txt+='<tr><td>#</td>';
                                     for(var j=0;j<list_node.length;j++){
                                         var apk=''+list_node[j].id+'';
                                         var host=''+list_node[j].host+'';
                                         txt+='<td align=center>'+apk+'<br>'+host+'</td>';
                                     }
                                     txt+='</tr>';
                                     for(var i=0;i<list_cmd.length;i++){
                                         var cmd=''+list_cmd[i].id+'';
                                         txt+='<tr align=center><td>'+(i+1)+'</td>';

                                         for(var j=0;j<list_node.length;j++){
                                             var apk=''+list_node[j].id+'';
                                             txt+='<td><a href="#" onclick="getCMD(\''+cmd+'\',\''+apk+'\');" >'+cmd+'</a><p>(<a href="rcmd/rcmd/get?type=txt&cmd='+cmd+'&apk='+apk+'" ><i>'+cmd+'</i></a>)</td>';
                                         }
                                         txt+='</tr>';
                                     }

                                     txt+='</table>';
                                     $("#list_cmd").html(txt);                                               
                                     txt=null;
                                     oper_data= new Date();
                                     document.getElementById('CLK').innerHTML=' '+oper_data;
                                     document.getElementById('ERR').innerHTML=' ';

                            },
                   error: function(err){
                            document.getElementById('ERR').innerHTML='Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')';
                           //alert('Ошибка:получения данных по запросу url:'+req_url+'\n Сервер вернул статус:'+err.statusText+'('+err.status+')');          

                   },
                   timeout: 5000
            });
            
            req=null;

        }
        $(document).ready(function() { 
                             getList();
                             getF('ext-tu','#list_flash');
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

<p><a href="rcmd/list?type=txt">List COMMAND</a>
<p><a href="rcmd/get?type=txt&cmd=open1&apk=ext-tu">open1 COMMAND</a>
<hr>
    <table width="100%"><tr><td>
    <div id="id_list_left">
       <div id="list_cmd"  ></div>
    </div>
    </td><td>
    <div id="id_list_right">
       <div id="list_flash" ></div>
    </div>
    </td></tr></table>
<hr>
    <div id="id_list_right">
       <div id="txt_cmd"  ></div>
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


