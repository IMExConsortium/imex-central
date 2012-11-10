package  edu.ucla.mbi.imex.central;

/* =============================================================================
 # $HeadURL::                                                                  $
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #                                                                             $
 # IcKeyspace                                                                  $
 #                                                                             $
 #=========================================================================== */

import edu.ucla.mbi.util.data.*;

public class IcKeyspace extends Keyspace {

    IcKeyspace() { }

    public IcKeyspace( Keyspace pub ) {
        this.setName( pub.getName() );
        this.setComments( pub.getComments() );
        this.setPrefix( pub.getPrefix() );
        this.setPostfix( pub.getPostfix() );
        this.setActive( pub.isActive() );
    }
}
