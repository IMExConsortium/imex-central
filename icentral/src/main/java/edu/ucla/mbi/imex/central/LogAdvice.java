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

            log.debug( "LogManager->addPubMonitor: id(pub)=" + ile.getRoot().getId() );

            getAttachmentManager().getTracContext()
                .getAdiDao().saveAdi( ile );

            //------------------------------------------------------------------
            // NOTE(LS): check if owner has the global auto watch option
            //           and watch all/my records options set; if yes
            //           add SORel record


            //------------------------------------------------------------------
            // NOTE(LS): get a list of users interested in record creation
                         
            //List<User> usrAddObsList = watchManager.getNewRecordObserverList();
            
            //           send messages to each user on the list provided global
            //           send mail user pref is set.
                      

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
        
        //----------------------------------------------------------------------   
        // NOTE(LS): get a list of observers
        
        //SorelDao sorelManager
        //    = getAttachmentManager().getTracContext().getSorelDao();
        // List usersWatchList = sorelManager.getObserverList( pub );

        //           send messages only if corresponding user pref 
        //           options (send mail and send watched rec info) 
        //           options  are set
                                       
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

    
        //----------------------------------------------------------------------   
        // NOTE(LS): get a list of observers
        
        //SorelDao sorelManager
        //    = getAttachmentManager().getTracContext().getSorelDao();
        // List usersWatchList = sorelManager.getObserverList( pub );

        //           send messages only if corresponding user pref 
        //           options (send mail and send watched rec info) 
        //           options  are set

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

        //----------------------------------------------------------------------
        // NOTE(LS): get a list of observers

        //SorelDao sorelManager
        //    = getAttachmentManager().getTracContext().getSorelDao();
        // List usersWatchList = sorelManager.getObserverList( pub );

        //           send messages only if corresponding user pref
        //           options (send mail and send watched rec info)
        //           options  are set
        
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

        //----------------------------------------------------------------------   
        // NOTE(LS): get a list of observers
        
        //SorelDao sorelManager
        //    = getAttachmentManager().getTracContext().getSorelDao();
        // List usersWatchList = sorelManager.getObserverList( pub );

        //           send messages only if corresponding user pref 
        //           options (send mail and send watched rec info) 
        //           options  are set
        
    }

    public void statePubMonitor( Object pub,  Object luser, 
                                 Object state, Object rpub ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: update publication state monitor called:"
                   + " pub=" + pub + " luser=" + luser
                   + " state=" + state );
        
        if( 1 == 1 ){  // from LogContext configureation file

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
        }

        //----------------------------------------------------------------------   
        // NOTE(LS): get a list of observers
        
        //SorelDao sorelManager
        //    = getAttachmentManager().getTracContext().getSorelDao();
        // List usersWatchList = sorelManager.getObserverList( pub );

        //           send messages only if corresponding user pref 
        //           options (send mail and send watched rec info) 
        //           options  are set

    }
    
    public void addAttMonitor( Object att,  Object luser, Object ratt ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: attachment monitor called:"
                   + " att=" + att + " luser=" + luser );
        
        if( 1 == 1 ){  // from LogContext configureation file
            
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
            } 
            if( adi instanceof IcComment ){
                ile = new IcLogEntry( (User) luser, pub,
                                      "Comment added(ID#" + adi.getId() +
                                      ": " +
                                      ((IcComment)adi).getSubject() + ")",
                                      "" );                
            }
            if( ile != null ){
                // log comments & attachments
                
                getAttachmentManager().getTracContext()
                    .getAdiDao().saveAdi( ile );
                
                // get observers forf <pub> publication
                //------------------------------------
                
                SorelDao sorelManager 
                    = getAttachmentManager().getTracContext().getSorelDao();
                List usersWatchList = sorelManager.getObserverList( pub );
                
                log.info("usersWatchList = " + usersWatchList);

                // NOTE(LS): add SORel only if the corresponding user prefer
                //            is set
                
                if( usersWatchList.contains( (User) luser ) == false ){
                    log.info("%%% adding User " + luser + " %%%");
              
                    sorelManager.addSORel(pub, (User) luser);
                    //add user to UserWatchList
                    usersWatchList.add((User) luser);
                }
                
                // trigger mail agent process
                //---------------------------
                //Get message Information

                String pubAuthor, pubTitle, pubId, pubPmid, alert, message;
                log.info("pub.getId()  = " + (pubId = pub.getId() + "") );
                log.info("pub.getAuthor() = " + ( pubAuthor = pub.getAuthor()));
                log.info("pub.getTitle()  = " + (pubTitle = pub.getTitle() ));
                log.info("pub.getPmid()  = " + (pubPmid = pub.getPmid()) );
                log.info("ile.getLabel()  = " + (alert = ile.getLabel()));
                if(pubAuthor.length() > 70)
                    pubAuthor = pubAuthor.substring(0, 70);
                if(pubTitle.length() > 70)
                    pubTitle = pubTitle.substring(0, 70);
                
                Iterator userWatchIterator = usersWatchList.iterator();
                //turn list of email addresses into csv
                String recipients = "";

                // NOTE(LS): send only if corresponding user pref options
                //           (send mail and send watched rec info) options
                //           are set
                
                while( userWatchIterator.hasNext() ){
                    String userEmail = ((User) luser).getEmail();
                    recipients += userEmail + " ";
                    log.info("userEmail = " + userEmail);
                    log.info( (User)userWatchIterator.next());
                }
                message = "EMAIL=\"" + recipients + "\"\n" +
                    "ID=\"" + pubId + "\"\n" +
                    "AUTHOR=\"" + pubAuthor + "\"\n" +
                    "TITLE=\"" + pubTitle + "\"\n" +
                    "PMID=\"" + pubPmid + "\"\n" +
                    "ALERT=\"" + alert + "\"\n" ;
                //Write message information to queue file
                
                try {
                    
                    // NOTE(LS): define get/setMailQueueDir(String path) method
                    //       and set the location through Spring
                    
                    String fileName = "/tmp/var/icentral/queue/"
                        + String.valueOf(System.currentTimeMillis()) 
                        + ".queue";
                    
                    File file = new File( fileName );
                    file.createNewFile();
                    FileOutputStream fout = new FileOutputStream(file);
                    fout.write( ( message ).getBytes());
                    fout.close();
                    
                }catch ( IOException ex ) { 
                    System.out.println(ex);
                }
                
            }
        }
    }

    //--------------------------------------------------------------------------
    // NOTE(LS): create a pointcut in
    //
    //               icentral/src/main/resources/spring/aop-logger.xml
    //
    //            that will result in newsMonitor called every time a new 
    //            news item is created - convenient place might be a call to
    //
    //               buildMailAnno( date, time, header, body, email );
    // 
    //            method within NewsAction class (the arguments of the
    //            newsMonitor method will likely have to be changed. The call 
    //            is, though, internal to NewsAction so it might not work... 
    

    public void newsMonitor( Object att,  Object luser, Object newsItem ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: news monitor called:"
                   + " att=" + att + " luser=" + luser );

        //----------------------------------------------------------------------
        // NOTE(LS): get a list of users interested in record creation

        //List<User> usrNewsObsList = watchManager.getNewsObserverList();
        
        //           send newsItem to each interested user provided global
        //           send mail user pref is set. 
        
    }

    //--------------------------------------------------------------------------
    // NOTE(LS): create a pointcut in
    //
    //               icentral/src/main/resources/spring/aop-logger.xml
    //
    //            that will result in newAccountMonitor called every time a new
    //            account is created - convenient place might be a call to
    //              
    //               UserMgrSupport.addUser (User)
    //
    //            method (the arguments of the newAcountMonitor method will 
    //            likely have to be changed). The call is, though internal 
    //            to UserMgrSupport so it might not work...
    
    
    public void newAccountMonitor( Object att,  Object luser, Object newsItem ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LogManager: new account monitor called:");

        //----------------------------------------------------------------------
        // NOTE(LS): get a list of users interested in account creation
        
        //List<User> usrNewsObsList = watchManager.getNewAccountObserverList();
        
        //           send info on new account to each interested user provided 
        //           global send mail user pref is set.

                     
    }
     
    public void delAttMonitor( int aid,  Object luser, Object ratt ){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "LogManager: attachment monitor called:"
                   + " ratt=" + ratt + " luser=" + luser );
        
        /*
        if( 1 == 1 ){  // from LogContext configureation file
            
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
            } 
            if( adi instanceof IcComment ){
                ile = new IcLogEntry( (User) luser, pub,
                                      "Comment added(ID#" + adi.getId() +
                                      ": " +
                                      ((IcComment)adi).getSubject() + ")",
                                      "" );                
            }
                            
            if( ile != null ){

                // log comments & attachments

                getAttachmentManager().getTracContext()
                    .getAdiDao().saveAdi( ile );
                

                // get observers for <pub> publication
                //------------------------------------
                
              
                // trigger mail agent process
                //---------------------------

                
                

  
            }
        */
    }
    
}
