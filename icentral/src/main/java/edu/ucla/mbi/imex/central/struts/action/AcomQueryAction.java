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

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class AcomQueryAction extends ManagerSupport {

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

    public List getAcom(){

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
                
                if ( key.equalsIgnoreCase( "ownac" ) ) {
                    String query = null ;
                    if ( getOpp() != null ) {
                        query = getOpp().get( "q" );
                    }
                    return acomOwner( query );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "curac" ) ) {
                    String query = null ;
                    if ( getOpp() != null ) {
                        query = getOpp().get( "q" );
                    }
                    return acomCurator( query );
                }
            }
        }
        return JSON;
    }
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // operations
    //-----------

    public String acomOwner( String q ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " q=" + q );
        
        List<User> ownerList = entryManager.acomOwner( q );
        
        if( ownerList != null && ownerList.size() > 0 ){
            
            for( Iterator<User> ii = ownerList.iterator(); ii.hasNext(); ){
                User cu = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cu.getLogin());
                getAcom().add( cr );
            }
        }
        return JSON;

    }
    
    //---------------------------------------------------------------------

    public String acomCurator( String q ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " q=" + q );
        
        List<User> curatorList = entryManager.acomCurator( q );
        
        if( curatorList != null && curatorList.size() > 0 ){
            
            for( Iterator<User> ii = curatorList.iterator(); ii.hasNext(); ){
                User cu = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cu.getLogin());
                getAcom().add( cr );
            }
        }
        return JSON;
    }
}
