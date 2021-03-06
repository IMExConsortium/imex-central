package edu.ucla.mbi.imex.central.struts.action;

/*==============================================================================
 *                                                                             $
 * EntryMgrAction - web interface to entry management                          $
 *                                                                             $
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

public class EntryMgrAction extends ManagerSupport implements LogAware{

    private final String NOPUB = "notfound";
    private final String PUBEDIT = "pubedit";
    private final String PUBNEW = "pubnew";
    private final String JSON = "json";
    private final String INPUT = "input";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";

    private Map<String,String> filter = null;
    
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
    /// Watch Manager
    //--------------

    private WatchManager watchManager;
    
    public void setWatchManager( WatchManager manager ) {
        this.watchManager = manager;
    }
    
    public WatchManager getWatchManager() {
        return this.watchManager;
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
    // IcPub
    //------

    private IcPub icpub = null;
    
    public void setPub( IcPub pub ) {
        this.icpub = pub;
    }

    public IcPub getPub(){
        return this.icpub;
    }

    //--------------------------------------------------------------------------
    // Stage
    //-------

    private String stage = null;
    
    public void setStage( String stage) {
	this.stage = stage;
	
	Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: set stage=" + stage  );

    }

    public String getStage(){
        return this.stage;
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
    
    public List<IcPub> getPublicationList(){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: getPublicationList"  );
        
        if ( tracContext.getPubDao() == null ) return null;

        log.debug( "getPublicationList: pubDao ok..."  );
        
        List<Publication> pl = tracContext.getPubDao().getPublicationList();

        log.debug( "publist=" + pl );
        
        if ( pl == null ) return null;
        
        List<IcPub> ipl = new ArrayList<IcPub>();
        for ( Iterator<Publication> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub jj = (IcPub) ii.next();
            ipl.add( jj );
        }
        return ipl;
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
    // PMID
    //-----

    private String pmid = null;

    public void setPmid( String pmid ) {
        this.pmid = sanitize( pmid );
    }
    
    public String getPmid(){
        return this.pmid;
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
    
    Map<String,Object> flags = null;

    public Map<String,Object> getFlags(){
        return this.flags;
    }
    
    //--------------------------------------------------------------------------
    // target states
    //--------------


    private List<String> targetStates = null;
    
    public void setTargetStates( List<String> states) {
        this.targetStates = states;
    }

    public List<String> getTargetStates() {
        return this.targetStates;
    }
    
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " icpub=" + icpub + " op=" + getOp() ); 
        System.out.println(  "id=" + getId() + " icpub=" + icpub + " op=" + getOp() ); 
        this.filter = buildFilter( (Map<String,String>) getOpp(), getStage() );
        
        if ( tracContext.getPubDao() == null ) return SUCCESS;
        
        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null) {
            luser = getUserContext().getUserDao().getUser( iusr.intValue() );
            log.debug( " user set to: " + luser );
        }
        
        if ( getId() > 0 && icpub == null && getOp() == null ) {
            
            log.debug(  "setting icpub=" + getId() );
            icpub = entryManager.getIcPub( getId() );
            
            if( luser != null && icpub != null ){ 
                flags = new HashMap<String,Object>();
                flags.put( "watch", getWatchStatus( luser, icpub ) );  
            }
            if( format != null && format.toUpperCase().equals("JSON") ) {
                return JSON;
            } 
            return SUCCESS;
        } 
        
        if( getPmid() != null ) {
            log.debug( "no ic pub record pmid=" + getPmid() +"<" );
            Publication nicp = entryManager.getPubByPmid( getPmid() );
             
            log.debug("nicp="+nicp);

            if ( nicp != null ) {
                icpub = new IcPub( nicp ); 
                return PUBNEW;
            } else {
                addActionError( "No publication found." ) ;
                icpub = new IcPub(new Publication());
                icpub.setPmid(getPmid());
                return NOPUB;
            }
        }

        if( getOp() == null ) return SUCCESS;
        
        if ( getId() > 0 && icpub == null ) {
            icpub = entryManager.getIcPub( getId() );
        }

        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {

                //--------------------------------------------------------------
                // initialize
                //-----------
                if ( key.equalsIgnoreCase( "init" ) ) {
                    return SUCCESS;                    
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "esrc" ) ) {

                    String imex = null ;
                    if ( getOpp() != null ) {
                        String ns  = getOpp().get( "ns" );
                        String ac  = getOpp().get( "ac" );

                        return searchIcPub( ns, ac );
                        
                    }
                    return searchIcPub( icpub, imex );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "eadd" ) ) {
                    return addIcPub( icpub );
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "edel" ) ) {
                    return deleteIcPub( icpub, luser );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "etsl" ) ) {
                    if( icpub != null ) {
                        return getTargetStates( icpub, val );
                    }
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "eldel" ) ) {

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
                        return deleteIcPubList( uidl, luser );
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "epup" ) ) {
                    return updateIcPubProperties( getId(), luser, icpub );
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "epix" ) ) {
                    return genIcPubImex( getId(), luser, icpub );
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "eidu" ) ) {

                    // update identifiers
                    //-------------------

                    return updateIcPubIdentifiers( icpub, luser,
                                                   getOpp().get( "pmid" ),
                                                   getOpp().get( "doi" ),
                                                   getOpp().get( "jsp" ));
                }

                //--------------------------------------------------------------
         
                if ( key.equalsIgnoreCase( "eatu" ) ) {

                    // update author/title
                    //--------------------
                    
                    return updateIcPubAuthTitle( icpub, luser,
                                                 getOpp().get( "ath" ),
                                                 getOpp().get( "ttl" ) );
                }

                //--------------------------------------------------------------
         
                if ( key.equalsIgnoreCase( "epmr" ) ) {

                    // resync with pubMed
                    //--------------------
                    
                    return resyncIcPubPubmed( icpub, luser,
                                              getOpp().get( "pmid" ) );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "edup" ) ) {

                    if ( getOpp() == null ) return SUCCESS;
                    
                    // parse dates
                    //------------

                    GregorianCalendar epGD = parseDate( getOpp().get( "epd" ) );
                    GregorianCalendar pGD = parseDate( getOpp().get( "pd" ) );
                    GregorianCalendar rGD = parseDate( getOpp().get( "rd" ) );
                    
                    updateIcPubDates( getId(), luser, epGD, pGD, rGD );

                    return JSON;

                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "esup" ) ) {

                    int sid=0;
                   
                    log.debug( "opp=" + getOpp() );
                    
                    if ( getOpp() == null ) return SUCCESS;
                    String nsid = getOpp().get( "nsn" );
                    log.debug( "opp.nsn=" + nsid );
                    try {
                        sid = Integer.parseInt(nsid);
                        updateIcPubState( getId(), luser, sid );
                    } catch ( Exception ex ) {
                        // should not happen
                        String res = updateIcPubState( getId(), luser, nsid );
                        return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                    }
                                        
                    return JSON;
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "emup" ) ) {

                    int sid=0;

                    log.debug( "opp=" + getOpp() );
                    
                    if ( getOpp() == null ) return SUCCESS;
                    String necm = getOpp().get( "necm" );
                    log.debug( "opp.necm=" + necm );
                    try {
                        String res 
                            = updateIcPubContactMail( getId(), luser, necm );
                        return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                    } catch ( Exception ex ) {
                        // should not happen
                        //updateIcPubContactMail( getId(), luser, necm );
                    }
                    
                    return JSON;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eauadd" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String ulogin = getOpp().get( "eauadd" );
                    try {
                        String res 
                            = addIcPubAdminUser( getId(), luser, ulogin );
                        return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return JSON;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eagadd" ) ) {
                    if ( getOpp() == null ) return JSON;
                    
                    String sgid = getOpp().get( "eagadd" );
                    try {
                        int gid = Integer.parseInt( sgid );
                        
                        String res = addIcPubAdminGroup( getId(), luser,  gid );
                        return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return JSON;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eaudel" ) ) {
                    if ( getOpp() == null ) return JSON;
                    
                    String udel = getOpp().get( "eaudel" );

                    log.debug("eaudel=" + udel);

                    if ( getId() > 0 && udel != null ) {
                        try {
                             List<Integer> uidl =
                                 new ArrayList<Integer>();
                             String[] us = udel.split(",");

                             for( int ii = 0; ii <us.length; ii++ ) {
                                 if( us[ii].length()>0 ) {
                                     uidl.add( Integer.valueOf( us[ii] ) );
                                 }
                             }
                             String res 
                                 = delIcPubAdminUsers( getId(), luser, uidl );
                             return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                    } else {
                        icpub = entryManager.getIcPub( getId() );
                        // validate access according to acl ???
                        setId( icpub.getId() );
                    }
                    return JSON;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eagdel" ) ) {
                    if ( getOpp() == null ) return JSON;

                    String gdel = getOpp().get( "eagdel" );
                    log.debug("eagdel=" + gdel);

                    if ( getId() > 0 && gdel != null ) {
                        try {
                             List<Integer> gidl =
                                 new ArrayList<Integer>();
                             String[] gs = gdel.split(",");

                             for( int ii = 0; ii <gs.length; ii++ ) {
                                 if( gs[ii] != null && gs[ii].length() > 0 ) {
                                     gidl.add( Integer.valueOf( gs[ii] ) );
                                 }
                             }
                             String res 
                                 = delIcPubAdminGroups( getId(), luser, gidl );
                             return res.equals(ACL_OPER) ? ACL_OPER : JSON;
                             
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                    } else {
                        icpub = entryManager.getIcPub( getId() );
                        // validate access according to acl ???
                        setId( icpub.getId() );
                    }
                    return JSON;
                }

                if ( key.equalsIgnoreCase( "ppg" ) ){

                    log.debug(  "\n\nop=" + getOp() );
                    log.debug(  "opp=" + getOpp() );
                    log.debug(  "luser=" + luser +"\n\n" );

                    if ( getOpp() == null ) {
                        return getIcPubRecords();
                    }
                    
                    String wfl= getOpp().get( "wfl" ) == null ?
                        "" :  getOpp().get( "wfl" );
                    
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    
                    String gfv = getOpp().get( "gfv" ) == null ? 
                        "" :  getOpp().get( "gfv" );
                    String sfv = getOpp().get( "sfv" ) == null ? 
                        "" :  getOpp().get( "sfv" );
                    String pfv = getOpp().get( "pfv" ) == null ?
                        "" :  getOpp().get( "pfv" );;
                    String ofv = getOpp().get( "ofv" ) == null ?
                        "" :  getOpp().get( "ofv" );
                    String efv = getOpp().get( "efv" ) == null ?
                        "" :  getOpp().get( "efv" );

                    String ffv = getOpp().get( "ffv" ) == null ?
                        "" :  getOpp().get( "ffv" );
                

                    String query = getOpp().get( "query" ) == null ?
                        "" :  getOpp().get( "query" );
                

                    if( !wfl.equalsIgnoreCase("true") || luser == null ){
			
                        return getIcPubRecords( max, off, skey, sdir, filter );
                        //                      gfv, sfv, pfv, ofv, efv, ffv );

                    } else {
                        return getWatchedRecords( luser, max, off, skey, sdir, 
						  filter );
                        //                        gfv, sfv, pfv, ofv, efv, ffv );
                    }
                }                
            }
        }
        return SUCCESS;
    }

    private Map<String,String> buildFilter( Map<String,String> fmap, String stage ){

        Map<String,String> filter = new HashMap<String,String>();
        
        if( fmap != null ){
            if( fmap.get( "gfv" ) != null ){
                filter.put( "stage", fmap.get( "gfv" ) );
            }
            
            if( fmap.get( "sfv" ) != null ){
                filter.put( "status", fmap.get( "sfv" ) );
            }
            if( fmap.get( "pfv" ) != null ){
                filter.put( "partner", fmap.get( "pfv" ) );
            }
            if( fmap.get( "ofv" ) != null ){
                filter.put( "owner", fmap.get( "ofv" ) );
            }
            if( fmap.get( "efv" ) != null ){
                filter.put( "editor", fmap.get( "efv" ) );
            }
            if( fmap.get( "ffv" ) != null ){
                filter.put( "cflag", fmap.get( "ffv" ) );
            }
            
            if( fmap.get( "jfv" ) != null ){
                filter.put( "nlmid", fmap.get( "jfv" ) );
            }
        }
        
        if( stage != null ){
            filter.put( "stage", stage );
        }
        
        return filter; 
    }
    
    

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // validation
    //-----------
    
    public void validateXXX() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: validate" );
       
        //boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                    
                    log.debug( " op=" + val);
                    if ( key.equalsIgnoreCase( "esrc" ) ) {

                        if ( getOpp() != null 
                             && getOpp().get( "imex" ) != null 
                             && ! getOpp().get( "imex" ).equals("") ) {
                            break;
                        }

                        if( getPub() == null || getPub().getPmid() == null ) {
                            addFieldError( "pub.pmid",
                                           "PMID field cannot be empty." );
                            
                        } else {
                            String pmid = getPub().getPmid();
                            try {
                                pmid = pmid.replaceAll( "\\s", "" );
                            } catch( Exception ex ) {
                                // should not happen
                            }
                            log.debug( " pmid=" + pmid +"<");
                            if( pmid.length() == 0  ) {
                                log.debug( "  empty PMID field" );
                                addFieldError( "pub.pmid",
                                               "PMID field cannot be empty." );
                            }
                        }

                        break;
                    }
                    
                    //----------------------------------------------------------
                    
                    if ( key.equalsIgnoreCase( "eatu" ) ) {
                        
                        String auth = null;
                        String title = null;
                        
                        if( getPub() != null ) {

                            auth = getPub().getAuthor();
                            title = getPub().getTitle();
                            
                            if (auth != null ) {
                                auth = auth.replaceAll( "^\\s+", "" );
                                auth = auth.replaceAll( "\\s+$", "" );
                            } else {
                                auth = "";
                            }
                            getPub().setAuthor( auth );

                            if ( title != null ) {
                                title = title.replaceAll( "^\\s+", "" );
                                title = title.replaceAll( "\\s+$", "" );
                            } else {
                                title= "";
                            }
                            getPub().setTitle( title );

                        }
                        
                        if( auth == null || auth.length() == 0 ) {
                            addFieldError( "pub.author",
                                           "Author field cannot be empty." );
                        }
                        
                        if( title == null || title.length() == 0 ) {
                            addFieldError( "pub.title",
                                           "Title field cannot be empty." );
                        }
                        
                        break;
                    }                                        
                }
            }
        }
    }
    
    private boolean aclTargetValidate( Publication pub ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: aclTargetValidate"  );
        
        return pub.testAcl( ownerMatch, adminUserMatch, adminGroupMatch);
    }
     
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // operations
    //-----------

    public String searchIcPub( Publication pub, String imex ) {

        Log log = LogFactory.getLog( this.getClass() );
        
        if( pub != null ) {
            log.debug( " search pub -> id=" + pub.getId() +
                      " pmid=" + pub.getPmid() );
        } else {  
            log.debug( " search pub -> imex=" + imex );
        }        

        try {
            imex = imex.replaceAll("\\D+", "" );
        } catch ( Exception ex ) {
            // skip error
        }

        if ( imex != null && !imex.equals("") ) {
            IcPub oldPub = entryManager.getIcPubByIcKey( imex );
            
            if ( oldPub != null ) {
                 
                if( !aclTargetValidate( oldPub ) ) return ACL_OPER;
                
                icpub = oldPub;
                setId( oldPub.getId() );
                return PUBEDIT;
            }

            addActionError( "No publication found" ) ;
            return NOPUB;

        }

        if( pub!= null && pub.getPmid() != null ){

            pub.setPmid( sanitize( pub.getPmid() ) );
            
            IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
            
            if ( oldPub != null ) {

                if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
                    
                icpub = oldPub;
                setId( oldPub.getId() );
                return PUBEDIT;
            }
        
            if( !pub.getPmid().equals("") ) {
                this.setPmid( pub.getPmid() );
                
                return PUBNEW;
            }
                
            addActionError( "No publication found" ) ;
            return NOPUB;
        } 
        
        return INPUT;

    }

    //------------------------------------------------------------------------------

    public String searchIcPub( String ns, String ac ) {

        Log log = LogFactory.getLog( this.getClass() );

        if( ns == null ||  ac == null ){
            addActionError( "No publication found" ) ;
            return NOPUB; 
        }
        
        IcPub oldPub = entryManager.getIcPubByNsAc( ns, ac );
        
        if ( oldPub != null ) {

            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
                
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        } else {
            addActionError( "No publication found" ) ;
            return NOPUB;
        }
    }
    
    //---------------------------------------------------------------------
    
    public String addIcPub( Publication pub ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " new pub -> id=" + pub.getId() +
                  " pmid=" + pub.getPmid() );

        pub.setPmid( sanitize( pub.getPmid() ) );
        
        // test if already in 
        //-------------------
        
        IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
        
        if ( oldPub != null ) {

            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
                
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        }
        
        // ACL target control NOTE: implement as needed
        //---------------------------------------------
        /* if ( ownerMatch != null && ownerMatch.size() > 0 ) { } */
        
        Integer usr = (Integer) getSession().get( "USER_ID" );
        if ( usr == null )  return ACL_OPER;
        log.debug( " login id=" + usr );
        
        User owner = 
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null )  return ACL_OPER;
        log.debug( " owner set to: " + owner );
        
        DataState state =  
            wflowContext.getWorkflowDao().getDataState( "NEW" );

        DataState stage =  
            wflowContext.getWorkflowDao().getDataStage( "PREQUEUE" );
        log.debug( " stage set to: " + stage );
        log.debug( " state set to: " + state );
        
        if ( state != null ) {

            try{

                IcPub newPub = entryManager.addIcPub( pub, owner, stage, state );
                if ( newPub != null ) {
                    icpub = newPub;
                    setId( newPub.getId() );
                    return PUBEDIT;
                }
            } catch( ImexCentralException icx ){
                statCode = icx.getStatusCode();
                statMessage = icx.getStatusMessage();
            }
        }
        
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    public String deleteIcPub( Publication pub, User luser ) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        entryManager.deleteIcPub( null, luser );
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    private String deleteIcPubList( List<Integer> pubs, User luser ) {
        
        //if( ! aclTargetValidate( pub ) ) return ACL_OPER;
        
        entryManager.deleteIcPub( null, luser );
        return SUCCESS;        
    }

    //--------------------------------------------------------------------------
    
    public String updateIcPubProperties( int id, User luser, Publication pub ) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        entryManager.updateIcPubProps( null, luser );
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    public String updateIcPubIdentifiers( IcPub pub, User user,
                                          String nPmid, 
                                          String nDoi, String nJsp) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NEW: pmid=  " + nPmid + " doi=" + nDoi + " jsp=" + nJsp );
                  
        pub.setPmid( sanitize( nPmid ) );
        pub.setDoi( sanitize( nDoi ) );
        pub.setJournalSpecific( sanitize( nJsp ) );


        try{
            entryManager.updateIcPubIdentifiers( pub, user, pub );
        } catch( ImexCentralException icx ){
            statCode = icx.getStatusCode();
            statMessage = icx.getStatusMessage();
        }
        
        return JSON;
    }

    //--------------------------------------------------------------------------

    public String updateIcPubAuthTitle( IcPub pub, User user,
                                        String nAuth, String nTitle ) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NEW: auth=" + nAuth + " title=" + nTitle );
        
        pub.setAuthor( sanitize( nAuth ) );
        pub.setTitle( sanitize( nTitle ) );

        entryManager.updateIcPubAuthTitle( pub, user, pub );

        return JSON;
    }

    //--------------------------------------------------------------------------

    public String resyncIcPubPubmed( IcPub pub, User user, String pmid ) {


        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "RESYNC: pmid=" + pmid );
        
        //pub.setPmid( sanitize( pmid ) );

        try{
        
            IcPub uPub = entryManager.resyncIcPubPubmed( pub, user, pub );
            
            if( uPub != null ){
                setPub( uPub );
            }
        } catch( ImexCentralException icx ){
            statCode = icx.getStatusCode();
            statMessage = icx.getStatusMessage();
        }
        return JSON;
    }

    //--------------------------------------------------------------------------


    public String genIcPubImex( int id, User luser, IcPub pub ) {
        
        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        IcPub oldPub = entryManager.genIcPubImex( pub, luser );
        
        if ( oldPub != null ) {
            icpub = oldPub;
            setId( oldPub.getId() );
            this.setPmid( oldPub.getPmid() );
            return JSON;        
        }

        return JSON;
    }


    //--------------------------------------------------------------------------

    public String getTargetStates( IcPub pub, String mode ) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        List<String> states = entryManager.getTargetStates( pub, mode );
        targetStates = states;
        return JSON;
    }


    //--------------------------------------------------------------------------
    
    private String updateIcPubContactMail( int id, User luser, String mail ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "id=" + id + " mail=" + mail );

        IcPub pub = entryManager.getIcPub( id );
        if(pub != null ){
            if( ! aclTargetValidate( pub ) ) return ACL_OPER;
            IcPub uPub =  entryManager
                .updateIcPubContactMail( pub, luser, mail );
            if( uPub != null ) {
                this.setPub( uPub );
            }        
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------
    
    private String updateIcPubDates( int  id, User luser,
                                     GregorianCalendar epd, 
                                     GregorianCalendar pd, 
                                     GregorianCalendar rd ) {
        
        IcPub pub = entryManager.getIcPub( id );

        if( pub != null ){

            if( ! aclTargetValidate( pub ) ) return ACL_OPER;

            IcPub uPub = entryManager.updateIcPubDates( pub, luser, 
                                                        epd, pd, rd );
            if ( uPub != null ) {
                this.setPub( uPub );
            }
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------
    
    
    private  String updateIcPubState( int id, User user, String state ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "id=" + id + " state=" + state );
        
        IcPub pub = entryManager.getIcPub( id );

        if( pub != null ){

            if( ! aclTargetValidate( pub ) ) return ACL_OPER;

            IcPub uPub =  entryManager.updateIcPubState( pub, user, state );
            if( uPub != null ) {
                this.setPub( uPub );
            }        
        }
        return SUCCESS;
    }
    
    private String updateIcPubState( int id, User user, int sid ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "id=" + id + " sid=" + sid );

        IcPub pub = entryManager.getIcPub( id );
        if( pub != null ){

            if( ! aclTargetValidate( pub ) ) return ACL_OPER;

            IcPub uPub =  entryManager.updateIcPubState( pub, user, sid );
            if( uPub != null ) {
                this.setPub( uPub );
            }        
        }
        return SUCCESS;
    }    
    
    //---------------------------------------------------------------------
    
    public String addIcPubAdminGroup( int id, User user, int grp ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add EAG: id=" + id + " ag= " + grp );
                
        IcPub oldPub = entryManager.getIcPub( id );
        Group agrp = getUserContext().getGroupDao().getGroup( grp );

        if ( oldPub != null && agrp != null ) {
            
            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;

            if(entryManager.addAdminGroup( oldPub, user, agrp ) == null)
				return ACL_OPER;
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delIcPubAdminGroups( int id, User user, 
                                       List<Integer> gidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop EAG: id=" + id + " aglist= " + gidl );

        IcPub oldPub = entryManager.getIcPub( id );
        if ( oldPub != null && gidl != null ) {
            
            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;

            entryManager.delAdminGroups( oldPub, user, gidl );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String addIcPubAdminUser( int id, User user,
                                     String ulogin ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add EAU: id=" + id + " au= " + ulogin );
                
        IcPub oldPub = entryManager.getIcPub( id );

        
        User ausr = getUserContext().getUserDao().getUser( ulogin );

        if ( oldPub != null && ausr != null ) {
        
            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
    
            entryManager.addAdminUser( oldPub, user, ausr );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delIcPubAdminUsers( int id, User user,
                                      List<Integer> uidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop EAU: id=" + id + " aulist= " + uidl );

        IcPub oldPub = entryManager.getIcPub( id );
        if ( oldPub != null && uidl != null ) {

            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;

            entryManager.delAdminUsers( oldPub, user, uidl );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
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

    private boolean getWatchStatus( User usr, Publication pub ){ 
        return watchManager.getWatchStatus( usr, pub );       
    }

    public String getWatchedRecords( User usr ) {
        return this.getWatchedRecords( usr, "", "", "", "", null );
    }
    
    public String getWatchedRecords( User usr, 
                                     String max, String off,
                                     String skey, String sdir,
				     Map<String,String> filter
                                     ){
	//                           String gfv, String sfv, String pfv,
        //                           String ofv, String efv, String ffv){
        
	
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getWatchedRecords: uid=" + usr.getId() );

        return getIcPubRecords( usr , max, off, skey, sdir, filter );
	//			gfv, sfv, pfv, ofv, efv, ffv);
        
    }
    
    public String getIcPubRecords() {
        return this.getIcPubRecords( null, "", "", "", "", null );
    }
    
    public String getIcPubRecords( String max, String off, 
                                   String skey, String sdir,
                                   Map<String,String> filter
                                   ){
        //                         String gfv, String sfv, String pfv, 
        //                         String ofv, String efv, String ffv){
        return getIcPubRecords( null, max, off, skey, sdir, filter );
        //			gfv, sfv, pfv, ofv, efv, ffv);
    }
    
    public String getIcPubRecords( User usr, 
                                   String max, String off, 
                                   String skey, String sdir, 
                                   Map<String,String> filter
                                   ){
        //                         String gfv, String sfv, String pfv, 
        //                         String ofv, String efv, String ffv){
        
        if ( tracContext.getPubDao() == null ) return null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getPubRecords: pubDao ok >" + sdir + "<"  );
        
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

        String sortKey ="";
        
        log.debug( "getPubRecords: skey " + skey);

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

            if(sortKey.equals( "" ) ){
                sortKey = skey;
            }
            
        } else {
            skey = "id";
            sortKey = "id";
        }

        List<Publication> pl = new ArrayList<Publication>();
        long total = 0;
        log.debug( "getPubRecords: " + filter);
        //gfv + " :: " + sfv + " :: "  + pfv + " :: " + efv + " :: " + ffv);

        Map<String,String> flt = new HashMap<String,String>();
        //flt.put( "stage", gfv );
        //flt.put( "status", sfv );
        //flt.put( "partner", pfv );
        //flt.put( "owner", ofv );
        //flt.put( "editor", efv );
        //flt.put( "cflag", ffv );
        
        //if ( gfv.equals("") && sfv.equals("") && pfv.equals("") && 
        //     ofv.equals("") && efv.equals("") && ffv.equals("") ){

        System.out.println("*");

        if ( filter.isEmpty() ){
            
            log.debug( "getPubRecords: unfiltered" );
            if( usr == null ){
                pl = tracContext.getPubDao()
                    .getPublicationList( first, blockSize, sortKey, asc );
                total = tracContext.getPubDao().getPublicationCount();
            } else {

                log.debug( "getting list" );
                pl = watchManager
                    .getPublicationList( usr, first, blockSize, sortKey, asc );

                log.debug( "getting count" );
                total = watchManager
                    .getPublicationCount( usr );
                log.debug( "got count" );
            }
            
        } else {
            
            log.debug( "getPubRecords: filtered" );
            
            if( usr == null ){
                pl = tracContext.getPubDao()
                    .getPublicationList( first, blockSize, 
                                         sortKey, asc, filter );            
                
                total = tracContext.getPubDao().getPublicationCount( filter );
            } else {
                pl = watchManager
                    .getPublicationList( usr, first, blockSize, 
                                         sortKey, asc, filter );
                total = watchManager
                    .getPublicationCount( usr, filter );
            }   
        }

        log.debug( "getPubRecords: total=" + total);

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

        for( Iterator<Publication> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub ip = (IcPub) ii.next();
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
            r.put( "stage", ip.getStage().getName() );
	    r.put( "state", ip.getState().getName() );
            r.put( "date", ip.getCreateDateString() );
            r.put( "time", ip.getCreateTimeString() );
            r.put( "editor", "N/A" );
            r.put( "imexDb", "N/A" );


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
                
                if( Integer.parseInt( year ) <1900 ||
                    Integer.parseInt( month ) < 1 ||
                    Integer.parseInt( day ) < 1 ){
                    return null;
                } else {

                    GregorianCalendar dateGC = 
                        new GregorianCalendar( Integer.parseInt( year ),
                                               Integer.parseInt( month )-1,
                                               Integer.parseInt( day ) );


                    return dateGC;                
                }
            }
        } catch( Exception ex ) {

            ex.printStackTrace();
            // ignore == parse to null
        }
        return null;        
    }

    //--------------------------------------------------------------------------
    
    private String sanitize( String str ){

        if( str != null ){
            try{
                str = str.replaceAll("^\\s+","");
                str = str.replaceAll("\\s+$","");
            } catch( Exception ex ){
                
            }
        } else {
            str = "";
        }
        return str;
    }
}
