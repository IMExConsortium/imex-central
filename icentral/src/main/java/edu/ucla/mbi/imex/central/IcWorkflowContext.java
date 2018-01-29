package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
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

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.context.JsonContext;
import edu.ucla.mbi.util.context.FileResource;

public class IcWorkflowContext extends WorkflowContext {


    /*
    private WorkflowDao wflowDao;

    public WorkflowDao getWorkflowDao() {
        return wflowDao;
    }

    public void setWorkflowDao( WorkflowDao dao ) {
        wflowDao = dao;
    }
    */

    //---------------------------------------------------------------------        
    
    public void initialize() { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "WorkflowContext: initializing" );
                
        FileResource fr = (FileResource) getConfig().get("json-source");
        if ( fr == null ) return;

        try {
            readJsonConfigDef( fr.getInputStream() );
        } catch ( Exception e ){
            log.info( "WorkflowContext: json-source error" );
            return;
        }
        
        log.info( "WorkflowContext: json-source OK " );
        log.debug( "config="+ getJsonConfigObject() );
        
        if ( getWorkflowDao() != null ) {
        
            log.info( "WorkflowContext: dao OK " );
            
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
                        
                        String name = state.getString( "name" );
                        String comments = state.getString( "comments" );
                        
                        log.debug( "state: name=" + name +
                                  " comments=" + comments );
                        
                        DataState oldState = getWorkflowDao().getDataState( name );
                        if ( oldState != null ) continue;
                        
                        DataState newState = new DataState();
                        newState.setName( name );
                        newState.setComments( comments );
                        getWorkflowDao().saveDataState( newState );
                    }

                }
            } catch ( Exception e ){
                log.info( "WorkflowContext: json-source error (status)" );
                e.printStackTrace();
                return;
            }   
        
            //-----------------------------------------------------------------        
            // initialize transitions
            //-----------------------

            try {
                if( getJsonConfigObject().getJSONArray("transition") != null ) {
                    
                    //"trans":[{name:"reserve",
                    //          from:"NEW",to:"RESERVED",
                    //          comments:"reserve "}]

                    log.info( "WorkflowContext: initializing transitions" );
                    
                    JSONArray groupArray = 
                        getJsonConfigObject().getJSONArray( "transition" );
                    
                    for ( int i = 0; i < groupArray.length(); i++ ) {
                        JSONObject trans = groupArray.getJSONObject( i );
                        if ( trans == null ) continue;
                            
                        String label = trans.getString( "name" );
                        String frStateName = trans.getString( "from" );
                        String toStateName = trans.getString( "to" );
                        String comments = trans.getString( "comments" );
                            
                        Transition oldTrans = getWorkflowDao().getTrans( label );
                        if ( oldTrans != null ) continue;
                        
                        DataState frState = 
                            getWorkflowDao().getDataState( frStateName );
                        DataState toState = 
                            getWorkflowDao().getDataState( toStateName );
                        
                        if ( frState == null || toState == null ) continue;
                        
                        Transition newTrans  = new Transition();
                        newTrans.setFromState( frState );
                        newTrans.setToState( toState );
                        newTrans.setName( label );
                        newTrans.setComments( comments );
                        
                        getWorkflowDao().saveTrans( newTrans );
                    }
                }
            } catch ( Exception e ){
                e.printStackTrace();
                log.info( "WorkflowContext: json-source error (trans)" );
                return;
            }
        }
    }

    //--------------------------------------------------------------------------

    public List<String> getStageList(){

	// returns the list of all stages 

	return getStageList( null );
    }

    public List<String> getStageList( String status ){

	// returns the list of stages compatible with the specified status

	Log log = LogFactory.getLog( this.getClass() );
	List<String> slist = new ArrayList<String>();

	JSONArray stageArray = null;

	try{
	    stageArray =
		getJsonConfigObject().getJSONArray( "stage" );
	
	} catch( Exception e ){
	    e.printStackTrace();
	    log.info( "WorkflowContext: json-source error (stage)" );	    
	}

	log.debug( "stageArray: " + stageArray );	    	    
	try{
	    if( stageArray != null){
		for ( int i = 0; i < stageArray.length(); i++ ) {

		    JSONObject joStage = stageArray.getJSONObject( i );
		    String stage = joStage.getString( "name" );

		    if( status == null) {
			slist.add( stage );
		    } else{
			JSONArray stalArray = joStage.getJSONArray( "status-allowed" ); 

			for ( int j = 0; j < stalArray.length(); j++ ) {		
			    if( stalArray.getString(j).equals(status) ){
				slist.add( stage );
				break;
			    }
			}
		    }
		}
	    }
	} catch( Exception e ){
	    e.printStackTrace();
	    log.info( "WorkflowContext: json-source error (status-allowed)" );	    
	}	
	return slist;
    }

    //--------------------------------------------------------------------------

    public List<String> getStatusList(){

	// return the list of all statuses

	return getStatusList( null );
    }

    public List<String> getStatusList( String stage ){

	// return the list of statuses compatible with the specified 
	// stage (or all)

	List<String> slist = new ArrayList<String>();

	Log log = LogFactory.getLog( this.getClass() );

	try {
	    if( stage == null ){

		// get all states

		JSONArray statusArray = 
		    getJsonConfigObject().getJSONArray( "state" );
		if( statusArray != null){
		    for ( int i = 0; i < statusArray.length(); i++ ) {
			JSONObject status = statusArray.getJSONObject( i );
			slist.add( status.getString("name") );
		    }
		}
	    } else {
		// get stage compatible states
		JSONArray stageArray =
		    getJsonConfigObject().getJSONArray( "stage" );
	
		log.debug( "stage: " + stageArray );

		if( stageArray != null){
		    for ( int i = 0; i < stageArray.length(); i++ ) {
			JSONObject joStage = stageArray.getJSONObject( i );
			if( joStage.getString("name").equals( stage ) ){
			    JSONArray statusArray 
				= joStage.getJSONArray( "status-allowed" );

			    if( statusArray != null ){
				for ( int j = 0; i<statusArray.length(); j++ ){
				    slist.add( statusArray.getString( j ) );
				}
			    }
			    break;
			}
		    }
		}
	    }
	} catch ( Exception e ){	  
	    log.info( "WorkflowContext: json-source error (getStatusList)" );
	}
	
	return slist;
    }

    //--------------------------------------------------------------------------

    public Map<String,String> 
	getNewStageState( String oldStage, String oldState, String newState ){

	Map<String,String> ssm = new HashMap<String,String>();
	
	if( this.getStatusList( oldStage ).contains( newState ) ){

	    // new state requestested within current stage

	    ssm.put("stage",oldStage);
	    ssm.put("state",newState);	 
	    return ssm;
	}


	List<String> slist = this.getStageList();
	
	if( slist.contains( newState ) ){
	    
	    // new stage request
	    ssm.put("stage", newState); 

	    if( this.getStatusList( newState ).contains( oldState ) ){
		
		// state retained
		ssm.put("state", oldState);
		
	    } else {
		// default state
		ssm.put("state", this.getStatusList( newState ).get(0) );
		
	    }
	    return ssm;
	}

	// new state in up-stage ? 

	try{
	    List<String> upstageList = 
		slist.subList( slist.lastIndexOf( oldStage+1 ),
			       slist.size() );
	    
	    for( Iterator<String> i = upstageList.iterator(); i.hasNext(); ){
		String nextStage = i.next();
		if( this.getStatusList( nextStage ).contains( newState ) ){
		    ssm.put( "stage", nextStage );
		    ssm.put( "state", newState );	 
		    return ssm;
		}
	    }
	} catch ( IndexOutOfBoundsException ex){
	    // shoudn't get here 
	}

	return ssm;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public List<String> getStatusListOld( String stage ){

	List<String> slist = new ArrayList<String>();

	Log log = LogFactory.getLog( this.getClass() );

	try {
	    JSONObject stages =
		getJsonConfigObject().getJSONObject( "stage" );
	
	    log.info("stages: "+stages);

	    JSONObject stageObj = stages.getJSONObject( stage );
	    JSONArray statusArray = stageObj.getJSONArray("status-allowed");

	    log.info( "statusArray: " + statusArray );

	    if( statusArray != null){
		for ( int i = 0; i < statusArray.length(); i++ ) {
		    String status = statusArray.getString( i );
		    slist.add(status);
		}
	    }
	} catch ( Exception e ){	  

	    log.info( "WorkflowContext: json-source error (getStatusList)" );
	}

	return slist;
    }
}
