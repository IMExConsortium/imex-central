package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # ClientException - client exeptions
 #
 #=========================================================================== */

public class ClientException extends ImexCentralException {
    
    public static final ClientException
        CLIENT_EXCEPTION = createException( 101, "Client Exception" );
    public static final ClientException
        NCBI_PROXY_FAULT = createException( 102, "NCBI proxy fault" );
    
    //--------------------------------------------------------------------------
    
    private ClientException( int code, String message ){ 
        super( code, message ); 
    }
    
    private static ClientException createException( int code,
                                                   String message ){   
        return new ClientException( code, message );
    }
    
}