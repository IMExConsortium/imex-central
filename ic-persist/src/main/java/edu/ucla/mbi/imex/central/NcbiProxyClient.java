package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $Id::                                                                  $
 # Version: $Rev::                                                        $
 #=========================================================================
 #
 # NcbiProxyClient - NCBI database accesed through DIP Proxy
 #                 
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;

import java.util.regex.*;

import java.io.*;
import java.net.URL;

import javax.xml.bind.*;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.proxy.*;
import edu.ucla.mbi.proxy.ncbi.*;

import edu.ucla.mbi.util.data.*;

public class NcbiProxyClient {
    
    private NcbiProxyPort port;
    
    private edu.ucla.mbi.dxf14.ObjectFactory
        dxo = new edu.ucla.mbi.dxf14.ObjectFactory();
    
    private edu.ucla.mbi.proxy.ncbi.ObjectFactory
        pxo = new edu.ucla.mbi.proxy.ncbi.ObjectFactory();
    
    public NcbiProxyClient() {
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiProxyClient: creating client" );
    }

    //---------------------------------------------------------------------
    
    String endpoint = null;
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint( String endpoint ) {
        this.endpoint = endpoint;
    }

    //---------------------------------------------------------------------
    
    boolean debug = false;

    public boolean getDebug() {
        return debug;
    }
    
    public void setDebug( boolean debug ) {
        this.debug = debug;
    }
    
    //---------------------------------------------------------------------

    public void initialize(){

        NcbiProxyService service = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiSoapDbService: initialize");
        log.info( "NcbiSoapDbService: endpoint=" + endpoint);
        
        System.out.println("pNcbiSoapDbService: initialize");
        System.out.println("NcbiSoapDbService: endpoint=>" + endpoint + "<");
        
        if ( endpoint == null ||  endpoint.length() == 0 ){
            log.info( "NcbiSoapDbService: default endpoint" );
            service = new NcbiProxyService();
            port = service.getProxyPort();
            System.out.println("NcbiSoapDbService: port=" + port);
        } else {
            
            try {
                URL url = new URL( endpoint + "?wsdl" );
                QName qn = new QName("http://mbi.ucla.edu/proxy/ncbi",
                                     "NcbiProxyService");
                System.out.println("NcbiSoapDbService: url =" + url);
                
                service = new NcbiProxyService( url, qn );           
                System.out.println("NcbiSoapDbService: service=" + service);
                port = service.getProxyPort();
                ( (BindingProvider) port ).getRequestContext()
                    .put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                          endpoint );   
                
                System.out.println("NcbiSoapDbService (D): port=" + port);
            } catch ( Exception ex ) {
                log.info( "NcbiSoapDbService: cannot initialize");
            }
            
        }

