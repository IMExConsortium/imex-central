package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * AcomQueryAction - autocomplete queries
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;
import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class AdminAction extends ManagerSupport {

    private final String JSON = "json";

    ////------------------------------------------------------------------------
    /// Entry Manager
    //--------------

    private EntryManager entryManager;
    
    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }
    
    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    //--------------------------------------------------------------------------

    List auto = null;

    public List getResult(){

        if( auto == null ){
            auto = new ArrayList();
        }
        return auto;
    }
    
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  " op=" + getOp() ); 
        
        if( getOp() == null ) return JSON;
        
      
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "sync" ) ) {
                    String pmid = val;
                    return adminSync( pmid );
                }
            }
        }
        return JSON;
    }
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // operations
    //-----------

    public String adminSync( String pmid ) {
        
        if( pmid != null ){
            IcPub pub = entryManager.getIcPubByPmid( pmid );

            if( pub != null ){
                String ppmid = pub.getPmid();
                if( ppmid != null && ppmid.equals( pmid ) ){ 
                    try{
                        entryManager.resyncIcPubPubmed( pub, pub.getOwner(), pub );
                        getResult().add("OK");
                    }catch( Exception ex ){
                        getResult().add("Exception");
                    }
                }                                              
            }            
        }
        return JSON;
    }
}
