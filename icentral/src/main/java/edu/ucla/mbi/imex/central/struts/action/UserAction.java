package edu.ucla.mbi.imex.central.struts.action;

/* =========================================================================
 *
 * UserAction action
 *                
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;

import java.util.*;

//import net.tanesha.recaptcha.ReCaptcha;
//import net.tanesha.recaptcha.ReCaptchaResponse;

import org.vps.crypt.Crypt;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.util.struts.captcha.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class UserAction extends UserSupport {

    //--------------------------------------------------------------------------
    // configuration
    //---------------

    UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager( UserManager manager ) {
        this.userManager = manager;
    }

    //---------------------------------------------------------------------
    // Captcha
    //--------
        
    Captcha captcha = null;

    public void setCaptcha( Captcha captcha ){
        this.captcha = captcha; 
    }

    public Captcha getCaptcha(){
        return this.captcha; 
    }
    
    String capresponse ="";
    
    public void setCaptchaResponse( String response ){
        this.capresponse = response;
    }

    public String getCaptchaResponse(){
        return this.capresponse;
    }
    
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
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    public String register( User user ) {

        // sanitize 
        user.setLogin( sanitize( user.getLogin(), 32 ) );
        user.setFirstName( sanitize( user.getFirstName(), 32 ) );
        user.setLastName( sanitize( user.getLastName(), 32 ) );        
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " register:" + user );
        log.debug( " register:login=" + user.getLogin() );
        log.debug( " register:login=" + user.getFirstName() );
        log.debug( " register:login=" + user.getLastName() );
        
        
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
        
        // create new account 
        //-------------------
        
        dao.saveUser( icUser );
        
        log.info( " Account created: " + user.getLogin() +
              " (" + user.getId() + ")" );

        // sent notification
        //------------------
        
        getUserManager()
            .notifyRegistrationByMail( icUser, notifyFrom, notifyServer );
        
        //icUser.notifyByMail( notifyFrom, notifyServer );
        
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
        
        if( uid <= 0) return HOME;

        UserDao dao = getUserContext().getUserDao();
        IcUser icUser = (IcUser) 
            dao.getUser( (String) getSession().get( "LOGIN") );
        
        if ( icUser != null ) {

            log.debug( " icUser=" + icUser );
            
            // get preferences
            //----------------
            
            if( getOp()!= null && getOp().equalsIgnoreCase( "getprefs" ) ){
                setUser( new User() );
                getUser().setPrefs( icUser.getPrefs() );
                return JSON;
            }
            
            // set preferences
            //----------------
            
            if( getOp()!= null && getOp().equalsIgnoreCase( "setprefs" ) ){
                log.info( " edit: setprefs" );
                log.info( " edit:" + this.prefs );
                
                icUser.setPrefs( this.prefs );
                
                // store new settings
                //-------------------

                dao.updateUser( icUser );

                setUser( new User() );
                getUser().setPrefs( icUser.getPrefs() );
                return JSON;
            }
            
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
                return HOME;
            }
                
            if ( uedit != null && uedit.equalsIgnoreCase( "reset" ) ) {
                log.debug( " edit: resetting uid=" + uid );
            }

            if ( getUser() == null ){
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

                // update & log current login time
                //--------------------------------
                
                icUser.setLoginTime( new GregorianCalendar() );
                dao.updateUser( icUser );
                
                
                // valid user
                //-----------
                
                if ( !icUser.isActivated() ) return ACTIVATE;

                getSession().put( "USER_ID", icUser.getId() );
                getSession().put( "LOGIN", icUser.getLogin() );
                getSession().put( "PREFS", icUser.getPrefs() );
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
                
                log.info( " referer:" + getReferer() );
                log.info( " fragment:" + getFragment() );
                rurl = getReferer();
                if(getFragment() != null)
                    rurl += getFragment();
                
                if( rurl != null ) return REDIRECT;
                
                //this.setMst("1:1");
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

        if( rurl != null ) return REDIRECT;
        
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
        getSession().put( "PREFS", null );

        if( rurl != null ) return REDIRECT;
        
        //this.setMst("1:1"); // NOTE: should be set in struts action conf
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
    private String referer;

    public void setReferer( String ref ) {
        this.referer = ref;
    }

    public String getReferer() {
        return this.referer;
    }
    
    private String fragment;

    public void setFragment( String frag ) {
        this.fragment = frag;
    }

    public String getFragment() {
        return this.fragment;
    }
    
    //---------------------------------------------------------------------
    
    public void validate() {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " validate: session=" + getSession() );
        
        
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

            System.out.println( captcha );
            System.out.println( capresponse );
            
            // test recaptcha
            //---------------

            boolean rvalid = true;
            
            if( captcha == null ){
                rvalid = true; 
            } else {
                rvalid = captcha.validate( capresponse );
            }
            
            if ( ! rvalid ) {  
                addActionError("Not a good CAPTCHA");
                log.info( "recaptcha: error" );                    
            } else {
                log.info( "recaptcha: OK" );
            }                            
                        
            // test password typos
            //--------------------
            
            log.debug( "UserAction->validate: pass0=" + pass0 + " pass1=" + pass1 );
            
            if( pass0 != null && pass1 != null && !pass0.equals( pass1 ) ) {
                addFieldError( "pass1", "Passwords do not match." );
            }
            return;
        }
        
        // edit options
        //-------------
        
        if( getOp() != null 
            && getOp().equalsIgnoreCase( "edit" ) 
            && getSession() != null ){ 
            
            log.debug( "UserAction->validate: session login=" + (String) getSession().get("LOGIN")  );
            
            if( getSession().get("LOGIN") != null ){
        
                // test passoword typos
                //---------------------

                if( pass0 != null && pass0.length() > 0 ){
                    
                    UserDao dao = getUserContext().getUserDao();
                    IcUser oldUser = 
                        (IcUser) dao.getUser( (String) getSession().get("LOGIN") );
                    
                    log.debug( "UserAction->validate: login=" + getUser().getLogin()  );
                    log.debug( "UserAction->validate: pass0=" + pass0 + " pass1=" + pass1 );
                    
                    log.debug( "UserAction->validate: oldUser" + oldUser);
                    
                    if ( !oldUser.testPassword( pass0 ) ) {
                        addFieldError( "pass0", "Wrong password." );
                    } else {
                        if ( pass1 == null || !pass1.equals( pass2) ) {
                            addFieldError( "pass1", "Passwords do not match." );
                        }
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
    
    //--------------------------------------------------------------------------    
    //sanitize
    //--------
    
    private String sanitize( String field, int maxlen ){

        String sfield = field;

        if( field == null ) return "";
        sfield = field.trim();
        
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
