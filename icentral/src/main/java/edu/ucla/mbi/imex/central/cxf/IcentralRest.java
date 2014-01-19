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
                                      @DefaultValue("url")
                                      @QueryParam("mode") String mode );
    
    @GET @Path("/map/{acc}")
        public Object getImexAcc( @DefaultValue("pmid") 
                                  @QueryParam("ns") String ns,
                                  @PathParam("acc") String acc );

}