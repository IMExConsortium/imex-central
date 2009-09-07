package edu.ucla.mbi.icentral.ws.server;

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

import edu.ucla.mbi.icentral.ws.*;

import javax.jws.WebService;
import javax.xml.ws.Holder;

@WebService(endpointInterface = "edu.ucla.mbi.icentral.ws.IcentralPort")

public class IcentralPortImpl implements IcentralPort {

    public void createPublication( Holder<Publication> publication )
        throws IcentralFault {
        return null;
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
