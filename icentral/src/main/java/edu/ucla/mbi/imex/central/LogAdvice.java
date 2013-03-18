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
                ObsMgrDao sorelManager = getAttachmentManager().getTracContext().getObsMgrDao();
                List usersWatchList = sorelManager.getObserverList( pub );
                
                log.info("usersWatchList = " + usersWatchList);
                if(usersWatchList.contains( (User) luser ) == false)
                {
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
                while(userWatchIterator.hasNext())
                {
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
                    File file = new File("/tmp/var/icentral/queue/" + String.valueOf(System.currentTimeMillis()) + ".queue");
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
