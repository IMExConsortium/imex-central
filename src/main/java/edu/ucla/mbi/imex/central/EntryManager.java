package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # EntryManager - businness logic of entry/journal management 
 #                 
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

import java.util.GregorianCalendar;
import java.util.Calendar;
       
import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

public class EntryManager {
    
    public EntryManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "EntryManager: creating manager" );
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
    //  UserContext
    //--------------
    
    private UserContext userContext;

    public void setUserContext( UserContext context ) {
        this.userContext = context;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }

    //---------------------------------------------------------------------
    // KeyContext
    //-----------

    private KeyspaceContext keyspaceContext;

    public void setKeyspaceContext( KeyspaceContext context ) {
        this.keyspaceContext = context;
    }

    public KeyspaceContext getKeyspaceContext() {
        return this.keyspaceContext;
    }
    
    //---------------------------------------------------------------------
    
    boolean debug = false;

    public boolean getDebug() {
        return debug;
    }
    
    public void setDebug( boolean debug ) {
        this.debug = debug;
    }
    
    //---------------------------------------------------------------------

    public void initialize(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryManager: initializing" );
    }


    //---------------------------------------------------------------------
    // Operations
    //---------------------------------------------------------------------
    // IcPub management
    //-----------------
    
    public IcPub getIcPub( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get pub -> id=" + id );
        
        IcPub oldPub =  (IcPub) tracContext.getPubDao()
            .getPublication( id );

        return oldPub;
    }

    //---------------------------------------------------------------------
    
    public IcPub getIcPubByPmid( String pmid ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get pub -> pmid=" + pmid );
        
        // test if already in
        //-------------------

        IcPub oldPub =  (IcPub) tracContext.getPubDao()
            .getPublicationByPmid( pmid );
        
        return oldPub;
    }
    
    //--------------------------------------------------------------------------

    public IcPub getIcPubByIcKey( String key ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get pub -> key=" + key );
        
        // test if already in
        //-------------------

        IcPub oldPub =  (IcPub) tracContext.getPubDao()
            .getPublicationByKey( key );
        
        return oldPub;
    }

    //--------------------------------------------------------------------------

    public long getPubCountByStatus( DataState state ){
        Map<String, String> crit = new HashMap<String, String>();
        crit.put( "status", state.getName());

        return tracContext.getPubDao().getPublicationCount( crit );
    }

    //--------------------------------------------------------------------------

    public List<IcPub> getPublicationByStatus( DataState state,
                                               Integer firstRec,
                                               Integer maxRec ){
        if( firstRec == null || firstRec.intValue() < 0) 
            firstRec = new Integer(0);
        
        if( maxRec == null || maxRec.intValue()<=0 ) maxRec = 5;
        
        String skey ="id";
        boolean asc = true;

        Map<String, String> crit = new HashMap<String, String>();
        crit.put( "status", state.getName());

        List<Publication> pl = tracContext.getPubDao()
            .getPublicationList( firstRec.intValue(), maxRec.intValue(), 
                                 skey, asc, crit);
        
        if( pl != null ){
            List<IcPub> res = new ArrayList<IcPub>();
            for( Iterator<Publication> ip = pl.iterator(); ip.hasNext(); ){
                Publication cp = ip.next(); 
                if( cp instanceof IcPub ){
                    res.add( (IcPub) cp );
                }
            }
            if( res.size() > 0 ) return res;
        }
        
        return null;
    }

    //--------------------------------------------------------------------------
    
    public long getPubCountByOwner( User owner ){
        
        Map<String, String> crit = new HashMap<String, String>();
        crit.put( "owner", owner.getLogin());

        return tracContext.getPubDao().getPublicationCount( crit );        
    }

    //--------------------------------------------------------------------------

    public List<IcPub> getPublicationByOwner( User owner,
                                              Integer firstRec,
                                              Integer maxRec ){
        
        if( firstRec == null || firstRec.intValue() < 0) 
            firstRec = new Integer(0);
        
        if( maxRec == null || maxRec.intValue()<=0 ) maxRec = 5;
        
        
        String skey ="id";
        boolean asc = true;

        Map<String, String> crit = new HashMap<String, String>();
        crit.put( "owner", owner.getLogin());

        List<Publication> pl = tracContext.getPubDao()
            .getPublicationList( firstRec.intValue(), maxRec.intValue(),
                                 skey, asc, crit);
        
        if( pl != null ){
            List<IcPub> res = new ArrayList<IcPub>();
            for( Iterator<Publication> ip = pl.iterator(); ip.hasNext(); ){
                Publication cp = ip.next(); 
                if( cp instanceof IcPub ){
                    res.add( (IcPub) cp );
                }
            }
            if( res.size() > 0 ) return res;
        }
        
        return null;
    }

    //--------------------------------------------------------------------------
    // autocompletion
    //---------------

    public List<User> acomOwner( String query ) {
        List<User> ulist 
            = tracContext.getPubDao().getOwners( query ); 
        return ulist;
    }

    public List<User> acomCurator( String query ) {
        List<User> ulist 
            = tracContext.getPubDao().getAdminUsers( query ); 
        return ulist;
    }
    
    public List<DataState> acomStatus( String query ) {
        List<DataState> ulist 
            = tracContext.getPubDao().getStates( query ); 
        return ulist;
    }
    
    public List<Group> acomGroup( String query ) {
        List<Group> ulist 
            = tracContext.getPubDao().getAdminGroups( query ); 
        return ulist;
    }


    //--------------------------------------------------------------------------
    // get through proxy
    //------------------

    public Publication getPubByPmid( String pmid ) {

        NcbiProxyClient cli = tracContext.getNcbiProxyClient();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " NcbiProxyClient=" + cli );

        if ( cli != null ) {
            Publication newPub =
                cli.getPublicationByPmid( pmid );
            
            return newPub;
        } 
        return null;
    }

    //--------------------------------------------------------------------------
    
    public IcPub addIcPub( Publication pub, User owner, DataState state ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if ( pub == null ) {
            log.info( " new pub -> null");
            return null;
        }

        if( pub.getPmid() != null && ! pub.getPmid().equals( "" ) ) {
        
            log.info( " new pub -> pmid=" + pub.getPmid() );
        
            return addPmidIcPub( pub, owner, state );
            
        } else {
            if( pub != null 
                && pub.getAuthor() != null && pub.getTitle() != null ){
                
                try {
                    pub.setAuthor( pub.getAuthor().replaceAll( "^\\s+", "" ) );
                    pub.setAuthor( pub.getAuthor().replaceAll( "\\s+$", "" ) );
                    
                    pub.setTitle( pub.getTitle().replaceAll( "^\\s+", "" ) );
                    pub.setTitle( pub.getTitle().replaceAll( "\\s+$", "" ) );
                } catch (PatternSyntaxException pse ) {
                    // should not happen
                }
                
                if ( pub.getAuthor().length() > 0 
                     && pub.getTitle().length() > 0 ) {
                    
                    IcPub icp = new IcPub( pub );
                    log.info( " IcPub=" + icp );
                    
                    
                    IcJournal icj = (IcJournal) tracContext.getJournalDao()
                        .getJournal( "UNPUBLISHED" );
                    icp.setSource( icj );
                    
                    icp.setOwner( owner ) ;
                    icp.setState( state );
                    
                    // set admin user/group sets
                    //--------------------------
                    
                    if ( icp.getSource() != null ) {
                        icp.getAdminUsers()
                            .addAll( icp.getSource().getAdminUsers() );
                        icp.getAdminGroups()
                            .addAll( icp.getSource().getAdminGroups() );
                    }

                    return (IcPub) 
                        tracContext.getPubDao().savePublication( icp );
                    
                    //return icp;
         
                    //return (IcPub) tracContext.getPubDao()
                    //    .getPublicationByPmid( icp.getPmid() );
                }
            }                
        }
        return null;
    }

    //--------------------------------------------------------------------------

    public IcPub addPmidIcPub( Publication pub, User owner, DataState state ) {

        Log log = LogFactory.getLog( this.getClass() );
        
        // test if already in 
        //-------------------

        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublicationByPmid( pub.getPmid() );
        
        if ( oldPub != null ) return oldPub;        
        
        // get through proxy
        //------------------        
        
        NcbiProxyClient cli = tracContext.getNcbiProxyClient();
        
        log.info( " NcbiProxyClient=" + cli );

        if ( cli != null ) {
            Publication newPub = 
                cli.getPublicationByPmid( pub.getPmid() );
            
            if ( newPub != null ) {
                IcPub icp = new IcPub( newPub );
                log.info( " IcPub=" + icp );
                
                if( icp.getSource() == null ) {
                    
                    log.info( " IcPub: no source" );

                } else {
                    Journal j = (Journal) icp.getSource();
                    
                    IcJournal icj = (IcJournal) tracContext.getJournalDao()
                        .getJournalByNlmid( j.getNlmid() );
                    
                    if ( icj == null ) {
                        icj = this.addIcJournal( j, owner );
                    }
                    
                    if ( icj == null ) return null;
                    icp.setSource( icj );
                }
                
                icp.setOwner( owner ) ;
                icp.setState( state );
                 
                // set admin user/group sets
                //--------------------------
                
                if ( icp.getSource() != null ) {
                    icp.getAdminUsers()
                        .addAll( icp.getSource().getAdminUsers() );
                    icp.getAdminGroups()
                        .addAll( icp.getSource().getAdminGroups() );
                }
                
                return (IcPub) tracContext.getPubDao().savePublication( icp );  
            }
        }                
        return null;
    }

    //--------------------------------------------------------------------------

    public void deleteIcPub( IcPub pub, User user ) {
        
    }

    //--------------------------------------------------------------------------

    public IcPub updateIcPubProps( IcPub pub, User luser ) {

        if( pub == null ) return pub;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryManager.updateIcPubProps: pub id=" + pub.getId()) ;

        tracContext.getPubDao().updatePublication( pub );

        return pub;
    }
    
    //--------------------------------------------------------------------------

    public List<String> getTargetStates( IcPub pub, String mode ) {

        List<String> stateList = new ArrayList<String>();

        if( pub == null ) { return stateList; }
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " EntryManager.getTargetStates: id=" +  pub.getId() 
                  + " state=" + pub.getState().getName() + " mode=" + mode);
        
        if( mode != null && mode.equals( "allowed" ) ) {
            List<Transition> trans = wflowContext.getWorkflowDao()
                .getAllowedTransList( pub.getState() );
            for( Iterator<Transition> ti = trans.iterator(); ti.hasNext(); ) {
                Transition t = ti.next();
                stateList.add(t.getToState().getName() );
            }
        } else {
            List<DataState> states = wflowContext.getWorkflowDao()
                .getDataStateList();
            for( Iterator<DataState> si = states.iterator(); si.hasNext(); ) {
                DataState s = si.next();
                stateList.add( s.getName() );
            }         
        }
        log.info( "DONE" );
        return stateList; 
    }
    
    //--------------------------------------------------------------------------

    public IcPub genIcPubImex( IcPub pub, User luser ) {

        if( pub == null ) { return null; }
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " EntryManager.genIcPubImex:  id=" + pub.getId() +
                  " imex=" + pub.getIcKey() );

        if( pub.getIcKey() == null ) {
            log.info( "KeyspaceContext=" + keyspaceContext );

            IcKey key = 
                (IcKey) keyspaceContext.getKeyspaceDao().nextKey( "imex" );
            pub.setIcKey( key );
            tracContext.getPubDao().updatePublication( pub );
        }

        return pub;
    }



    //--------------------------------------------------------------------------

    public IcPub updateIcPubDates( IcPub pub, User luser,
                                   GregorianCalendar epd, 
                                   GregorianCalendar pd, 
                                   GregorianCalendar rd ) {
        
        if( pub != null && luser != null ) {
            pub.setExpectedPubDate( epd );
            pub.setPubDate( pd );
            pub.setReleaseDate( rd );
            Log log = LogFactory.getLog( this.getClass() );
            log.info( " EntryManager.updateIcPubDates: calling updateIcPubProps");
            this.updateIcPubProps( pub, luser );
            
            //tracContext.getPubDao().savePublication( pub );
            return pub;
        }
        return null;
    }

    //--------------------------------------------------------------------------


    public IcPub updateIcPubIdentifiers( IcPub pub, User luser, 
                                         Publication npub ) {
        
        if( pub != null && npub != null && luser != null) {
            pub.setDoi( npub.getDoi() );
            pub.setPmid( npub.getPmid() );
            pub.setJournalSpecific( npub.getJournalSpecific() );
            this.updateIcPubProps( pub, luser );
           
            //tracContext.getPubDao().savePublication( uPub );
            return pub;
        }
        return null;
    }
    
    //--------------------------------------------------------------------------

    public IcPub updateIcPubAuthTitle( IcPub pub, User user, Publication npub ) {
        
        if( pub != null ) {
            pub.setAuthor( npub.getAuthor() );
            pub.setTitle( npub.getTitle() );
            updateIcPubProps( pub, user );
            
            //tracContext.getPubDao().savePublication( uPub );
            return pub;
        }
        return null;
    }

    //--------------------------------------------------------------------------

    public IcPub resyncIcPubPubmed( IcPub pub, User luser, Publication npub ) {
        
        if( pub == null || luser == null || npub == null ) return null;
        
        Publication pmPub = this.getPubByPmid( npub.getPmid() );
        if( pmPub != null ){
            pub.setAuthor( pmPub.getAuthor() );
            pub.setTitle( pmPub.getTitle() );
            pub.setAbstract( pmPub.getAbstract() );
      
            IcJournal journal = 
                this.addIcJournal( ((Journal) pmPub.getSource()).getNlmid(),
                                   luser );
            pub.setSource( journal );
 
            Log log = LogFactory.getLog( this.getClass() );
            //log.debug( "abst=" + pmPub.getAbstract() );

            this.updateIcPubProps( pub, luser );
            
            //tracContext.getPubDao().savePublication( uPub );
        }
        return pub; 
    }

    //--------------------------------------------------------------------------

    public IcPub updateIcPubState( IcPub pub, User luser, DataState state ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if( pub != null && state !=null ) {

            /*  test of allowed transition ???
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
            
            pub.setState( state );
            tracContext.getPubDao().savePublication( pub );
        
            return pub;
        }
        return null;
    }

    //--------------------------------------------------------------------------

    
    public IcPub updateIcPubState( IcPub pub, User user, String stateName ) {

        DataState state = wflowContext.getWorkflowDao().getDataState( stateName );

        if( pub != null && state != null ) {
            this.updateIcPubState( pub, user, state ); 
            return pub;
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    
    public  IcPub updateIcPubState( IcPub pub, User user, int sid ) {
        
        DataState state = wflowContext.getWorkflowDao().getDataState( sid );
        
        if( pub != null && state != null ) {
            this.updateIcPubState( pub, user, state );
            return pub;
        }
        return null;

    }
    
    //--------------------------------------------------------------------------

    public IcPub  updateIcPubContactMail( IcPub pub, User user, String mail ) {
        
        if( pub != null && user != null && mail != null ) {
            pub.setContactEmail( mail );
            updateIcPubProps( pub, user );
            //tracContext.getPubDao().savePublication( pub );
        }
        return pub;
    }

    //--------------------------------------------------------------------------

    public IcPub addAdminUser( Publication pub, User luser, User auser ) {
        
        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );

        if ( oldPub != null ) {
            oldPub.getAdminUsers().add( auser );
            tracContext.getPubDao().updatePublication( oldPub );
        }
        
        return oldPub;        
    }
    
    //-------------------------------------------------------------------------


    public IcPub addAdminGroup( Publication pub, User luser, Group agroup ) {
        
        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );

        if ( oldPub != null ) {
            oldPub.getAdminGroups().add( agroup );
            tracContext.getPubDao().updatePublication( oldPub );
        }

        return oldPub;        
    }

    //---------------------------------------------------------------------
    
    public IcPub delAdminUsers( Publication pub, User luser, 
                                List<Integer> udel ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = udel.iterator();
              ii.hasNext(); ) {

            int duid = ii.next().intValue();

            IcPub oldPub = (IcPub) tracContext.getPubDao()
                .getPublication( pub.getId() );
            User duser = getUserContext().getUserDao().getUser( duid );

            log.info( "pub=" + oldPub.getId() + " uid=" + duid);
            
            if ( duser != null && oldPub != null) {
                Set<User> users = oldPub.getAdminUsers();
                
                for ( Iterator<User> iu = users.iterator();
                      iu.hasNext(); ) {

                    User ou = iu.next();
                    if ( ou.getId() == duser.getId() ) {
                        oldPub.getAdminUsers().remove( ou );
                        break;
                    }
                }
                tracContext.getPubDao().updatePublication( oldPub );
                log.info( "users=" + oldPub.getAdminUsers() );
            }
        }

        return (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );
    }

    //---------------------------------------------------------------------

    public IcPub delAdminGroups( Publication pub, User luser,
                                 List<Integer> gdel ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = gdel.iterator();
              ii.hasNext(); ) {

            int dgid = ii.next().intValue();

            IcPub oldPub = (IcPub) tracContext.getPubDao()
                .getPublication( pub.getId() );
            Group group = getUserContext().getGroupDao().getGroup( dgid );

            log.info( "pub=" + oldPub.getId() + " gid=" + dgid);
            
            if ( group != null && oldPub != null) {
                Set<Group> groups = oldPub.getAdminGroups();
                
                for ( Iterator<Group> ig = groups.iterator();
                      ig.hasNext(); ) {

                    Group og = ig.next();
                    if ( og.getId() == group.getId() ) {
                        oldPub.getAdminGroups().remove( og );
                        break;
                    }
                }
                tracContext.getPubDao().updatePublication( oldPub );
                log.info( "groups=" + oldPub.getAdminGroups() );
            }
        }

        return (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );
    }
    
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // IcJournal management
    //----------------------
    
    public IcJournal getIcJournal( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get journal -> id=" + id );
        
        IcJournal oldJournal = (IcJournal) tracContext
            .getJournalDao().getJournal( id );
        
        return oldJournal;
    }

    //---------------------------------------------------------------------

    public IcJournal getIcJournalByNlmid( String nlmid ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get jrnl -> nlmid=" + nlmid );

        // test if already in
        //-------------------

        IcJournal oldJrnl =  (IcJournal) tracContext.getJournalDao()
            .getJournalByNlmid( nlmid );
        
        return oldJrnl;
    }

    //---------------------------------------------------------------------
    
    public IcJournal addAdminUser( Journal journal, User user ) {

        IcJournal oldJournal = (IcJournal) tracContext.getJournalDao()
            .getJournal( journal.getId() );

        if ( oldJournal != null ) {
            oldJournal.getAdminUsers().add( user );
            tracContext.getJournalDao()
                .updateJournal( oldJournal );
        }
        return oldJournal;
    }
    
    //---------------------------------------------------------------------
    
    public IcJournal addAdminGroup( Journal journal, Group group ) {

        IcJournal oldJournal = (IcJournal) tracContext.getJournalDao()
            .getJournal( journal.getId() );

        if ( oldJournal != null ) {
            oldJournal.getAdminGroups().add( group );
            tracContext.getJournalDao()
                .updateJournal( oldJournal );
        }
        return oldJournal;
    }
    
    //---------------------------------------------------------------------

    public IcJournal delAdminUsers( Journal journal, List<Integer> udel ) {

        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = udel.iterator();
              ii.hasNext(); ) {

            int duid = ii.next().intValue();

            IcJournal oldJournal = (IcJournal) tracContext.getJournalDao()
                .getJournal( journal.getId() );
            User user = getUserContext().getUserDao().getUser( duid );

            log.info( "journal=" + journal.getId() + " uid=" + duid);
            
            if ( user != null && oldJournal != null) {
                Set<User> users = oldJournal.getAdminUsers();
                
                for ( Iterator<User> iu = users.iterator();
                      iu.hasNext(); ) {

                    User ou = iu.next();
                    if ( ou.getId() == user.getId() ) {
                        oldJournal.getAdminUsers().remove( ou );
                        break;
                    }
                }
                tracContext.getJournalDao().updateJournal( oldJournal );
                log.info( "groups=" +oldJournal.getAdminGroups() );
            }
        }

        return (IcJournal) tracContext.getJournalDao()
            .getJournal( journal.getId() );
    }

    //---------------------------------------------------------------------

    public IcJournal delAdminGroups( Journal journal, List<Integer> gdel ) {

        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = gdel.iterator();
              ii.hasNext(); ) {

            int dgid = ii.next().intValue();

            IcJournal oldJournal = (IcJournal) tracContext.getJournalDao()
                .getJournal( journal.getId() );
            Group group = getUserContext().getGroupDao().getGroup( dgid );

            log.info( "journal=" + journal.getId() + " gid=" + dgid);
            
            if ( group != null && oldJournal != null) {
                Set<Group> groups = oldJournal.getAdminGroups();
                
                for ( Iterator<Group> ig = groups.iterator();
                      ig.hasNext(); ) {

                    Group og = ig.next();
                    if ( og.getId() == group.getId() ) {
                        oldJournal.getAdminGroups().remove( og );
                        break;
                    }
                }
                tracContext.getJournalDao().updateJournal( oldJournal );
                log.info( "groups=" +oldJournal.getAdminGroups() );
            }
        }

        return (IcJournal) tracContext.getJournalDao()
            .getJournal( journal.getId() );
    }

    //---------------------------------------------------------------------
    
    public IcJournal addIcJournal( String nlmid, User owner ) {

        Journal newJrnl = new Journal();
        newJrnl.setNlmid( nlmid );
        
        return addIcJournal( newJrnl, owner );
    }
    
    public IcJournal addIcJournal( Journal jrnl, User owner ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new jrnl -> nlmid= " + jrnl.getNlmid() );
        
        // test if already in 
        //-------------------

        IcJournal oldJrnl = (IcJournal) tracContext.getJournalDao()
            .getJournalByNlmid( jrnl.getNlmid() );
        
        if ( oldJrnl != null ) return oldJrnl;        
        
        // get journal through proxy
        //--------------------------        
        
        NcbiProxyClient cli = tracContext.getNcbiProxyClient();
        if ( cli == null ) return null;
        log.info( " NcbiProxyClient=" + cli );
        
        Journal newJrnl = cli.getJournalByNlmid( jrnl.getNlmid() );
        if ( newJrnl == null ) return null;
        
        IcJournal icjrnl = new IcJournal( newJrnl );
        log.info( " IcJournal=" + icjrnl );

        // defaults defined in userContext
        //--------------------------------
        
        Map defs = (Map) userContext.getJsonConfig().get( "default" );
        if ( defs == null ) return null;
        
        String ousr = (String) defs.get( "owner" ); 
        String ausr = (String) defs.get( "adminuser" ); 
        String agrp = (String) defs.get( "admingroup" ); 

        log.info( "defs: ou =" + ousr + " au=" + ausr + " ag=" + agrp );

        icjrnl.setOwner( userContext.getUserDao().getUser( ousr ) );
        icjrnl.getAdminUsers().add( userContext.getUserDao()
                                    .getUser( ausr ) );
        icjrnl.getAdminGroups().add( userContext.getGroupDao()
                                     .getGroup( agrp ) );
        
        // commit new journal
        //-------------------
                
        tracContext.getJournalDao().saveJournal( icjrnl );
                
        return (IcJournal) tracContext.getJournalDao()
            .getJournalByNlmid( icjrnl.getNlmid() );
    }

    //---------------------------------------------------------------------
    
    public IcJournal updateIcJournal( Journal jrnl, Journal newJrnl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " jrnl -> nlmid= " + jrnl.getNlmid() );

        // sanity check
        //-------------
        
        IcJournal oldJrnl = (IcJournal) tracContext.getJournalDao()
            .getJournalByNlmid( jrnl.getNlmid() );
        
        if ( oldJrnl == null ) return null;

        if ( newJrnl.getTitle() != null ) {
            oldJrnl.setTitle( newJrnl.getTitle() );
        }
        if ( newJrnl.getNlmid() != null ) {
            oldJrnl.setNlmid( newJrnl.getNlmid() );
        }
        if ( newJrnl.getIssn() != null ) {
            oldJrnl.setIssn( newJrnl.getIssn() );
        }
        if ( newJrnl.getWebsiteUrl() != null ) {
            oldJrnl.setWebsiteUrl( newJrnl.getWebsiteUrl() );
        }
        if ( newJrnl.getComments() != null ) {
            oldJrnl.setComments( newJrnl.getComments() );
        }
        
        tracContext.getJournalDao().updateJournal( oldJrnl );
        return oldJrnl;
    }
}
