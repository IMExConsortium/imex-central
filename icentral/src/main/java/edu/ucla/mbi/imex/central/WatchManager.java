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

import org.json.*;
       
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
        
        List<DataItem> diList = getTracContext().getSorelDao()
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
    
    public List<Publication> 
        getPublicationList( User usr, 
                            int firstRecord, int blockSize,
                            String skey, boolean asc, 
                            Map<String,String> flt ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " getPublicationList -> id=" + usr.getId() );
        
        List<DataItem> diList = getTracContext().getSorelDao()
            .getSubjectList( usr, firstRecord, blockSize ,
                             skey, asc, flt );

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
        return getTracContext().getSorelDao().getSubjectCount( usr );
    }

    //---------------------------------------------------------------------

    public long getPublicationCount( User usr, Map<String,String> flt ){       
        return getTracContext().getSorelDao().getSubjectCount( usr, flt );
    }
    
    //---------------------------------------------------------------------
    
    public boolean getWatchStatus( User usr, Publication pub ){

        if( usr == null || pub == null ) return false;

        if( getTracContext().getSorelDao()
            .getSORel( usr, pub )  == null ) return false;
            
        return true;
    }

    //---------------------------------------------------------------------

    public boolean setWatchStatus( User usr, Publication pub, 
                                   boolean watch ){
        
        if( usr == null || pub == null ) return false;

        if( watch ){
            getTracContext().getSorelDao().addSORel( pub, usr );
            return true;
        } else{
            getTracContext().getSorelDao().dropSORel( pub, usr );
            return false;
        }
    }

    //---------------------------------------------------------------------

    public List<User> getObserverList( Publication pub ){
        return getTracContext().getSorelDao().getObserverList( pub );
    }
    
    //---------------------------------------------------------------------

    public void addWatchByAttachmentPref( User user, Publication pub,
                                          boolean force ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("addWatchByAttachmentPref");

        if( force ){
            this.setWatchStatus( user, pub, true );
        } else {

            String byAttFlag = 
                PrefUtil.getPrefOption( user.getPrefs(), 
                                        "attachment-owner" );            
            if( byAttFlag != null 
                && byAttFlag.equalsIgnoreCase( "true" ) ){
                this.setWatchStatus( user, pub, true );
            }
        }
        log.debug("addWatchByAttachmentPref: DONE");
    }
    
    //---------------------------------------------------------------------

    public void addWatchByCommentPref( User user, Publication pub,
                                       boolean force ){

        Log log = LogFactory.getLog( this.getClass() );

        log.debug("addWatchByCommentPref");

        if( force ){
            this.setWatchStatus( user, pub, true );
        } else {

            String byCommentFlag = 
                PrefUtil.getPrefOption( user.getPrefs(),
                                        "comment-owner" );            
            if( byCommentFlag != null 
                && byCommentFlag.equalsIgnoreCase( "true" ) ){
                this.setWatchStatus( user, pub, true );
            }
        }
        log.debug("addWatchByCommentPref: DONE");
    }
    
    //---------------------------------------------------------------------
    // Event observers
    //----------------
    
    public void addNewsObserver( User usr ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addNewsObserver; user =" +  usr);

        log.debug( "ddNewsObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "ddNewsObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao().addEORel( "news", usr );
    }

    public void dropNewsObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropNewsObserver; user =" +  usr);

        log.debug( "dropNewsObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropNewsObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao().dropEORel( "news", usr );
    }

    
    //--------------------------------------------------------------------------
    // Attachments
    //------------

    public void addAttachmentObserver( User usr ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addAttachmentObserver; user =" +  usr);

        log.debug( "addAttachmentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addAttachmentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .addEORel( "new-attachment", usr );
    }

    public void dropAttachmentObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropAttachementObserver; user =" +  usr);

        log.debug( "dropAttachementObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropAttachementObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .dropEORel( "new-attachment", usr );
    }

    //--------------------------------------------------------------------------

    public void addWatchedAttachmentObserver( User usr ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addAttachmentObserver; user =" +  usr);

        log.debug( "addWatchedAttachmentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addWatchedAttachmentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .addEORel( "new-watched-attachment", usr );
    }

    public void dropWatchedAttachmentObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropWatchedAttachementObserver; user =" +  usr);

        log.debug( "dropWatchedAttachementObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropWatchedAttachementObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .dropEORel( "new-watched-attachment", usr );
    }

    //--------------------------------------------------------------------------
    // Comments
    //---------

    public void addCommentObserver( User usr ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addCommentObserver; user =" +  usr);

        log.debug( "addCommentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addCommentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .addEORel( "new-comment", usr );
    }


    public void dropCommentObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropCommentObserver; user =" +  usr);

        log.debug( "dropCommentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropCommentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .dropEORel( "new-comment", usr );
    }

    //--------------------------------------------------------------------------

    public void addWatchedCommentObserver( User usr ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addWatchedCommentObserver; user =" +  usr);

        log.debug( "addWatchedCommentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addWatchedCommentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .addEORel( "new-watched-comment", usr );
    }


    public void dropWatchedCommentObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropWatchedCommentObserver; user =" +  usr);

        log.debug( "dropWatchedCommentObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropWatchedCommentObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao()
            .dropEORel( "new-watched-comment", usr );
    }


    //--------------------------------------------------------------------------

    public void addNewRecordObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addNewRecordObserver; user =" +  usr);

        log.debug( "addNewRecordObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addNewRecordObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao().addEORel( "new-record", usr );
    }

    public void dropNewRecordObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropNewRecordObserver; user =" +  usr);

        log.debug( "dropNewRecordObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropNewRecordObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao().dropEORel( "new-record", usr );
        
    }

    //--------------------------------------------------------------------------
                                                                        
    public void addNewAccountObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "addNewAccountObserver; user =" +  usr);

        log.debug( "addNewAccountObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "addNewAccountObserver; eorel =" 
                   + getTracContext().getEorelDao() );

        getTracContext().getEorelDao().addEORel( "new-account", usr );
    }

    public void dropNewAccountObserver( User usr ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "dropNewAccountObserver; user =" +  usr);

        log.debug( "dropNewAccountObserver; getTracContext=" 
                   + getTracContext() );
        log.debug( "dropNewAccountObserver; eorel =" 
                   + getTracContext().getEorelDao() );
        getTracContext().getEorelDao().dropEORel( "new-account", usr );
        
    }

    //--------------------------------------------------------------------------
    // List queries
    //-------------

    public List<User> getNewsObserverList(){
        return getTracContext().getEorelDao().getEORel( "news" );
    }
    
    //--------------------------------------------------------------------------

    public List<User> getNewRecordObserverList(){
        return getTracContext().getEorelDao().getEORel( "new-record" );        
    }

    //--------------------------------------------------------------------------

    public List<User> getNewAccountObserverList(){
        return getTracContext().getEorelDao().getEORel( "new-account" );        
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public List<User> getAttachmentObserverList(){
        return getTracContext().getEorelDao().getEORel( "new-attachment" );        
    }

    //--------------------------------------------------------------------------
    
    public List<User> getWatchedAttachmentObserverList(){
        return getTracContext().getEorelDao()
            .getEORel( "new-watched-attachment" );        
    }

    //--------------------------------------------------------------------------
    
    public boolean getWatchedAttachmentStatus( User usr ){

        if( getTracContext().getEorelDao()
            .getEORel( "new-watched-attachment", usr )  != null ){
            return true;
        }             
        return false;   
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public List<User> getCommentObserverList(){
        return getTracContext().getEorelDao().getEORel( "new-comment" );        
    }

    //--------------------------------------------------------------------------

    public List<User> getWatchedCommentObserverList(){
        return getTracContext().getEorelDao()
            .getEORel( "new-watched-comment" );        
    }

    public boolean getWatchedCommentStatus( User usr ){
        
        if( getTracContext().getEorelDao()
            .getEORel( "new-watched-comment", usr )  != null ){
            return true;
        }             
        return false;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // private methods 

   
}
