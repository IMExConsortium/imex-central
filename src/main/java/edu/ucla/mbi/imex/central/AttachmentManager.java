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

        // read Attachment/LogContext here ? 
    }
    
    //--------------------------------------------------------------------------
    // Operations
    //--------------------------------------------------------------------------
    // AttachedDataItem management
    //----------------------------
    
    public AttachedDataItem getIcAdi( int id ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get IcAdi -> id=" + id );
        
        AttachedDataItem adi 
            = (AttachedDataItem) tracContext.getAdiDao().getAdi( id );

        return adi;
    }

    //--------------------------------------------------------------------------

    public List<AttachedDataItem> getAdiListByRoot( IcPub icpub ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get getAdiListByRoot" );

        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();

        List<AttachedDataItem> adiList =
            adiDao.getAdiListByRoot( icpub );
        
        return adiList;
    }

    //--------------------------------------------------------------------------
    
    public IcFlag getIcFlag( int id ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( " get getIcFlag" );

        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();

        IcFlag flag = adiDao.getIcFlag( id );
        return flag;
    }
    
    //--------------------------------------------------------------------------

    public long countCommByRoot( IcPub icpub ){

        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();
        
        long cnt = adiDao.countIcCommByRoot( icpub );
        return cnt;
    }

    public long countLogEntryByRoot( IcPub icpub ){
        
        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();
        
        long cnt = adiDao.countIcLogEntryByRoot( icpub );
        return cnt;
    }

    //--------------------------------------------------------------------------
    
    public AttachedDataItem addIcAdi( AttachedDataItem adi, User owner ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if ( adi == null || owner == null) {
            log.info( " new  adi/owner -> null");
            return null;
        }

        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();
        adiDao.saveAdi( adi );
        
        return adi;
    }

    public void dropIcAdi( int aid, User owner ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if ( owner == null) {
            log.info( " missing user info");
            return;
        }
        
        IcAdiDao adiDao = (IcAdiDao) tracContext.getAdiDao();
        adiDao.deleteAdi( aid );
    }
    
}
