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

public class IcGroup extends Group {

    public IcGroup() {}
        
    public IcGroup( Group group ) {
        setId( group.getId() );
        setLabel( group.getLabel() );
        setName( group.getName() );

        setComments( group.getComments() == null ? "" : group.getComments() );
    }

    public String toString() {
	
	StringBuffer sb = new StringBuffer();
	
	sb.append( " IcGroup(id=" + getId() );
	sb.append( " label=" + getLabel() );
	sb.append( " comments=" + getComments() );
	sb.append( ")" );

	return sb.toString();
    }

}
