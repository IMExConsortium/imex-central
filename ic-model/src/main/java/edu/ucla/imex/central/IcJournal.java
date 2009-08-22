package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $HeadURL::                                                             $
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
 #=========================================================================
 #
 # IcGroup
 #
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.Group;

public class IcJournal extends IcGroup {

    private String websiteURL;
    private String nlmid;

    //---------------------------------------------------------------------

    public IcJournal() {}
        
    public IcJournal( Group group ) {
        setId( group.getId() );
        setLabel( group.getLabel() );
        setName( group.getName() );

        setComments( group.getComments() == null ? "" : group.getComments() );
    }

    //---------------------------------------------------------------------
    
    public String getWebsiteUrl() {
        return websiteURL;
    }
    
    public void setWebsiteUrl( String url ) {
        websiteURL = url;
    }

    public String getNlmid() {
        return nlmid;
    }
    
    public void setNlmid( String nlmid ) {
        this.nlmid = nlmid;
    }

    
    //---------------------------------------------------------------------
    
    public String toString() {
	
	StringBuffer sb = new StringBuffer();
	
	sb.append( " IcJournal(id=" + getId() );
	sb.append( " label=" + getLabel() );
	sb.append( " comments=" + getComments() );
	sb.append( ")" );

	return sb.toString();
    }

}


