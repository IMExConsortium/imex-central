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
       
import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.dao.*;

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
    
    private IcWorkflowContext wflowContext;

    public void setWorkflowContext( IcWorkflowContext context ) {
        this.wflowContext = context;
    }

    public IcWorkflowContext getWorkflowContext() {
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

    public void cleanup(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryManager: cleanup called" );
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
    
    public IcPub getIcPubByNsAc( String ns, String ac ) {

        if( ns == null || ac == null ) return null;

        if( ns.equalsIgnoreCase( "pmid" ) ){
            return (IcPub) tracContext.getPubDao()
                .getPublicationByPmid( ac );
        }

        if( ns.equalsIgnoreCase( "imex" ) ){
            return (IcPub) tracContext.getPubDao()
                .getPublicationByKey( ac );
        }

        if(  tracContext.getPubDao() instanceof IcPubDao ){
            
            IcPubDao dao = (IcPubDao) tracContext.getPubDao();
            
            if( ns.equalsIgnoreCase( "doi" ) ){
                return (IcPub) dao.getPublicationByDoi( ac );
            }
        
            if( ns.equalsIgnoreCase( "jint" ) ){
                return (IcPub) dao.getPublicationByJint( ac );
            }
        }
        
        return null;
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

    public List<DataState> acomStage( String query ) {
        List<DataState> ulist 
            = tracContext.getPubDao().getStages( query ); 
	Log log = LogFactory.getLog( this.getClass() );
	log.info( " acomStage count=" + ulist.size() );
        return ulist;
    }
    
    public List<Group> acomGroup( String query ) {
        List<Group> ulist 
            = tracContext.getPubDao().getAdminGroups( query ); 
        return ulist;
    }

    public List<Group> acomJGroup( String query ) {
        List<Group> ulist 
            = tracContext.getJournalDao().getAdminGroups( query ); 
        return ulist;
    }


    //--------------------------------------------------------------------------
    // get through proxy
    //------------------

    public Publication getPubByPmid( String pmid ) 
        throws ImexCentralException{

        NcbiProxyClient cli = tracContext.getNcbiProxyClient();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " NcbiProxyClient=" + cli );
        Publication newPub = null;
        if ( cli != null ) {
            newPub = cli.getPublicationByPmid( pmid );
            log.info( " newPub" + newPub );
	    System.out.println( " newPub" + newPub );

        }
        return newPub;
    }

    //--------------------------------------------------------------------------
    
    public IcPub addIcPub( Publication pub, User owner, DataState stage, 
                           DataState state ) throws ImexCentralException {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if ( pub == null ) {
            log.info( " new pub -> null");
            return null;
        }

        if( pub.getPmid() != null && ! pub.getPmid().equals( "" ) ) {
        
            log.info( " new pub -> pmid=" + pub.getPmid() );
            
            return addPmidIcPub( pub, owner, stage, state );
            
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
                    icp.setStage( stage );
                   

                    icp.setActUser( owner );
                    icp.setActDate();

                    icp.setModUser( owner ) ;
                    icp.setModDate() ;

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

    public IcPub addPmidIcPub( Publication pub, User owner, DataState stage, 
                               DataState state ) throws ImexCentralException{
        
        Log log = LogFactory.getLog( this.getClass() );
        
        // test if already in 
        //-------------------

        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublicationByPmid( pub.getPmid() );
        
        if ( oldPub != null ) return oldPub;        
        
        // get through proxy
        //------------------        
        
        NcbiProxyClient cli = tracContext.getNcbiProxyClient();

        if ( cli != null ) {
        
            log.info( " NcbiProxyClient=" + cli );

            Publication newPub = null;
            
            try{
                
                newPub = cli.getPublicationByPmid( pub.getPmid() );
                
            } catch( ClientException cx ){
                
            }
                    

            if ( newPub != null ) {
                IcPub icp = new IcPub( newPub );
                log.info( " IcPub=" + icp );
                
                if( icp.getSource() == null ) {
                    log.info( " IcPub: no source" );
                    
                    IcJournal icj = (IcJournal) tracContext.getJournalDao()
                        .getJournal( "UNPUBLISHED" );
                    
                    icp.setSource( icj );

                } else {
                    Journal j = (Journal) icp.getSource();
                    IcJournal icj = null;

                    try{

                        log.info( " IcPub: getting source info" );

                        icj = (IcJournal) tracContext.getJournalDao()
                            .getJournalByNlmid( j.getNlmid() );
                        
                    }catch( Exception ex ){
                        ex.printStackTrace();

                        icj = (IcJournal) tracContext.getJournalDao()
                            .getJournal( "UNPUBLISHED" );
                    }
                    
                    if ( icj == null ) {
                        icj = this.addIcJournal( j, owner );
                    }
                    
                    if ( icj == null ) return null;
                    icp.setSource( icj );
                }
                
                icp.setOwner( owner );

                icp.setStage( stage );
                icp.setState( state );

                icp.setActUser( owner );
                icp.setActDate();
                icp.setModUser( owner );
                icp.setModDate();
                              
                log.info( stage);
                log.info( state);


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
        
        ((IcPubDao)tracContext.getPubDao()).updatePublication( pub, luser );

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

            ((IcPubDao)tracContext.getPubDao()).updatePublication( pub, luser );
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
                                         Publication npub ) 
        throws EntryException{
        
        if( pub != null && npub != null && luser != null) {
            pub.setDoi( npub.getDoi() );

            String npmid = npub.getPmid();
            String ndoi = npub.getDoi();

            // sanitize new values
            
            if( npmid != null && !npmid.equals("") ){
                try{
                    npmid = npmid.replaceFirst("^\\s+","");
                    npmid = npmid.replaceFirst("\\s+$","");
                } catch(Exception ex){
                    // should not happen
                }
            }

            if( ndoi != null && !ndoi.equals("") ){
                try{
                    ndoi = ndoi.replaceFirst("^\\s+","");
                    ndoi = ndoi.replaceFirst("\\s+$","");
                } catch(Exception ex){
                    // should not happen
                }
            }

            // test if pmid already there !!!!
            
            if ( npmid != null && npmid.length() > 0 &&
                 getIcPubByNsAc( "pmid", npmid ) != null &&
                 getIcPubByNsAc( "pmid", npmid ).getId() == pub.getId() ){

                // duplicated pmid                
                throw EntryException.DUP_PMID;
            }

            if ( ndoi != null && ndoi.length() > 0 &&
                 getIcPubByNsAc( "doi", ndoi ) != null &&
                 getIcPubByNsAc( "doi", ndoi ).getId() == pub.getId() ){
                
                // duplicated doi
                throw EntryException.DUP_DOI;
            }

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

    public IcPub resyncIcPubPubmed( IcPub pub, User luser, Publication npub ) 
        throws ImexCentralException{
        
        if( pub == null || luser == null || npub == null ) return null;
        
        Publication pmPub = this.getPubByPmid( npub.getPmid() );
        if( pmPub != null ){
            pub.setAuthor( pmPub.getAuthor() );
            pub.setTitle( pmPub.getTitle() );
            pub.setAbstract( pmPub.getAbstract() );
            pub.setDoi( pmPub.getDoi() );
      
            if( pmPub instanceof PublicationVIP && pub instanceof PublicationVIP ){
                pub.setVolume( ((PublicationVIP)pmPub).getVolume() );
                pub.setIssue( ((PublicationVIP)pmPub).getIssue() );
                pub.setPages( ((PublicationVIP)pmPub).getPages() );
                pub.setYear( ((PublicationVIP)pmPub).getYear() );
            }

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
            
            IcPub oldPub = (IcPub) tracContext.getPubDao()
                .getPublication( pub.getId() );

            if( oldPub != null ){
		
                DataState oldState = oldPub.getState();
                DataState oldStage = oldPub.getStage();
		
		// NOTE: getNewStageState imposes stage/state update policy

		Map<String,String> ssm = 
		    wflowContext.getNewStageState( oldStage.getName(), 
						   oldState.getName(), 
						   state.getName());
		if( ssm.size() == 0 ){
		    // illegal op:  
		    return null;
		}
		
		String newState = ssm.get( "state" );
		String newStage = ssm.get( "stage" );
		
		if( oldState.equals(newState) && oldStage.equals(newStage) ){
		    // no chanage
		    return oldPub;
		} else {
		    DataState nState = 
			wflowContext.getWorkflowDao().getDataState( newState );
		    DataState nStage = 
			wflowContext.getWorkflowDao().getDataStage( newStage );
		    
		    oldPub.setState( nState );
                    oldPub.setStage( nStage );

                    //tracContext.getPubDao().savePublication( pub );                                                                                                                                           
                    log.debug( "updating state/stage");
                   
		    ((IcPubDao) tracContext.getPubDao())
                        .updatePublication( oldPub, luser );

                    return oldPub;
		}
		
		/*

                // to be moved into workflow context

                DataState oSt = oldPub.getState();
                DataState oSg = oldPub.getStage();
            
                DataState stage = oldPub.getStage();
            
                log.debug( "stage/state (o): " + oSt + " / " +  oSg );
            
		
		// NOTE: setting state to 'queue' updates stage
		//       setting state to non que/prequeue states sets stage to curation

                if( state.getName().equals("QUEUE") ){

		    if( oSt.getName().equals("NEW") || 
			oSt.getName().equals("REQUEST")
			){
			state = wflowContext.getWorkflowDao().getDataState( oSt.getName() );
		    } else {
			state = wflowContext.getWorkflowDao().getDataState( "NEW" );
		    }
                    stage = wflowContext.getWorkflowDao().getDataStage( "QUEUE" );
                }

                if( ! ( state.getName().equals("NEW") ||
			state.getName().equals("QUEUE") ||
			state.getName().equals("REQUEST") ||
                        state.getName().equals("DISCARDED") ) ){
                    stage = wflowContext.getWorkflowDao().getDataStage( "CURATION" );
                }

                log.debug( "stage/state (n): " + state + " / " +  stage );

            
                //log.debug( oSt.getName() + " : " +  oSg.getName() + " : " 
                //           +  state.getName() + " : " +  stage.getName()  );

                
                // o be moved into workflow context: END

                if( stage.getName().equals(oSg.getName()) 
                    && state.getName().equals(oSt.getName()) ){
                    
                    return pub;
                } else {
                    oldPub.setState( state );
                    oldPub.setStage( stage );
            
                    //tracContext.getPubDao().savePublication( pub );            
                    log.debug( "updating state/stage");
                    ((IcPubDao) tracContext.getPubDao())
                        .updatePublication( oldPub, luser );
                    
                    return oldPub;
                    //return (IcPub) ((IcPubDao)tracContext.getPubDao())
                    //    .getPublication( oldPub.getId() );
                }
		*/
	    }
	}
	return null;
    }

    //--------------------------------------------------------------------------

    
    public IcPub updateIcPubState( IcPub pub, User user, String stateName ) {

        DataState state = wflowContext.getWorkflowDao().getDataState( stateName );

        if( pub != null && state != null ) {
            return this.updateIcPubState( pub, user, state ); 
            //return pub;
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    
    public  IcPub updateIcPubState( IcPub pub, User user, int sid ) {
        
        DataState state = wflowContext.getWorkflowDao().getDataState( sid );
        
        if( pub != null && state != null ) {
            return this.updateIcPubState( pub, user, state );
            //return pub;
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
            ((IcPubDao)tracContext.getPubDao()).updatePublication( oldPub, luser );
        }
        
        return oldPub;        
    }
    
    //-------------------------------------------------------------------------


    public IcPub addAdminGroup( Publication pub, User luser, Group agroup ) {
       
        
        Log log = LogFactory.getLog( this.getClass() );
        
        List uniqueRoles = 
            (List) wflowContext.getJsonConfig().get( "admin-role-unique" ); 
        
        IcPub oldPub = 
            (IcPub) tracContext.getPubDao().getPublication( pub.getId() );
        
        if( oldPub != null ){
            Set<Group> adminGroups =  pub.getAdminGroups();

            if( adminGroups.contains( agroup ) ){
                return oldPub;  // NO-OP: admin group already present
            }
            boolean doAdd = true;
            Iterator uniqueRoleIterator = uniqueRoles.iterator();
            while( uniqueRoleIterator.hasNext() ){
                Role role = userContext.getRoleDao()
                    .getRole( uniqueRoleIterator.next().toString() );     
                if( agroup.getRoles().contains( role ) ){
                    Iterator groupIterator = adminGroups.iterator();    
                    while( groupIterator.hasNext() ){
                        Group g = (Group) groupIterator.next();
                        
                        if( g.getRoles().contains( role ) ){
                            doAdd = false;
                            break;
                        }
                    }
                }
            }
            if( doAdd ){
                oldPub.getAdminGroups().add( agroup );
                ((IcPubDao)tracContext.getPubDao()).updatePublication( oldPub, luser );
            }else{
                return null;
            }
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
                ((IcPubDao)tracContext.getPubDao()).updatePublication( oldPub, luser );
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
                ((IcPubDao)tracContext.getPubDao()).updatePublication( oldPub, luser );
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
    
    public IcJournal addIcJournal( String nlmid, User owner ) 
        throws ImexCentralException{

        Journal newJrnl = new Journal();
        newJrnl.setNlmid( nlmid );
        
        return addIcJournal( newJrnl, owner );
    }
    
    public IcJournal addIcJournal( Journal jrnl, User owner ) 
        throws ImexCentralException{
        
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

    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    public String getIcJournalYear( Journal jrnl, boolean first ) {
        
        String year  = tracContext.getJournalDao()
            .getJournalYear( jrnl, first );
        
        if( year != null){ 
            return year;
        }
        return "";
    }

    public List<String> getIcJournalYearList( Journal jrnl, boolean first ) {
        return tracContext.getJournalDao()
            .getJournalYearList( jrnl, first );        
    }

    //--------------------------------------------------------------------------
    
    public String getIcJournalVolume( Journal jrnl, boolean first, 
                                      String year ) {
        
        String volume  = tracContext.getJournalDao()
            .getJournalVolume( jrnl, first, year );
        
        if( volume != null){ 
            return volume;
        }
        return "";
    }

    public List<String> getIcJournalVolumeList( Journal jrnl, boolean first,
                                                String year ) {
        return tracContext.getJournalDao()
            .getJournalVolumeList( jrnl, first, year );        
    }
    
    //--------------------------------------------------------------------------

    public String getIcJournalIssue( Journal jrnl, boolean first,
                                     String year, String volume ) {

        String issue  = tracContext.getJournalDao()
            .getJournalIssue( jrnl, first, year, volume );
        
        if( issue != null){ 
            return issue;
        }
        return "";
    }

    public List<String> getIcJournalIssueList( Journal jrnl, boolean first,
                                               String year, String volume ) {
        return tracContext.getJournalDao()
            .getJournalIssueList( jrnl, first, year, volume );        
    }

}
