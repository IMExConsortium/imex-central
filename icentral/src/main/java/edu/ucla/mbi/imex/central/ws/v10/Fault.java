package edu.ucla.mbi.imex.central.ws.v10;

/*===========================================================================
 * $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/icentral/s#$
 * $Id:: Fault.java 182 2010-08-24 16:14:49Z lukasz                         $
 * Version: $Rev:: 182                                                      $
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
        ID_MISSING = createFault( 4, "identifier missing" );

    public static final IcentralFault
        ID_UNKNOWN = createFault( 5, "identifier unknown" );

    public static final IcentralFault
        NO_RECORD = createFault( 6, "no record(s) found" );

    public static final IcentralFault
        NO_REC_CR = createFault( 7, "record not created" );

    public static final IcentralFault
        STAT_UNKNOWN = createFault( 8, "status unknown" );

    public static final IcentralFault
        NO_IMEX = createFault( 9, "IMEx identifier missing" );

    public static final IcentralFault
        USR_UNKNOWN = createFault( 10, "unknown user" );

    public static final IcentralFault
        GRP_UNKNOWN = createFault( 11, "unknown group" );
    
    public static final IcentralFault
        UNSUP = createFault( 98, "operation not supported" );
    public static final IcentralFault
        UNKNOWN = createFault( 99, "internal server error" );
    
    //--------------------------------------------------------------------------
    
    private static ObjectFactory faultFactory;
    
    private static IcentralFault createFault( int code,
                                              String message ) {
        
        if ( faultFactory == null ) {
            faultFactory = new ObjectFactory();
        }
        
        ImexCentralFault icf = faultFactory.createImexCentralFault();
        icf.setFaultCode( code );
        icf.setMessage( message );
        IcentralFault flt = new IcentralFault( "ImexCentral Fault", icf );
        return flt;
    }
    
}
