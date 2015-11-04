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

public class IcDataStage extends DataState { 

    public IcDataStage() { }

    public IcDataStage( DataState state ) {
        this.setName( state.getName() );
        this.setComments( state.getComments() );                
    }

}
