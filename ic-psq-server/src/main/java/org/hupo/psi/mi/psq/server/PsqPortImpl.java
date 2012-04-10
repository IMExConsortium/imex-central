package org.hupo.psi.mi.psq.server;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # PsqPortImpl: implementation of PSICQUIC 1.1 SOAP service 
 #
 #=========================================================================== */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import java.io.InputStream;

import javax.jws.WebService;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.WebServiceContext;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.*;
import org.hupo.psi.mi.psq.server.index.*;
import org.hupo.psi.mi.psq.server.data.*;

import edu.ucla.mbi.util.JsonContext;

import javax.annotation.*;

@WebService( name = "PsicquicService", 
             targetNamespace = "http://psi.hupo.org/mi/psicquic",
             serviceName = "PsicquicService",
             portName = "IndexBasedPsicquicServicePort",
             endpointInterface = "org.hupo.psi.mi.psq.PsqPort",
             wsdlLocation = "/WEB-INF/wsdl/psicquic11.wsdl")

public class PsqPortImpl implements PsqPort {

    @Resource
        WebServiceContext wsContext;

    org.hupo.psi.mi.psq.ObjectFactory psqOF =
        new org.hupo.psi.mi.psq.ObjectFactory();
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    Index index = null;

    public void setIndex( Index index ){
        this.index = index;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    RecordDao  rdao = null;

    public void setRecordDao( RecordDao dao ){
        this.rdao= dao;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    JsonContext psqContext;
    
    public void setPsqContext( JsonContext context ){
        psqContext = context;
    }

    public JsonContext getPsqContext(){
        return psqContext;
    }

    //--------------------------------------------------------------------------

    private void initialize() {
        initialize( false );
    }

    //--------------------------------------------------------------------------
    
    private void initialize( boolean force) {

        if ( getPsqContext().getJsonConfig() == null || force ) {

            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing psq context" );
            String jsonPath =
                (String) getPsqContext().getConfig().get( "json-config" );
            log.info( "JsonPsqDef=" + jsonPath );
            
            if ( jsonPath != null && jsonPath.length() > 0 ) {

                String cpath = jsonPath.replaceAll("^\\s+","" );
                cpath = jsonPath.replaceAll("\\s+$","" );

                ServletContext sc = (ServletContext) 
                    wsContext.getMessageContext()
                    .get( MessageContext.SERVLET_CONTEXT );
                    
                log.info( "ServletContext =" + sc );
                
                try {
                    InputStream is =
                        sc.getResourceAsStream( cpath );
                    getPsqContext().readJsonConfigDef( is );
                    
                } catch ( Exception e ){
                    log.info( "JsonConfig reading error" );
                }
            }
        }
    }

    //==========================================================================
    // WEB SERVICE OPERATIONS
    //=======================
    
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

        initialize();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "PsqPortImpl: getByQuery: context =" + psqContext);
        log.info( "PsqPortImpl: getByQuery: q=" + query );
        
        if( infoRequest != null ){              
            log.info( "                         FR=" 
                      + infoRequest.getFirstResult() );
            log.info( "                         BS=" 
                      + infoRequest.getBlockSize() );
        }
        
        org.hupo.psi.mi.psq.server.index.ResultSet 
            rs = index.query( query );
        
        QueryResponse qr = psqOF.createQueryResponse();
        qr.setResultSet( psqOF.createResultSet() );
        
        String mitab="";

        for( Iterator i = rs.getResultList().iterator(); i.hasNext(); ){
            Map in = (Map) i.next();
            
            String pid = (String) in.get("pid");

            String drecord =  rdao.getRecord( pid ,"" );
        
            log.info( " SolrDoc: pid=" + pid + " :: "  + drecord );
            mitab += drecord + "\n";
        }
        qr.getResultSet().setMitab( mitab );     

        return qr;
    };
    
    //--------------------------------------------------------------------------

    public QueryResponse getByInteractorList( List<DbRef> dbRef,
                                              RequestInfo infoRequest,
                                              String operand )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    //--------------------------------------------------------------------------
    
    public QueryResponse getByInteraction( DbRef dbRef,
                                           RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    //--------------------------------------------------------------------------
    
    public QueryResponse getByInteractionList( List<DbRef> dbRef,
                                               RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException{
        throw new NotSupportedMethodException( "", null );
    };


    //==========================================================================
    // META DATA
    //==========

    public List<String> getSupportedReturnTypes(){
        
        initialize();
        return (List<String>) ((Map) ((Map) getPsqContext().getJsonConfig()
                                      .get( "service" )).get( "soap" ))
            .get( "supported-return-type" );
    };
    
    //--------------------------------------------------------------------------
    
    public String getVersion(){
        initialize();
        return (String) ((Map) ((Map) getPsqContext().getJsonConfig()
                                .get( "service" )).get( "soap" ))
            .get( "version" );
    };

    //--------------------------------------------------------------------------
    
    public List<String> getSupportedDbAcs(){
        
        initialize();
        return (List<String>) ((Map) ((Map) getPsqContext().getJsonConfig()
                                      .get( "service" )).get( "soap" ))
            .get( "supported-db-ac" );
    };
    
    //--------------------------------------------------------------------------

    public String getProperty( String property ){     
        
        initialize();
        return (String) ((Map) ((Map) ((Map) getPsqContext().getJsonConfig()
                                       .get( "service" )) .get( "soap" )) 
                         .get( "properties" ))
            .get( property );
    };
    
    //--------------------------------------------------------------------------
    
    public List<Property> getProperties(){

        initialize();
        Map propmap = (Map) ((Map) ((Map) getPsqContext().getJsonConfig()
                                    .get( "service" )).get( "soap" ))
            .get( "properties" );
        
        List<Property> pl = new ArrayList<Property>();
        
        for( Iterator pi = propmap.entrySet().iterator(); pi.hasNext(); ){
            Map.Entry me = (Map.Entry) pi.next();
            
            Property p = psqOF.createProperty();
        
            p.setKey( (String) me.getKey() );
            p.setValue( (String) me.getValue() );
            pl.add( p );
        }
        return pl;
    };
}
