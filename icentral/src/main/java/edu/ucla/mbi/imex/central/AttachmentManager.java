package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id:: EntryManager.java 213 2011-06-24 20:08:57Z lukasz                     $
 # Version: $Rev:: 213                                                         $
 #==============================================================================
 #
 # AttachmentManager - business logic of attachments (log, comments) management 
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

import edu.ucla.mbi.imex.central.dao.*;


public class AttachmentManager {
    
    public AttachmentManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "AttachmentManager: creating manager" );
    }

    //--------------------------------------------------------------------------
    //  TracContext
    //--------------

    private TracContext tracContext;
    
    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }
    
    public TracContext getTracContext() {
        return this.tracContext;
    }

    //--------------------------------------------------------------------------
    //  UserContext
    //--------------
    
    private UserContext userContext;

    public void setUserContext( UserContext context ) {
        this.userContext = context;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }
    
    //--------------------------------------------------------------------------
    
    boolean debug = false;

    public boolean getDebug() {
        return debug;
    }
    
    public void setDebug( boolean debug ) {
        this.debug = debug;
    }
    
    //--------------------------------------------------------------------------

    public void initialize(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "AttachmentManager: initializing" );
    }
    
    //--------------------------------------------------------------------------
    // Operations
    //--------------------------------------------------------------------------
    // IcLogEntry management
    //----------------------
    
    public IcLogEntry getIcLogEntry( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get IcLogEntry -> id=" + id );
        
        IcLogEntry lent =  (IcLogEntry) tracContext.getAdiDao()
            .getAdi( id );

        return lent;
    }

    //--------------------------------------------------------------------------
    
    public IcLogEntry addIcLogEntry( IcLogEntry lent, User owner ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if ( lent == null || owner == null) {
            log.info( " new log entry/owner -> null");
            return null;
        }

        IcAdiDao adiDao = (IcAdiDao) getTracContext().getAdiDao();
        adiDao.saveAdi( lent );

        
        return lent;
    }
}
