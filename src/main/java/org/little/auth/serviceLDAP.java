package org.little.auth;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * @author av
 *  
 */
public class serviceLDAP {

       private String                                    searchBase;
       private DirContext                                ctx;
       private NamingEnumeration<SearchResult>           answer;
       private SearchResult                              searchR;
       private NamingEnumeration<? extends Attribute>    n_attrs;
       private static final Logger log = LoggerFactory.getLogger(serviceLDAP.class);

        private void _clear(){
             ctx          = null;
        }

        private void clear(){
             searchBase   = null;
             answer       = null;
             searchR      = null;
             n_attrs      = null;
        }
        protected  serviceLDAP(){
            clear();
            _clear();

        }
        protected  boolean isConnect(){
                     return ctx!=null;
        }
        protected  boolean isAnswer(){
                     return ctx!=null && answer != null;
        }
        protected  boolean  connect(String    ldapURL,String _searchBase,String userName,String userPassword){
               searchBase   = _searchBase;
               Hashtable<String, String> env = new Hashtable<String, String>();

                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.SECURITY_AUTHENTICATION, "simple");
                env.put(Context.SECURITY_PRINCIPAL     , userName);
                env.put(Context.SECURITY_CREDENTIALS   , userPassword);
                env.put(Context.PROVIDER_URL           , ldapURL);

