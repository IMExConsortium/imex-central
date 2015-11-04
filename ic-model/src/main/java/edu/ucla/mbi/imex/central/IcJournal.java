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
        
        this.setTitle( journal.getTitle() );
        this.setOwner( journal.getOwner() );
        this.setNlmid( journal.getNlmid() );
        this.setIssn( journal.getIssn() );
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( new Date() );
        this.setCrt( calendar );

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


    public Group getImexGroup(){
        
        Set<Group> agroups = this.getAdminGroups();
        for( Iterator<Group> iag = agroups.iterator(); 
             iag.hasNext(); ){ 
            Group cag = iag.next();
            for( Iterator<Role> ir = cag.getRoles().iterator(); 
                 ir.hasNext(); ){
                Role cr = ir.next();
                if( cr.getName().equalsIgnoreCase("IMEX PARTNER") ){
                    return cag;
                }
            }
        }
        return null;
    }

}


