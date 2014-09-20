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

//import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;

public class IcComment extends Comment {
    
    IcComment() { }
    
    public IcComment( User owner, IcPub root, String label, String body ){
        this( owner, root, label, body, "TEXT" );
    }
    
    public IcComment( User owner, IcPub root, String label, 
                      String body, String bodyTp ){
        
        this.setOwner( owner );
        this.setRoot( root );
        
        this.setLabel( label );
        this.setBody( body );

        if( bodyTp != null ){
            if( bodyTp.equalsIgnoreCase("HTML") ){
                this.setHtml();
            }
            if( bodyTp.equalsIgnoreCase("WIKI") ){
                this.setWiki();
            }
        }
    }
    
    public IcFlag getIcFlag(){
        return (IcFlag) this.getState();
    }

    public IcComment setIcFlag( IcFlag flag ){
        this.setState( flag );
        return this;
    }
}

