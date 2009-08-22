package edu.ucla.mbi.util.data.*;

/* ========================================================================
 # $Id:: UserContext.java 465 2009-08-21 02:36:27Z lukasz                 $
 # Version: $Rev:: 465                                                    $
 #=========================================================================
 #                                                                        $
 # JsonContext: JSON-based workflow configuration                         $
 #                                                                        $
 #     TO DO:                                                             $
 #                                                                        $
 #======================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import org.json.*;

import edu.ucla.mbi.util.data.dao.*;

public class WorkflowContext extends JsonContext {

    private WorkflowDAO wflowDao;

    public WorkflowDAO getWorkflowDao() {
        return wflowDao;
    }

    public void setWorkflowDAO( WorkflowDAO dao ) {
        wflowDao = dao;
    }
    
    //---------------------------------------------------------------------        
    
    public void initialize() { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "WorkflowContext: initializing" );
        
        FileResource fr = (FileResource) getConfig().get("json-source");
        if ( fr == null ) return;

        try {
            readJsonConfigDef( fr.getInputStream() );
        } catch ( Exception e ){
            log.info( "UserContext: json-source error" );
            return;
        }

        if ( wflowDao != null ) {
            
            //-----------------------------------------------------------------        
            // initialize states
            //------------------
            
            try {
                if( getJsonConfigObject().getJSONArray("state") != null ) {
                    
                    // "state":[{"name":"user",
                    //             "comments":"default user role"}]    
                    
                    JSONArray stateArray = 
                        getJsonConfigObject().getJSONArray( "state" );
            
                    for ( int i = 0; i < stateArray.length(); i++ ) {
                        JSONObject state = stateArray.getJSONObject( i );
                        if ( state == null ) continue;

                        String name = state.getString( "state" );
                        String comments = state.getString( "comments" );
                        
                        log.info( "state: name=" + name +
                                  " comments=" + comments );
                        
                        DataState oldState = wflowDao.getState( name );
                        if ( oldState != null ) continue;
                        
                        DataState newState = new DataState();
                        newState.setName( name );
                        newState.setComments( comments );
                        wflowDao.saveState( newRole );
                    }

                }
            } catch ( Exception e ){
                log.info( "WorkflowContext: json-source error" );
                return;
            }   
        
            //-----------------------------------------------------------------        
            // initialize transitions
            //-----------------------

            try {
                if( getJsonConfigObject().getJSONArray("trans") != null ) {
                    
                    //"trans":[{name:"reserve",
                    //          from:"NEW",to:"RESERVED",
                    //          comments:"reserve "}]

                    log.info( "WorkflowContext: initializing transitions" );
                    
                    JSONArray groupArray = 
                        getJsonConfigObject().getJSONArray( "trans" );
                    
                    for ( int i = 0; i < groupArray.length(); i++ ) {
                        JSONObject trans = groupArray.getJSONObject( i );
                        if ( group == null ) continue;
                            
                        String label = trans.getString( "name" );
                        String frStateName = trans.getString( "from" );
                        String toStateName = trans.getString( "to" );
                        String comments = trans.getString( "comments" );
                            
                        Transition oldTrans = wflowDao.getTrans( name );
                        if ( oldTrans != null ) continue;
                        
                        DataState frState = wflowDao.getState( frStateName);
                        DataState toState = wflowDao.getState( toStateName);
                        
                        if ( frState == null || toState == null ) continue;

                        Transition newTrans  = new Transition();
                        newTrans.setFromState( frState );
                        newTrans.setToState( toState );
                        newTrans.setName( name );
                        newTrans.setComments( comments );
                        
                        wflowDao.saveTrans( newTrans );
                    }
                }
            } catch ( Exception e ){
                e.printStackTrace();
                log.info( "WorkflowContext: json-source error" );
                return;
            }
        }
    }
}
