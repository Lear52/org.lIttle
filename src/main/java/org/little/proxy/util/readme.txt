
1 если клиент авторизирован goto 11
2 получаем ip клиента
3 user=userlist.isGuest(ip клиента)
3 if(user!=null) goto END
4 получаем type аутентификации (type_auth)
5 принимаем заголовок http.header
6 if( http.header.auth == type_auth) goto 9
7 посылаем 401+type_auth
8 goto 5
9 получаем user
10 if( user.isCheck() ==false) goto 7
11 (клиент авторизирован)=true;
12 запрашиваем страницу у сервера
13 if (http.header.cod==401)(клиент авторизирован)=false;