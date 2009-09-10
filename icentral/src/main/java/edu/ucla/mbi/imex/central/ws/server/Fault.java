package edu.ucla.mbi.imex.central.ws;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
 *===========================================================================
 *
 * FaultDef:
 *
 *    fault definitions
 *
 *========================================================================= */

import java.util.Map;
import java.util.HashMap;

public class Fault {
    
    public static final IcentralFault
        AUTH = createFault( 2, "unauthorized user" );
    public static final IcentralFault
        INVALID_OP = createFault( 3, "invalid operation" );
    public static final IcentralFault
        UNKNOWN = createFault( 99, "internal server error" );
        
    //---------------------------------------------------------------------
    
    private static ObjectFactory faultFactory;
    
    private static IcentralFault createFault( int code,
                                              String message ) {
        
        if ( faultFactory == null ) {
            faultFactory = new ObjectFactory();
        }
        
        ImexCentralFault icf = faultFactory.createImexCentralFault();
        icf.setFaultCode( code );
        icf.setMessage( message );
        return new IcentralFault("ImexCentral Fault", icf);
    }
    
}