package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
 #=========================================================================
 #
 # EntryManager - businness logic of entry/journal management 
 #                 
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
       
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
    
    //---------------------------------------------------------------------

    public IcPub addIcPub( Publication pub, User owner, DataState state ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " new pub -> pmid=" + pub.getPmid() );
        
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
                
                tracContext.getPubDao().savePublication( icp );
                return (IcPub) tracContext.getPubDao()
                    .getPublicationByPmid( icp.getPmid() );
            }
        }                
        return null;
    }

    //---------------------------------------------------------------------

    public void deleteIcPub( IcPub pub ) {
        
    }

    //---------------------------------------------------------------------

    public IcPub updateIcPubProps( IcPub pub ) {
        return null;
    }

    //---------------------------------------------------------------------

    public IcPub updateIcPubState( IcPub pub ) {
        return null;
    }

    //---------------------------------------------------------------------

    public IcPub addAdminUser( Publication pub, User user ) {
        
        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );

        if ( oldPub != null ) {
            oldPub.getAdminUsers().add( user );
            tracContext.getPubDao()
                .updatePublication( oldPub );
        }

        return oldPub;        
    }
    
    //---------------------------------------------------------------------

    public IcPub addAdminGroup( Publication pub, Group group ) {
        
        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublication( pub.getId() );

        if ( oldPub != null ) {
            oldPub.getAdminGroups().add( group );
            tracContext.getPubDao()
                .updatePublication( oldPub );
        }

        return oldPub;        
    }

    //---------------------------------------------------------------------
    
    public IcPub delAdminUsers( Publication pub, List<Integer> udel ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        for ( Iterator<Integer> ii = udel.iterator();
              ii.hasNext(); ) {

            int duid = ii.next().intValue();

            IcPub oldPub = (IcPub) tracContext.getPubDao()
                .getPublication( pub.getId() );
            User user = getUserContext().getUserDao().getUser( duid );

            log.info( "pub=" + oldPub.getId() + " uid=" + duid);
            
            if ( user != null && oldPub != null) {
                Set<User> users = oldPub.getAdminUsers();
                
                for ( Iterator<User> iu = users.iterator();
                      iu.hasNext(); ) {

                    User ou = iu.next();
                    if ( ou.getId() == user.getId() ) {
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

    public IcPub delAdminGroups( Publication pub, List<Integer> gdel ) {
        
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
