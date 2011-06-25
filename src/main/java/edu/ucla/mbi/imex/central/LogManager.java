package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # LogManager - businness logic of operation logger
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
import edu.ucla.mbi.util.dao.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

public class LogManager {
    
    public LogManager() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "LogManager: creating log manager" );
    }
    
    public void monitor( Object operation){
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "LogManager: monitor called: " + operation );
    }
}
