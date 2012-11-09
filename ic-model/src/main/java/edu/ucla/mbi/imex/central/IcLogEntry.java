package edu.ucla.mbi.imex.central;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcLogEntry - log entry attached to one or more data items
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;

public class IcLogEntry extends Comment {
    
    IcLogEntry() { }

    public IcLogEntry( User owner, IcPub root,
                       String label, String body){
        
        this.setOwner( owner );
        this.setRoot( root );
        
        
        this.setLabel( label );
        this.setBody( body );
    }
}

