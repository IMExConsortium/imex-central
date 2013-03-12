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

    public void setUserPrefManager( UserPrefManager manager ) {
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
        User user = new User();
        UserDao userDao = getUserContext().getUserDao();
        if(getId() > 0)
        {
            try {
                user = userDao.getUser(getId());
                log.debug(this.preferences = user.getPrefs());
            }catch( Exception ex ) {
                log.debug(ex);
            }
        }
        log.debug( "before if length = : " + this.preferences.length());
        if(this.preferences == null || this.preferences.length() <= 0)
        {
            
            log.debug( "No prefs found, updating with Defaults" );
            this.preferences = getUserPrefManager().getDefUserPrefs();
            user.setPrefs(this.preferences);
            userDao.updateUser(user);
        }

        if( getOp() == null ) return SUCCESS;
        //IcPub icpub = null;

        //if(  getId() > 0 ) {           
        //    icpub = entryManager.getIcPub( getId() );
        //}
        
        //if( icpub == null ) return JSON;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);

            if ( val != null && val.length() > 0 ) {
                
                if ( key.equalsIgnoreCase( "opcode1" ) ) {
                    return execOp1();
                }

                if ( key.equalsIgnoreCase( "opcode2" ) ) {
                    return execOp2();
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

    
    private String execOp1(){
        
        //watchManager.doSomething();

        return JSON;
    }

    //--------------------------------------------------------------------------

    private String execOp2(){

        //watchManager.doSomethingElse();

        return JSON;
    }

    //--------------------------------------------------------------------------

    private String execOp3(String param){

        //watchManager.doSomethingElse( param );
        return JSON;  // ACL_PAGE/ACL_ERROR
    }

}
