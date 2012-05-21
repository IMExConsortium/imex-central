package org.hupo.psi.mi.psq.client;
        
/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # PsqClient - PSICQUIC client
 #
 #=========================================================================== */

import java.util.*;

import org.hupo.psi.mi.psq.*;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import javax.xml.namespace.QName;
import java.net.URL;

import org.apache.commons.cli.*;

//import edu.ucla.mbi.imex.central.ws.*;

public class PsqClient {
    
    static PsqService service;
    static PsqPort port;
    static String endpoint = "http://10.1.1.206:8080/ic-psq-server/ws";

    String url="http://127.0.0.1:8080/ic-psq-server/ws";
    String op ="query";
    String query ="*:*";
    String ns ="";
    String ac ="";

    public void connect( String  url ) {
        
        try {
            URL psqUrl = new URL( url + "?wsdl" );            
            QName qn = new QName( "http://psi.hupo.org/mi/psicquic",
                                  "PsicquicService");
            service = new PsqService( psqUrl, qn );
            port = service.getIndexBasedPsicquicServicePort();
            
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                      url );
            /*
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.USERNAME_PROPERTY, args[0] );
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.PASSWORD_PROPERTY, args[1] );
            */
        } catch ( Exception ex ) {
            ex.printStackTrace();
            System.out.println( "PsqService: cannot connect" );
        }        
    }

    public String getProperty(String name ){
      
        try{
            return port.getProperty( name );
        }catch ( Exception ex ) {
            ex.printStackTrace();
            System.out.println( "PsqService(getProperty): operation error" );
        }
        return null;
    }

    public List<Map<String,String>> getProperties(){

        try{
            List<Property> pl = port.getProperties();
            if( pl!=null ){
                List<Map<String,String>> propList = new ArrayList<Map<String,String>>();
                for(Iterator<Property> ip = pl.iterator(); ip.hasNext(); ){

                    Property cp = ip.next();
                    Map<String,String> cpm = new HashMap<String,String>();
                    cpm.put("key",cp.getKey());
                    cpm.put("value",cp.getValue());
                    propList.add(cpm);
                }
                return propList;
            }
        }catch ( Exception ex ) {
            ex.printStackTrace();
            System.out.println( "PsqService(Properties):" +
                                " operation error" );
        }
        return null;
    }

    public QueryResponse getByQuery( String query, int first, int size ){
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();

        ri.setFirstResult( first );
        ri.setBlockSize( size );
        
        try{
            QueryResponse qr = port.getByQuery( query, ri );
        
            //throws NotSupportedMethodException, 
            //       NotSupportedTypeException, 
            //       PsicquicServiceException
            
            if( qr != null ) {
                
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" 
                                        + qr.getResultInfo().getFirstResult());
                }
                
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" 
                                        + qr.getResultSet().getMitab() );
                }   
            }
            return qr;
            
        } catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(ByQuery):" +
                                " operation error" );
        }
        return null;

    }

    public String getVersion(){
        try{
            return port.getVersion();
        }catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(Version):" + 
                                " operation error" );
        }
        return null;
    }

    public List<String> getSupportedDbAcs(){
        try{
            return port.getSupportedDbAcs();
        }catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(SupportedDbAcs):" +
                                " operation error" );
        }
        return null;
    }

    public List<String> getSupportedReturnTypes(){
        try{
            return port.getSupportedReturnTypes();
        }catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(SupportedReturnTypes):" +
                                " operation error" );
        }
        return null;
    }
    
    public QueryResponse getByInteraction( DbRef dbref, int first, int size ){
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();

        ri.setFirstResult( first );
        ri.setBlockSize( size );
        
        try{
            QueryResponse qr = port.getByInteraction( dbref , ri );
        
            //throws NotSupportedMethodException, 
            //       NotSupportedTypeException, 
            //       PsicquicServiceException
            
            if( qr != null ) {
                
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" 
                                        + qr.getResultInfo().getFirstResult());
                }
                
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" 
                                        + qr.getResultSet().getMitab() );
                }   
            }
            return qr;
            
        } catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(port.getByInteraction):" +
                                " operation error" );
        }
        return null;

    }

    public QueryResponse getByInteractor( DbRef dbref, int first, int size ){
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();

        ri.setFirstResult( first );
        ri.setBlockSize( size );
        
        try{
            QueryResponse qr = port.getByInteractor( dbref , ri );
        
            //throws NotSupportedMethodException, 
            //       NotSupportedTypeException, 
            //       PsicquicServiceException
            
            if( qr != null ) {
                
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" 
                                        + qr.getResultInfo().getFirstResult());
                }
                
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" 
                                        + qr.getResultSet().getMitab() );
                }   
            }
            return qr;
            
        } catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(port.ByInteractor):" +
                                " operation error" );
        }
        return null;

    }

    public QueryResponse getByInteractorList( List<DbRef> dbrl, 
                                              int first, int size, 
                                              String operand ){
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();

        ri.setFirstResult( first );
        ri.setBlockSize( size );
        
        try{
            QueryResponse qr = port.getByInteractorList( dbrl, ri, operand );
        
            //throws NotSupportedMethodException, 
            //       NotSupportedTypeException, 
            //       PsicquicServiceException
            
            if( qr != null ) {
                
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" 
                                        + qr.getResultInfo().getFirstResult());
                }
                
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" 
                                        + qr.getResultSet().getMitab() );
                }   
            }
            return qr;
            
        } catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(ByInteractorList):" +
                                " operation error" );
        }
        return null;

    }

    public QueryResponse getByInteractionList( List<DbRef> dbrl, 
                                               int first, int size ){
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();

        ri.setFirstResult( first );
        ri.setBlockSize( size );
        
        try{
            QueryResponse qr = port.getByInteractionList( dbrl, ri );
            
            //throws NotSupportedMethodException, 
            //       NotSupportedTypeException, 
            //       PsicquicServiceException
            
            if( qr != null ) {
                
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" 
                                        + qr.getResultInfo().getFirstResult());
                }
                
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" 
                                        + qr.getResultSet().getMitab() );
                }   
            }
            return qr;
            
        } catch( Exception ex ){
            ex.printStackTrace();
            System.out.println( "PsqService(ByInteractionList):" +
                                " operation error" );
        }
        return null;

    }



}
