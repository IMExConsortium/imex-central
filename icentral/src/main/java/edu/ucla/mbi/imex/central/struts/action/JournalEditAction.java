package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * JournalEditAction - web interface to journal record editing page
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class JournalEditAction extends ManagerSupport {

    private final String JSON = "json";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String IMEX_ROLE = "IMEx partner";
    
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
    //  TracContext
    //--------------

    private TracContext tracContext;
    
    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }
    
    public TracContext getTracContext() {
        return this.tracContext;
    }

    //--------------------------------------------------------------------------
    //  WorkflowContext
    //-----------------
    
    private WorkflowContext wflowContext;

    public void setWorkflowContext( WorkflowContext context ) {
        this.wflowContext = context;
    }

    public WorkflowContext getWorkflowContext() {
        return this.wflowContext;
    }


    //--------------------------------------------------------------------------
    // status
    //------

    private int statCode = 0;
    private String statMessage = "OK";

    public int getStatusCode(){
        return this.statCode;
    }

    public String getStatusMessage(){
        return this.statMessage;
    }


    //--------------------------------------------------------------------------
    // GroupAll list
    //--------------

    public List<Group> getGroupAll(){
        
        if ( getUserContext().getGroupDao() != null ) {
            return getUserContext().getGroupDao().getGroupList();
        }
        return null;
    }

    //--------------------------------------------------------------------------
    // GroupImex list
    //---------------

    public List<Group> getGroupImex(){
        
        if ( getUserContext().getGroupDao() != null &&
             getUserContext().getRoleDao() != null ){

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "IMEX_ROLE="  + IMEX_ROLE );
            
            Role role = getUserContext().getRoleDao().getRole( IMEX_ROLE );

            log.info( "role="  + role );
            
            List<Group> rlist 
                = getUserContext().getGroupDao().getGroupList(role);
            
            

            log.info( "rlist="  + rlist );

            return rlist;

        }
        return null;
    }
        
    //--------------------------------------------------------------------------
    //  IcJournal
    //-----------
    
    private IcJournal journal = null;

    public void setJournal( IcJournal journal ) {
	this.journal = journal;
    }
    
    public IcJournal getJournal(){
	return this.journal;
    }
    
    //--------------------------------------------------------------------------
    // Records
    //--------

    private Map<String,Object> records = null;

    public void setRecords( Map<String,Object> records ) {
        this.records = records;
    }
    
    public Map<String,Object> getRecords(){
        return this.records;
    }

    // Init
    //-----

    private Map<String,Object> init = null;
    
    public Map  getInit(){

        if( init == null ){
            init = new HashMap<String,Object>();
        }
        return init;
    }

    //--------------------------------------------------------------------------
    
    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
       
        
        log.info( "id=" + getId() + " op=" + getOp() ); 
        
        if ( tracContext.getJournalDao() == null ) return SUCCESS;
        
        if ( getId() > 0 ){
            log.debug( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );
        } else {
            return SUCCESS;
        }
        
        if( getOp() == null ||  getOpp() == null ){
            return SUCCESS;
        }
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get( key );
            
            log.info(  "op=" + key + "  val=" + val );
            
            //------------------------------------------------------------------
            //------------------------------------------------------------------
            // journal operations
            //-------------------
                
            if( key.equalsIgnoreCase( "jpup" ) ){
                
                try{
                    
                    IcJournal nj = new IcJournal();

                    if( getOpp().get( "title" ) != null ){
                        nj.setTitle( getOpp().get( "title" ) );
                    }
                    if( getOpp().get( "nlmid" ) != null ){
                        nj.setNlmid( getOpp().get( "nlmid" ) );
                    }
                    if( getOpp().get( "issn") != null ){
                        nj.setIssn( getOpp().get( "issn" ) );
                    }
                    if( getOpp().get( "wurl") != null ){
                        nj.setWebsiteUrl( getOpp().get( "wurl" ) );
                    }
                    return updateJournalProperties( getId(), nj );
                }catch( NumberFormatException nfe ){
                    // abort on error
                    nfe.printStackTrace();
                }
                return SUCCESS;
            }
                
            //------------------------------------------------------------------
            
            if( key.equalsIgnoreCase( "jauadd" ) ){
                
                String ulogin = getOpp().get( "jauadd" );
                try{
                    return addJournalAdminUser( getId(), ulogin );                        
                }catch( NumberFormatException nfe ){
                    // abort on error
                    nfe.printStackTrace();
                }
                return SUCCESS;
            }
            
            //------------------------------------------------------------------
            
            if( key.equalsIgnoreCase( "jagadd" ) ){
                
                String sgid = getOpp().get( "jagadd" );
                try{
                    int gid = Integer.parseInt( sgid );
                    return addJournalAdminGroup( getId(), gid );
                }catch( NumberFormatException nfe ){
                        // abort on error
                }
                return SUCCESS;
            }

            //------------------------------------------------------------------

            if( key.equalsIgnoreCase( "jaudel" ) ){
                    
                String udel = getOpp().get( "jaudel" );
                if( udel == null ){ return SUCCESS; }
                
                try{
                    List<Integer> uidl = new ArrayList<Integer>();
                    String[] us = udel.split(",");
                    
                    for( int ii = 0; ii <us.length; ii++ ) {
                        if( us[ii].length() >0 ){
                            uidl.add( Integer.valueOf( us[ii] ) );
                        }
                    }
                    return delJournalAdminUsers( getId(), uidl );
                }catch( Exception ex ){
                        // should not happen
                }   
                    
                return SUCCESS;
            }

            //------------------------------------------------------------------

            if( key.equalsIgnoreCase( "jagdel" ) ){
                    
                String gdel = getOpp().get( "jagdel" );
                if( gdel == null ){ return SUCCESS; }
                
                try{
                    List<Integer> gidl = new ArrayList<Integer>();
                    String[] gs = gdel.split(",");
                    
                    for( int ii = 0; ii <gs.length; ii++ ) {
                        if( gs[ii].length() >0 ){
                            gidl.add( Integer.valueOf( gs[ii] ) );
                        }
                    }
                    return delJournalAdminGroups( getId(), gidl );
                }catch( Exception ex ){
                    // should not happen
                }     
                return SUCCESS;
            }
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // validation
    //-----------

    public void validate() {
    
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "validate" );
        
        //boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for( Iterator<String> i = getOp().keySet().iterator(); 
                 i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);
                
                if( val != null && val.length() > 0 ){
                    
                    log.debug( " op=" + val);
                    
                    //----------------------------------------------------------
                    
                    if( key.equalsIgnoreCase( "jsrc" ) ){
                        if( getJournal() == null 
                            || getJournal().getNlmid() == null ) {
                            addFieldError( "journal.nlmid",
                                           "NLMID field cannot be empty." );
                        }else{
                            String nlmid = getJournal().getNlmid();
                            try {
                                nlmid = nlmid.replaceAll( "\\s", "" );
                            }catch( Exception ex ){
                                // should not happen
                            }
                            if( nlmid.length() == 0  ){
                                addFieldError( "journal.nlmid",
                                               "NLMID field cannot be empty." );
                            }
                        }
                        
                        break;
                    }
                }
            }
        }
        
        //if ( loadUserFlag && getId() > 0 ) {
        //    user = getUserContext().getUserDao().getUser( getId() );
        //    setBig( false );
        //}
        
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // operations
    //-----------
    
    public String updateJournalProperties( int id, Journal jrnl ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "update journal: id=" + id );
        
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal == null || journal == null ) return SUCCESS;
        
        entryManager.updateIcJournal( oldJournal, jrnl );
        
        journal = entryManager.getIcJournal( id );
        setId( journal.getId() );
        
        return JSON;
    }

    //---------------------------------------------------------------------

    public String addJournalAdminGroup( int id, int grp ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add JAG: id=" + id + " ag= " + grp );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        Group agrp = getUserContext().getGroupDao().getGroup( grp );

        if ( oldJournal != null && agrp != null ) {
            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                entryManager.addAdminGroup( oldJournal, agrp );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JSON;
            }
            return ACL_OPER;
        }
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String delJournalAdminGroups( int id, List<Integer> gidl ){
    
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop JAG: id=" + id + " aglist= " + gidl );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if( oldJournal != null && gidl != null ){
            if( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                    adminGroupMatch ) ){

                entryManager.delAdminGroups( oldJournal, gidl );

                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JSON;
            }
            return ACL_OPER;
        }
        return SUCCESS;
    }
    
    //---------------------------------------------------------------------
    
    public String addJournalAdminUser( int id,  String ulogin ){
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add JAG: id=" + id + " au= " + ulogin );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        User ausr = getUserContext().getUserDao().getUser( ulogin );
        
        if( oldJournal != null && ausr != null ){

            if( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                    adminGroupMatch ) ){
                
                entryManager.addAdminUser( oldJournal, ausr );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JSON;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }
    
    //---------------------------------------------------------------------
    
    public String delJournalAdminUsers( int id, List<Integer> uidl ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop EAG: id=" + id + " aglist= " + uidl );

        IcJournal oldJournal = entryManager.getIcJournal( id );
        if( oldJournal != null && uidl != null ){

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ){
                
                log.debug( "ACL test passed");
                
                entryManager.delAdminUsers( oldJournal, uidl );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JSON;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }
}
