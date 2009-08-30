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
        log.info( " new pub -> id=" + pub.getId() +
                  " pmid=" + pub.getPmid() );

        // test if already in 
        //-------------------

        IcPub oldPub = (IcPub) tracContext.getPubDao()
            .getPublicationByPmid( pub.getPmid() );
        
        if ( oldPub != null ) return oldPub;        

        // get through proxy
        //------------------        
        
        NcbiProxyClient cli = tracContext.getNcbiProxyClient();
        
        if ( cli != null ) {
            Publication newPub = 
                cli.getPublicationByPmid( pub.getPmid() );
        
            if ( newPub != null ) {
                IcPub icp = new IcPub( newPub );
                
                if( icp.getSource() == null ) {
                    IcJournal icj = (IcJournal) tracContext.getJournalDao()
                        .getJournalByNlmid( "0410462" ); // pub.getNlmid();
                    
                    if ( icj != null ) {
                        icp.setSource( icj ); 
                    } else {

                        // add journal
                        //------------
                        
                    }
                }

                icp.setOwner( owner ) ;
                icp.setState( state );
                
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
    // IcJournal management
    //----------------------
    
    public IcJournal getIcJournal( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get journal -> id=" + id );
        
        IcPub oldPub =  (IcPub) tracContext.getPubDao()
            .getPublication( id );

        IcJournal oldJournal = (IcJournal) tracContext
            .getJournalDao().getJournal( id );
        
        return oldJournal;
    }


}



