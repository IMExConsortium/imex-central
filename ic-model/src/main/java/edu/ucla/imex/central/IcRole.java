package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $HeadURL::                                                             $
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
 #=========================================================================
 #
 # IcRole
 #
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.Role;

public class IcRole extends Role {

    public IcRole() {}
        
    public IcRole( Role role ) {
        setId( role.getId() );
        setName( role.getName() );
        setComments( role.getComments() == null ? "" : role.getComments() );
    }

    public String toString() {
	
	StringBuffer sb = new StringBuffer();
	
	sb.append( " IcRole(id=" + getId() );
	sb.append( " name=" + getName() );
	sb.append( " comments=" + getComments() );
	sb.append( ")" );

	return sb.toString();
    }

}
