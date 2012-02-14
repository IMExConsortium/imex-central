package edu.ucla.mbi.imex.central;

/* =========================================================================
 # $HeadURL::                                                              $
 # $Id::                                                                   $
 # Version: $Rev::                                                         $
 #==========================================================================
 #
 # IcDataState
 #
 #======================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ucla.mbi.util.data.DataState;

public class IcDataState extends DataState { 

    public IcDataState() { }

    public IcDataState( DataState state ) {
        this.setName( state.getName() );
        this.setComments( state.getComments() );                
    }

}