	System.out.println( "NcbiSoapDbService: initialize: DONE");
	
    }
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // get publication by pmid
    //-------------------------

    public Publication getPublicationByPmid( String pmid ) 
        throws ClientException{
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiProxyClient: getPublicationByPmid: " + 
                  " pmid= " + pmid );

        //Holder<DatasetType> resDataset = new  Holder<DatasetType>();
        //Holder<String> resNative = new  Holder<String>();
        //Holder<XMLGregorianCalendar> timestamp =
        //    new Holder<XMLGregorianCalendar>();

        GetPubmedArticle gpaReq = pxo.createGetPubmedArticle();
        gpaReq.setNs( "pmid" );
        gpaReq.setAc( pmid );
        gpaReq.setMatch( "exact" );
        gpaReq.setDetail( "full" );
        gpaReq.setFormat( "dxf" );
                     
        edu.ucla.mbi.proxy.ncbi.Result result = null;
        
        try {        
	   
            log.info( "NcbiProxyClient: (A) port=" + port );
            log.info( "NcbiProxyClient: (A) pmid=" + pmid );

            result = port.getPubmedArticle( gpaReq );
            log.info( "NcbiProxyClient: (A) result=" + result );
        } catch ( Exception  ex ){
            log.info( "NcbProxyClient: getPublicationByPmid(ex): " + ex );            
            if( debug ){
                ex.printStackTrace();
            }

            // reinitialize on the first error
            
            try {
                log.info( "NcbiSoap: reinitializing dip-proxy connection");                
                initialize();
                
                log.info("NcbiProxyClient: (B) port=" + port);
                result = port.getPubmedArticle( gpaReq );
                
            } catch ( Exception ex2 ){
                log.info( "NcbProxyClient: getPublicationByPmid(ex2): " + ex2);                
                if( debug ){
                    ex2.printStackTrace();
                }
                throw ClientException.NCBI_PROXY_FAULT;
            }
        }         
        
        System.out.println("NcbiProxyClient: (C) port=" + port);
        
        /*
         catch ( Exception ex3 ) {
            log.info( "NcbiProxyClient: getPublicationByPmid(ex3): " + ex3 );
            if( debug ){
                ex3.printStackTrace();
            }
            
            throw ClientException.CLIENT_EXCEPTION;
        }
        */
        
        //DatasetType dataset = resDataset.value;

        DatasetType dataset = null;
        if( result != null){
            dataset = result.getDataset();
        }
        NodeType nodeT = null;
        
        log.info("NcbiProxyClient: result=" + result);
        log.info("NcbiProxyClient: dataset=" + dataset);
        
        if( dataset != null && dataset.getNode() != null &&
            dataset.getNode().size() == 1 ) {
            nodeT = dataset.getNode().get( 0 );
            
            if ( debug ) {
                try{
                    edu.ucla.mbi.dxf14.ObjectFactory
                        dof= new edu.ucla.mbi.dxf14.ObjectFactory();
                    
                    JAXBContext jc = DxfJAXBContext.getDxfContext();
                    
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
                                        new Boolean( true ) );
                                        
                    java.io.StringWriter sw = new StringWriter();
                    marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                            "UTF-8");
                    
                    marshaller.marshal( dof.createDataset(dataset), sw );
                    log.info( "NcbiProxyClient(debug): " + sw.toString() );
                    System.out.println("NcbiProxyClient(debug): " + sw.toString());
                } catch ( JAXBException jex ){
                    jex.printStackTrace();
                }
            }
            
            if ( nodeT != null && nodeT.getAc() != null ){

                if( nodeT.getAc().equals("") ) {
                    return null;
                }
                
                PublicationVIP newPub = new PublicationVIP();

                newPub.setPmid( nodeT.getAc() );
                
                List<AttrType> atl =nodeT.getAttrList().getAttr();

                if ( atl != null ) {
                    for ( Iterator<AttrType> ii = atl.iterator(); 
                          ii.hasNext(); ) {
                        
                        AttrType ia = ii.next();

                        if (ia.getName().equals("title") ){ 
                            newPub.setTitle( ia.getValue().getValue() );
                        }
                        if (ia.getName().equals("authors") ){ 
                            newPub.setAuthor( ia.getValue().getValue() );
                        }
                        if (ia.getName().equals("abstract") ){ 
                            newPub.setAbstract( ia.getValue().getValue() );
                        }

                        if (ia.getName().equals("volume") ){ 
                            newPub.setVolume( ia.getValue().getValue() );
                        }
                        if (ia.getName().equals("issue") ){ 
                            newPub.setIssue( ia.getValue().getValue() );
                        }
                        if (ia.getName().equals("pages") ){ 
                            newPub.setPages( ia.getValue().getValue() );
                        }

                        if (ia.getName().equals("year") ){ 
                            newPub.setYear( ia.getValue().getValue() );
                        }

                        if (ia.getName().equals("publication-date") ){
                            String sdate = ia.getValue().getValue(); // 1999-10-01
                            
                            int year = 1999;
                            int month = 0;
                            int day =1;
                            
                            try {
                                Pattern p = Pattern.compile( "(\\d+)-(\\d+)-(\\d+)" );
                                Matcher m = p.matcher( sdate );
                                if( m.matches() ) {
                                    String syear = m.group(1) ;
                                    year = Integer.parseInt( syear );
                                    String smonth = m.group(2) ;
                                    month = Integer.parseInt( smonth ) - 1;
                                    String sday = m.group(3) ;
                                    day = Integer.parseInt( sday );
                                }
                            } catch ( Exception e ) {
                                // data parsing errors: ignore ?
                            }

                            GregorianCalendar date = 
                                new GregorianCalendar( year, month, day );
                            newPub.setPubDate( date );
                        }                      
                    }
                }
                
                List<XrefType> xtl = nodeT.getXrefList().getXref();

                if ( xtl != null ) {
                    for ( Iterator<XrefType> ii = xtl.iterator();
                          ii.hasNext(); ) {
                        
                        XrefType ix = ii.next();
                        
                        if( ix.getNs().equals("nlmid") ){
                            String nlmid = ix.getAc();

                            if ( newPub.getSource() == null ) {
                                Journal jrnl = getJournalByNlmid( nlmid );
                                if ( jrnl != null ) {
                                    newPub.setSource( jrnl );
                                }
                            }
                        }
                        
                        if( ix.getNs().equals( "issn" ) ){
                            String issn = ix.getAc();
                            log.debug( "issn=" + issn);
                            // nothing (for now)
                        }

                        if( ix.getNs().equals( "doi" ) ){
                            String doi = ix.getAc();
                            // nothing (for now)
                            newPub.setDoi( doi );
                            log.debug( "doi=" + doi );
                            
                        }

                        if( ix.getNs().equals( "pii" ) ){
                            String pii = ix.getAc();
                            log.debug( "pii=" + pii );
                            // nothing (for now)
                            //newPub.setJournalSpecific( pii );
                        }
                    }
                }
                
                if ( debug && nodeT != null ) {
                    edu.ucla.mbi.dxf14.ObjectFactory
                        dof= new edu.ucla.mbi.dxf14.ObjectFactory();
                    
                    DatasetType ddoc= dof.createDatasetType();
                    JAXBContext jc = DxfJAXBContext.getDxfContext();
                    
                    try{ 

                        Marshaller marshaller = jc.createMarshaller();
                        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
                                                new Boolean( true ) );
                        
                        ddoc.getNode().add( nodeT );
                        
                        java.io.StringWriter sw = new StringWriter();
                        marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                                "UTF-8");
                        
                        marshaller.marshal( dof.createDataset(ddoc), sw );
                        log.info( "NcbiProxyClient(debug): " + sw.toString() );
                        
                    } catch ( JAXBException jex ){
                        jex.printStackTrace();
                    }          
                }
                return newPub;
            }    
        }
        return null;
    }

    
    //---------------------------------------------------------------------        
    // get journal by nlmid
    //----------------------
    
    public Journal getJournalByNlmid( String nlmid ) 
        throws ClientException{
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiProxyClient: getJournalByNlmid: nlmid= " + nlmid );
        
        //Holder<DatasetType> jDT = new  Holder<DatasetType>();
        //Holder<String> jNative = new  Holder<String>();
        //Holder<XMLGregorianCalendar> jTime =
        //    new Holder<XMLGregorianCalendar>();
        
        edu.ucla.mbi.proxy.ncbi.GetJournal gjReq = pxo.createGetJournal();
        gjReq.setNs( "nlmid" );
        gjReq.setAc( nlmid );
        gjReq.setMatch( "exact" );
        gjReq.setDetail( "full" );
        gjReq.setFormat( "dxf" );

        //<xs:element name="ns" type="xs:string" default="nlmid" />
        //<xs:element name="ac" type="xs:string"/>
        //<xs:element name="match" type="xs:string" default="exact"/>
        //<xs:element name="detail" type="xs:string" default="stub" />
        //<xs:element name="format" type="xs:string" default="dxf"  />
        //<xs:element name="client" type="xs:string" default="" minOccurs="0" />
        //<xs:element name="depth" type="xs:int" default="0" minOccurs="0" />
        
        edu.ucla.mbi.proxy.ncbi.Result result = null; 
        
        try {   
            result = port.getJournal( gjReq );
        } catch ( ProxyFault f ){
            log.info( "NcbiSoap: getDxfRefList: " + f );
            if( debug ){
                f.printStackTrace();
            }
            throw ClientException.NCBI_PROXY_FAULT;
        } catch ( Exception ex ) {
            log.info( "NcbiSoap: getDxfRefList: " + ex );
            if( debug ){
                ex.printStackTrace();
            }
            
            throw ClientException.CLIENT_EXCEPTION;
        }
        
        Journal newJrnl = null;

        DatasetType jDataset = null;

        if( result != null ){
            jDataset = result.getDataset();
        }
        
        NodeType jT = null;
        if( jDataset != null && jDataset.getNode() != null &&
            jDataset.getNode().size() == 1 ) {
            jT = jDataset.getNode().get( 0 );
            
            if ( jT != null ){
                
                newJrnl = new Journal();
                newJrnl.setNlmid( nlmid );
                newJrnl.setTitle( jT.getLabel() );
                
                List<XrefType> jxtl = jT.getXrefList().getXref();
                
                if ( jxtl != null ) {
                    for ( Iterator<XrefType> ixx = jxtl.iterator();
                          ixx.hasNext(); ) {
                        
                        XrefType xx = ixx.next();
                        
                        if ( xx.getNs().equals("issn") ){
                            String issn = xx.getAc();
                            newJrnl.setIssn( issn );
                        }
                    }
                }
            }
        }
        
        if ( debug && jT != null ) {
            edu.ucla.mbi.dxf14.ObjectFactory
                dof= new edu.ucla.mbi.dxf14.ObjectFactory();
            
            DatasetType ddoc= dof.createDatasetType();
            JAXBContext jc = DxfJAXBContext.getDxfContext();
            
            try{ 
                
                Marshaller marshaller = jc.createMarshaller();
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
                                        new Boolean( true ) );
                
                ddoc.getNode().add( jT );
                
                java.io.StringWriter sw = new StringWriter();
                marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                        "UTF-8");
                
                marshaller.marshal( dof.createDataset(ddoc), sw );
                log.info( "NcbiProxyClient(debug): " + sw.toString() );
                
            } catch ( JAXBException jex ){
                jex.printStackTrace();
            }          
        }        
        return newJrnl;
    }
}
