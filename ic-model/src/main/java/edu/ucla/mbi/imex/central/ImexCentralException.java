package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # ManagerException - entry/journal management exeptions
 #
 #=========================================================================== */

public class ImexCentralException extends Exception {

    private int code = 0;
    
    //--------------------------------------------------------------------------
    
    protected ImexCentralException( int code, String message ){
        super( message );
        this.code = code;
    }
    
    public int getStatusCode(){
        return code;
    }

    public String getStatusMessage(){
        return getMessage();
    }

}
