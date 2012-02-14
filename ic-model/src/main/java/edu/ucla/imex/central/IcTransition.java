package edu.ucla.mbi.imex.central;

/* =========================================================================
 # $HeadURL::                                                              $
 # $Id::                                                                   $
 # Version: $Rev::                                                         $
 #==========================================================================
 #
 # IcTransition - transition between data states
 #
 #======================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ucla.mbi.util.data.Transition;

public class IcTransition extends Transition { 

    public IcTransition() { }

    public IcTransition( Transition trans ) {
        this.setName( trans.getName() );
        this.setComments( trans.getComments() );
        this.setFromState( trans.getFromState() );
        this.setToState( trans.getToState() );
    }
}
