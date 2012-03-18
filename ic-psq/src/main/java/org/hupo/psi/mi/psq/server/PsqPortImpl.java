package org.hupo.psi.mi.psq.server;

import org.hupo.psi.mi.psq.*;

import java.util.List;
import javax.jws.WebService;

@WebService( name = "psicquicService", 
             targetNamespace = "http://psi.hupo.org/mi/psicquic",
             serviceName = "psicquicService",
             portName = "psicquic",
             endpointInterface = "org.hupo.psi.mi.psq.PsqPort",
             wsdlLocation = "/WEB-INF/wsdl/psicquic11.wsdl")

public class PsqPortImpl implements PsqPort {
    
    public QueryResponse getByInteractor( DbRef dbRef,
                                          RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {

        throw new NotSupportedMethodException( "", null );
    };
    
    public QueryResponse getByQuery( String query,
                                     RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {

        throw new NotSupportedMethodException( "", null );
    };

    public String getVersion(){
        return "1.1";
    };
    
    public List<String> getSupportedReturnTypes(){
        return null;
    };

    public QueryResponse getByInteraction( DbRef dbRef,
                                           RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    public QueryResponse getByInteractorList( List<DbRef> dbRef,
                                              RequestInfo infoRequest,
                                              String operand )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    public List<String> getSupportedDbAcs(){
        return null;
    };

    public QueryResponse getByInteractionList( List<DbRef> dbRef,
                                               RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException{
        throw new NotSupportedMethodException( "", null );
    };

    public String getProperty( String property ){     
        return null;
    };
    
    public List<Property> getProperties(){
        return null;
    };
}
