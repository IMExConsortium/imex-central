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

import edu.ucla.mbi.util.context.WorkflowContext;

import edu.ucla.mbi.imex.central.*;

public class AcomQueryAction extends ManagerSupport {

    private final String JSON = "json";
    
    private IcWorkflowContext wfx;


    ////------------------------------------------------------------------------
    /// Workflow Context
    //------------------

    public void setWorkflowContext( IcWorkflowContext context ){
	wfx = context;
    }


    ////------------------------------------------------------------------------
    /// Entry Manager
    //---------------

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

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "pstac" ) ) {
                    String query = null ;
                    if ( getOpp() != null ) {
                        query = getOpp().get( "stage" );
                    }
                    return acomStatus( query );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "psgac" ) ) {
                    String status = null ;
                    if ( getOpp() != null ) {
                        status = getOpp().get( "status" );
                    }
                    return acomStage( status );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "pagac" ) ) {
                    String query = null ;
                    if ( getOpp() != null ) {
                        query = getOpp().get( "q" );
                    } else {
                        query = "role:partner";
                    }
                    return acomGroup( query );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jagac" ) ) {
                    String query = null ;
                    if ( getOpp() != null ) {
                        query = getOpp().get( "q" );
                    } else {
                        query = "role:partner";
                    }
                    return acomJGroup( query );
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
            
            for( Iterator<User> ii = ownerList.iterator(); 
		 ii.hasNext(); ){
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
            for( Iterator<User> ii = curatorList.iterator(); 
		 ii.hasNext(); ){
                User cu = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cu.getLogin());
                getAcom().add( cr );
            }
        }
        return JSON;
    }

    //---------------------------------------------------------------------

    public String acomStatus( String stage ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " stage=" + stage );
        	
	List<String> stateList = wfx.getStatusList( stage );
	
	if( stateList != null && stateList.size() > 0 ){
	    for( Iterator<String> ii
                     = stateList.iterator(); ii.hasNext(); ){
                String state = ii.next();
		Map <String,String> cr = new HashMap<String,String>();
                cr.put( "name",state );
                getAcom().add( cr );                                                      
            }                                                        
	}
	
	log.debug( " acom=" + getAcom() );
        return JSON;
    }

    //---------------------------------------------------------------------

    public String acomStage( String status ) {

        Log log = LogFactory.getLog( this.getClass() );
	List<String> stageList = wfx.getStageList( status );	
	log.info("acomStage: status="+status);
        if( stageList != null && stageList.size() > 0 ){
            
            for( Iterator<String> ii 
		     = stageList.iterator(); ii.hasNext(); ){
                String cs = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cs);
                getAcom().add( cr );
            }
        }
        return JSON;
    }

    //---------------------------------------------------------------------

    public String acomGroup( String q ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " q=" + q );
        
        List<Group> groupList = entryManager.acomGroup( q );
        
        if( groupList != null && groupList.size() > 0 ){
            
            for( Iterator<Group> ii = groupList.iterator(); 
		 ii.hasNext(); ){
                Group cg = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cg.getLabel());
                getAcom().add( cr );
            }
        }
        return JSON;
    }

    public String acomJGroup( String q ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " q=" + q );
        
        List<Group> groupList = entryManager.acomJGroup( q );
        
        if( groupList != null && groupList.size() > 0 ){
	    for( Iterator<Group> ii = groupList.iterator(); 
		 ii.hasNext(); ){
                Group cg = ii.next();
                Map <String,String> cr = new HashMap<String,String>();
                cr.put("name",cg.getLabel());
                getAcom().add( cr );
            }
        }
        return JSON;
    }

}
