package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * EntryEditorAction - Action supporting viewing and editing of individual
 *                     publication entry records
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

public class EntryEditorAction extends ManagerSupport implements LogAware{

    private final String NOPUB = "notfound";
    private final String PUBEDIT = "pubedit";
    private final String PUBNEW = "pubnew";
    private final String JSON = "json";
    private final String INPUT = "input";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";

    public static final String STATUS_POPUP = "pub-status-popup";

    
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
    
    private IcWorkflowContext wflowContext;

    public void setWorkflowContext( IcWorkflowContext context ) {
        this.wflowContext = context;
    }

    public IcWorkflowContext getWorkflowContext() {
        return this.wflowContext;
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


    private User luser = null;
    public User  getLuser(){
        return this.luser;
    }
    

    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " icpub=" + icpub + " op=" + getOp() ); 
        
        if ( tracContext.getPubDao() == null ) return SUCCESS;

        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
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
                icpub.setPmid( getPmid() );
                return NOPUB;
            }
        }
        
        // entry Id must be specified

        if( (getId() <= 0 && getPmid() == null && icpub == null) 
            || getOp() == null ) return SUCCESS;
        
        if ( getId() > 0 && icpub == null ) {

            log.debug("getting pub: id=" + getId());
            icpub = entryManager.getIcPub( getId() );
        }
        
        log.debug( "scanning ops..." );

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

                if ( key.equalsIgnoreCase( "popup" ) ) {
                    if( val != null && val.equalsIgnoreCase("status") ){
                        return STATUS_POPUP;                    
                    }
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eadd" ) ) {
                    return addIcPub( icpub );
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "etsl" ) ) {
                    if( icpub != null ) {
                        return getTargetStates( icpub, val );
                    }
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
            }
        }
        return SUCCESS;
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
        
        DataState stage =  
            wflowContext.getWorkflowDao().getDataStage( "PREQUEUE" );
        log.debug( " stage set to: " + stage );

        DataState state =  
            wflowContext.getWorkflowDao().getDataState( "NEW" );
        log.debug( " state set to: " + state );
        
        if ( stage != null &&  state != null) {
            try{
                IcPub newPub = entryManager
                    .addIcPub( pub, owner, stage, state );
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
            pub = entryManager.updateIcPubIdentifiers( pub, user, pub );
        } catch( EntryException ex ){
            this.statCode = ex.getStatusCode();
            this.statMessage = ex.getStatusMessage();
            
            // reset pub
            
            this.setPub( entryManager.getIcPub( pub.getId() ));
            
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
            // this.setPmid( oldPub.getPmid() );  LS: not sure if needed
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

	log.debug( "pub: " + pub );

        if( pub != null ){

	    log.debug( "aclTargetValidate: " + aclTargetValidate( pub ) );

            if( ! aclTargetValidate( pub ) ) return ACL_OPER;

            IcPub uPub =  entryManager.updateIcPubState( pub, user, sid );
            if( uPub != null ) {
                this.setPub( uPub );
            }        

	    log.debug( "uPub: " + uPub);

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
    //---------------------------------------------------------------------

    private boolean getWatchStatus( User usr, Publication pub ){ 
        return watchManager.getWatchStatus( usr, pub );       
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
