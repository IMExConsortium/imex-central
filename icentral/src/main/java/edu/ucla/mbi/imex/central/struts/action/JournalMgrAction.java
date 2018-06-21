package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * JournalMgrAction - web interface to journal management
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;
import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class JournalMgrAction extends ManagerSupport {

    private final String NOJOU = "notfound";
    private final String JEDIT = "jedit";
    private final String JSON = "json";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";

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
    
    private IcWorkflowContext wflowContext;

    public void setWorkflowContext( IcWorkflowContext context ) {
        this.wflowContext = context;
    }

    public IcWorkflowContext getWorkflowContext() {
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

            Role role = getUserContext().getRoleDao().getRole( IMEX_ROLE );
            return ((IcGroupDao)getUserContext().getGroupDao()).getGroupList( role );
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
    
    public List<IcJournal> getJournalList(){
        
        if ( tracContext.getJournalDao() == null ) return null;
        
        List<Journal> jl = tracContext.getJournalDao().getJournalList();
        if ( jl == null ) return null;

        List<IcJournal> ijl = new ArrayList<IcJournal>();
        for ( Iterator<Journal> ii = jl.iterator(); ii.hasNext(); ) {
            IcJournal jj = (IcJournal) ii.next();
            ijl.add( jj );
        }
        return ijl;
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
    
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "id=" + getId() + " journal=" + journal + " op=" + getOp() ); 
        
        if ( tracContext.getJournalDao() == null ) return SUCCESS;
        
        if ( getId() > 0 && getOp() == null && journal == null ) {
            log.debug( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );  
            return SUCCESS;
        }
        
        if( getOp() == null ) return SUCCESS;
        
        
        journal = entryManager.getIcJournal( getId() );



        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {

                //--------------------------------------------------------------
                //--------------------------------------------------------------
                // journal operations
                //-------------------
                
                if ( key.equalsIgnoreCase( "jsrc" ) ) {
                    if ( getJournal() == null ) return SUCCESS;
                    String nlmid = getJournal().getNlmid();
                    return searchJournal( nlmid );
                }

                //--------------------------------------------------------------
    
                if ( key.equalsIgnoreCase( "jadd" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    String nlmid = getOpp().get( "jadd" );
                    return addJournal( nlmid );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jdel" ) ) {
                    return deleteJournal( journal );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jldel" ) ) {

                    if ( getOpp() == null ) return SUCCESS;
                    
                    String udel = getOpp().get( "del" );

                    if ( udel != null ) {
                        List<Integer> uidl =
                            new ArrayList<Integer>();
                        try {
                            udel = udel.replaceAll("\\s","");
                            String[] us = udel.split(",");

                            for( int ii = 0; ii <us.length; ii++ ) {
                                uidl.add( Integer.valueOf( us[ii] ) );
                            }
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                        return deleteJournalList( uidl );
                    }
                    return SUCCESS;
                }
                
                if ( key.equalsIgnoreCase( "jpg" ) ) {
                    if ( getOpp() == null ) {
                        return this.getIcJournalRecords();
                    }
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    

                    
                    Map<String,String> flt = new HashMap<String,String>();

                    if( getOpp().get( "pfv" ).length() > 0){
                        flt.put( "partner", getOpp().get( "pfv" ) );
                    }

                    if ( flt.size() == 0 ){

                        log.debug( "getPubRecords: unfiltered" );

                        return this.getIcJournalRecords( max, off, 
                                                         skey, sdir, flt );
                        
                    } else {
                        
                        log.debug( "getPubRecords: filtered" );

                        return this.getIcJournalRecords( max, off,
                                                         skey, sdir, flt );
                        
                    }                   
                }
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
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                    
                    log.debug( " op=" + val);
                    
                    //----------------------------------------------------------

                    if ( key.equalsIgnoreCase( "jsrc" ) ) {
                        if( getJournal() == null 
                            || getJournal().getNlmid() == null ) {
                            addFieldError( "journal.nlmid",
                                           "NLMID field cannot be empty." );
                        } else {
                            String nlmid = getJournal().getNlmid();
                            try {
                                nlmid = nlmid.replaceAll( "\\s", "" );
                            } catch( Exception ex ) {
                                // should not happen
                            }
                            if( nlmid.length() == 0  ) {
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
    // operations: Journal
    //----------------------
    
    public String searchJournal( String nlmid ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " search journal -> nlmid=" + nlmid );
        
        IcJournal oldJnrl =
            entryManager.getIcJournalByNlmid( nlmid );

        if ( oldJnrl != null ) {
            journal = oldJnrl;
            setId( oldJnrl.getId() );
            return JEDIT;
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------

    public String addJournal( String nlmid ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " new jrnl -> nlmid=" + nlmid );
        
        // test if already in 
        //-------------------
        
        IcJournal oldJnrl = 
            entryManager.getIcJournalByNlmid( nlmid );
        
        if ( oldJnrl != null ) {
            journal = oldJnrl;
            setId( oldJnrl.getId() );
            return JEDIT;
        }
        
        // ACL target control NOTE: implement if/as needed
        //------------------------------------------------                
        /* if ( ownerMatch != null && ownerMatch.size() > 0 ) { } */
        
        Integer usr = (Integer) getSession().get( "USER_ID" );        
        if ( usr == null ) return ACL_OPER;
        log.debug( " login id=" + usr );

        User owner = 
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null ) return ACL_OPER;
        log.debug( " owner set to: " + owner );
        
        try{
            IcJournal newJnrl = entryManager.addIcJournal( nlmid, owner );
            // cannot connect to proxy ?
        
            if ( newJnrl != null ) {
                journal = newJnrl;
                setId( newJnrl.getId() );
                return JEDIT;
            }

        } catch( ImexCentralException icx ){
            statCode = icx.getStatusCode();
            statMessage = icx.getStatusMessage();
        }
        
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    public String deleteJournal( Journal journal ) {
        /*
          NOTE: must check dependencies before removal 
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteJournalList( List<Integer> ournals ) {
        /*

        NOTE: must check dependencies before removal

        for ( Iterator<Integer> ii = states.iterator();
              ii.hasNext(); ) {
            
            int gid = ii.next();
            DataState s = wflowContext.getWorkflowDao()
                .getDataState( gid );
                                     
            log.debug( " delete state -> id=" + s.getId() );
            wflowContext.getWorkflowDao().deleteDataState( s );                
        }
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateJournalProperties( int id, Journal jrnl ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "update properties: journal id=" + id );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal == null || journal == null ) return SUCCESS;
        
        entryManager.updateIcJournal( oldJournal, jrnl );
        
        journal = entryManager.getIcJournal( id );
        setId( journal.getId() );
        log.debug( "pdate properties: " + JSON );
        return JSON;
    }

    //---------------------------------------------------------------------

    public String addJournalAdminGroup( int id, int grp ) {
        
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
                return JEDIT;
            }
            return ACL_OPER;
        }
        
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String delJournalAdminGroups( int id, List<Integer> gidl ) {
    
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop JAG: id=" + id + " aglist= " + gidl );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal != null && gidl != null ) {
            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {

                entryManager.delAdminGroups( oldJournal, gidl );

                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        
        setId( 0 );
        return SUCCESS;
    }
    
    //---------------------------------------------------------------------
    
    public String addJournalAdminUser( int id,  String ulogin ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add JAG: id=" + id + " au= " + ulogin );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        User ausr = getUserContext().getUserDao().getUser( ulogin );
        
        if ( oldJournal != null && ausr != null ) {

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                entryManager.addAdminUser( oldJournal, ausr );
            
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delJournalAdminUsers( int id, List<Integer> uidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop EAG: id=" + id + " aglist= " + uidl );

        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal != null && uidl != null ) {

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                log.debug( "ACL test passed");
                
                entryManager.delAdminUsers( oldJournal, uidl );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }
     

    //---------------------------------------------------------------------
    // record list operations
    //-----------------------

    /*
      {"recordsReturned":25, 
       "totalRecords":1397, 
       "startIndex":0, 
       "sort":null, 
       "dir":"asc", 
       "pageSize":10, 
       "records":[{"id":"0", "title":"...", "author":"...",
                   "imexid":"...","pmid":"...",
                   "owner":"","status:"","date":"..."},
                  {...}]
       }
    */
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    public String getIcJournalRecords() {
        return this.getIcJournalRecords( "", "", "", "", null );
    }
    
    public String getIcJournalRecords( String max, String off, 
                                       String skey, String sdir, 
                                       Map<String,String> flt ){
        
        if ( tracContext.getJournalDao() == null ) return null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "getIcJournalRecords"  );
        
        int first = 0;
        int blockSize = 10; // NOTE: initialize for defaults ?
        
        if ( off != null ) {
            try {
                first = Integer.parseInt( off );
            } catch ( NumberFormatException nex ) {
                // ignore == use default
            }
        }

        if ( max != null ) {
            try {
                blockSize = Integer.parseInt( max );
            } catch ( NumberFormatException nex ) {
                // ignore == use default
            }
        }

        boolean asc = true;
        if( sdir != null && sdir.equalsIgnoreCase("desc")){
            asc = false;
        }
        List<Journal> jl;
        long total;

        if( flt == null ){
            jl = tracContext.getJournalDao().getJournalList( first, blockSize, skey, asc );
            total = tracContext.getJournalDao().getJournalCount();
        } else {
            jl = tracContext.getJournalDao().getJournalList( first, blockSize, skey, asc, flt );
            total = tracContext.getJournalDao().getJournalCount( flt );
        }
        // buid record map
        //----------------
        
        records = new HashMap<String,Object>();
        records.put("recordsReturned", jl.size() );
        records.put("totalRecords", total );
        records.put("startIndex", first );
        records.put("sort", skey );
        records.put("dir", sdir );
        records.put("pageSize", max );

        List<Map<String,Object>> rl = new ArrayList<Map<String,Object>> ();
        records.put("records", rl );
        
        Map<Journal,List> jstat = tracContext.getJournalDao()
            .getJournalListStats( jl ); 

        log.debug( "jstat count=" + jstat.size());
        
        for( Iterator<Journal> ii = jl.iterator(); ii.hasNext(); ) {

            IcJournal ij = (IcJournal) ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ij.getId() );
            r.put( "nlmid", ij.getNlmid() );
            r.put( "title", ij.getTitle() );
            r.put( "owner", ij.getOwner().getLogin() );
            r.put( "date", ij.getCreateDateString() );
            r.put( "time", ij.getCreateTimeString() );
            
            Group imex = ij.getImexGroup();
            if( imex != null ){
                r.put( "imex", true );
                r.put( "imexDB", imex.getName() );
            } else {
                r.put( "imex", false );               
            }

            long pqueue = 0;
            long pqueueNew = 0;
            long pqueueDisc = 0;
            long queue = 0;
            long queueNew = 0;
            long queueDisc = 0;

            long curate = 0;
            long curReserved = 0;
            long curInprog = 0;
            long curIncomp = 0;
            long curProcessed = 0;
            long curDisc = 0;

            long release = 0;
            long relReleased = 0;
            long relRetracted = 0;
            

            List sl = jstat.get( ij );
            log.debug( "ij="+ ij.getTitle() + " sl size=" + sl.size() );

            for( Iterator is = sl.iterator(); is.hasNext(); ){
                Map ss = (Map) is.next();
                
                log.debug( " stage="+ ((DataState)ss.get( "stage" )).getName() +
                           " stat=" + ((DataState)ss.get( "state" )).getName() + 
                           " count=" +  ss.get( "count" ));
                
                long count = (Long) ss.get( "count" );

                if( ((DataState)ss.get( "stage" )).getName().equals("PREQUEUE") ){
                
                    pqueue += count;
                    if( ((DataState)ss.get( "state" )).getName().equals( "DISCARDED" )){
                        pqueueDisc += count;                       
                    }
                    if( ((DataState)ss.get( "state" )).getName().equals( "NEW" )){
                        pqueueNew += count;                       
                    }
                    continue;
                }
                 
                if( ((DataState)ss.get( "stage" )).getName().equals( "QUEUE" ) ){
                    queue += count;
                    if( ((DataState)ss.get( "state" )).getName().equals( "DISCARDED" )){
                        queueDisc += count;
                    }
                    if( ((DataState)ss.get( "state" )).getName().equals( "NEW" )){
                        queueNew += count;
                    }
                    continue;
                }

                if( ((DataState)ss.get( "stage" )).getName().equals( "CURATION" ) ){
                    
                    if( ((DataState)ss.get( "state" )).getName().equals( "RESERVED" )){
                        curate += count;
                        curReserved += count;
                        continue;
                    }

                    if( ((DataState)ss.get( "state" )).getName().equals( "INPROGRESS" )){
                        curate += count;
                        curInprog += count;
                        continue;
                    }

                    if( ((DataState)ss.get( "state" )).getName().equals( "INCOMPLETE" )){
                        curate += count;
                        curIncomp += count;
                        continue;
                    }

                    if( ((DataState)ss.get( "state" )).getName().equals( "PROCESSED" )){
                        curate += count;
                        curProcessed += count;
                        continue;
                    }

                    if( ((DataState)ss.get( "state" )).getName().equals( "DISCARDED" )){
                        curate += count;
                        curDisc += count;
                        continue;
                    }
                }

                if( ((DataState)ss.get( "stage" )).getName().equals( "RELEASE" ) ){
                    
                    if( ((DataState)ss.get( "state" )).getName().equals( "RELEASED" )){
                        release += count;
                        relReleased += count;
                        continue;
                    }

                    if( ((DataState)ss.get( "state" )).getName().equals( "RETRACTED" )){
                        release += count;
                        relRetracted += count;
                        continue;
                    }
                }
            }

            r.put( "pqueue", pqueue );
            r.put( "pqueueNew", pqueueNew );
            r.put( "pqueueDiscarded", pqueueDisc );

            r.put( "queue", queue );
            r.put( "queueNew", queueNew );
            r.put( "queueDiscarded", queueDisc );

            r.put( "curation", curate );
            r.put( "curationReserved", curReserved);
            r.put( "curationInprogress", curInprog);
            r.put( "curationIncomplete", curIncomp);
            r.put( "curationProcessed", curProcessed);
            r.put( "curationDiscarded", curDisc);

            r.put( "release", release );
            r.put( "releaseReleased", relReleased );
            r.put( "releaseRetracted", relRetracted );

            log.debug("pq:" + pqueue + ":" + pqueueDisc +
                      "q: " + queue  + ":" + queueDisc +
                      "c: " + curate + ":" + curDisc +
                      "r: " + release+ ":" + relRetracted); 
            
            rl.add( r );
        }
        return JSON;
    }

    //--------------------------------------------------------------------------

    private GregorianCalendar parseDate( String date ) {
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "parseDate: " + date );
        
        // FORMAT: 2004/[0]3/12
        //----------------------

        try{
            Pattern p = Pattern.compile("(\\d{4}?)/(\\d\\d?)/(\\d\\d?)");
            Matcher m = p.matcher(date);
            if (  m.matches() ) {

                String year = m.group(1);
                String month = m.group(2);
                String day = m.group(3);
                
                log.debug( "Y=" + year + " M=" + month + " D=" + day );
                
                GregorianCalendar dateGC = 
                    new GregorianCalendar( Integer.parseInt( year ),
                                           Integer.parseInt( month )-1,
                                           Integer.parseInt( day ) );
                
                return dateGC;                
            }
        } catch( Exception ex ) {
            // ignore == parse to null
        }
        return null;        
    }
}