                try {
                     ctx        = new InitialLdapContext(env, null);
                }
                catch (NamingException ex3) {
                       log.error("error init connection LDAP (userName:"+userName+" userPassword:"+userPassword+" ldapURL:"+ldapURL+") ex:"+ex3);
                       _clear();
                       clear();
                       return false;
                }
                log.trace("init connection LDAP (userName:"+userName+" ldapURL:"+ldapURL+")");
                return true;
        }

        protected  void  close(){
               if(ctx!=null) try{ ctx.close(); } catch (NamingException ex){}
               _clear();
               clear();
        }
        protected  boolean searchUserName(String search_base,String search_name){
               //String searchFilter="(&(objectClass=util)(|(userPrincipalName="+search_name+")(name="+search_name+")(displayName="+search_name+")))";
               String searchFilter="(|(userPrincipalName="+search_name+")(name="+search_name+")(displayName="+search_name+"))";
               if(ctx==null){
                  log.error("connect LDAP is null (InitialLdapContext is null)");
                  clear();
                  return false;
               }
               try{
                  SearchControls searchCtls = new SearchControls();
                  searchCtls.setReturningAttributes(null);
                  searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                  String[] attr=new String[] { "userPrincipalName", "name", "displayName"};
                  searchCtls.setReturningAttributes(attr);
                  answer = ctx.search(search_base, searchFilter, searchCtls);
               }        
               catch (NamingException ex) {
                      log.error("error searching directory"+" ex:"+ex);
                      clear();
                      return false;
               }
               log.trace("Sarching LDAP (searchBase:"+search_base+" searchFilter:"+searchFilter+") is:"+((answer!=null)?"true":"false"));
               boolean ret=false;
               if(answer!=null){
                  while(ret==false){
                        if(answer.hasMoreElements()==false)break;
                                  //log.trace("-----------------------------------------------1");
                        try{searchR=(SearchResult)answer.next();}catch(Exception ex){break;}
                                  //log.trace("-----------------------------------------------2");
                        if(searchR==null)break;
                                  //log.trace("-----------------------------------------------3");
                        Attributes  attrs = searchR.getAttributes();
                        if(attrs==null)break;
                                  //log.trace("-----------------------------------------------4");
                        n_attrs = attrs.getAll();
                        if(n_attrs==null)break;
                                  //log.trace("-----------------------------------------------5");
                        while(n_attrs.hasMoreElements()){
                              try{
                                  Attribute attr=null;
                                  attr=(Attribute)n_attrs.next();
                                  //log.trace("-----------------------------------------------51");
                                  //String id=attr.getID();
                                  String vol=attr.get().toString();
                                  //log.trace("-----------------------------------------------id:"+id+" vol:"+vol+" name:"+search_name);
                                  if(vol.equals(search_name)){
                                     //log.trace("-----------------------------------------------vol:"+vol+" name:"+search_name);
                                     ret=true;
                                     break;
                                  }
                              }
                              catch(Exception ex){break;}
                        }
                        searchR =null;
                  }

              }
              log.trace("Compare LDAP for:"+search_name+" is:"+ret);
              clear();
              return ret;
        }
        protected  void search(String searchBase,String searchFilter){
              if(searchFilter==null || searchBase==null){
                  log.error("search LDAP is null (searchBase:"+searchBase+" searchFilter:"+searchFilter+")");
                  clear();
                  return ;
               }
               if(ctx==null){
                  log.error("connect LDAP is null (InitialLdapContext is null)");
                  clear();
                  return ;
               }
               try{
                  SearchControls searchCtls = new SearchControls();
                  searchCtls.setReturningAttributes(null);
                  searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                  String[] attr=new String[] { "userPrincipalName", "name", "description","displayName","userPassword"};
                  searchCtls.setReturningAttributes(attr);
                  answer = ctx.search(searchBase, searchFilter, searchCtls);
               }        
               catch (NamingException ex) {
                      log.error("error searching directory"+" ex:"+ex);
                      clear();
                      return;
               }
               log.trace("Sarching LDAP (searchBase:"+searchBase+" searchFilter:"+searchFilter+")");

        }
        protected  void searchName(String id){
                String searchFilter = "(&(objectClass=util)(|(displayName="+id+")(name="+id+")))";
                search(searchBase,searchFilter);

        }
        protected  void searchName(String id,String passwd){
                String searchFilter = "(&(objectClass=util)(|(displayName="+id+")(name="+id+")))";
                search(searchBase,searchFilter);
        }
        private void getSearch(){
                if(answer==null){
                   clear();
                   return;
                }
                try {                                
                     if(searchR==null){
                        n_attrs=null;
                        while(true){
                              if(answer.hasMoreElements()==false)return;
                              searchR = (SearchResult) answer.next();
                              if(searchR != null){
                                 System.out.println("----------------------------------------");
                                 Attributes  attrs = searchR.getAttributes();
                                 if(attrs!=null){
                                    n_attrs = attrs.getAll();
                                    if(n_attrs != null){
                                       return;
                                    }
                                 }
                              }
                        }
                        //return;
                     }
                     else{
                        return;
                     }
                }        
                catch (NamingException ex) {
                       log.error("error get searching result ex:"+ex);
                       clear();
                       return;
                }
        }  


        protected  String [] get(){

                if(ctx==null||answer==null){
                   log.error("search LDAP is null ");
                   return null;
                }

                while(true){
                     getSearch();
                     if(n_attrs==null)return null;
                     if(n_attrs.hasMoreElements())break;
                     searchR =null;
                }

                try {
                     String [] ret=new String [2];
                     Attribute attr = (Attribute)n_attrs.next();
                     ret[0]=attr.getID();
                     ret[1]=attr.get().toString();
                     log.trace("get data LDAP (ID:"+ret[0]+" data:"+ret[1]+")");
                     return ret;

                }        
                catch (NamingException ex) {
                       log.error("error searching LDAP ex:"+ex);
                       clear();
                       return null;

                }
                catch (NoSuchElementException ex1) {
                       log.error(" "+ex1);
                       String [] ret=new String [2];ret[0]=" ";ret[1]=" ";
                       //clear();
                       return ret;
                }
                catch (Exception ex2) {
                       log.error("error searching LDAP ex:"+ex2);
                       clear();
                       return null;

                }

        }
        protected  static String getFullDomain(String domain){
               if(domain.contains("VIP"))return "vip.cbr.ru";
               if(domain.contains("REGION"))return "region.cbr.ru";
               if(domain.contains("KR-NNOV"))return "kr-nnov.cbr.ru";
               if(domain.contains("KR-SPB"))return "kr-spb.cbr.ru";
               if(domain.contains("PORTAL"))return "portal.cbr.ru";
               if(domain.contains("vip"))return "vip.cbr.ru";
               if(domain.contains("region"))return "region.cbr.ru";
               if(domain.contains("kr-nnov"))return "kr-nnov.cbr.ru";
               if(domain.contains("kr-spb"))return "kr-spb.cbr.ru";
               if(domain.contains("portal"))return "portal.cbr.ru";
               return "cbr.ru";
        }
        protected  static String getFullName(String domainUsername, String defaultdomainName){
               //String username=domainUsername;
               if(domainUsername.contains("\\")){
                  //username = domainUsername;
                  String []domainUserPair = domainUsername.split("\\\\");
                  if(domainUserPair.length >= 2){
                     return domainUserPair[1]+"@"+getFullDomain(domainUserPair[0]);
                  }
               }

               if(!domainUsername.contains("@")){
                  return domainUsername+"@"+defaultdomainName;
               }
               //--------------------------------------------------------------------------------------
               return domainUsername;
        }
        protected  static boolean auth(String domainUsername, String password, String defaultdomainName,String ldapURL){
               if (password == null || password.trim().length() == 0) {
                   return false;
               }
               //--------------------------------------------------------------------------------------
               //String defaultLdapURL = ldapURL!=null ? ldapURL : "";
               //--------------------------------------------------------------------------------------

               String username=domainUsername;
               String domainName;
               if(defaultdomainName == null)domainName= "ru";
               else domainName=defaultdomainName;
               //--------------------------------------------------------------------------------------
               domainUsername=getFullName(domainUsername,domainName);
               String []domainUserPair = domainUsername.split("@");
               if(domainUserPair.length >= 2) domainName = domainUserPair[1];
               //--------------------------------------------------------------------------------------
               String domainString = "";
               for(String subDomainName : domainName.split("\\.")) {
                   if(domainString.length() > 0) {
                      domainString += ",";
                   }
                   domainString += "dc=" + subDomainName;
               }
               //--------------------------------------------------------------------------------------
               log.trace("LDAP (URL:"+ldapURL+" domain:"+domainString+" username:"+username);

               serviceLDAP ldap=new serviceLDAP();
               boolean ret=ldap.connect(ldapURL,domainString,username,password);
               log.trace("LDAP (URL:"+ldapURL+" domain:"+domainString+" username:"+username+" auth:"+ret);
               if(ret==true)ret=ldap.searchUserName(domainString,username);
               log.trace("LDAP (URL:"+ldapURL+" domain:"+domainString+" username:"+username+" auth:"+ret);
               ldap.close();
               return ret;
        }

        protected  static void getTEST_Id(String id){
                serviceLDAP ldap=new serviceLDAP();

                ldap.connect("ldap://rdc22-vip01.vip.cbr.ru:3268","dc=vip,dc=cbr,dc=ru","ShadrinAV@vip.cbr.ru","Andrey14@");

                ldap.searchName(id);
                while(true){
                      String [] ret=ldap.get();
                      if(ret==null)break;
                      System.out.println(ret[0]+":"+ret[1]);
                      //if(ret[0].equals("name"))             System.out.println(" name:"+ ret[1]);
                      //else
                      //if(ret[0].equals("userPrincipalName"))System.out.println(" login:"+ ret[1]);
                }
                System.out.println(" ");
                //System.out.println("----------------------------------");
                ldap.close();

                return ;
        }
        /**
         * 
         *  
         */
        protected  static void main(String[] args) throws Exception {
               boolean ret;
               ret= serviceLDAP.auth("ShadrinAV@vip.cbr.ru","Andrey14@","vip.cbr.ru","ldap://rdc22-vip01.vip.cbr.ru:3268");
               System.out.println("auth:"+ret);
               ret= serviceLDAP.auth("ShadrinAV","Andrey14@","vip.cbr.ru","ldap://rdc22-vip01.vip.cbr.ru:3268");
               System.out.println("auth:"+ret);
               ret= serviceLDAP.auth("VIP\\ShadrinAV","Andrey14@","vip.cbr.ru","ldap://rdc22-vip01.vip.cbr.ru:3268");
               System.out.println("auth:"+ret);
               ret= serviceLDAP.auth("\\ShadrinAV","Andrey14@","vip.cbr.ru","ldap://rdc22-vip01.vip.cbr.ru:3268");
               System.out.println("auth:"+ret);
               ret= serviceLDAP.auth("ShadrinAV@","Andrey14@","vip.cbr.ru","ldap://rdc22-vip01.vip.cbr.ru:3268");
               System.out.println("auth:"+ret);
                //LDAP ldap=new LDAP();
                //boolean ret=ldap.connect("ldap://rdc22-vip01.vip.cbr.ru:3268","dc=vip,dc=cbr,dc=ru","ShadrinAV@vip.cbr.ru","Andrey14@-");
                //boolean ret=ldap.connect("ldap://rdc22-vip01.vip.cbr.ru:3268","dc=vip,dc=cbr,dc=ru","ShadrinAV","Andrey14@");
                //ldap.close();
                //getTEST_Id("������*");

        }


}

/*

title: {md5}8uB+CiwuUH/HZEpHkZYpWg==
userPassword:: e21kNX1lYVFmWkx2Y05US1laTGZvQlZ4Si9BPT0=
dn: uid=044525248_00,ou=users,dc=kbr,dc=cbr,dc=ru
uid: 044525248_00
userPassword:: e21kNX1IeWQ4aThVQzBxMy9jZ05YcGlPTUdRPT0=
objectClass: top
objectClass: inetOrgPerson
objectClass: person
objectClass: organizationalPerson
title: {md5}M8rlqvZ9QmpijSb3iBH5Vg==

*/