package edu.ucla.mbi.util.struts2.action;
                                                                            
/* =========================================================================
 * $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-util-s#$
 * $Id:: UserMgrSupport.java 475 2009-08-21 23:46:54Z lukasz               $
 * Version: $Rev:: 475                                                     $
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
import edu.ucla.mbi.util.struts2.interceptor.*;

public abstract class WorkflowMgrSupport extends ManagerSupport {

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
    //  DataState
    //------------
    
    private DataState state = null;

    public void setDataState( DataState state ) {
	this.state = state;
    }
    
    public DataState getDataState(){
	return this.state;
    }
    
    //---------------------------------------------------------------------
    
    public List<DataState> getStateList(){
     
        if ( wflowContext.getWorkflowDao() == null ) return null;
        return wflowContext.getWorkflowDao().getDataStateList();
    }


    //---------------------------------------------------------------------
    // Transition
    //-----------

    private Transition trans = null;
    
    public void setTrans( Transition trans ) {
        this.trans = trans;
    }

    public Transition getTransition(){
        return this.trans;
    }

    //---------------------------------------------------------------------
    
    public List<Transition> getTransList(){
        
        if ( wflowContext.getWorkflowDao() == null ) return null;
        return wflowContext.getWorkflowDao().getTransList();
    }
    
    //---------------------------------------------------------------------

    public String execute() throws Exception{
        
        if( getOp() == null || 
            wflowContext.getWorkflowDao() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            if ( val != null && val.length() > 0 ) {

                //---------------------------------------------------------
                // state operations
                //-----------------
                
                if ( key.equalsIgnoreCase( "sed" ) &&
                     getId() > 0 && state == null ) {
                    state = wflowContext.getWorkflowDao()
                        .getDataState( getId() );
                    return SUCCESS;
                }

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "sadd" ) ) {
                    return addDataState( state );
                }

                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "sdel" ) ) {
                    return deleteDataState( state );
                }

                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "sldel" ) ) {

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
                        return deleteDataStateList( uidl );
                    }
                    return SUCCESS;
                }

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "spup" ) ) {
                    return updateDataStateProperties( getId(), state );
                }
                

                //---------------------------------------------------------
                // transition operations
                //----------------------

                if ( key.equalsIgnoreCase( "ted" ) &&
                     getId() > 0 && trans == null ) {
                    trans = wflowContext.getWorkflowDao()
                        .getTrans( getId() );
                    return SUCCESS;
                }

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "tadd" ) ) {
                    return addTrans( trans );
                }
                
                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "tdel" ) ) {
                    return deleteTrans( trans );
                }

                //---------------------------------------------------------

                if ( key.equalsIgnoreCase( "tldel" ) ) {

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
                        return deleteTransList( uidl );
                    }
                    return SUCCESS;
                }

                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "tpup" ) ) {
                    return updateTransProperties( getId(), trans );
                }
                
                //---------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "tsup" ) ) {


                    int fid=0;
                    int tid=0;

                    return updateTransStates( getId(), fid, tid );
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
        log.info( "WorkflowMgr: validate" );
        
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
    // operations: DataState
    //----------------------

    public String addDataState( DataState state ) {

        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;

        wflowContext.getWorkflowDao().saveDataState( state );
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new group -> id=" + state.getId() +
                  " name=" + state.getName() );

        this.state = null;
        return SUCCESS;
    }


    //---------------------------------------------------------------------

    public String deleteDataState( DataState state ) {
        
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteDataStateList( List<Integer> states ) {
        
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateDataStateProperties( int id, DataState state ) {

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
        return SUCCESS;
    }


    //---------------------------------------------------------------------
    // operations: Transition
    //-----------------------

    public String addTrans( Transition trans ) {

        if( wflowContext.getWorkflowDao() == null || 
            state == null ) return SUCCESS;

        wflowContext.getWorkflowDao().saveTrans( trans );
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new trans -> id=" + trans.getId() +
                  " name=" + trans.getName() );

        this.trans = null;
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String deleteTrans( Transition trans ) {
        
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteTransList( List<Integer> trans ) {
        
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateTransProperties( int id, Transition trans ) {
        
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
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    private String updateTransStates( int id, int fid, int tid ) {
        
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
        return SUCCESS;
    }    
}
