package org.hupo.psi.mi.psq.client;
        
/* =============================================================================
 # $Id:: EntryManager.java 140 2010-03-22 16:25:42Z lukasz                     $
 # Version: $Rev:: 140                                                         $
 #==============================================================================
 #
 # iclient - simple icentral web service client
 #
 #=========================================================================== */

import java.util.*;

import org.hupo.psi.mi.psq.*;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import javax.xml.namespace.QName;
import java.net.URL;

//import edu.ucla.mbi.imex.central.ws.*;

public class psqclient {

    
    static PsqService service;
    static PsqPort port;
    static String endpoint = "http://10.1.1.206:8080/ic-psq-server/ws";
    
    public static void connect(String[] args) {

        try {
            URL url = new URL( endpoint + "?wsdl" );
            System.out.println( "WSDL: " + endpoint + "?wsdl" );
            QName qn = new QName( "http://psi.hupo.org/mi/psicquic",
            "psicquicService");
            service = new PsqService( url, qn );
            port = service.getPsicquic();
            
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                      endpoint );


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
    

    public static void main( String[] args ) {
        System.out.println( "psqclient" );

        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();
        String query ="";

        try{
            query = args[2];
            System.out.println( "Q=" + query );

            ri.setFirstResult( Integer.parseInt( args[0] ));
            ri.setBlockSize( Integer.parseInt( args[1] ));
            
        connect( args );
        } catch( Exception ex){            
            ex.printStackTrace();
        }
        try{
            QueryResponse qr = port.getByQuery( query, ri);
            
            if( qr != null ) {

                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: FR=" + qr.getResultInfo().getFirstResult()); 
                }
                 
                if( qr.getResultSet() != null ){
                    System.out.println( " ResulSet: mitab=" + qr.getResultSet().getMitab() );
                }
                
                
                /*
                for ( Iterator<Publication> 
                          pli = pl.getPublication().iterator(); pli.hasNext(); 
                      ) {
                    Publication p = pli.next();

                    System.out.println( " Author: " + p.getAuthor() );
                    System.out.println( " Title: " + p.getTitle() );
                }
                */
            }
            
        } catch ( Exception psqf ){

            psqf.printStackTrace();

            //System.out.println( "SERVER FAULT: CODE:" + icf.getFaultInfo().getFaultCode() + 
            //                    " MESSAGE: " + icf.getFaultInfo().getMessage()  );
        }

        

    }

}
