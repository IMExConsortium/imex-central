package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # WatchManager - businness logic of watch lists (subject/event relationships) 
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

public class WatchManager {
    
    public WatchManager() {
    Log log = LogFactory.getLog( this.getClass() );
    log.info( "WatchManager: creating manager" );
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
    
    public List<Publication> 
        getPublicationList( User usr, 
                            int firstRecord, int blockSize,
                            String skey, boolean asc ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " getPublicationList -> id=" + usr.getId() );
        
        List<DataItem> diList = getTracContext().getObsMgrDao()
            .getSubjectList( usr, firstRecord, blockSize ,skey, asc );

        if( diList != null ){

            List<Publication> pl = new ArrayList<Publication>();
            
            for( Iterator<DataItem> dii = diList.iterator(); 
                 dii.hasNext(); ){

                Publication p = (Publication) dii.next();
                log.debug( "getPublicationList: add id=" + p.getId() );
                pl.add( p );
            }
            
            return pl;
        } else {
            return null;
        }
    }

    //---------------------------------------------------------------------

    public long getPublicationCount( User usr ){       
        return getTracContext().getObsMgrDao().getSubjectCount( usr );
    }

    //---------------------------------------------------------------------
    
    public boolean getWatchStatus( User usr, Publication pub ){

        if( usr == null || pub == null ) return false;

        if( getTracContext().getObsMgrDao()
            .getWatchStatus( usr, pub )  == null ) return false;
            
        return true;
    }

    //---------------------------------------------------------------------

    public boolean setWatchStatus( User usr, Publication pub, 
                                   boolean watch ){
        
        if( usr == null || pub == null ) return false;

        if( watch ){
            getTracContext().getObsMgrDao().addSORel( pub, usr );
            return true;
        } else{
            getTracContext().getObsMgrDao().dropSORel( pub, usr );
            return false;
        }
    }
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // private methods 


}
