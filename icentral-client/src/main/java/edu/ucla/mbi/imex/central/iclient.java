package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id:: EntryManager.java 140 2010-03-22 16:25:42Z lukasz                     $
 # Version: $Rev:: 140                                                         $
 #==============================================================================
 #
 # iclient - simple icentral web service client
 #
 #=========================================================================== */

import java.util.*;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import javax.xml.namespace.QName;
import java.net.URL;

import edu.ucla.mbi.imex.central.ws.*;

public class iclient {

    static IcentralService service;
    static IcentralPort port;
    static String endpoint = "https://imexcentral.org/icentraltest/ws";
    
    public static void connect(String[] args) {

        try {
            URL url = new URL( endpoint + "?wsdl" );
            System.out.println( "WSDL: " + endpoint + "?wsdl" );
            QName qn = new QName("http://imex.mbi.ucla.edu/icentral/ws",
                                 "ImexCentralService");
            service = new IcentralService( url, qn );
            port = service.getImexCentralPort();
            
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                      endpoint );
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.USERNAME_PROPERTY, args[0] );
            ( (BindingProvider) port ).getRequestContext()
                .put( BindingProvider.PASSWORD_PROPERTY, args[1] );
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
            System.out.println( "IcentralService: cannot connect" );
        }        
    }

    public static void main( String[] args ) {
        System.out.println( "iclient" );
        
        String ac = args[2];
        System.out.println( "PMID=" + ac );

        connect(args);
        
        ObjectFactory icOF = new ObjectFactory();
        
        List<Identifier> idl = new ArrayList<Identifier>();
        Identifier id = icOF.createIdentifier();
        id.setNs("pmid");
        id.setAc(ac);
        idl.add(id);
        try{
            PublicationList pl = port.getPublicationById( idl );
            
            if( pl != null ) {
                for ( Iterator<Publication> 
                          pli = pl.getPublication().iterator(); pli.hasNext(); 
                      ) {
                    Publication p = pli.next();

                    System.out.println( " Author: " + p.getAuthor() );
                    System.out.println( " Title: " + p.getTitle() );
                }
            }
            
        } catch ( IcentralFault icf ){
            System.out.println( "SERVER FAULT: CODE:" + icf.getFaultInfo().getFaultCode() + 
                                " MESSAGE: " + icf.getFaultInfo().getMessage()  );
        }
    }
}
