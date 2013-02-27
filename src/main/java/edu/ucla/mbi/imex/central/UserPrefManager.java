package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # UserPrefManager - businness logic of user preferences manager 
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

public class UserPrefManager {
    
    public UserPrefManager() {
    Log log = LogFactory.getLog( this.getClass() );
    log.info( "UserPrefManagerManager: creating manager" );
    }


    private UserContext userContext;

    public void setUserContext( UserContext context ) {
        this.userContext = context;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }

    private String defUserPrefs="";

    public void setDefUserPrefs( String prefs ) {
        this.defUserPrefs = prefs;
    }

    public String getDefUserPrefs() {
        return this.defUserPrefs;
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
        log.info( "WatchManager: initializing" );
    }


    //---------------------------------------------------------------------
    // Operations
    //---------------------------------------------------------------------
    
    public void op1( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " op1 -> id=" + id );
        
        return;
    }

    //---------------------------------------------------------------------
    
    public void op2( int id, String par1 ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " op1 -> id=" + id + " par1->" + par1);

        return;
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // private methods 


}
