package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # EntryException - entry/journal management exeptions
 #
 #=========================================================================== */

public class EntryException extends ImexCentralException {
    
    public static final EntryException
        ENTRY_EXCEPTION = createException( 201, "Entry Exception" );
    
    public static final EntryException
        DUP_PMID = createException( 202, "PMID already exists" );
    
    public static final EntryException
        DUP_DOI = createException( 203, "DOI already exists" );
    
    //--------------------------------------------------------------------------
    
    private EntryException( int code, String message ){ 
        super( code, message ); 
    }
    
    private static EntryException createException( int code,
                                                   String message ){   
        return new EntryException( code, message );
    }
    
}