package edu.ucla.mbi.imex.central.ws.server;

/* #=======================================================================
 # $Id:: DipCachingImpl.java 317 2009-07-25 17:32:52Z lukasz              $
 # Version: $Rev:: 317                                                    $
 #=========================================================================
 #
 # IcentralPortImpl - ImexCentral SOAP port implementation 
 #
 #
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.BindingProvider; 
import javax.xml.ws.Holder;

import javax.xml.ws.handler.MessageContext; 

import java.util.*;
import javax.annotation.*;         

import edu.ucla.mbi.imex.central.ws.*;

@WebService(endpointInterface = "edu.ucla.mbi.imex.central.ws.IcentralPort")
                                 
public class IcentralPortImpl implements IcentralPort {

    @Resource 
        WebServiceContext wsContext;
      
    public void createPublication( Holder<Publication> publication )
        throws IcentralFault {

        MessageContext context = wsContext.getMessageContext();
        Map requestHeaders = 
            (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS) ;
        
        
    }
    
    public Publication createPublicationById( Identifier identifier )
        throws IcentralFault {
        return null;
    }

    public PublicationList getPublicationById( List<Identifier> identifier )
        throws IcentralFault {
        return null;
    }
    
    public PublicationList getPublicationByOwner( List<String> owner )
        throws IcentralFault {
        return null;
    }
    
    public PublicationList getPublicationByStatus( List<String> status )
        throws IcentralFault { 
        return null;
    }

    public Publication updatePublicationStatus( Identifier identifier,
                                                String status )
        throws IcentralFault {
        return null;
    }
}
