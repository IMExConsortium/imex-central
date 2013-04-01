package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # NotificationManager -  
 #                 
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;

public class NotificationManager {
    
    public NotificationManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "NotificationManager: creating notification manager" );
    }
     
    String queueDir = "/var/icentral/queue";

    public void setQueueDir( String dirName ){
        queueDir = dirName;
    }

    public void initialize(){
        Log log = LogFactory.getLog( this.getClass() );
	log.info( "NotificationManager: initialized" );
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    public void updateNotify( Publication pub, IcLogEntry logEntry,
                              List<User> rcpLst){  
        recordNotify( pub, logEntry, rcpLst, "mail-record-watched" );
    }
    
    public void newRecordNotify( Publication pub, IcLogEntry logEntry,
                                 List<User> rcpLst ){    
        recordNotify( pub, logEntry, rcpLst, "mail-record-new" );
    }
    
    //--------------------------------------------------------------------------
    
    public void recordNotify( Publication pub, IcLogEntry logEntry,
                              List<User> rcpLst, String rcpFlag ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "updateNotify called" );

        if( pub == null || logEntry == null 
            || rcpLst == null || rcpLst.size() == 0 ){
            
            log.info( "updateNotify: DONE" );
            return;
        }
        
        String pubId = Integer.toString( pub.getId() );
        String pubAuthor = pub.getAuthor();
        String pubTitle = pub.getTitle();
        String pubPmid = pub.getPmid();
        
        String alert = logEntry.getLabel();
        String message;
                
        log.debug( "pub.getId()  = " + pubId );
        log.debug( "pub.getAuthor() = " + pubAuthor );
        log.debug( "pub.getTitle()  = " + pubTitle );
        log.debug( "pub.getPmid()  = " + pubPmid );
        log.debug( "ile.getLabel()  = " + alert );
        
        if( pubAuthor.length() > 70 )
            pubAuthor = pubAuthor.substring( 0, 70 );
        if( pubTitle.length() > 70 )
            pubTitle = pubTitle.substring( 0, 70 );


        String recipients = "";
        boolean send = false;
        
        Iterator<User> rcpi = rcpLst.iterator();
        
        while( rcpi.hasNext() ){
            User recipient = rcpi.next();
            
            String globalMailFlag = 
                PrefUtil.getPrefOption( recipient.getPrefs(), "message-mail" );

            String mailFlag = 
                PrefUtil.getPrefOption( recipient.getPrefs(), rcpFlag );
            
            if( globalMailFlag != null && mailFlag != null
                && globalMailFlag.equalsIgnoreCase( "true" ) 
                && mailFlag.equalsIgnoreCase( "true" ) 
                ){
                String rcptMail = recipient.getEmail();
                
                recipients += " " + rcptMail + ",";
                send = true;
            } 
        }

        if( !send ) return;  // no mail notifiactions requested
        
        recipients  = recipients.substring(0, recipients.length() - 1);
                                           
        message = "EMAIL=\"" + recipients + "\"\n" 
            + "MODE=\"RECORD_UPDATE\"\n" 
            + "ID=\"" + pubId + "\"\n" 
            + "AUTHOR=\"" + pubAuthor.replace("\"","\\\"") + "\"\n" 
            + "TITLE=\"" + pubTitle.replace("\"","\\\"") + "\"\n" 
            + "PMID=\"" + pubPmid.replace("\"","\\\"") + "\"\n" 
            + "ALERT=\"" + alert.replace("\"","\\\"") + "\"\n" ;
        
        //Write message information to a queue file
        
        try {
            String fileName = queueDir + File.separator 
                + String.valueOf( System.currentTimeMillis() ) 
                + ".queue";
            
            File file = new File( fileName );
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream( file );
            fout.write( ( message ).getBytes());
            fout.close();
            
        }catch ( IOException ex ) { 
            System.out.println( ex );
        }
    }
    
    public void newsNotify( String newsItem, List<User> rcpLst ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "newsNotify called" );

        if( newsItem == null || rcpLst == null || rcpLst.size() == 0 ){
            log.info( "newsNotify: DONE" );
            return;
        }       
        
        String recipients = "";
        boolean send = false;
        
        Iterator<User> rcpi = rcpLst.iterator();
        
        while( rcpi.hasNext() ){
            User recipient = rcpi.next();
            
            String globalMailFlag = 
                PrefUtil.getPrefOption( recipient.getPrefs(), "message-mail" );
            
            String mailFlag = PrefUtil.getPrefOption( recipient.getPrefs(), 
                                                      "mail-news" );
            if( globalMailFlag != null && mailFlag != null
                && globalMailFlag.equalsIgnoreCase( "true" )
                && mailFlag.equalsIgnoreCase( "true" ) ){
                
                String email = recipient.getEmail();
                recipients += " " + email  + ",";
                send = true;
            } 
        }

        if( !send ) return;  // no mail notifications requested
        
        recipients  = recipients.substring(0, recipients.length() - 1);
                                           
        String message = "EMAIL=\"" + recipients + "\"\n" 
            + "MODE=\"NEWS_ITEM\"\n" 
            + "NEWS_ITEM=\"" + newsItem.replace("\"","\\\"") +   "\"\n" ;
        
        //Write message information to a queue file
        
        try {
            String fileName = queueDir + File.separator 
                + String.valueOf( System.currentTimeMillis() ) 
                + ".queue";
            
            File file = new File( fileName );
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream( file );
            fout.write( ( message ).getBytes());
            fout.close();
            
        }catch ( IOException ex ) { 
            System.out.println( ex );
        }
    }
    
    public void newAccountNotify( User user, List<User> rcpLst ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "newAccountNotify called" );
        
        if( user == null || rcpLst == null || rcpLst.size() == 0 ){
            log.info( "newAccountNotify: DONE" );
            return;
        }        
        
        String recipients = "";
        boolean send = false;
        
        Iterator<User> rcpi = rcpLst.iterator();
        
        while( rcpi.hasNext() ){
            User recipient = rcpi.next();
            
            // NOTE: restricted only to current administrators
            //------------------------------------------------

            if( recipient.getAllRoleNames().contains( "administrator" ) ){

                String globalMailFlag = 
                    PrefUtil.getPrefOption( recipient.getPrefs(), 
                                            "message-mail" );
                
                String mailFlag = PrefUtil.getPrefOption( recipient.getPrefs(), 
                                                          "mail-account-new" );

                if( globalMailFlag != null && mailFlag != null
                    && globalMailFlag.equalsIgnoreCase( "true" )
                    && mailFlag.equalsIgnoreCase( "true" ) ){
                
                    String email = recipient.getEmail();
                    recipients += " " + email + ",";
                    send = true;
                } 
            }
        }
        
        if( !send ) return;  // no mail notifications requested
        
        recipients  = recipients.substring(0, recipients.length() - 1);
        
        String login = user.getLogin();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String affiliation = user.getAffiliation();
        String email = user.getEmail();
        
        String message = "EMAIL=\"" + recipients + "\"\n"
            + "MODE=\"NEW_ACCOUNT\"\n" 
            + "NEW_LOGIN=\"" + login + "\"\n" 
            + "FIRST_NAME=\"" + firstName.replace("\"","\\\"") + "\"\n" 
            + "LAST_NAME=\"" + lastName.replace("\"","\\\"") + "\"\n" 
            + "AFFILIATION=\"" + affiliation + "\"\n" 
            + "EMAIL=\"" + email + "\"\n" ;
        
        //Write message information to a queue file
        
        try {
            String fileName = queueDir + File.separator 
                + String.valueOf( System.currentTimeMillis() ) 
                + ".queue";
            
            File file = new File( fileName );
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream( file );
            fout.write( ( message ).getBytes());
            fout.close();
            
        }catch ( IOException ex ) { 
            System.out.println( ex );
        }
    }
}
