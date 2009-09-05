package edu.ucla.mbi.imex.central.struts.action;
                                                                   
/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * EntryMgrAction - web interface to entry management
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class EntryMgrAction extends ManagerSupport {

    private final String PUBEDIT = "pubedit";
    private final String JEDIT = "jedit";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    //---------------------------------------------------------------------
    // Entry Manager
    //--------------

    private EntryManager entryManager;
    
    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }
    
    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    //---------------------------------------------------------------------
    //  TracContext
    //--------------

    private TracContext tracContext;
    
    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }
    
    public TracContext getTracContext() {
        return this.tracContext;
    }

    //---------------------------------------------------------------------
    //  WorkflowContext
    //-----------------
    
    private WorkflowContext wflowContext;

    public void setWorkflowContext( WorkflowContext context ) {
        this.wflowContext = context;
    }

    public WorkflowContext getWorkflowContext() {
        return this.wflowContext;
    }


    //---------------------------------------------------------------------
    // GroupAll list
    //--------------

    public List<Group> getGroupAll(){
        
        if ( getUserContext().getGroupDao() != null ) {
            return getUserContext().getGroupDao().getGroupList();
        }
        return null;
    }
    
    //---------------------------------------------------------------------
    //  mode: journal/icpub
    //---------------------

    private String mode = "journal";
    
    public String getMode() {
        return this.mode;
    }
    
    public void setMode( String mode ) {
        this.mode = mode;
    }

    
    //---------------------------------------------------------------------
    //  IcJournal
    //-----------
    
    private IcJournal journal = null;

    public void setJournal( IcJournal journal ) {
	this.journal = journal;
    }
    
    public IcJournal getJournal(){
	return this.journal;
    }
    
    //---------------------------------------------------------------------
    
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


    //---------------------------------------------------------------------
    // IcPub
    //------

    private IcPub icpub = null;
    
    public void setPub( IcPub pub ) {
        this.icpub = pub;
    }

    public IcPub getPub(){
        return this.icpub;
    }

    //---------------------------------------------------------------------
    
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
    
    //---------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.info(  "mode=" + mode + " id=" + getId() + 
                   " journal=" + journal + " icpub=" + icpub ); 
        
        if ( tracContext.getJournalDao() == null ||
             tracContext.getPubDao() == null ) return SUCCESS;
        
        if ( mode.equals( "journal" ) && 
             getId() > 0 && journal == null ) {
            
            log.info( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );  
            return SUCCESS;
        }

        if ( mode.equals( "icpub" ) && getId() > 0 && icpub == null ) {
            
            log.info(  "setting icpub=" + getId() );
            icpub = entryManager.getIcPub( getId() );
            return SUCCESS;
        }

        if( getOp() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            if ( val != null && val.length() > 0 ) {

                //---------------------------------------------------------
                // journal operations
                //-------------------
                
                if ( key.equalsIgnoreCase( "jadd" ) ) {
                    return addJournal( journal );
                }

                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "jdel" ) ) {
                    return deleteJournal( journal );
                }

                //---------------------------------------------------------

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

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jpup" ) ) {
                    return updateJournalProperties( getId(), journal );
                }
                
                //---------------------------------------------------------
                
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
                
                //---------------------------------------------------------
                
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

                //---------------------------------------------------------

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
                
                //---------------------------------------------------------

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
                

                //---------------------------------------------------------
                //---------------------------------------------------------
                // icpub operations
                //-----------------

                if ( key.equalsIgnoreCase( "esrc" ) ) {
                    return searchIcPub( icpub );
                }

                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "eadd" ) ) {
                    return addIcPub( icpub );
                }
                
                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "edel" ) ) {
                    return deleteIcPub( icpub );
                }

                //---------------------------------------------------------

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

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "epup" ) ) {
                    return updateIcPubProperties( getId(), icpub );
                }
                
                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "esup" ) ) {
                    int sid=0;
                    return updateIcPubState( getId(), sid );
                }

                //---------------------------------------------------------
                
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

                //---------------------------------------------------------
                
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

                //---------------------------------------------------------
                
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

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "eagdel" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String gdel = getOpp().get( "aegdel" );

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
            }
        }
        return SUCCESS;
    }


    //---------------------------------------------------------------------
    // validation
    //-----------
    
    public void validate() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgr: validate" );
        
        /*
        boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {

                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                  
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
                }
            }
        }
        
        if ( loadUserFlag && getId() > 0 ) {
            user = getUserContext().getUserDao().getUser( getId() );
            setBig( false );
        }
        */        
    }

    
    //---------------------------------------------------------------------
    // operations: Journal
    //----------------------

    public String addJournal( Journal journal ) {
        /*
        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;

        wflowContext.getWorkflowDao().saveDataState( state );
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new group -> id=" + state.getId() +
                  " name=" + state.getName() );

        this.state = null;
        */
        return SUCCESS;
    }


    //---------------------------------------------------------------------

    public String deleteJournal( Journal journal ) {
        /*
        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;
        
        DataState oldState = wflowContext.getWorkflowDao()
            .getDataState( state.getId() );
        if ( oldState == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " delete state -> id=" + oldState.getId() );
        wflowContext.getWorkflowDao().deleteDataState( oldState );        
        
        this.state = null;
        setId( 0 );
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteJournalList( List<Integer> ournals ) {
        /*
        if( wflowContext.getWorkflowDao() == null || 
            states == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        
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
    
    public String updateJournalProperties( int id, Journal journal ) {
        /*
        if( wflowContext.getWorkflowDao() == null ) return SUCCESS;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "id=" + id );

        DataState oldState = wflowContext.getWorkflowDao()
            .getDataState( id );
        if ( oldState == null ) return SUCCESS;

        oldState.setName( state.getName() );
        oldState.setComments( state.getComments() );
        
        wflowContext.getWorkflowDao().updateDataState( oldState );
        this.state = wflowContext.getWorkflowDao().getDataState( id );
        
        log.info( " updated state(props) -> id=" + id );
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String addJournalAdminGroup( int id, int grp ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "add JAG: id=" + id + " ag= " + grp );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        Group agrp = getUserContext().getGroupDao().getGroup( grp );

        if ( oldJournal != null && agrp != null ) {
            if ( testAcl( oldJournal,
                          ownerMatch, adminUserMatch, adminGroupMatch ) ) {
                
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
            if ( testAcl( oldJournal,
                          ownerMatch, adminUserMatch, adminGroupMatch ) ) {

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

            if ( testAcl( oldJournal,
                          ownerMatch, adminUserMatch, adminGroupMatch ) ) {
                
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

            if ( testAcl( oldJournal, ownerMatch, adminUserMatch, 
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

    public String searchIcPub( Publication pub ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( " search pub -> id=" + pub.getId() +
                  " pmid=" + pub.getPmid() );
        
        IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
        
        if ( oldPub != null ) {
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        }
        return SUCCESS;
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

        //protected Set<String> ownerMatch;
        //protected Set<String> groupMatch;

        if ( ownerMatch != null && ownerMatch.size() > 0 ) {

            // ACL target control 
            //-------------------
            
            

        }

        User owner = null;
        
        Integer usr = (Integer) getSession().get( "USER_ID" );
        log.info( " login id=" + usr );
        if ( usr != null ) {
            owner = getUserContext().getUserDao().getUser( usr.intValue() );
            log.info( " owner set to: " + owner );
        }

        DataState state =  
            wflowContext.getWorkflowDao().getDataState( "NEW" );
        log.info( " state set to: " + state );
        
        if ( owner != null && state != null ){
            icpub = entryManager.addIcPub( pub, owner, state );
            
            return PUBEDIT;
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

    //---------------------------------------------------------------------
    
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

    //---------------------------------------------------------------------
    
    private String updateIcPubState( int id, int sid) {
        
        entryManager.updateIcPubState( null );
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
    //---------------------------------------------------------------------

    private boolean testAcl( Journal jrnl ,
                             Set<String> owner, Set<String> aUser, 
                             Set<String> aGroup ) {
        try{
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "ACL Test: jrnl=" + jrnl + 
                  "\n           owner= " + owner +
                  "\n           ausr= " + aUser +
                  "\n           agrp= " + aGroup);

        if ( jrnl == null ) return false;
        if ( owner == null && aUser == null && aGroup == null ) return true;
              
        
        // owner match
        //------------
        
        if ( ownerMatch != null ) {
            if ( ownerMatch.contains( jrnl.getOwner() ) ) {
                log.info( "ACL Test: owner matched");
                return true;
            } 
        }
        
        log.info( "ACL Test: no owner match");
        
        // admin user match
        //-----------------

        if ( adminUserMatch != null ) {
            for( Iterator<User> oi =jrnl.getAdminUsers().iterator();
                 oi.hasNext(); ) {
                
                String usr = oi.next().getLogin();
                if ( adminUserMatch.contains( usr ) ) {
                    log.info( "ACL Test: ausr matched");
                    return true;
                }
            }
        }
        log.info( "ACL Test: no ausr match");

        // admin group match
        //------------------
                
        if ( adminGroupMatch != null ) {

            for( Iterator<Group> gi =jrnl.getAdminGroups().iterator();
                 gi.hasNext(); ) {
                
                String grp = gi.next().getLabel();
                if ( adminGroupMatch.contains( grp ) ) {
                    log.info( "ACL Test: agrp matched");
                    return true;
                }
            }
        }
        
        log.info( "ACL Test: no agrp match");
        return false;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
