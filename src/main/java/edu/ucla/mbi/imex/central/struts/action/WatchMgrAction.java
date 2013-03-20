package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * WatchMgrAction action
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

public class WatchMgrAction extends ManagerSupport {
    
    private static final String JSON = "json";    
    private static final String REDIRECT = "redirect";    
    private static final String ACL_PAGE = "acl_page";
    private static final String ACL_OPER = "acl_oper";
    
    ////------------------------------------------------------------------------
    /// Watch Manager
    //---------------
    
    private WatchManager watchManager;

    public void setWatchManager( WatchManager manager ) {
        this.watchManager = manager;
    }

    public WatchManager getWatchManager() {
        return this.watchManager;
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
    
    //--------------------------------------------------------------------------

    Map<String,Object> flags = null;

    public Map<String,Object> getFlags(){
        return this.flags;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public String execute() throws Exception {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " op=" + getOp() );
        
        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null) {
            luser = getUserContext().getUserDao().getUser( iusr.intValue() );
            log.debug( " user set to: " + luser );
        }
        
        if( getOp() == null ) return SUCCESS;

        IcPub icpub = null;

        if(  getId() > 0 ) {           
            icpub = (IcPub) getWatchManager().getTracContext().getPubDao()
                .getPublication( getId() );
        }
               
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);

            if ( val != null && val.length() > 0 ) {
                
                if ( key.equalsIgnoreCase( "wpg" ) ) {
                    return getWatchPage( getId(), getOpp() );
                }

                if ( key.equalsIgnoreCase( "wflu" ) ) {
                    if( icpub != null && getOpp() != null ){
                        String wfl = getOpp().get( "wfl" );
                        return updateWatchStatus( luser, icpub,  wfl );
                    }
                }
            }
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    
    private String getWatchPage( int id, Map opp ){
        
        //watchManager.getWatchPage( id );

        return JSON;
    }

    //--------------------------------------------------------------------------

    private String updateWatchStatus( User usr, Publication pub,  String wfl ){
        
        boolean watch 
            = (wfl!= null && wfl.equalsIgnoreCase("true") ) ? true : false;
        
        flags = new HashMap<String,Object>();
        
        flags.put("watch", watchManager.setWatchStatus( usr, pub, watch ) );
                  
        return JSON;
    }
}