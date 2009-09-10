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

import org.apache.commons.codec.binary.Base64;

import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.BindingProvider; 
import javax.xml.ws.Holder;

import javax.xml.ws.soap.Addressing;

import javax.xml.ws.handler.MessageContext; 

import java.util.*;
import javax.annotation.*;         

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.data.*;
//import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;
import edu.ucla.mbi.imex.central.ws.*;

@WebService(serviceName = "ImexCentralService", 
            portName = "ImexCentralPort", 
            endpointInterface = "edu.ucla.mbi.imex.central.ws.IcentralPort", 
            targetNamespace = "http://imex.mbi.ucla.edu/icentral/ws",
            wsdlLocation = "/WEB-INF/wsdl/icentral.wsdl") 

public class IcentralPortImpl implements IcentralPort {

    @Resource 
        WebServiceContext wsContext;
 
    //---------------------------------------------------------------------
    // Entry Manager
    //--------------

    private EntryManager entryManager;

    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }

    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    
    public void 
        createPublication( Holder<edu.ucla.mbi.imex.central.ws.Publication> 
                           publication ) throws IcentralFault {

        MessageContext context = wsContext.getMessageContext();
        Map requestHeaders = 
            (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS) ;
        
    }

    //---------------------------------------------------------------------
    // IcentralPort interface
    //-----------------------
    
    public edu.ucla.mbi.imex.central.ws.Publication 
        createPublicationById( Identifier identifier )
        throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        
        log.info( " login=" + c.getLogin() );
        log.info( " pass=" + c.getPass() );
        log.info( " identifier=" + identifier );
        
        log.info( " entry manager=" +  entryManager);
        log.info( " credentials test=" +  c.test() );
        
        if ( ! c.test() ) throw Fault.AUTH;

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

    public edu.ucla.mbi.imex.central.ws.Publication 
        updatePublicationStatus( Identifier identifier, String status )
        throws IcentralFault {
        return null;
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    
    class Credentials{
        
        String login = null;
        String pass = null;
        
        public String getLogin(){
            return login;
        }
        
        public String getPass(){
            return pass;
        }
        
        public Credentials( MessageContext context ) {
            
            try {
                Map requestHeaders =
                    (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS ) ;
                
                String b64str = (String) 
                    ((List) requestHeaders.get("Authorization")).get(0);            
                String lpString = 
                    new String( Base64.decodeBase64( b64str.substring(6) ) );
                
                login = lpString.substring( 0, lpString.indexOf( ":" ) );
                pass = lpString.substring( lpString.indexOf( ":" ) + 1 );
            } catch ( Exception e ) {
                // ignore: login/pass left at null
            }
        }

        public boolean test() {
            
            if ( entryManager == null ||  
                 entryManager.getUserContext() == null ) return false;
            
            UserDao dao = entryManager.getUserContext().getUserDao();
            if (dao == null ) return false;
            
            User user = dao.getUser( login );
            
            if ( user != null && pass != null ) {
                return user.testPassword( pass );
            } 
            return false;            
        }
    }
}
