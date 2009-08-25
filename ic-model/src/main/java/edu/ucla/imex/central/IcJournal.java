package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $HeadURL::                                                             $
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
 #=========================================================================
 #
 # IcJournal
 #
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.data.*;

public class IcJournal extends Journal { 

    public IcJournal() {}

    public IcJournal( Journal journal ){
        
    }

    //---------------------------------------------------------------------
    
    public String toString() {
	
	StringBuffer sb = new StringBuffer();
	
	sb.append( " IcJournal(id=" + getId() );
	sb.append( " title=" + getTitle() );
	sb.append( " nlmid=" + getNlmid() );
	sb.append( ")" );

	return sb.toString();
    }

}


