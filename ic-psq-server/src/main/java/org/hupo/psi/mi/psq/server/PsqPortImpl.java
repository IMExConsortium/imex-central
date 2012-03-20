package org.hupo.psi.mi.psq.server;

import org.hupo.psi.mi.psq.*;

import java.util.List;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.server.index.*;
import org.hupo.psi.mi.psq.server.index.solr.*;

@WebService( name = "psicquicService", 
             targetNamespace = "http://psi.hupo.org/mi/psicquic",
             serviceName = "psicquicService",
             portName = "psicquic",
             endpointInterface = "org.hupo.psi.mi.psq.PsqPort",
             wsdlLocation = "/WEB-INF/wsdl/psicquic11.wsdl")

public class PsqPortImpl implements PsqPort {
    
    org.hupo.psi.mi.psq.ObjectFactory psqOF =
        new org.hupo.psi.mi.psq.ObjectFactory();

    //--------------------------------------------------------------------------
    
    public QueryResponse getByInteractor( DbRef dbRef,
                                          RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {

        throw new NotSupportedMethodException( "", null );
    };
    
    //--------------------------------------------------------------------------

    public QueryResponse getByQuery( String query,
                                     RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "PsqPortImpl: getByQuery: q=" + query );
        if( infoRequest != null ){              
            log.info( "                         FR=" + infoRequest.getFirstResult() );
            log.info( "                         BS=" + infoRequest.getBlockSize() );
        }

        
        Index ix = new SolrIndex();

        org.hupo.psi.mi.psq.server.index.ResultSet 
            rs = ix.query( query );
        
        QueryResponse qr = psqOF.createQueryResponse();
        qr.setResultSet( psqOF.createResultSet() );
        qr.getResultSet().setMitab( rs.getResultList().toString() ); 

        
        return qr;
    };

    //--------------------------------------------------------------------------

    public String getVersion(){
        return "1.1";
    };
    
    //--------------------------------------------------------------------------

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
