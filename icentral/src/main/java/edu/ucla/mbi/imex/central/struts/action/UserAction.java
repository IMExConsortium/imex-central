package edu.ucla.mbi.imex.central.struts.action;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * UserAction action
 *                
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;

import java.util.*;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.vps.crypt.Crypt;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class UserAction extends UserSupport {

    //---------------------------------------------------------------------
    // new user registration
    //---------------------

    private String notifyFrom;

    public void setNotifyFrom( String from ) {
        this.notifyFrom = from;
    }
    
    private String notifyServer;

    public void setNotifyServer( String server ) {
        this.notifyServer = server;
    }

    public String register( User user ) {

	Log log = LogFactory.getLog( this.getClass() );
        log.info( " register:" + user );
        
        UserDao dao = getUserContext().getUserDao();
        IcUser icUser = new IcUser( user );
	
	// set password
	//-------------

	icUser.encryptPassword( pass0 );
	
	// generate activation key
	//------------------------
	
	icUser.setActivationKey();
	log.info( " activationKey: " + icUser.getActivationKey() );

	icUser.setActivated( false );
	icUser.setEnabled( true );
	
	// sent notification
	//------------------

	icUser.notifyByMail( notifyFrom, notifyServer );
	
	// create new account 
	//-------------------
	
	dao.saveUser( icUser );
	
	log.info( " Account created: " + user.getLogin() +
		  " (" + user.getId() + ")" );
	
	return ACTIVATE;
    }


    //---------------------------------------------------------------------
    // user activation
    //----------------

    public String activate( User user ) {

	Log log = LogFactory.getLog( this.getClass() );
        log.info( " activate:" + user );

        if ( user != null ){

            log.info( " login:" + user.getLogin() );

            UserDao dao =  getUserContext().getUserDao();
            IcUser icUser = (IcUser) dao.getUser( getUser().getLogin() );
	    
            if ( icUser != null &&
                 icUser.testPassword( getPass0() ) ) {

		if ( !icUser.testActivationKey( getUser().getActivationKey() ) 
		     ) {
		    addFieldError( "user.activationKey", 
				   "Activation key does not match." );
		    return ACTIVATE;
		} 
		
		icUser.setActivated( true );
		dao.updateUser( icUser );
		
                // valid user
                //-----------
		
                getSession().put( "USER_ID", icUser.getId() );
                getSession().put( "LOGIN", icUser.getLogin() );

                Map<String,Integer> roles = new HashMap();
                Map<String,Integer> groups = new HashMap();
                
		if ( icUser.getRoles()  != null ) {
                    for ( Iterator ii = icUser.getRoles().iterator();
                          ii.hasNext(); ) {
                        IcRole r = (IcRole) ii.next();
                        log.info( "  role=" + r.toString() );
                        roles.put( r.getName(), r.getId() );
                    }
                    
                }

                if ( icUser.getGroups() != null ) {
                    for ( Iterator ig = icUser.getGroups().iterator();
                          ig.hasNext(); ) {
                        IcGroup g = (IcGroup) ig.next();
                        log.info( "  group=" + g.toString() );
                        groups.put( g.getLabel(), g.getId() );
                        
                        if ( g.getRoles()  != null ) {
                            for ( Iterator ir = g.getRoles().iterator();
                                  ir.hasNext(); ) {
                                IcRole r = (IcRole) ir.next();
                                log.info( "  role=" + r.toString() );
                                roles.put( r.getName(), r.getId() );
                            }
                        }
                    }
                }
                
                getSession().put( "USER_ROLE", roles );
                getSession().put( "USER_GROUP", groups );
		log.info( " login: session set" );

                return HOME;
            }
	}
	
	return INPUT;
    }


    //---------------------------------------------------------------------
    // user edit
    //----------

    private String uedit;
    
    public void setUedit( String uedit ) {
        this.uedit = uedit;
    }

    public String edit() {
	
	Log log = LogFactory.getLog( this.getClass() );
        log.debug( " edit: uid=" + getSession().get( "USER_ID" ) );
        
	int uid = (Integer) getSession().get( "USER_ID" );
        
	if ( uid > 0 ){

            UserDao dao = getUserContext().getUserDao();
            IcUser icUser = (IcUser) 
		dao.getUser( (String) getSession().get( "LOGIN") );
            if ( icUser != null ) {
                log.debug( " icUser=" + icUser );
		if ( uedit != null && uedit.equalsIgnoreCase( "save" ) ) {

		    log.info( " edit: updating uid=" + uid );

		    // incorporate form changes
		    //-------------------------

		    icUser.setFirstName( getUser().getFirstName() );
		    icUser.setLastName( getUser().getLastName() );
		    icUser.setAffiliation( getUser().getAffiliation() );
		    icUser.setEmail( getUser().getEmail() );
 
		    if ( pass0 != null && icUser.testPassword( pass0 ) ) {
			icUser.encryptPassword( pass1 );			
		    }

		    // store new settings
		    //-------------------
		    
		    dao.updateUser( icUser );				    		    
		}
                
		if ( uedit != null && uedit.equalsIgnoreCase( "reset" ) ) {

		    log.debug( " edit: resetting uid=" + uid );
		}

		if (getUser() == null ){
		    setUser( new User() );
		}
                getUser().setLogin( (String) getSession().get( "LOGIN"));
		getUser().setFirstName( icUser.getFirstName() );
		getUser().setLastName( icUser.getLastName() );
		getUser().setAffiliation( icUser.getAffiliation() );
		getUser().setEmail( icUser.getEmail() );

	    }
	    
	    setPass0("");
	    setPass1("");
	    setPass2("");

	    return UEDIT;
	}

	return HOME;
    }


    //---------------------------------------------------------------------
    // user login
    //------------

    public String login( User user ) {

	Log log = LogFactory.getLog( this.getClass() );
	
	if ( user != null ){

	    log.info( " login:" + user.getLogin() );
	
            UserDao dao = getUserContext().getUserDao();
            IcUser icUser = (IcUser) dao.getUser( getUser().getLogin() );
	    
            
	    if ( icUser !=null && 
		 icUser.testPassword( getPass0() ) ) {

		// valid user
		//-----------
		
                if ( !icUser.isActivated() ) return ACTIVATE;

		getSession().put( "USER_ID", icUser.getId() );
		getSession().put( "LOGIN", icUser.getLogin() );
		log.debug( " login: session set" );

                Map<String,Integer> roles = new HashMap();
                Map<String,Integer> groups = new HashMap();

		if ( icUser.getRoles()  != null ) {

		    for ( Iterator ii = icUser.getRoles().iterator(); 
			  ii.hasNext(); ) {
			IcRole r = (IcRole) ii.next();
			log.debug( "  role=" + r.toString() );
			roles.put( r.getName(),r.getId());
		    }	    
                }
                
                if ( icUser.getGroups() != null ) {
                    for ( Iterator ig = icUser.getGroups().iterator();
                          ig.hasNext(); ) {
                        Group g = (Group) ig.next();
                        log.debug( "  group=" + g.toString() );
                        groups.put( g.getLabel(), g.getId() );

                        if ( g.getRoles()  != null ) {
                            for ( Iterator ir = g.getRoles().iterator();
                                  ir.hasNext(); ) {
                                IcRole r = (IcRole) ir.next();
                                log.debug( "  role=" + r.toString() );
                                roles.put( r.getName(), r.getId() );
                            }
                        }
                    }
                }

                getSession().put( "USER_ROLE", roles );
                getSession().put( "USER_GROUP", groups );
                log.debug( " login: session set" );

		return HOME;
	    }

	    if( icUser != null ){
		log.debug( " login: id=" + icUser.getId() );
		log.debug( " login: oldpass=" + icUser.getPassword() );
	    }
            if( getPass1() != null ){
                log.debug( " login: newpass" + getPass1() );
                log.debug( " login: " + Crypt.crypt( "ab", getPass1() ) );
            }
	}
	log.info( " login: unknown user" );
	addActionError( "User/Password not recognized." );
	return INPUT;
    }

    //---------------------------------------------------------------------
    // user logout
    //------------

    public String logout() {

	Log log = LogFactory.getLog( this.getClass() );
	log.info( " logout: " + getSession().get( "LOGIN" ) );

	getSession().put( "USER_ID", -1 );
	getSession().put( "USER_ROLE", null );
	getSession().put( "LOGIN", "" );
	
	return HOME;
    }

    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // terms agree
    //------------
    
    private boolean agree;

    public void setAgree( boolean agree ) {

	this.agree = agree;
    }

    public boolean getAgree() {
	return this.agree;
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


    //---------------------------------------------------------------------
    // new password
    //--------------

    private String pass0;

    public void setPass0( String pass ) {
        this.pass0 = pass;
    }

    public String getPass0() {
	return this.pass0;
    }

    private String pass1;

    public void setPass1( String pass ) {
        this.pass1 = pass;
    }

    public String getPass1() {
	return this.pass1;
    }

    private String pass2;

    public void setPass2( String pass ) {
        this.pass2 = pass;
    }

    public String getPass2() {
	return this.pass2;
    }
    
    
    //---------------------------------------------------------------------

    public void validate() {

	Log log = LogFactory.getLog( this.getClass() );
		    
	// registration options
	//---------------------

	if( getOp() != null && getOp().equalsIgnoreCase( "reg" ) ) { 

	    // test login
	    //-----------

	    if( getUser() != null ){
		log.debug( " validate:" + getUser().getLogin() );
                
                UserDao dao = getUserContext().getUserDao();
                IcUser oldUser = 
                    (IcUser) dao.getUser( getUser().getLogin() );
		if( oldUser != null ){
		    addFieldError( "user.login","User name already taken. " +
				   "Please, select another one.");
		    log.debug( " old login... id=" + oldUser.toString() );
		} 
	    }

	    // test recaptcha
	    //---------------

	    if( recaptcha != null ) {

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

	    // test password typos
	    //--------------------
	    
	    if( pass0 != null && pass1 != null && !pass0.equals( pass1 ) ) {
		addFieldError( "pass1", "Passwords do not match." );
	    }
	    return;
	}


	// edit options
	//-------------

	if( getOp() != null && getOp().equalsIgnoreCase( "edit" ) ) { 

	    // test passoword typos
            //---------------------

            if( pass0 != null && pass0.length() > 0 ){

                UserDao dao = getUserContext().getUserDao();
                IcUser oldUser = 
                    (IcUser) dao.getUser( getUser().getLogin() );
                
		if ( !oldUser.testPassword( pass0 ) ) {
		    addFieldError( "pass0", "Wrong password." );
		} else {
		    if ( pass1 == null || !pass1.equals( pass0) ) {
			addFieldError( "pass1", "Passwords do not match." );
		    }
		}	
	    }	    
	    return;
	}

	// activate options
	//-----------------

	if( getOp() != null && getOp().equalsIgnoreCase( "activate" ) ) { 
            return;
	}
    }
}
