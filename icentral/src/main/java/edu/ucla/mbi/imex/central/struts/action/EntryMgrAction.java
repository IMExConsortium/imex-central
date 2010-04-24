package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * EntryMgrAction - web interface to entry management
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

public class EntryMgrAction extends ManagerSupport {

    private final String NOPUB = "notfound";
    private final String NOJOU = "notfound";
    private final String PUBEDIT = "pubedit";
    private final String PUBNEW = "pubnew";
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
    // GroupAll list
    //--------------

    public List<Group> getGroupAll(){
        
        if ( getUserContext().getGroupDao() != null ) {
            return getUserContext().getGroupDao().getGroupList();
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    //  mode: journal/icpub
    //---------------------

    private String mode = "journal";
    
    public String getMode() {
        return this.mode;
    }
    
    public void setMode( String mode ) {
        this.mode = mode;
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
    
    public List<IcPub> getPublicationList(){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "publist called..."  );
        
        if ( tracContext.getPubDao() == null ) return null;

        log.info( "getPublicationList: pubDao ok..."  );
        
        List<Publication> pl = tracContext.getPubDao().getPublicationList();

        log.info( "publist=" + pl );
        
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
        this.pmid = pmid;
    }
    
    public String getPmid(){
        return this.pmid;
    }

    
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.info(  "mode=" + mode + " id=" + getId() + 
                   " journal=" + journal + " icpub=" + icpub +
                   " op=" + getOp() ); 
        
        if ( tracContext.getJournalDao() == null ||
             tracContext.getPubDao() == null ) return SUCCESS;
        
        if ( mode.equals( "journal" ) && 
             getId() > 0 && journal == null ) {
            
            log.info( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );  
            return SUCCESS;
        }

        if ( mode.equals( "icpub" ) && getId() > 0 
             && icpub == null && getOp() == null ) {
            
            log.info(  "setting icpub=" + getId() );
            icpub = entryManager.getIcPub( getId() );
            return SUCCESS;
        } 
        
        if( getPmid() != null ) {

            Publication nicp = entryManager.getPubByPmid( getPmid() );
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
            
            log.info(  "op=" + key + "  val=" + val );

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

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jpup" ) ) {
                    return updateJournalProperties( getId(), journal );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jauadd" ) ) {
                    System.out.print("jauadd");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String ulogin = getOpp().get( "jauadd" );
                    try {
                        return addJournalAdminUser( getId(), ulogin );
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                        nfe.printStackTrace();
                    }
                    return SUCCESS;
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jagadd" ) ) {
                    System.out.print("jagadd");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String sgid = getOpp().get( "jagadd" );
                    try {
                        int gid = Integer.parseInt( sgid );
                        return addJournalAdminGroup( getId(), gid );
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jaudel" ) ) {
                    System.out.print("jaudel");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String udel = getOpp().get( "jaudel" );
                    
                    if ( getId() > 0 && udel != null ) {
                        try {
                             List<Integer> uidl =
                                 new ArrayList<Integer>();
                             String[] us = udel.split(",");

                             for( int ii = 0; ii <us.length; ii++ ) {
                                 uidl.add( Integer.valueOf( us[ii] ) );
                             }
                             return delJournalAdminUsers( getId(), uidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }   
                    } else {
                        journal = entryManager.getIcJournal( getId() );
                        setId( journal.getId() );
                    }
                    return SUCCESS;
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jagdel" ) ) {
                    System.out.print("jagdel");
                    if ( getOpp() == null ) return SUCCESS;

                    String gdel = getOpp().get( "jagdel" );

                    if ( getId() > 0 && gdel != null ) {
                        try {
                             List<Integer> gidl =
                                 new ArrayList<Integer>();
                             String[] gs = gdel.split(",");

                             for( int ii = 0; ii <gs.length; ii++ ) {
                                 gidl.add( Integer.valueOf( gs[ii] ) );
                             }
                             return delJournalAdminGroups( getId(), gidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }   
                    } else {
                        journal = entryManager.getIcJournal( getId() );
                        setId( journal.getId() );
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
                    String flt= getOpp().get( "flt" );

                    return this.getIcJournalRecords( max, off, 
                                                     skey, sdir, flt );
                }

                //--------------------------------------------------------------
                //--------------------------------------------------------------
                // icpub operations
                //-----------------

                if ( key.equalsIgnoreCase( "esrc" ) ) {
                    String imex = null ;
                    if ( getOpp() != null ) {
                        imex = getOpp().get( "imex" );
                    }
                    return searchIcPub( icpub, imex );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "eadd" ) ) {
                    return addIcPub( icpub );
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "edel" ) ) {
                    return deleteIcPub( icpub );
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
                        return deleteIcPubList( uidl );
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "epup" ) ) {
                    return updateIcPubProperties( getId(), icpub );
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "epix" ) ) {
                    return genIcPubImex( getId(), icpub );
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "eidu" ) ) {

                    // update identifiers
                    //-------------------

                    return updateIcPubIdentifiers( getId(), icpub );
                }

                //--------------------------------------------------------------
         
                if ( key.equalsIgnoreCase( "eatu" ) ) {

                    // update author/title
                    //--------------------

                    return updateIcPubAuthTitle( getId(), icpub );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "edup" ) ) {

                    if ( getOpp() == null ) return SUCCESS;
                    
                    // parse dates
                    //------------

                    GregorianCalendar epGD = parseDate( getOpp().get( "epd" ) );
                    GregorianCalendar pGD = parseDate( getOpp().get( "pd" ) );
                    GregorianCalendar rGD = parseDate( getOpp().get( "rd" ) );
                    
                    return updateIcPubDates( getId(), epGD, pGD, rGD );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "esup" ) ) {

                    int sid=0;
                    
                    if ( getOpp() == null ) return SUCCESS;
                    String nsid = getOpp().get( "nsn" );
                    
                    try {
                        sid = Integer.parseInt(nsid);
                    } catch ( Exception ex ) {
                        // should not happen
                        return SUCCESS;
                    }
                    
                    return updateIcPubState( getId(), sid );
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eauadd" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String ulogin = getOpp().get( "eauadd" );
                    try {
                        return addIcPubAdminUser( getId(), ulogin );
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eagadd" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String sgid = getOpp().get( "eagadd" );
                    try {
                        int gid = Integer.parseInt( sgid );
                        return addIcPubAdminGroup( getId(), gid );
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eaudel" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String udel = getOpp().get( "eaudel" );

                    if ( getId() > 0 && udel != null ) {
                        try {
                             List<Integer> uidl =
                                 new ArrayList<Integer>();
                             String[] us = udel.split(",");

                             for( int ii = 0; ii <us.length; ii++ ) {
                                 uidl.add( Integer.valueOf( us[ii] ) );
                             }
                             return delIcPubAdminUsers( getId(), uidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                    } else {
                        icpub = entryManager.getIcPub( getId() );
                        setId( icpub.getId() );
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eagdel" ) ) {
                    if ( getOpp() == null ) return SUCCESS;

                    String gdel = getOpp().get( "eagdel" );
                    log.info("eagdel=" + gdel);

                    if ( getId() > 0 && gdel != null ) {
                        try {
                             List<Integer> gidl =
                                 new ArrayList<Integer>();
                             String[] gs = gdel.split(",");

                             for( int ii = 0; ii <gs.length; ii++ ) {
                                 gidl.add( Integer.valueOf( gs[ii] ) );
                             }
                             return delIcPubAdminGroups( getId(), gidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                    } else {
                        icpub = entryManager.getIcPub( getId() );
                        setId( icpub.getId() );
                    }
                    return SUCCESS;
                }

                if ( key.equalsIgnoreCase( "ppg" ) ) {

                    log.info(  "\n\nop=" + getOp() );
                    log.info(  "opp=" + getOpp() );
                    if ( getOpp() == null ) {
                        return getIcPubRecords();
                    }
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    
                    String sfv = getOpp().get( "sfv" ) == null ? 
                        "" :  getOpp().get( "sfv" );
                    String pfv = getOpp().get( "pfv" ) == null ?
                        "" :  getOpp().get( "pfv" );;
                    String efv = getOpp().get( "efv" ) == null ?
                        "" :  getOpp().get( "efv" );
                    
                    return getIcPubRecords( max, off, skey, sdir,
                                            sfv, pfv, efv );
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
        log.info( "EntryMgr: validate" );
       
        //boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                    
                    log.info( " op=" + val);
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
                            log.info( " pmid=" + pmid +"<");
                            if( pmid.length() == 0  ) {
                                log.info( "  empty PMID field" );
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
                    

                    /*
                      
                    //-----------------------------------------------------
                    
                    if ( key.equalsIgnoreCase( "add" ) ) {
                                            
                        // add user validation
                        //--------------------
                        
                        // login: unique
                        //---------------
                        
                        String newLogin = 
                            sanitizeString( user.getLogin() );
                        
                        if ( newLogin != null ) {

                            // test if unique
                            //---------------
                            
                            if ( getUserContext().getUserDao() != null &&
                                 getUserContext().getUserDao().getUser( newLogin ) != null ) {
                                
                                newLogin = null;
                                user.setLogin( newLogin );
                            }
                        }                                
                        
                        if ( newLogin != null ) {
                            user.setLogin( newLogin );
                        } else {
                            addFieldError( "user.login", 
                                           "User Login must be unique." );
                        }


                        // first name: non-empty
                        //-----------------------

                        String newFirstName = 
                            sanitizeString( user.getFirstName() );
                        
                        if ( newFirstName != null ) {
                            user.setFirstName( newFirstName );
                        } else {
                            addFieldError( "user.firstName", 
                                           "First name field" +
                                           " cannot be empty." );
                        }   

                        // last name: non-empty
                        //---------------------

                        String newLastName = 
                            sanitizeString( user.getLastName() );
                        
                        if ( newLastName != null ) {
                            user.setLastName( newLastName );
                        } else {
                            addFieldError( "user.lastName", 
                                           "Last name field" +
                                           " cannot be empty." );
                        }   
                        
                        // email: non-empty
                        //-----------------

                        String newEmail = 
                            sanitizeString( user.getEmail() );
                        
                        if ( newEmail != null ) {
                            user.setEmail( newEmail );
                        } else {
                            addFieldError( "user.email", 
                                           "E-mail field" +
                                           " cannot be empty." );
                        }
                        break;
                    }
                    
                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "del" ) ) {
                        // user drop validation: NONE ?                        
                        break;
                    }

                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "ldel" ) ) {
                        // user list drop validation: NONE ?                        
                        break;
                    }

                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "pup" ) ) {

                        if ( user == null || 
                             getUserContext().getUserDao() == null )  return;
                        
                        // user property update validation                       
                        //---------------------------------
                       
                        // first name: non-empty
                        //-----------------------

                        String newFirstName = 
                            sanitizeString( user.getFirstName() );
                        
                        if ( newFirstName != null ) {
                            user.setFirstName( newFirstName );
                        } else {
                            addFieldError( "user.firstName", 
                                           "First name field" +
                                           " cannot be empty." );
                        }   

                        // last name: non-empty
                        //---------------------

                        String newLastName = 
                            sanitizeString( user.getLastName() );
                        
                        if ( newLastName != null ) {
                            user.setLastName( newLastName );
                        } else {
                            addFieldError( "user.lastName", 
                                           "Last name field" +
                                           " cannot be empty." );
                        }   
                        
                        // email: non-empty
                        //-----------------

                        String newEmail = 
                            sanitizeString( user.getEmail() );
                        
                        if ( newEmail != null ) {
                            user.setEmail( newEmail );
                        } else {
                            addFieldError( "user.email", 
                                           "E-mail field" +
                                           " cannot be empty." );
                        }
                        
                        break;
                    }
                    
                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "sup" ) ) {
                        // user status update validation                       
                        break;
                    }

                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "prs" ) ) {
                        // user password reset validation                       
                        break;
                    }
                    
                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "radd" ) ) {
                        // user role add validation: NONE
                        break;
                    }
                    
                    //-----------------------------------------------------
                    
                    if ( key.equalsIgnoreCase( "rdel" ) ) {
                        // user role drop validation: NONE
                        break;
                    }

                    //-----------------------------------------------------

                    if ( key.equalsIgnoreCase( "gadd" ) ) {
                        // user group add validation: NONE
                        break;
                    }
                    
                    //-----------------------------------------------------
                    
                    if ( key.equalsIgnoreCase( "gdel" ) ) {
                        // user group drop validation: NONE                                              
                        break;
                    }

                    */
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
        log.info( " search journal -> nlmid=" + nlmid );
        
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
        log.info( " new jrnl -> nlmid=" + nlmid );
        
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
        log.info( " login id=" + usr );

        User owner = 
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null ) return ACL_OPER;
        log.info( " owner set to: " + owner );
        
        IcJournal newJnrl = entryManager.addIcJournal( nlmid, owner );
        if ( newJnrl != null ) {
            journal = newJnrl;
            setId( newJnrl.getId() );
            return JEDIT;
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
                                     
            log.info( " delete state -> id=" + s.getId() );
            wflowContext.getWorkflowDao().deleteDataState( s );                
        }
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateJournalProperties( int id, Journal jrnl ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "update journal: id=" + id );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal == null || journal == null ) return SUCCESS;
        
        entryManager.updateIcJournal( oldJournal, jrnl );
        
        journal = entryManager.getIcJournal( id );
        setId( journal.getId() );
        
        return JEDIT;
    }

    //---------------------------------------------------------------------

    public String addJournalAdminGroup( int id, int grp ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "add JAG: id=" + id + " ag= " + grp );
                
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
        log.info( "drop JAG: id=" + id + " aglist= " + gidl );
        
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
        log.info( "add JAG: id=" + id + " au= " + ulogin );
                
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
        log.info( "drop EAG: id=" + id + " aglist= " + uidl );

        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal != null && uidl != null ) {

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                log.info( "ACL test passed");
                
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
    //---------------------------------------------------------------------
    // operations: IcPub
    //------------------

    public String searchIcPub( Publication pub, String imex ) {

        Log log = LogFactory.getLog( this.getClass() );

        if( pub != null ) {
            log.info( " search pub -> id=" + pub.getId() +
                      " pmid=" + pub.getPmid() );
        } else {  
            log.info( " search pub -> imex=" + imex );
        }        

        try {
            imex = imex.replaceAll("\\D+", "" );
        } catch ( Exception ex ) {
            // skip error
        }

        if ( imex != null && !imex.equals("") ) {
            IcPub oldPub = entryManager.getIcPubByIcKey( imex );

            if ( oldPub != null ) {
                icpub = oldPub;
                setId( oldPub.getId() );
                return PUBEDIT;
            }
        }

        IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
        
        if ( oldPub != null ) {
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        }
        
        if( pub !=null && pub.getPmid() != null && 
            !pub.getPmid().equals("") ) {
            this.setPmid( pub.getPmid() );

            return PUBNEW;
        }
        
        addActionError( "No publication found" ) ;
        return NOPUB;
    }
    
    //---------------------------------------------------------------------
    
    public String addIcPub( Publication pub ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new pub -> id=" + pub.getId() +
                  " pmid=" + pub.getPmid() );

        // test if already in 
        //-------------------
        
        IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
        
        if ( oldPub != null ) {
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        }
        
        // ACL target control NOTE: implement as needed
        //---------------------------------------------
        /* if ( ownerMatch != null && ownerMatch.size() > 0 ) { } */
        
        Integer usr = (Integer) getSession().get( "USER_ID" );
        if ( usr == null )  return ACL_OPER;
        log.info( " login id=" + usr );
        
        User owner = 
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null )  return ACL_OPER;
        log.info( " owner set to: " + owner );
        
        DataState state =  
            wflowContext.getWorkflowDao().getDataState( "NEW" );
        log.info( " state set to: " + state );
        
        if ( state != null ) {
            IcPub newPub = entryManager.addIcPub( pub, owner, state );
            if ( newPub != null ) {
                icpub = newPub;
                setId( newPub.getId() );
                return PUBEDIT;
            }
        }
        
        return SUCCESS;
    }
    
    //---------------------------------------------------------------------

    public String deleteIcPub( Publication pub ) {

        entryManager.deleteIcPub( null );
        return SUCCESS;

        /*
        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;
        
        Transition oldTrans = wflowContext.getWorkflowDao()
            .getTrans( trans.getId() );
        if ( oldTrans == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " delete trans -> id=" + oldTrans.getId() );
        wflowContext.getWorkflowDao().deleteTrans( oldTrans );        
        
        this.trans = null;
        setId( 0 );
        */
    }

    //---------------------------------------------------------------------

    private String deleteIcPubList( List<Integer> pubs ) {
        
        entryManager.deleteIcPub( null );
        return SUCCESS;

        /*
        if( wflowContext.getWorkflowDao() == null || 
            trans == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = trans.iterator();
              ii.hasNext(); ) {
            
            int gid = ii.next();
            Transition t = wflowContext.getWorkflowDao()
                .getTrans( gid );
                                     
            log.info( " delete trans -> id=" + t.getId() );
            wflowContext.getWorkflowDao().deleteTrans( t );                
        }
        */
    }

    //--------------------------------------------------------------------------
    
    public String updateIcPubProperties( int id, Publication pub ) {

        entryManager.updateIcPubProps( null );
        return SUCCESS;

        /*
        if( wflowContext.getWorkflowDao() == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "id=" + id );

        Transition oldTrans = wflowContext.getWorkflowDao()
            .getTrans( id );
        if ( oldTrans == null ) return SUCCESS;
        
        oldTrans.setName( trans.getName() );
        oldTrans.setComments( trans.getComments() );
        
        wflowContext.getWorkflowDao().updateTrans( oldTrans );
        this.trans = wflowContext.getWorkflowDao().getTrans( id );
        
        log.info( " updated trans(props) -> id=" + id );
        */
    }

    //--------------------------------------------------------------------------

    public String updateIcPubIdentifiers( int id, Publication pub ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( " updateIcPubIds: id=" + id );
        
        IcPub uPub =  entryManager.updateIcPubIdentifiers( id, pub );
        if( uPub != null ) {
            this.setPub( uPub );
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    public String updateIcPubAuthTitle( int id, Publication pub ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( " updateIcPubAuthTitle: id=" + id );

        IcPub uPub =  entryManager.updateIcPubAuthTitle( id, pub );
        if( uPub != null ) {
            this.setPub( uPub );
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------


    public String genIcPubImex( int id, IcPub pub ) {

        IcPub oldPub = entryManager.genIcPubImex( pub );
        
        if ( oldPub != null ) {
            icpub = oldPub;
            setId( oldPub.getId() );
            this.setPmid( oldPub.getPmid() );
            return JSON;        
        }

        return JSON;
    }


    //--------------------------------------------------------------------------
    
    private String updateIcPubContactMail( int id, String mail ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "id=" + id + " mail=" + mail );

        IcPub pub =  entryManager.updateIcPubContactMail( id, mail );
        if( pub != null ) {
            this.setPub( pub );
        }        
        return SUCCESS;
    }


    private String updateIcPubDates( int  id, GregorianCalendar epd, 
                                     GregorianCalendar pd, 
                                     GregorianCalendar rd ) {
        
        IcPub pub = entryManager.updateIcPubDates( id, epd, pd, rd );
        if ( pub != null ) {
            this.setPub( pub );
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------
    
    private String updateIcPubState( int id, int sid) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "id=" + id + " sid=" + sid );

        IcPub pub =  entryManager.updateIcPubState( id, sid );
        if( pub != null ) {
            this.setPub( pub );
        }        
        return SUCCESS;

        /*
        if ( wflowContext.getWorkflowDao() == null ||
             !( id > 0 && fid > 0 && tid > 0)) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "id=" + id + " from=" + fid + " to=" + tid );
        
        Transition oldTrans = wflowContext.getWorkflowDao().getTrans( id );
        DataState fState = wflowContext.getWorkflowDao().getDataState( fid );
        DataState tState = wflowContext.getWorkflowDao().getDataState( tid );
                
        if ( oldTrans == null || 
             fState == null || tState == null ) return SUCCESS;
        
        oldTrans.setFromState( fState );
        oldTrans.setToState( tState );        
        wflowContext.getWorkflowDao().updateTrans( oldTrans );
        
        this.trans = wflowContext.getWorkflowDao().getTrans( id );
        log.info( "updated trans(states)=" +this.trans );
        */
    }    

    //---------------------------------------------------------------------
    
    public String addIcPubAdminGroup( int id, int grp ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "add EAG: id=" + id + " ag= " + grp );
                
        IcPub oldPub = entryManager.getIcPub( id );
        Group agrp = getUserContext().getGroupDao().getGroup( grp );

        if ( oldPub != null && agrp != null ) {
            
            entryManager.addAdminGroup( oldPub, agrp );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delIcPubAdminGroups( int id, List<Integer> gidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "drop EAG: id=" + id + " aglist= " + gidl );

        IcPub oldPub = entryManager.getIcPub( id );
        if ( oldPub != null && gidl != null ) {
            
            entryManager.delAdminGroups( oldPub, gidl );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String addIcPubAdminUser( int id, String ulogin ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "add EAU: id=" + id + " au= " + ulogin );
                
        IcPub oldPub = entryManager.getIcPub( id );
        User ausr = getUserContext().getUserDao().getUser( ulogin );

        if ( oldPub != null && ausr != null ) {
            
            entryManager.addAdminUser( oldPub, ausr );
            icpub = entryManager.getIcPub( id );
            setId( icpub.getId() );
            
            return PUBEDIT;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delIcPubAdminUsers( int id, List<Integer> uidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "drop EAU: id=" + id + " aulist= " + uidl );

        IcPub oldPub = entryManager.getIcPub( id );
        if ( oldPub != null && uidl != null ) {

            entryManager.delAdminUsers( oldPub, uidl );
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

    public String getIcPubRecords() {
        return this.getIcPubRecords( "", "", "", "", "", "", "" );
    }
    
    public String getIcPubRecords( String max, String off, 
                                   String skey, String sdir, 
                                   String sfv, String pfv, String efv ) {

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
        }

        if ( sdir != null && sdir.equals( "false" ) ) {
            asc = false;
        } else {
            sdir ="true";
        }
        
        String sortKey ="id";
        
        if ( skey != null ) {
            if ( skey.equals( "pub" ) ) {
                sortKey ="author";
            }
            if ( skey.equals( "date" ) ) {
                sortKey ="crt";
            }
        } else {
            skey = "id";
            sortKey = "id";
        }

        List<Publication> pl = new ArrayList<Publication>();
        long total = 0;
        log.info( "getPubRecords: " + sfv + " :: " + pfv + " :: " + efv);
        if ( sfv.equals("") && pfv.equals("") && efv.equals("") ) {
            
            log.info( "getPubRecords: unfiltered" );
            
            pl = tracContext.getPubDao()
                .getPublicationList( first, blockSize, sortKey, asc );
            total = tracContext.getPubDao().getPublicationCount();

        } else {
            
            log.info( "getPubRecords: filtered" );

            Map<String,String> flt = new HashMap<String,String>();

            flt.put("status", sfv);
            flt.put("partner", pfv);
            flt.put("editor", efv);
            
            pl = tracContext.getPubDao()
                .getPublicationList( first, blockSize, sortKey, asc, flt );            

            total = tracContext.getPubDao().getPublicationCount( flt );


        }

        log.info( "getPubRecords: total=" + total);

        // buid record map
        //----------------
        
        records = new HashMap<String,Object>();
        records.put("recordsReturned", pl.size() );
        records.put("totalRecords", total );
        records.put("startIndex", first );
        records.put("sort", skey );
        records.put("dir", sdir );
        records.put("pageSize", max );

        List<Map<String,Object>> rl = new ArrayList<Map<String,Object>> ();
        records.put("records", rl );

        for( Iterator<Publication> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub ip = (IcPub) ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ip.getId() );
            r.put( "pmid", ip.getPmid() );
            r.put( "imexId", ip.getImexId() );
            r.put( "title", ip.getTitle() );
            r.put( "author", ip.getAuthor() );
            r.put( "owner", ip.getOwner().getLogin() );
            r.put( "state", ip.getState().getName() );
            r.put( "date", ip.getCreateDateString() );
            r.put( "time", ip.getCreateTimeString() );
            r.put( "editor", "[N/A]" );
            r.put( "imexDb", "[N/A]" );

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
                    //log.info( "r:" + role.getName() );
                }
                //log.info( "g:" + g.getLabel() );
                
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
                    //log.info( "r:" + role.getName() );
                }
                //log.info( "u:" + u.getLogin() );
                
            }
            if ( !editor.equals("") ) {
                r.put( "editor", editor.substring(0,editor.length()-1 ) );
            }
            
            rl.add( r );
        }
        
        return JSON;
    }
    
    //---------------------------------------------------------------------

    public String getIcJournalRecords() {
        return this.getIcJournalRecords( "", "", "", "", "" );
    }
    
    public String getIcJournalRecords( String max, String off, 
                                       String skey, String sdir, 
                                       String flt ) {
        
        if ( tracContext.getJournalDao() == null ) return null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "getJournalRecords: journalDao ok..."  );
        
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

    private GregorianCalendar parseDate( String date ) {
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "parseDate: " + date );
        
        // FORMAT: 2004/[0]3/12
        //----------------------

        try{
            Pattern p = Pattern.compile("(\\d{4}?)/(\\d\\d?)/(\\d\\d?)");
            Matcher m = p.matcher(date);
            if (  m.matches() ) {

                String year = m.group(1);
                String month = m.group(2);
                String day = m.group(3);
                
                log.info( "Y=" + year + " M=" + month + " D=" + day );
                
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
