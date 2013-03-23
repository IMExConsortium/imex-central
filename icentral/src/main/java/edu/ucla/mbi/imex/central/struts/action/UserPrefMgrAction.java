package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * UserPrefMgrAction action
 *                
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import org.json.*;

import java.io.*;
import java.util.*;


import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class UserPrefMgrAction extends ManagerSupport {
    
    private static final String JSON = "json";    
    private static final String REDIRECT = "redirect";    
    private static final String ACL_PAGE = "acl_page";
    private static final String ACL_OPER = "acl_oper";
    
    ////------------------------------------------------------------------------
    /// Watch Manager
    //---------------
    
    private UserPrefManager uprefManager;

    public void setUserPrefManager( UserPrefManager manager ){
        this.uprefManager = manager;
    }

    public UserPrefManager getUserPrefManager() {
        return this.uprefManager;
    }
    
    //--------------------------------------------------------------------------
    // format
    //-------

    private String format = null;

    public void setFormat( String format ) {
        this.format = format;
    }

    public String getFormat(){
        return this.format;
    }
    
    //--------------------------------------------------------------------------
    // results
    //--------
    
    String preferences;
    
    public String getPreferences(){
        return this.preferences;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public String execute() throws Exception {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "|id=" + getId() + " op=" + getOp() );

        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null) {
            luser = getUserContext().getUserDao().getUser( iusr.intValue() );
            log.debug( " user set to: " + luser );
        } else  {
            return SUCCESS;
        }
        
        if( getOp() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);

            if ( val != null && val.length() > 0 ) {
                
                if ( key.equalsIgnoreCase( "view" ) ) {
                    return execView( luser );
                }

                if ( key.equalsIgnoreCase( "update" ) ) {
                    return execUpdate( luser );
                }
                

                if ( key.equalsIgnoreCase( "defset" ) ) {
                    return execDefset( luser );
                }
                
                if ( key.equalsIgnoreCase( "opcode3" ) ) {
                    
                    // get comment by id
                    //------------------
                    
                    if ( getOpp() == null ) return JSON;
                    String prop1 = getOpp().get( "prop1" );
                    return execOp3( prop1 );
                }                            
            }
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    
    private String execView( User user ){
        
        Log log = LogFactory.getLog( this.getClass() );

        //log.debug( "|id=" + getId() + " op=" + getOp() );
        
        //User user = new User();
        //UserDao userDao = getUserContext().getUserDao();
        //if( getId() > 0 ){
        //    try {
        //        user = userDao.getUser(getId());
          
        this.preferences = user.getPrefs();
        
        //    }catch( Exception ex ) {
        //          log.debug(ex);
        //    }
        //}
        
        log.debug( "before if length = : " + this.preferences.length() );
        
        if( this.preferences == null || this.preferences.length() <= 0){
            log.debug( "No prefs found, updating with Defaults" );
            this.preferences = getUserPrefManager().getDefUserPrefs();
            user.setPrefs(this.preferences);
            getUserContext().getUserDao().updateUser( user );
        }

        return JSON;
    }

    //--------------------------------------------------------------------------

    private String execUpdate( User user ){

        Log log = LogFactory.getLog( this.getClass() );
        
        log.debug( "|update called" );
        log.debug( " opp=" + getOpp() );

        String upref = user.getPrefs();

        try{
            JSONObject jUpref = new JSONObject( upref );

            process( jUpref, getOpp() );
            String nUpref = jUpref.toString(); 
            
            user.setPrefs( nUpref );
            getUserContext().getUserDao().updateUser( user );
            
        } catch( JSONException jex ){
        }
        return JSON;
    }

    //--------------------------------------------------------------------------
    
    private void process( JSONObject jo, Map<String,String> opp ){
        
        Log log = LogFactory.getLog( this.getClass() );
        try{       
            try{ 
                String nval = opp.get( jo.getString( "opp" ) );
                if( nval != null ){
                    jo.put( "value", nval );
                    log.debug( "opp=" + jo.getString( "opp" ) 
                               + " value=" + nval );
                }
            } catch( JSONException jex ){
                log.debug("no value" );
            }

            try{ 
                JSONObject jod = jo.getJSONObject( "option-def" );
                for( Iterator i = jod.keys(); i.hasNext(); ){
                    
                    String k = (String) i.next();
                    log.debug("|START key=" + k );
                    
                    try{
                        process( jod.getJSONObject( k ), opp );
                    } catch( JSONException jex ){
                        log.debug("key jex=" + jex );
                    }
                    log.debug("|DONE key=" + k );
                }
            } catch( JSONException jex ){
                log.debug( "| no option-def" );
            }
        } catch( Exception ex ){
            ex.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------

    private String execDefset( User user ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " execDefset called" );
        
        this.preferences = getUserPrefManager().getDefUserPrefs();
        
        user.setPrefs( this.preferences );
        getUserContext().getUserDao().updateUser( user );
        return JSON;
    }

    //--------------------------------------------------------------------------

    private String execOp3(String param){

        //watchManager.doSomethingElse( param );
        return JSON;  // ACL_PAGE/ACL_ERROR
    }
}
