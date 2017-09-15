package edu.ucla.mbi.imex.central;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcComment - comment attached to one or more data items
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.data.*;

public class IcScore extends edu.ucla.mbi.util.data.Score {
    
    IcScore() { }
    
    public IcScore( User owner, IcPub root, String name){
        this( owner, root, name, 0.0f );
    }
    
    public IcScore( User owner, IcPub root, String name, float value ){ 
	
        this.setOwner( owner );
        this.setRoot( root );
        
        this.setName( name );
        this.setValue( value );
    }
    
}

