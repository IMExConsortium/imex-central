package edu.ucla.mbi.imex.central;

/* ========================================================================
 # $Id:: NcbiDbSoapService.java 317 2009-07-25 17:32:52Z lukasz           $
 # Version: $Rev:: 317                                                    $
 #=========================================================================
 #
 # NcbiProxyClient - NCBI database accesed through DIP Proxy
 #                 
 #====================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
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
    
    public NcbiProxyClient() {
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "NcbiProxyClient: creating client" );
        
        //NcbiProxyService service = new NcbiProxyService();
        //port = service.getProxyPort();
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
        log.info( "NcbiSoapDbService: endpoint=" + endpoint);

        if ( endpoint == null ||  endpoint.length() == 0 ){
            log.info( "NcbiSoapDbService: default endpoint" );
            service = new NcbiProxyService();
            port = service.getProxyPort();
        } else {
            
            try {

                URL url = new URL( endpoint + "?wsdl" );
                QName qn = new QName("http://mbi.ucla.edu/proxy/ncbi",
                                     "NcbiProxyService");
                service = new NcbiProxyService( url, qn );           
                port = service.getProxyPort();
                ( (BindingProvider) port ).getRequestContext()
                    .put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                          endpoint );   
            } catch ( Exception ex ) {
                log.info( "NcbiSoapDbService: cannot initialize");
            }
            
        }
    }
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // get publication by pmid
    //-------------------------

    public Publication getPublicationByPmid( String pmid ) {
        
	Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiProxyClient: getPublicationByPmid: " + 
                  " pmid= " + pmid );

        Holder<DatasetType> resDataset = new  Holder<DatasetType>();
        Holder<String> resNative = new  Holder<String>();
        Holder<XMLGregorianCalendar> timestamp =
            new Holder<XMLGregorianCalendar>();


        try {        
            port.getPubmedArticle( "pmid", pmid, "", "full", "dxf", "",
                                   0, timestamp, resDataset, resNative );
        } catch ( ProxyFault f ){
            log.info( "NcbiSoap: getDxfRefList: " + f );
        } catch ( Exception ex ) {
            log.info( "NcbiSoap: getDxfRefList: " + ex );
        }

        DatasetType dataset = resDataset.value;
        NodeType nodeT = null;
        if( dataset != null && dataset.getNode() != null &&
            dataset.getNode().size() == 1 ) {
            nodeT = dataset.getNode().get( 0 );
         
            if ( nodeT != null ){

                Publication newPub = new Publication();
                newPub.setPmid( pmid );
                
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
                        
                        if ( ix.getNs().equals("nlmid") ){
                            String nlmid = ix.getAc();

                            if ( newPub.getSource() == null ) {
                                Journal jrnl = getJournalByNlmid( nlmid );
                                if ( jrnl != null ) {
                                    newPub.setSource( jrnl );
                                }
                            }
                        }
                        
                        if (ix.getNs().equals("issn") ){
                            String issn = ix.getAc();
                            // nothing (for now)
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
    
    public Journal getJournalByNlmid( String nlmid ) {
        
	Log log = LogFactory.getLog( this.getClass() );
        log.info( "NcbiProxyClient: getJournalByNlmid: nlmid= " + nlmid );
        
        Holder<DatasetType> jDT = new  Holder<DatasetType>();
        Holder<String> jNative = new  Holder<String>();
        Holder<XMLGregorianCalendar> jTime =
            new Holder<XMLGregorianCalendar>();
        
        try {
            port.getJournal( "nlm", nlmid, "", "full", "dxf", "",
                             0, jTime, jDT, jNative );
        } catch ( ProxyFault f ){
            log.info( "NcbiSoap: getDxfRefList: " + f );
            f.printStackTrace();
        } catch ( Exception ex ) {
            log.info( "NcbiSoap: getDxfRefList: " + ex );
        }

        Journal newJrnl = null;
        
        DatasetType jDataset = jDT.value;
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