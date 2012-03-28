package org.hupo.psi.mi.psq.server;

import org.hupo.psi.mi.psq.*;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import java.io.InputStream;

import javax.jws.WebService;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.WebServiceContext;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.server.index.*;
import org.hupo.psi.mi.psq.server.index.solr.*;


import org.hupo.psi.mi.psq.server.data.*;
import org.hupo.psi.mi.psq.server.data.derby.*;


import edu.ucla.mbi.util.JsonContext;

import javax.annotation.*;


@WebService( name = "psicquicService", 
             targetNamespace = "http://psi.hupo.org/mi/psicquic",
             serviceName = "psicquicService",
             portName = "psicquic",
             endpointInterface = "org.hupo.psi.mi.psq.PsqPort",
             wsdlLocation = "/WEB-INF/wsdl/psicquic11.wsdl")

public class PsqPortImpl implements PsqPort {

    @Resource
        WebServiceContext wsContext;

    org.hupo.psi.mi.psq.ObjectFactory psqOF =
        new org.hupo.psi.mi.psq.ObjectFactory();
    
    JsonContext psqContext;

    //--------------------------------------------------------------------------

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
        log.info( "PsqPortImpl: getByQuery: context =" + psqContext);
        log.info( "PsqPortImpl: getByQuery: q=" + query );
        
        if( infoRequest != null ){              
            log.info( "                         FR=" 
                      + infoRequest.getFirstResult() );
            log.info( "                         BS=" 
                      + infoRequest.getBlockSize() );
        }

        initialize();
        
        Index ix = index; // new SolrIndex( getPsqContext() );

        org.hupo.psi.mi.psq.server.index.ResultSet 
            rs = ix.query( query );
        
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
