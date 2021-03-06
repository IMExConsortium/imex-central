package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * JournalViewAction - web interface to journal management
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

public class JournalViewAction extends ManagerSupport {

    private final String NOJOU = "notfound";
    private final String JEDIT = "jedit";
    private final String JSON = "json";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";
    
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

    ////------------------------------------------------------------------------
    /// Index Manager
    //---------------

    private IndexManager indexManager;
    
    public void setIndexManager( IndexManager manager ) {
        this.indexManager = manager;
    }
    
    public IndexManager getIndexManager() {
        return this.indexManager;
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

    //-------------------------------------------------------------------------
    // initial year/volume/issue/stage/status
    //---------------------------------------
    
    private String iyear = "";

    public void setIYear( String year){
	iyear = year;
	Log log = LogFactory.getLog( this.getClass() );
	log.info("iyear: " + iyear);
    }
    
    public String getIYear(){
	return iyear;
    }

    //--------------------------------------------------------------------------

    private String ivolume = "";

    public void setIVolume( String volume ){
	ivolume = volume;
	Log log = LogFactory.getLog( this.getClass() );
	log.info("ivolume: " + ivolume);
    }
    
    public String getIVolume(){
	return ivolume;
    }

    //--------------------------------------------------------------------------

    private String iissue = "";

    public void setIIssue( String issue ){
	iissue = issue;
	Log log = LogFactory.getLog( this.getClass() );
	log.info("iIssue: " + iissue);
    }
    
    public String getIIssue(){
	return iissue;
    }

    //--------------------------------------------------------------------------

    private String istage = "";

    public void setIStage( String stage){
	istage = stage;
	Log log = LogFactory.getLog( this.getClass() );
	log.info("iStage: " + istage);
    }
    
    public String getIStage(){
	return istage;
    }

    //--------------------------------------------------------------------------

    private String istatus = "";

    public void setIStatus( String status){
	istatus = status;
	Log log = LogFactory.getLog( this.getClass() );
	log.info("iStatus: " + istatus);
    }
    
    public String getIStatus(){
	return istatus;
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
        if (getId() <= 0 ){ setId( 1 ); }        // default journal
        
        log.info( "id=" + getId() + " journal=" + journal + " op=" + getOp() ); 
   
        if ( tracContext.getJournalDao() == null ) return SUCCESS;
        
        if ( getId() > 0 && getOp() == null ) {
            log.debug( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );  
            return SUCCESS;
        }
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.info(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {
            
                //--------------------------------------------------------------
                //--------------------------------------------------------------
                // journal operations
                //-------------------
                
                if ( key.equalsIgnoreCase( "init" ) ) {
                    
                    if ( getId() > 0 && journal == null ) {
                        log.debug( "setting journal=" + getId() );
                        journal = entryManager.getIcJournal( getId() );
                    }

                    if( init == null ){
                        init = new HashMap<String,Object>();
                    }
                    
                    init.put( "jid", journal.getId() );
                    init.put("title", journal.getTitle() );
                    
                    if( getOpp() != null 
                        && getOpp().get("year") != null 
                        && getOpp().get("volume") != null 
                        && getOpp().get("issue") != null ){
                        
                        init.put( "year", getOpp().get( "year" ));
                        init.put( "volume", getOpp().get( "volume" ));
                        init.put( "issue", getOpp().get( "issue" ));
                        
                    } else {

                        if( getOpp() != null && getOpp().get("year") != null ){
                            init.put( "year", getOpp().get( "year" ));
                        }
                    }
                    
                    //----------------------------------------------------------                    
                    
                    // get most recent YVI here: false == last
                    
                    if( init.get("year") == null 
                        || ((String)init.get("year")).length() == 0 ){
                        String year = entryManager
                            .getIcJournalYear( journal, false );
                        init.put( "year", year );

                        List<String> yearList = entryManager
                            .getIcJournalYearList( journal, false );
                        init.put( "year-list", yearList );
                    }
                    
                    if( init.get("volume") == null 
                        || ((String)init.get("volume")).length() == 0 ){
                        String volume = entryManager
                            .getIcJournalVolume( journal, false,
                                                 (String) init.get( "year" ) );
                        init.put( "volume", volume );

                        List<String> volumeList = entryManager
                            .getIcJournalVolumeList( journal, false, 
                                                     (String) init.get( "year" ) );
                        init.put( "volume-list", volumeList );
                    }
                    
                    if( init.get("issue") == null 
                        || ((String)init.get("issue")).length() == 0){
                        String issue = entryManager
                            .getIcJournalIssue( journal, false,
                                                (String) init.get( "year" ),
                                                (String) init.get( "volume" ) );
                        init.put( "issue", issue );
                    
                        List<String> issueList = entryManager
                            .getIcJournalIssueList( journal, false,
                                                    (String) init.get( "year" ),
                                                    (String) init.get( "volume" ));
                        init.put( "issue-list", issueList );
                    }
                    
                    return JSON;
                }


                if ( key.equalsIgnoreCase( "jsrc" ) ) {
                    if ( getJournal() == null ) return SUCCESS;
                    String nlmid = getJournal().getNlmid();
                    return searchJournal( nlmid );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jpg" ) ) {

                    log.info( "jpg" );

                    if ( getOpp() == null ) {
                        return this.getIcJournalRecords();
                    }
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    String flt= getOpp().get( "flt" );

                    return this.getIcJournalRecords( max, off, 
                                                     skey, sdir, flt );
                }

                if ( key.equalsIgnoreCase( "jppg" ) ) {
                    
                    log.info("jppg:" + getOpp());

                    if ( getOpp() == null ) {
                        //return this.getIcJPubRecords();
                        
                        return SUCCESS;
                    }
                    
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    String flt= getOpp().get( "flt" );

                    String yrn= getOpp().get( "yr" );
                    String von= getOpp().get( "vo" );
                    String isn= getOpp().get( "is" );
                    String nnav= getOpp().get( "nnav" );
             
                    String sfv= getOpp().get( "sfv" );  // status
                    String gfv= getOpp().get( "gfv" );  // stage

       
                    if( nnav != null && nnav.equals( "year" ) ){
                        von="";
                        isn="";
                    }

                    if( nnav != null && nnav.equals( "volume" ) ){
                        isn="";
                    }

                    return this.getIcJPubRecords( getId(), null, max, off, 
                                                  skey, sdir, 
                                                  sfv, gfv, "", "", "",
                                                  yrn, von, isn );
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
        return this.getIcJournalRecords( "", "", "", "", "" );
    }
    
    public String getIcJournalRecords( String max, String off, 
                                       String skey, String sdir, 
                                       String flt ) {
        
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

        List<Journal> jl = 
            tracContext.getJournalDao().getJournalList( first, blockSize );
        long total =  
            tracContext.getJournalDao().getJournalCount();

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

        for( Iterator<Journal> ii = jl.iterator(); ii.hasNext(); ) {
            IcJournal ij = (IcJournal) ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ij.getId() );
            r.put( "nlmid", ij.getNlmid() );
            r.put( "title", ij.getTitle() );
            r.put( "owner", ij.getOwner().getLogin() );
            r.put( "date", ij.getCreateDateString() );
            r.put( "time", ij.getCreateTimeString() );
            rl.add( r );
        }
        return JSON;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

/*
    public String getIcJPubRecords() {
        return this.getIcJPubRecords( 1, null, "", "", "", "", "", "", "", "","","","","" );
    }
*/

    public String getIcJPubRecords( int id, String max, String off,
                                    String skey, String sdir,
                                    String sfv, String pfv,
                                    String ofv, String efv,
                                    String ffv ){

        return getIcJPubRecords( id, null, max, off, skey, sdir, sfv, pfv,
                                 ofv, efv, ffv,"","","" );
    }
    
/*
    public String getIcJPubRecords( int id, String max, String off,
                                    String skey, String sdir,
                                    String flt ){
        
        return getIcJPubRecords( id, null, max, off, skey, sdir, "", "",
                                 "", "", "","","","" );
    }
*/

    public String getIcJPubRecords( int id, String max, String off,
                                    String skey, String sdir,
                                    String flt, String yr, 
                                    String vo, String is ){

        return getIcJPubRecords( id, null, max, off, skey, sdir, "", "",
                                 "", "", "",yr, vo, is );
    }
   
    //return this.getIcJPubRecords( max, off,
    //                              skey, sdir, flt );

    //--------------------------------------------------------------------------

    public String getIcJPubRecords( int jid, User usr, 
                                    String max, String off, 
                                    String skey, String sdir, 
                                    String sfv, String gfv, 
                                    String ofv, String efv,
                                    String ffv, String yr, 
                                    String vo, String is ){
        
        if ( tracContext.getPubDao() == null ) return null;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "getPubRecords: pubDao ok >" + sdir + "<"  );
        
        int first = 0;
        int blockSize = 10; // NOTE: initialize for defaults ?
        boolean asc = true;

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
        } else {
            max ="";
        }

        /*
        if ( sdir != null && sdir.equals( "false" ) ) {
            asc = false;
        } else {
            sdir ="true";
        }
        */

        if ( sdir != null &&
             ( sdir.equals( "false" )
               || sdir.equals( "desc" ) ) ){
            asc = false;
            sdir ="desc";
        } else {
            sdir ="asc";
        }

        String sortKey ="id";
        
        if( skey != null && !skey.equals("") ){
            if( skey.equals( "pub" ) ){
                sortKey ="author";
            }
            if( skey.equals( "date" ) ){
                sortKey ="crt";
            }
            if( skey.equals( "owner" ) ){
                sortKey ="owner";
            }
            if( skey.equals( "pmid" ) ){
                sortKey ="pmid";
            }
            if( skey.equals( "imexId" ) ){
                sortKey ="imex";
            }
            
            if( skey.equals( "actUser" ) ){
                sortKey ="actUser";
            }
            if( skey.equals( "actTStamp" ) ){
                sortKey ="actDate";
            }
            if( skey.equals( "modUser" ) ){
                sortKey ="modUser";
            }
            if( skey.equals( "modTStamp" ) ){
                sortKey ="modDate";
            }
        } else {
            skey = "id";
            sortKey = "id";
        }

        List<IcPub> pl = new ArrayList<IcPub>();
        long total = 0;
        log.debug( "getPubRecords: " + sfv + " :: " + gfv + " :: " + efv + " :: " + ffv);

        Map<String,String> flt = new HashMap<String,String>();
        flt.put( "jid", String.valueOf(jid) );
        flt.put( "status", sfv );
        flt.put( "stage", gfv );
        flt.put( "owner", ofv );
        flt.put( "editor", efv );
        flt.put( "cflag", ffv );

        /*        
        if ( jid <=0 && sfv.equals("") && gfv.equals("") && ofv.equals("") 
             && efv.equals("") && ffv.equals("") 
             && (yr == null || yr.equals("") ) 
             && (vo == null || vo.equals("") ) 
             && (is == null || is.equals("") ) ){
            
            log.info( "getJPubRecords: unfiltered" );
            
            if( usr == null ){
                pl = tracContext.getPubDao()
                    .getPublicationList( first, blockSize, sortKey, asc );
                total = tracContext.getPubDao().getPublicationCount();
            } else {
                // *
                log.debug( "getting list" );
                pl = watchManager
                    .getPublicationList( usr, first, blockSize, sortKey, asc );

                log.debug( "getting count" );
                total = watchManager
                    .getPublicationCount( usr );
                log.debug( "got count" );
             * //
            }
            
         } else {
           */ 
          
        log.info( "GetJPubRecords: filtered" );
            
        IcJournal journal = entryManager.getIcJournal( jid );

        // get all years

        List<String> yearList = entryManager
            .getIcJournalYearList( journal, false );
        
        // no year set: get most recent
        
        if( yr == null || yr.length() == 0 ){
            yr = entryManager
                .getIcJournalYear( journal, false );
        }

        // get all volumes (for current year)
        
        List<String> volumeList = entryManager
            .getIcJournalVolumeList( journal, false, yr );
        
        if( vo == null || vo.length() == 0 ){
            
            // no volume set: get most recent 

            vo = entryManager
                .getIcJournalVolume( journal, false, yr );
        } else {
            
            // volume set: reset of not in current year
            if( ! volumeList.contains( vo ) ){
                 vo = entryManager
                     .getIcJournalVolume( journal, false, yr );
            }
        }
            
        // get all issues (for current volume)
        
        List<String> issueList = entryManager
            .getIcJournalIssueList( journal, false, yr,vo );
 
        if( is == null || is.length() == 0){
            // no issue  set: get most recent
            is = entryManager
                .getIcJournalIssue( journal, false, yr, vo );
        } else {
            // issue set: reset of not in current volume 
            if( ! issueList.contains( is ) ){
                is = entryManager
                    .getIcJournalIssue( journal, false, yr, vo );
            }
        }
        
        flt.put( "year", yr );
        flt.put( "volume", vo );
        flt.put( "issue", is );
        
        /*
        pl = tracContext.getPubDao()
            .getPublicationList( first, blockSize, 
                                 sortKey, asc, flt );            

        

        total = tracContext.getPubDao().getPublicationCount( flt );
        */

        
        pl = getIndexManager()
            .getPublicationList( first, blockSize,
                                 sortKey, asc, flt );
        
        total = getIndexManager().getPublicationCount( flt );
        
        
        log.debug( "GetPubRecords: total=" + total);


        // buid init map
        //--------------

        init = new HashMap<String,Object>();

        init.put("jid",  String.valueOf(jid) );
        
        init.put("year", yr );
        //List<String> yearList = entryManager
        //    .getIcJournalYearList( journal, false );
        init.put( "year-list", yearList );

        init.put("volume", vo );
        //List<String> volumeList = entryManager
        //    .getIcJournalVolumeList( journal, false, yr );
        init.put("volume-list", volumeList );

        init.put("issue", is );
        //List<String> issueList = entryManager
        //    .getIcJournalIssueList( journal, false, yr,vo );
        init.put("issue-list", issueList );


        // buid record map
        //----------------
        
        records = new HashMap<String,Object>();
        records.put("recordsReturned", pl.size() );
        records.put("totalRecords", total );
        records.put("startIndex", first );
        records.put("sort", skey );
        records.put("dir", sdir );
        records.put("pageSize", max );
        records.put("filter", flt );

        List<Map<String,Object>> rl = new ArrayList<Map<String,Object>> ();
        records.put("records", rl );

        for( Iterator<IcPub> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub ip =  ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ip.getId() );
            r.put( "pmid", ip.getPmid() );
            r.put( "doi", ip.getDoi() );
            r.put( "jintId", ip.getJournalSpecific() );
            r.put( "imexId", ip.getImexId() );
            r.put( "cEmail", ip.getContactEmail() );

            r.put( "title", ip.getTitle() );
            r.put( "author", ip.getAuthor() );
            r.put( "owner", ip.getOwner().getLogin() );
            r.put( "state", ip.getState().getName() );
            r.put( "date", ip.getCreateDateString() );
            r.put( "time", ip.getCreateTimeString() );
            r.put( "editor", "N/A" );
            r.put( "imexDb", "N/A" );

            r.put( "actUser", ip.getActUser().getLogin() );
            r.put( "actTStamp", ip.getActDateStr() );

            r.put( "modUser", ip.getModUser().getLogin() );
            r.put( "modTStamp", ip.getModDateStr() );

            //"pages","issue","volume","year","stage",

            r.put( "pages", ip.getPages() );
            r.put( "issue", ip.getIssue() );
            r.put( "volume", ip.getVolume() );
            r.put( "year", ip.getYear() );

            //r.put( "stage", "pQueue" );
	    r.put( "stage", ip.getStage().getName() );

            // set partner
            //------------

            String partner = "";

            Set<Group> gs = ip.getAdminGroups();
            for( Iterator<Group> gi = gs.iterator(); gi.hasNext(); ) {

                Group g = gi.next();
                Set<Role> rs = g.getRoles();

                for( Iterator<Role> ri = rs.iterator(); ri.hasNext(); ) {
                    Role role = ri.next();
                    if( role.getName().toUpperCase().equals(PARTNER) ) {
                        partner += g.getLabel()+":";
                    }
                    //log.debug( "r:" + role.getName() );
                }
                //log.debug( "g:" + g.getLabel() );
                
            }
            if ( !partner.equals("") ) {
                r.put( "imexDb", partner.substring(0,partner.length()-1 ) );
            }
            

            // set editors
            //------------

            String editor = "";

            Set<User> us = ip.getAdminUsers();
            for( Iterator<User> ui = us.iterator(); ui.hasNext(); ) {

                User u = ui.next();
                Set<Role> rs = u.getRoles();

                for( Iterator<Role> ri = rs.iterator(); ri.hasNext(); ) {
                    Role role = ri.next();
                    if( role.getName().toUpperCase().equals(EDITOR) ) {
                        editor += u.getLogin()+":";
                    }
                    //log.debug( "r:" + role.getName() );
                }
                //log.debug( "u:" + u.getLogin() );
                
            }
            if ( !editor.equals("") ) {
                r.put( "editor", editor.substring(0,editor.length()-1 ) );
            }
            
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
