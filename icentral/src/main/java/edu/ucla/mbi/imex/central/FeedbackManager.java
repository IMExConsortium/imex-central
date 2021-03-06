package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # FeedbackManager - businness logic of entry/journal management 
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

public class FeedbackManager {
    
    public FeedbackManager() {
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "FeedbackManager: creating manager" );
    }

    //---------------------------------------------------------------------
    //  UserContext
    //--------------
    
    private UserContext userContext;

    public void setUserContext( UserContext context ) {
        this.userContext = context;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }
    
    //---------------------------------------------------------------------
    // mail config
    //------------

    String adminMail;

    public String getAdminMail() {
        return adminMail;
    }

    public void setAdminMail( String mail ) {
        adminMail = mail;
    }

    String mailServer;

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer( String server ) {
        mailServer = server;
    }


    //---------------------------------------------------------------------
    
    public void initialize(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "FeedbackManager: initializing" );
    }


    public void cleanup(){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "FeedbackManager: cleanup" );
    }


    
    //---------------------------------------------------------------------
    // Operations
    //---------------------------------------------------------------------
    // registered user feedback
    //-------------------------

    public void regUserFeedback( Integer uid, 
                                 String about, String comment ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "regUserFeedback");
   
        IcUser icUser = (IcUser) userContext.getUserDao().getUser( uid );

        icUser.sendComment( adminMail, mailServer, about, comment );
    }

    //---------------------------------------------------------------------
    // email usee feedback
    //--------------------
    
    public void mailUserFeedback( String email, 
                                  String about, String comment ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "mailUserFeed: email=" + email);

        if ( email != null ) {
            try {
                email = email.replaceAll("^\\s+","");
                email = email.replaceAll("\\s+$","");
            } catch (Exception e ) {
                // cannot be here
            }
        }

        if ( email != null ) {
            comment = "\n\nFrom: " + email + "\n\n" + comment;
        }
        IcUser.sendComment( adminMail, adminMail, mailServer,
                            about, comment );
    }
}
 
