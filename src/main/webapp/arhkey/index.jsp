<%@ page language="java" 
%>
<%@ page contentType="text/html; charset=UTF-8"%>    
<%
boolean is_all=false;
        String  _is_all = (String) request.getParameter("eml");
        if(_is_all!=null)is_all=true;

String  req_url;
String  prn_utl;
String  go_url;
        if(is_all){
           req_url="arh/list";
           prn_utl="перечень сертификатов";
           go_url="index.jsp";
        }
        else{
           prn_utl="перечень сообщений";
           req_url="arh/x509";
           go_url="index.jsp?eml=true";
        }

%>    

<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
   <meta name="Author" content="Andrey Shadrin">


   <link   rel="stylesheet"      type="text/css"       href="inc/ui1121/jquery-ui.css"> 
   <link   rel="stylesheet"      type="text/css"       href="inc/main.css">
   <link   rel="stylesheet"      type="text/css"       href="inc/clean.css">
   <script language="javascript" type="text/javascript" src="inc/jquery-3.5.1.min.js"></script>
   <script language="javascript" type="text/javascript" src="inc/ui1121/jquery-ui.min.js"></script>
   <script language="javascript" type="text/javascript" src="inc/jquery.columns.js"></script>
   <script language="javascript" type="text/javascript" src="inc/mustache.js"></script> 
   <title>LIST CERTIFICATE</title>

<script type="text/javascript">
        var rec_data=[];
        var oper_data= new Date();
        var columns1;
        var type_page="0";

        function showPage(){
                 if(type_page==="0")getStateAll();
                 if(type_page==="1")getStateType('X509 CRL');
                 if(type_page==="2")getStateType('CERTIFICATE REQUEST');
                 if(type_page==="3")getStateType('CERTIFICATE');
                 if(type_page==="4")getAlarm();
        }

        function showState(){

                if(columns1!=undefined)columns1.destroy();

                columns1 = $('#list_msg').columns({
                size:3000,
                search: false,
                showRows: [ 100, 200, 300],
                size: 100,
                data: rec_data,
                schema: [
                  {"header":"№"                   ,"key":"num"           , "template":"<center><center>{{num}}</center>"},
                  <% if(is_all){ %>
                  {"header":"От кого"             ,"key":"from"          , "template":"{{from         }}"},
                  {"header":"Послано"             ,"key":"sentdate"      , "template":"<nobr>{{sentdate     }}</nobr>"},
                  {"header":"Принято"             ,"key":"receivedate"   , "template":"<nobr>{{receivedate  }}</nobr>"},
                  <% } %>
                  {"header":"Владелец сертификата","key":"x509subject"   , "template":"{{x509subject  }}"},
                  {"header":"УДЦ"                 ,"key":"x509issuer"    , "template":"{{x509issuer   }}"},
                  {"header":"Серийный"            ,"key":"x509serial"    , "template":"{{x509serial   }}"},
                  {"header":"имя файла файл"      ,"key":"filename"      , "template":"<a href=arh/get?x509_id={{x509_id}}>cert{{x509_id}}</a>"},
                  {"header":"Действует с"         ,"key":"x509begindate" , "template":"<nobr>{{x509begindate}}</nobr>"},
                  {"header":"Действует до"        ,"key":"x509enddate"   , "template":"<nobr>{{x509enddate  }}</nobr>"},
                  {"header":"тип"                 ,"key":"x509type"      , "template":"{{x509type     }}"},
                  {"header":"тип файла"           ,"key":"x509typefile"  , "template":"{{x509typefile }}"},
                  {"header":"Отменен"             ,"key":"x509DateRL"    , "template":"{{x509DateRL}}"},
                  {"header":""                    ,"key":"x509_id"       , "template":"{{}}"},
                  //{"header":"Кому"               ,"key":"to"            , "template":"{{to           }}"},
                  //{"header":"ID"                 ,"key":"id"            , "template":"{{id           }}"},
                  //{"header":"mime type"          ,"key":"mime"          , "template":"{{mime         }}"},
                  <% if(is_all){ %>
                  {"header":""                    ,"key":"uid"           , "template":"{{}}"},
                  <% } %>

                ]
            });
            oper_data= new Date();

        }
        function getStateAll(){
            var req_url='<%=req_url%>';
            _getState(req_url);
        }
        function getStateType(type){
            var req_url='<%=req_url%>?type='+type;
            _getState(req_url);
        }
        function getAlarm(){
            var req_url='arh/alarm';
            _getState(req_url);
        }

        function _getState(req_url){
            rec_data=[];
            jQuery.ajax({
                   url:  req_url, 
                   cache: false,
                   type: "GET",
                   dataType: "JSON",
                   contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            
                   success: function(data1){
                                          //--------------------------------------------
                                     var data=data1;
                                     if(data.state!=false)
                                     for(i=0;i<data.list.length;i++){
                                         rec_data[i]={};
                                         var d=data.list[i].header;
                                         rec_data[i].num             =i+1            ;
                                         rec_data[i].uid             =d.uid          ;
                                         rec_data[i].from            =d.From         ;
                                         //rec_data[i].to              =d.To           ;
                                         //rec_data[i].id              =d.Id           ;
                                         rec_data[i].subject         =d.Subject      ;
                                         rec_data[i].filename        =d.Filename     ;
                                         rec_data[i].sentdate        =d.SentDate     ;
                                         rec_data[i].receivedate     =d.ReceiveDate  ;
                                         rec_data[i].x509_id         =d.X509_id      ;
                                         rec_data[i].x509type        =d.X509Type     ;
                                         rec_data[i].x509typefile    =d.X509TypeFile ;
                                         rec_data[i].x509begindate   =d.X509BeginDate;
                                         rec_data[i].x509enddate     =d.X509EndDate  ;
                                         rec_data[i].x509serial      =d.X509Serial   ;
                                         rec_data[i].x509subject     =d.X509Subject  ;
                                         rec_data[i].x509issuer      =d.X509Issuer   ;
                                         rec_data[i].x509DateRL      =d.X509DateRL   ;

                                     } 
                                     
                                     showState();

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
                showPage(); 
                setInterval(showPage,30000);
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
               <input class="ts" name="radio" type="radio" id="r0" value="0" checked>Все объекты
               <input class="ts" name="radio" type="radio" id="r1" value="1">X509 CRL
               <input class="ts" name="radio" type="radio" id="r2" value="2">CERTIFICATE REQUEST
               <input class="ts" name="radio" type="radio" id="r3" value="3">CERTIFICATE
               <input class="ts" name="radio" type="radio" id="r3" value="4">ALARM
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
           showPage(); 
        });
</script>

<table>
<tr>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="/">Главная</a></font></td>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="<%=go_url%>"><%=prn_utl%></a></font></td>
<td><font color="#FF0000">/</font></td>
<td><font size="-2"><i><div id="CLK"></div></font></td>
<td><font size="-2"><font color="#FF0000"><i><div id="ERR"></div></font></font></td>
</tr>
</table>

<hr>
<table width="100%"> 
<tr><td>
    <div id="id_list_right" style="background-color: #f7f7f9;width:100% ">
       <div id="list_msg" style="float:rigth;width:100% " ></div>
    </div>
</td></tr>
</table>

<hr>
<table>
<tr>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="/">Главная</a></font></td>
<td><font color="#FF0000">/</font></td>
<td><font size="-1"><a href="<%=go_url%>"><%=prn_utl%></a></font></td>
<td><font color="#FF0000">/</font></td>
</tr>
</table>

</body>
</html>


