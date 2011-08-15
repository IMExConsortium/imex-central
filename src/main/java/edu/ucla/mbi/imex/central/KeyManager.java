package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $Id:: EntryManager.java 93 2009-12-09 02:03:29Z lukasz                 $
 # Version: $Rev:: 93                                                     $
 #=========================================================================
 #
 # KeyManager - businness logic of imex key management 
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

public class KeyManager {
    
    public KeyManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "KeyManager: creating manager" );
    }

    //---------------------------------------------------------------------
    //  TracContext
    //-------------

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
        log.info( "KeyManager: initializing" );
    }


    //---------------------------------------------------------------------
    // Operations
    //---------------------------------------------------------------------
    // generate new key
    //-----------------
    
    public String generateNewKey() {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        //Key newKey = keyDAO.generate(owner);
        //String key = newKey.getAccession();
        
        String key ="IM-";
        
        return key;
    }

    //---------------------------------------------------------------------
    
    public IcKey getKey( String acc ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        //IcKey key = keyspaceContext.getKeyspaceDao().getKey( acc );
        
        return null;
    }

    //---------------------------------------------------------------------
    
    public IcKey retireKey( String acc ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        //IcKey key = keyspaceContext.getKeyspaceDao().retireKey( acc );
        
        return null;
    }

}
