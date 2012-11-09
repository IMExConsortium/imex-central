package edu.ucla.mbi.imex.central;

/* =========================================================================
 # $HeadURL::                                                              $
 # $Id::                                                                   $
 # Version: $Rev::                                                         $
 #==========================================================================
 #
 # IcUser
 #
 #
 #======================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;


import edu.ucla.mbi.util.User;

public class IcUser extends User {

    public IcUser() {}
    
    public IcUser( User user ) {
	
	setFirstName( user.getFirstName() );
	setLastName( user.getLastName() );
	setAffiliation( user.getAffiliation() );
	setTitle( user.getTitle() ); 
	setEmail( user.getEmail() );
	setLogin( user.getLogin() ); 
	setActivated( false );
	setEnabled( false );

        setActivationKey( user.getActivationKey() );
        setPassword( user.getPassword() );



        if ( user.getRoles() != null ) {
            getRoles().addAll( user.getRoles() );
        }

        if ( user.getGroups() != null ) {
            getGroups().addAll( user.getGroups() );
        }
        
    }

    public String toString() {
	
	StringBuffer sb = new StringBuffer();
	
	sb.append( " IcUser(id=" + getId() );
	sb.append( " login=" + getLogin() );
	sb.append( " email=" + getEmail() );
	sb.append( ")" );

	return sb.toString();
    }

    public void notifyByMail( String from, String server ) {

	Properties props = new Properties();
	props.put("mail.from", from);
	props.put("mail.smtp.host", server);
	
	Session session = Session.getInstance(props, null);

	//-----------------------------------------------------------------

	String message = 
	    "Dear " + getFirstName() + " " + getLastName() + ",\n" +
	    " thank you for registering as an ImexCentral user.\n" +
	    "In order to activate your ImexCentral account (user name: " + 
	    getLogin() + "), please, use the key:\n\n" +
	    "    " + getActivationKey() +"\n\n" +
	    "when loging in for the first time.  Without activation\n" +
	    "the account might be terminated shortly.\n\n\n"+
	    "Regards,\nThe ImexCentral Deamon\n\n";
	
	//-----------------------------------------------------------------

	try {
	    MimeMessage msg = new MimeMessage( session );
	    msg.setFrom();
	    msg.setRecipients( Message.RecipientType.TO,
			       getEmail() );
	    msg.setSubject( "ImexCentral Account Activation" );
	    msg.setSentDate( new Date() );
	    msg.setText( message );
	    Transport.send( msg );
	} catch ( MessagingException mex ) {
	    System.out.println("send failed, exception: " + mex);
	}
    }

    public void sendComment( String to, String server,
			     String about, String comment ) {
	
	Log log = LogFactory.getLog( this.getClass() );

	Properties props = new Properties();
        props.put("mail.from", getEmail() );
        props.put("mail.smtp.host", server);
	
        Session session = Session.getInstance(props, null);

	try {
            MimeMessage msg = new MimeMessage( session );
            msg.setFrom();
            msg.setRecipients( Message.RecipientType.TO,
                               to );
            msg.setSubject( "DIP Feedback:" + about);
            msg.setSentDate( new Date() );
            msg.setText( comment );
            Transport.send( msg );

	    log.info( "send ok: " + to );

        } catch ( MessagingException mex ) {
	    
	    log.info( "send to: " + to );
	    log.info( "send failed, exception: " + mex );
        }
    }

    public static void sendComment( String from, String to, String server, 
				    String about, String comment ) {
	
	Log log = LogFactory.getLog( IcUser.class );

	Properties props = new Properties();
        props.put("mail.from", from );
        props.put("mail.smtp.host", server);
	
        Session session = Session.getInstance(props, null);

	try {
            MimeMessage msg = new MimeMessage( session );
            msg.setFrom();
            msg.setRecipients( Message.RecipientType.TO,
                               to );
            msg.setSubject( "ImexCentral Feedback:" + about );
            msg.setSentDate( new Date() );
            msg.setText( comment );
            Transport.send( msg );
	    log.info( "send ok: " + to );

        } catch ( MessagingException mex ) {
	    log.info( "send to: " + to );
	    log.info( "send failed, exception: " + mex );
        }
    }
}
