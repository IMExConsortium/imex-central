package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # UserManager - 
 #                 
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

import java.util.GregorianCalendar;
import java.util.Calendar;
       
import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

public class UserManager {
    
    public UserManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "UserManager: creating user manager" );
    }
    
    public void notifyRegistrationByMail( IcUser user, 
                                          String notifyFrom,
                                          String notifyServer ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "UserManager: notifyRegistration " );

        if( user != null ){
            user.notifyByMail( notifyFrom, notifyServer );
        }
    }
}
