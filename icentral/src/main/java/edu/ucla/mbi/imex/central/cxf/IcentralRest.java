package edu.ucla.mbi.imex.central.cxf;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # RESTful Web service interface
 #
 #=========================================================================== */

import javax.ws.rs.*;

//import org.hupo.psi.mi.*;
//import org.hupo.psi.mi.psq.*;

public interface IcentralRest {
    
    @GET @Path("/rec/{acc}")
        public Object getRecordByAcc( @DefaultValue("imex")
                                      @QueryParam("ns") String ns,
                                      @PathParam("acc") String acc,  
                                      @DefaultValue("redirect")
                                      @QueryParam("mode") String mode );

    @GET @Path("/dta/{format}/{acc}")
        public Object getDataFileByAcc( @DefaultValue("imex")
                                        @QueryParam("ns") String ns,
                                        @PathParam("acc") String acc,
                                        @PathParam("format") String format,
                                        @DefaultValue("redirect")
                                        @QueryParam("mode") String mode );
    
    @GET @Path("/map")
        public Object getImexAcc( @DefaultValue( "pmid" )
                                  @QueryParam( "ns" ) String ns,
                                  @DefaultValue( "" ) 
                                  @QueryParam( "acc" ) String acc );

}