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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.Crypt;

import javax.mail.*;
import javax.mail.internet.*;


import edu.ucla.mbi.util.data.User;

public class IcUser extends User {

    private String sha1pass = "";
    private String passcrypt = "";
    
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
    
    public String getSha1pass(){
        return sha1pass;
    }

    public void setSha1pass( String pass ){
        this.sha1pass = pass;
    }

    public String getPassCrypt(){
        return passcrypt;
    }

    public void setPassCrypt( String pass ){
        this.passcrypt = pass;
    }
    
    private GregorianCalendar loginTime;

    public void setLoginTime( GregorianCalendar time) {
        this.loginTime = time;
    }

    public GregorianCalendar getLoginTime() {
        return this.loginTime;
    }
        
    private String encrypSHA1(String input ){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }catch( NoSuchAlgorithmException e ){
            throw new RuntimeException(e);
        }
    } 

    public void encryptPassword( String pass ) {
        //super.encryptPassword( pass );
        this.passcrypt = Crypt.crypt( pass );
        this.sha1pass = Crypt.crypt( encrypSHA1( pass ) );
    }
    
    public boolean testPassword( String pass ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "Test Password: " );
        
        if( ( sha1pass == null || sha1pass.equals( "" )) &&
            ( getPassword() == null || getPassword().equals( "" )) &&
            ( passcrypt == null ||passcrypt.equals( "" ) ) ) {
            return true;
        } 
        
        if( pass == null || pass.equals( "" )  ) {
            return false;
        }
        
        if( pass.startsWith( "SHA1:") && (pass.length() > 5 ) ){
            return testSHA1Password( pass.substring(5) );
        }

        if( passcrypt == null || passcrypt.equals( "" ) ){

            // update encrypted password
            
            if( super.testPassword( pass ) ){
                this.passcrypt = Crypt.crypt( pass );
                super.setPassword("");
            }
        }

        if( sha1pass == null || sha1pass.equals( "" ) ){

            // update SHA1-encrypted password
            
            if( super.testPassword( pass ) ){                
                this.sha1pass = Crypt.crypt( encrypSHA1( pass ) );                
            }
        }
        
        return passcrypt.equals( Crypt.crypt( pass, passcrypt ) );
    } 

    public boolean testSHA1Password( String pass ) {
        
        if( sha1pass == null || sha1pass.equals( "" )  ) {
            //return true;
            return false;
        }

        if( pass == null || pass.length() == 0 ) {
            return false;
        }
         
        if( sha1pass.equals( Crypt.crypt( pass, sha1pass ) ) ) {
            return true;           
        }
        return false;
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

        Log log = LogFactory.getLog( this.getClass() );
        log.debug("from: " + from + " server: " + server);

        Properties props = new Properties();
        props.put("mail.from", from);
        props.put("mail.smtp.host", server);
        
        Session session = Session.getInstance(props, null);

        //----------------------------------------------------------------------
        //sanitize
        //--------
    
        String sFirstName = sanitize( getFirstName(), 16 );    
        String sLastName = sanitize( getLastName(), 32 );
        String sLogin = sanitize( getLogin(), 32 );
        
        //----------------------------------------------------------------------
        
    
        String message = 
            "Thank you for registering as an ImexCentral user.\n" +
            "In order to activate your ImexCentral account (user name: " + 
            sLogin + "), please, use the key:\n\n" +
            "    " + getActivationKey() +"\n\n" +
            "when loging in for the first time.  Without activation\n" +
            "the account might be terminated shortly.\n\n\n"+
            "Regards,\nThe ImexCentral Deamon\n\n";
        
        log.debug("message:  "+ message);
        
        //----------------------------------------------------------------------

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
    
    private String sanitize( String field, int maxlen ){

        String sfield = field;
        
        if( field == null ) return "";
        sfield = sfield.trim();
        
        int spi = field.indexOf( " " ); 
        if(spi > 0 ){
            sfield = sfield.substring( 0, spi );
            if( sfield.length() > maxlen ){
                sfield = sfield.substring( maxlen );
            }            
        }
        
        return sfield;    
    }    
}
