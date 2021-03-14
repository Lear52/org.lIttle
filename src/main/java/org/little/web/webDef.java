package org.little.web;
/** 
 * Класс webDef - содержит 
 *
 * 
 * @author Andrey Shadrin, Copyright &#169; 2012
 * @version 1.0
 */ 
public final class webDef{
       final private static int    CLASS_ID  =402;
             public  static int    getClassId(){return CLASS_ID;}

        /*
        * команда выхода
        */
	public static final String cmd_exit              = "x";

	public static final String cmd_error             = "err";

	public static final String cmd_create_task        = "ct";
	public static final String cmd_del_task           = "dt";
	public static final String cmd_set_task           = "st";
        //------------------------------------------------------
        /*
        * команда создания персоны
        */
	public static final String cmd_create_org        = "co";
	//public static final String cmd_add_org           = "ao";
        /*
        * команда модификации персоны
        */
	public static final String cmd_set_org           = "so";
        /*
        * команда вызова формы изменения-добавления персоны
        */
	public static final String cmd_edit_org          = "eo";
        /*
        * команда удаления персоны
        */
	public static final String cmd_del_org           = "do";
        //------------------------------------------------------
        /*
        * команда создания группы
        */
	public static final String cmd_create_group      = "cg";
        /*
        * команда модификации группы
        */
	public static final String cmd_set_group         = "sg";
        /*
        * команда удаления группы
        */
	public static final String cmd_del_group         = "dg";

        //------------------------------------------------------
        /*
        * команда аутентификации пользователя
        */
	public static final String cmd_auth              = "a";
        /*
        * команда блокировки
        */
	public static final String cmd_block_user        = "bu";
        /*
        * команда создания пользователя
        */
	public static final String cmd_create_user       = "cu";
        /*
        * команда вызова формы изменения-добавления пользователя
        */
	public static final String cmd_edit_user         = "eu";
	//public static final String cmd_add_user          = "au";
        /*
        * команда модификации пользователя
        */
	public static final String cmd_set_user          = "su";
        /*
        * команда удаления пользователя
        */
	public static final String cmd_del_user          = "du";
        //------------------------------------------------------
	public static final String request_block         = "b";

	public static final String request_cmd           = "c";

	public static final String request_msg           = "msg";

	public static final String request_group_id      = "gid";
	public static final String request_group_name    = "gn";
	public static final String request_groups        = "g";
	public static final String request_groups_r      = "gr";
	public static final String request_groups_w      = "gw";
	public static final String request_login         = "l";
	//public static final String request_name          = "n";
	public static final String request_page          = "w";
	public static final String request_passwd        = "p";
	public static final String request_userid        = "uid";

	public static final String request_img_h        = "h";
	public static final String request_img_w        = "w";
	public static final String request_img_file     = "f";

	
	public static final String request_org_id        = "oid";
	public static final String request_org_parent_id = "opid";
	public static final String request_org_lastname  = "oln";
	public static final String request_org_name      = "onn";
	public static final String request_org_secondname= "osn";
	public static final String request_org_job       = "ojb";
	public static final String request_org_type      = "otp";
	public static final String request_org_addr1     = "oa1";
	public static final String request_org_addr2     = "oa2";
	public static final String request_org_tel       = "otl";
	public static final String request_org_email     = "oel";

	public static final int    value_org_type_men    = 1;
	public static final int    value_org_type_fem    = 2;
	public static final int    value_org_type_dep    = 3;

	public static final String session_org_user      = "prj0_org_user";
        public static final String session_error         = "prj0_error";

        public static final String cmd_sendmail          = "sendmail";
        public static final String cmd_sendmail_ok       = "sendmail_ok";
	public static final String request_mail_subj     = "mail_subj";
	public static final String request_mail_msg      = "mail_msg";
	public static final String request_mail_to       = "mail_to";
	public static final String scheduller_run        = "web scheduller is run";
                    
// access?c=ub&b=1&l=login&w=/users.jsp  access?c=ub&b=0&l=login&w=/users.jsp
// access?c=ab                                                                                     новый рользователь
// access?c=x                                                                                      выход
// access?c=a&l=login&p=passwd                                                                     аутентификация
// access?c=su&uid=uid&l=login&p=passwd&n=name&g=proups&b=0&gr=request_groups_r&gw=request_groups_w&w=/users.jsp
// access?c=au&l=login&p=passwd&n=name&g=proups&b=0&gr=request_groups_r&gw=request_groups_w&w=/users.jsp
// users.jsp?c=au                                                                                  пустой пользователь  
// 
};
