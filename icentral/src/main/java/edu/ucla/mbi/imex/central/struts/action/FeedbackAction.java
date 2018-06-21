package edu.ucla.mbi.imex.central.struts.action;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * FeedbackAction action
 *                
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.vps.crypt.Crypt;

import java.util.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class FeedbackAction extends PortalSupport {

    public static final String ACCEPTED = "accepted";

    //---------------------------------------------------------------------
    // configuration
    //--------------
    
    FeedbackManager feedbackManager;

    public FeedbackManager getFeedbackManager() {
        return feedbackManager;
    }

    public void setFeedbackManager( FeedbackManager manager ) {
        this.feedbackManager = manager;
    }

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
    // registered feedback
    //--------------------

    public String regFeed() {
	
	Log log = LogFactory.getLog( this.getClass() );
        log.info( "regFeed");	
	
	Integer uid = (Integer) getSession().get( "DIP_USER_ID" );
        
        feedbackManager.regUserFeedback( uid, getAbout(), getComment());
        
	return ACCEPTED;
    }
    
    //--------------------------------------------------------------------------
    // email feedback
    //----------------

    public String mailFeed() {

	Log log = LogFactory.getLog( this.getClass() );
        log.info( "mailFeed: email=" + email);
	
        feedbackManager.mailUserFeedback( email, getAbout(), getComment());
	
        return ACCEPTED;
    }
    
    //--------------------------------------------------------------------------

    String submit;

    public String getSubmit() {
	return submit;
    }

    public void setSubmit( String submit ) {
	this.submit = submit;
    }


    String email;

    public void setEmail( String email ) {
	this.email = email;
    }

    public String getEmail() {
        return email;
    }

    String comment;

    public void setComment( String comment ) {
	this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    String about;

    public void setAbout( String about ) {
	this.about = about;
    }

    public String getAbout() {
        return about;
    }


    //---------------------------------------------------------------------
    // recaptcha
    //----------
    
    private ReCaptcha recaptcha = null;

    public void setReCaptcha( ReCaptcha recaptcha ) {

	this.recaptcha = recaptcha;
    }

    private String rcf;

    public void setRecaptcha_challenge_field( String field ) {
	this.rcf = field;
    }

    private String rrf;

    public void setRecaptcha_response_field( String field ) {
	this.rrf = field;
    }

    private boolean reCaptchaActive = false;

    public void setReCaptchaActive( boolean active ) {
        this.reCaptchaActive = active;
    }

    public boolean getReCaptchaActive(){
        return this.reCaptchaActive;
    }

    public boolean isReCaptchaActive(){
        return this.reCaptchaActive;
    }


    //--------------------------------------------------------------------------

    public String execute() throws Exception {

	if( getSubmit() != null && getSubmit().length() > 0 ) {

	    if ( getSession().get( "DIP_USER_ID" ) != null &&
		 (Integer) getSession().get( "DIP_USER_ID" )  > 0 ) {
		return regFeed();
	    } else {
		return mailFeed();
	    }
	} 
	return SUCCESS;
    }
    
    //---------------------------------------------------------------------

    public void validate() {

	Log log = LogFactory.getLog( this.getClass() );
		    
	if( getSubmit() != null && getSubmit().length() > 0 ) {


	    String comment = getComment();
	    if ( comment != null ) {
		try {
		    comment = comment.replaceAll("^\\s+","");
		    comment = comment.replaceAll("\\s+$","");
		} catch ( Exception ex ) {
		    // cannot be here 
		}
		setComment( comment );
	    }

	    if ( comment == null || comment.length() == 0 ) {
		addFieldError( "comment",
			       "Comment field cannot be left empty" );
	    }
	    
	    if ( getSession().get( "DIP_USER_ID" ) != null &&
		 (Integer) getSession().get( "DIP_USER_ID" )  > 0 ) {
		
		
		
	    } else {
		
		// test recaptcha
		//---------------

		if( isReCaptchaActive() && recaptcha != null ) {
		    
		    ReCaptchaResponse reCaptchaResponse = 
			recaptcha.checkAnswer( ServletActionContext.
					   getRequest().getRemoteHost(),  
					       rcf, rrf );  
		    
		    if ( !reCaptchaResponse.isValid() ) {  
			addActionError("Not a good CAPTCHA");
		    } else {
			
			log.info( "  recaptcha response=" + 
				  reCaptchaResponse.getErrorMessage() );
		    }
		}
	    }
	}
    }
}
