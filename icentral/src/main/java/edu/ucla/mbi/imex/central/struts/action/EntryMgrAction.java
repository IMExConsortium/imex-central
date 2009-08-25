package edu.ucla.mbi.imex.central.struts.action;
        
                                                                            
/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * WorkflowMgrSupport action
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
        
        if ( tracContext.getPubDao() == null ) return null;
        
        List<Publication> pl = tracContext.getPubDao().getPublicationList();
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
            journal = (IcJournal) tracContext
                .getJournalDao().getJournal( getId() );  
            return SUCCESS;
        }

        if ( mode.equals( "icpub" ) && 
             getId() > 0 && icpub == null ) {
            
            log.info(  "setting icpub=" + getId() );
            icpub = (IcPub) tracContext.getPubDao().getPublication( getId() );
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
                // icpub operations
                //-----------------

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
    // operations: IcPub
    //------------------

    public String addIcPub( Publication pub ) {
        /*
        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;

        wflowContext.getWorkflowDao().saveTrans( trans );
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new trans -> id=" + trans.getId() +
                  " name=" + trans.getName() );

        this.trans = null;
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String deleteIcPub( Publication pub ) {
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteIcPubList( List<Integer> pubs ) {
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateIcPubProperties( int id, Publication pub ) {
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    private String updateIcPubState( int id, int sid) {
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
        return SUCCESS;
    }    
}
