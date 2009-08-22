package edu.ucla.mbi.imex.central;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * IcPublication - a traceable publication
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.data.*;

public class IcPublication extends Publication {
    
    IcPublication() {}

    public IcPublication( Publication pub ) {
        
    }

    private String imexId;
    
    public void setImexId( String imexId ) {
        this.imexId = imexId;
    }

    public String setImexId() {
        return imexId;
    }

    //---------------------------------------------------------------------    

    private Set<ImexEntry> imexEntrySets;

    public void setImexSets( Set<ImexEntry> sets ){
        imexEntrySets = sets;
    }

    public Set<ImexEntry>  getImexSets(){
        return imexEntrySets;
    }

}

