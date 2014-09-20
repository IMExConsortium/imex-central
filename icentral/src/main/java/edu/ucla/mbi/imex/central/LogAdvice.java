package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # LogAdvice - AOP logger
 #                 
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.lang.reflect.*;
       
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.imex.central.dao.*;

public class LogAdvice {

    ////------------------------------------------------------------------------
    /// Attachment Manager
    //--------------------

    private AttachmentManager attManager;

    public void setAttachmentManager( AttachmentManager manager ) {
        this.attManager = manager;
    }

    public AttachmentManager getAttachmentManager() {
        return this.attManager;
    }

    ////------------------------------------------------------------------------
    /// Watch Manager
    //---------------

    private WatchManager watchManager;

    public void setWatchManager( WatchManager manager ) {
        this.watchManager = manager;
    }

    public WatchManager getWatchManager() {
        return this.watchManager;
    }

    ////------------------------------------------------------------------------
    /// Notification Manager
    //----------------------

    private NotificationManager notificationManager;

    public void setNotificationManager( NotificationManager manager ) {
        this.notificationManager = manager;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public LogAdvice() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "LogManager: creating log manager" );
    }
    
    //--------------------------------------------------------------------------

    public void simpleMonitor(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "LogManager: monitor called");
    }
    
    //--------------------------------------------------------------------------
    
    public void addPubMonitor( Object pub,  Object owner, 
                               Object state, Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: add publication monitor called:" 
                   + " rpub=" + rpub 
                   + " state=" + state 
                   + " own=" + owner);
        
        //DataState ds = getAttachmentManager().getTracContext()
        //    .getWorkflowDao().getDataState( (String) state );

        DataState ds = (DataState) state;
        
        if( rpub instanceof IcPub && ((IcPub)rpub).getId() != null ){
            
            IcLogEntry ile 
                = new IcLogEntry( (User) owner, (IcPub) rpub,
                                  "Publication record created."
                                  + " State: " + ds.getName(), "" );

            log.debug( "LogManager->addPubMonitor:"
                       + " id(pub)=" + ile.getRoot().getId() );

            getAttachmentManager().getTracContext()
                .getAdiDao().saveAdi( ile );

            // update watch list if needed
            //----------------------------
           
            watchManager
                .addWatchByRecordOwnerPref( (User) owner, (IcPub) rpub, false );
            
            // get a list of observers and send out notifications
            //---------------------------------------------------

            List<User> newRecObsLst = watchManager.getNewRecordObserverList();
            notificationManager
                .newRecordNotify( (IcPub) rpub, ile, newRecObsLst );
            
        } else {
            log.warn( "LogManager: add publication monitor warning:"
                      + " pub=" + rpub
                      + " state=" + state
                      + " own=" + owner );
        }
    }

    public void updatePubMonitor( Object pub, Object luser, 
                                  Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: update publication monitor called:" 
                   + " pub=" + pub 
                   + " luser=" + luser);

        IcLogEntry ile 
            = new IcLogEntry( (User) luser, (IcPub) pub,
                              "Publication record updated.", "" );
        getAttachmentManager().getTracContext()
            .getAdiDao().saveAdi( ile );
        
        // get a list of observers and send out notifications
        //---------------------------------------------------

        List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
        notificationManager.updateNotify( (IcPub) pub, ile, obsLst );                   
    }
    
    public void updatePubExceptionMonitor( Object pub, Object luser, 
                                  Object icx ){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "LogManager: exception monitor called:"  
                  + " icx=" + ((ImexCentralException)icx).getStatusCode()  
                  + " pubid=" + ((IcPub)pub).getId() 
                  + " luser=" + ((User)luser).getLogin() );        
    }

    public void updatePubAdminUserMonitor( Object pub, Object luser, 
                                           Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: update publication monitor called:" 
                   + " pub=" + pub 
                   + " luser=" + luser);

        IcLogEntry ile 
            = new IcLogEntry( (User) luser, (IcPub) pub,
                              "Admin user information updated.", "" );
        getAttachmentManager().getTracContext()
            .getAdiDao().saveAdi( ile );

        // get a list of observers and send out notifications
        //---------------------------------------------------

        List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
        notificationManager.updateNotify( (IcPub) pub, ile, obsLst );        
    }

    public void updatePubAdminGroupMonitor( Object pub, Object luser, 
                                            Object rpub ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: update publication monitor called:" 
                   + " pub=" + pub 
                   + " luser=" + luser);
        
        IcLogEntry ile 
            = new IcLogEntry( (User) luser, (IcPub) pub,
                              "Admin group information updated.", "" );
        getAttachmentManager().getTracContext()
            .getAdiDao().saveAdi( ile );

        // get a list of observers and send out notifications
        //---------------------------------------------------
        
        List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
        notificationManager.updateNotify( (IcPub) pub, ile, obsLst );        
    }

    public void genImexMonitor( Object pub, Object luser, 
                                Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: imex ac monitor called:" 
                   + " pub=" + pub 
                   + " luser=" + luser 
                   + " imexac=" + ((IcPub)rpub).getImexId() );
        
        IcLogEntry ile 
            = new IcLogEntry( (User) luser, (IcPub) pub,
                              "IMEx accession assigned. IMEx Id=" 
                              + ((IcPub)rpub).getImexId(), "" );
        
        getAttachmentManager().getTracContext()
            .getAdiDao().saveAdi( ile );

        // get a list of observers and send out notifications
        //---------------------------------------------------
        
        List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
        notificationManager.updateNotify( (IcPub) pub, ile, obsLst );
    }
    
    public void statePubMonitor( Object pub,  Object luser, 
                                 Object state, Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "statePubMonitor: "
                   + " pub=" + pub + " luser=" + luser
                   + " state=" + state );
        
        String stateName = "";
        if( state instanceof java.lang.String){
            stateName = (String) state;
        } else {
            stateName = ((DataState)state).getName();
        }

        DataState ds = getAttachmentManager().getTracContext()
            .getWorkflowDao().getDataState( stateName );
            
                    
        IcLogEntry ile 
            = new IcLogEntry( (User) luser, (IcPub) pub,
                              "Publication state updated: " 
                              + ds.getName(), "" );

        getAttachmentManager().getTracContext()
            .getAdiDao().saveAdi( ile );
        
        // get a list of observers and send out notifications
        //---------------------------------------------------

        List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
        notificationManager.updateNotify( (IcPub) pub, ile, obsLst );        
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    public void addAttMonitor( Object att,  Object luser, Object ratt ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: attachment monitor called (add):"
                   + " att=" + att + " luser=" + luser );
             
        if( att == null || ! (att instanceof AttachedDataItem) ) return;

        AttachedDataItem adi = (AttachedDataItem) att;
        IcPub pub = (IcPub) adi.getRoot();
        
        String attName="";
            
        IcLogEntry ile = null;

        if( adi instanceof IcAttachment){
            ile = new IcLogEntry( (User) luser, pub,
                                  "Attachment added(ID#" + adi.getId() + 
                                  ": " + 
                                  ((IcAttachment)adi).getSubject() + ")", 
                                  "" );

            // update watch list if needed
            //----------------------------

            watchManager.addWatchByAttachmentPref( (User) luser, pub, false );            
        }
        
        if( adi instanceof IcComment ){
            ile = new IcLogEntry( (User) luser, pub,
                                  "Comment added(ID#" + adi.getId() +
                                  ": " +
                                  ((IcComment)adi).getSubject() + ")",
                                  "" );   

            // update watch list if needed 
            //----------------------------
            
            watchManager.addWatchByCommentPref( (User) luser, pub, false );
        }
        
        if( ile != null ){
            
            // log comments & attachments
            //---------------------------
            
            getAttachmentManager().getTracContext().getAdiDao().saveAdi( ile );
            
            // get a list of publication observers
            //------------------------------------

            List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
            
            // attachments
            //------------

            if( adi instanceof IcAttachment){
                
                List<User> uobsLst = new ArrayList<User>();
                
                for( Iterator<User> io = obsLst.iterator(); io.hasNext(); ){
                    
                    User co = io.next();
                    String mailFlag = PrefUtil
                        .getPrefOption( co.getPrefs(), "mail-record-watched" );
                    
                    if( watchManager.getWatchedAttachmentStatus( co ) ){                        
                        if( mailFlag != null
                            && mailFlag.equalsIgnoreCase( "true" ) ){
                            uobsLst.add( co );
                        }
                    }
                }
                
                // get a list of attachment observers, send out notifications
                //-----------------------------------------------------------
                
                List<User> aobsLst = watchManager.getAttachmentObserverList();
                
                if( aobsLst != null ){
                    log.debug( "LogManager: aobsLst.size=" 
                               + aobsLst.size() );
                    
                    for( Iterator<User> ia = aobsLst.iterator(); 
                         ia.hasNext(); ){
                        User cu = ia.next();
                        
                        if( !uobsLst.contains( cu ) ){
                            uobsLst.add( cu );
                        }
                    }
                }
                
                log.debug( "LogManager: uobsLst(attachment).size="
                           + uobsLst.size() );
                
                notificationManager.attachmentNotify( (IcPub) pub, 
                                                          ile, uobsLst );
            }
            
            // comments
            //---------
            
            if( adi instanceof IcComment){
                
                List<User> uobsLst = new ArrayList<User>();

                
                for( Iterator<User> io = obsLst.iterator(); io.hasNext(); ){
                    User co = io.next();

                    String mailFlag = PrefUtil
                        .getPrefOption( co.getPrefs(), "mail-record-watched" );
                    
                    if( watchManager.getWatchedCommentStatus( co ) ){
                        
                        if( mailFlag != null
                            && mailFlag.equalsIgnoreCase( "true" ) ){
                            uobsLst.add( co );
                        }
                    }
                }    
                
                // get a list of comment observers, send out notifications
                //--------------------------------------------------------
                
                List<User> cobsLst = watchManager.getCommentObserverList();
                
                if( cobsLst != null ){
                    log.debug( "LogManager: cobsLst.size=" 
                               + cobsLst.size() );
                    
                    for( Iterator<User> ic = cobsLst.iterator(); 
                         ic.hasNext(); ){
                        User cu = ic.next();
                        if( !uobsLst.contains( cu ) ){
                            uobsLst.add( cu );
                        }
                    }
                }
                
                log.debug( "LogManager: uobsLst(comments).size=" 
                           + uobsLst.size() );
                
                notificationManager.commentNotify( (IcPub) pub, 
                                                   ile, uobsLst );         
            }
        }
    }

    //--------------------------------------------------------------------------    

    public void delAttMonitor( int aid,  Object luser, Object ratt ){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "LogManager: attachment monitor called (delete):"
                   + " ratt=" + ratt + " luser=" + luser );
                
        if( ratt == null || ! (ratt instanceof AttachedDataItem) ) return;
        
        AttachedDataItem adi = (AttachedDataItem) ratt;
        IcPub pub = (IcPub) adi.getRoot();
        
        String attName = "";
        IcLogEntry ile = null;
        
        if( adi instanceof IcAttachment){
            ile = new IcLogEntry( (User) luser, pub,
                                  "Attachment dropped(ID#" + adi.getId() + 
                                  ": " + 
                                  ((IcAttachment)adi).getSubject() + ")", 
                                  "" );                
        } 
        if( adi instanceof IcComment ){
            ile = new IcLogEntry( (User) luser, pub,
                                  "Comment dropped(ID#" + adi.getId() +
                                  ": " +
                                  ((IcComment)adi).getSubject() + ")",
                                  "" );                
        }
        
        if( ile != null ){
            
            // log comments & attachments
            //---------------------------

            getAttachmentManager().getTracContext().getAdiDao().saveAdi( ile );

            // get a list of observers and send out notifications
            //---------------------------------------------------
            
            List<User> obsLst = watchManager.getObserverList( (IcPub) pub );
            notificationManager.updateNotify( (IcPub) pub, ile, obsLst );       
        }
    }
    
    public void newsMonitor( Object rnewsItem ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: news monitor called:" );
        log.debug( rnewsItem );
        
        List<User> usrNewsObsLst = watchManager.getNewsObserverList();
        notificationManager.newsNotify( (String) rnewsItem, usrNewsObsLst );        
    }

    public void newAccountMonitor( Object user ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: new account monitor called:");
        
        List<User> usrNewsObsLst = watchManager.getNewAccountObserverList();
        notificationManager.newAccountNotify( (User) user, usrNewsObsLst );
    }
}