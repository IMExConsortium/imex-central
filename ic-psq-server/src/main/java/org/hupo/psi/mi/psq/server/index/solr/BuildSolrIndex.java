package org.hupo.psi.mi.psq.server.index.solr;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # BuildSolrIndex: Build Solr-based index
 #
 #=========================================================================== */

import java.util.*;
import java.net.*;
import java.io.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.util.JAXBResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.cli.*;

import org.apache.solr.common.*;
import org.apache.solr.core.*;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.client.solrj.response.*;

import java.util.zip.*;
import java.lang.Thread.State;

import edu.ucla.mbi.util.JsonContext;

public class BuildSolrIndex{

    private List<String> solrURL = 
        Arrays.asList( "http://localhost:8080/ic-psq/solr/psq-01" );    
    private String rmgrURL = 
        "http://localhost:8080/ic-psq/recordmgr";
    
    public static final String 
        CONTEXT = "../etc/ic-psq/server/psq-context.json";
    
    public static final String 
        RFRMT = "mif254";
    
    Map<String,String> xsltIndex;
    Map<String,String> xsltRecord;
    
    String rfrmt;

    //String mifDir =  "/home/lukasz/imex_test";

    String rootDir= null;
    boolean zip = false;

        
    SolrServer solr = null;

    Transformer psqtr = null;
    Transformer tabtr = null;

    //--------------------------------------------------------------------------

    JsonContext psqContext;
    
    public void setPsqContext( JsonContext context ){
        psqContext = context;
    }

    public JsonContext getPsqContext(){
        return psqContext;
    }

    //--------------------------------------------------------------------------
    
    
    public BuildSolrIndex( String ctx, String rfrmt,
                           String dir, boolean zip ){
        
        this.zip = zip;
        this.rfrmt = rfrmt;
        this.rootDir = dir;

        // get context
        //------------

        psqContext = new JsonContext();

        try{                     
            psqContext.readJsonConfigDef( new FileInputStream( ctx ) );
            Map jctx = (Map) getPsqContext().getJsonConfig();

            // index transformers
            //-------------------

            xsltIndex = (Map<String,String>) ((Map) jctx.get( "index" ))
                .get( "xslt" );
            
            // SOLR server(s)
            //---------------

            List slst = 
                (List) ((Map) ((Map) jctx.get( "index" )).get( "solr" ))
                .get("server");
            
            if( slst != null ){

                solrURL = new ArrayList<String>();

                for( Iterator i = slst.iterator(); i.hasNext(); ){

                    Map ci = (Map) i.next();
                    String url = (String) ci.get("url");
                    String core = (String) ci.get("core");
                    
                    solrURL.add( url + core );
                }
            }

            // record manager
            //---------------

            rmgrURL = (String) ((Map) jctx.get( "store" ))
                .get( "record-mgr" );
            

            // record transformers
            //--------------------

            xsltRecord = (Map<String,String>) ((Map) jctx.get( "store" ))
                .get( "xslt" );
            
            
        } catch( Exception ex ){
            ex.printStackTrace();
        }

        try{
            
            // connect to solr
            //----------------

            System.out.println( " Solr URL: " + solrURL.get(0) );
            System.out.println( " RecordMgr URL: " + rmgrURL );
            System.out.println( " Index XSLT: " + xsltIndex.get( rfrmt ) );
            System.out.println( " Record XSLT: " + xsltRecord.get( rfrmt ) );
            
            solr = new CommonsHttpSolrServer( solrURL.get(0) );
            
            //set mif2psq transformer
            //-----------------------
            
            try {
                DocumentBuilderFactory
                    dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware( true );

                DocumentBuilder db = dbf.newDocumentBuilder();               
                Document xslDoc = db.parse( new File( xsltIndex.get( rfrmt ) ) );

                DOMSource xslDomSource = new DOMSource( xslDoc );
                TransformerFactory
                    tFactory = TransformerFactory.newInstance();
                
                psqtr = tFactory.newTransformer( xslDomSource );
                
            } catch( Exception ex ) {
                ex.printStackTrace();
            }


            //set mif2tab transformer
            //-----------------------

            try {
                DocumentBuilderFactory
                    dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware( true );

                DocumentBuilder db = dbf.newDocumentBuilder();               
                Document xslDoc = db.parse( new File( xsltRecord.get( rfrmt ) ) );
                
                DOMSource xslDomSource = new DOMSource( xslDoc );
                TransformerFactory
                    tFactory = TransformerFactory.newInstance();
                
                tabtr = tFactory.newTransformer( xslDomSource );
                
            } catch( Exception ex ) {
                ex.printStackTrace();
            }

            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void start(){
    
        File dirFl = new File( rootDir );
        try{
            processFiles( dirFl.getCanonicalPath(), dirFl );
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void processFiles( String root, File file ){
        
        File[] filesAndDirs = file.listFiles();
        List<File> filesDirs = Arrays.asList( filesAndDirs );
        for(File sfile : filesDirs) {
            if ( ! sfile.isFile() ) {
                //recursive call: must be a directory
                processFiles( root, sfile );
            } else {
                index( root, sfile );                
            }
        }
    }
    
    public void index( String root, File file ){
        System.out.println( file );

        // transform/add -> index
        //-----------------------

        try{

            InputStream is = null;
            
            if( zip ){
                ZipFile zf = new ZipFile( file );
                is = zf.getInputStream( zf.entries().nextElement() );
            } else {
                is = new FileInputStream( file );  
            }
            
            StreamSource ssNative = new StreamSource( is );
            
            DOMResult domResult = new DOMResult();

            //transform into psq dom
            //----------------------
            psqtr.clearParameters();
            psqtr.setParameter( "file", 
                                file.getCanonicalPath().replace(root,"") );
            
            psqtr.transform( ssNative, domResult );           
            Node nd = domResult.getNode().getFirstChild();
            
            NodeList dl = nd.getChildNodes();
            for( int i = 0; i< dl.getLength() ;i++ ){
                if(dl.item(i).getNodeName().equals("doc")){
                    SolrInputDocument doc =  new SolrInputDocument();
                    NodeList field = dl.item(i).getChildNodes();
                    for( int j = 0; j< field.getLength() ;j++ ){
                        if( field.item(j).getNodeName().equals("field") ){
                            Element fe = (Element) field.item(j);
                            String name = fe.getAttribute("name");
                            String value = fe.getFirstChild().getNodeValue();
                            doc.addField( name,value );
                        }
                    }                    
                    solr.add( doc );
                }
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        // transform/add -> mitab
        //-----------------------

        try{
            
            InputStream is = null;
            
            if( zip ){
                ZipFile zf = new ZipFile( file );
                is = zf.getInputStream( zf.entries().nextElement() );
            } else {
                is = new FileInputStream( file );  
            }
            
            StreamSource ssNative = new StreamSource( is );
            
            DOMResult domResult = new DOMResult();
            
            //transform into mitab dom
            //------------------------
            
            String pid = "";
            String mtb = "";
            tabtr.clearParameters();
            tabtr.setParameter( "file",
                                file.getCanonicalPath().replace(root,"") );            
            tabtr.transform( ssNative, domResult );           
            Node nd = domResult.getNode().getFirstChild();
            
            NodeList dl = nd.getChildNodes();
            for( int i = 0; i< dl.getLength() ;i++ ){
                if(dl.item(i).getNodeName().equals("doc")){

                    SolrInputDocument doc =  new SolrInputDocument();
                    NodeList field = dl.item(i).getChildNodes();

                    for( int j = 0; j< field.getLength() ;j++ ){
                        if( field.item(j).getNodeName().equals("field") ){
                            Element fe = (Element) field.item(j);
                            String name = fe.getAttribute("name");
                            if( name.equals("mitab") ){
                                String value = 
                                    fe.getFirstChild().getNodeValue();
                                mtb += value+"\n";
                            }
                        }
                    }                    
                }
            }

            // add mitab
            //----------

            try{
                String postData = URLEncoder.encode("op", "UTF-8") + "=" 
                    + URLEncoder.encode( "add", "UTF-8");
                postData += "&" + URLEncoder.encode("pid", "UTF-8") + "=" 
                    + URLEncoder.encode( pid, "UTF-8");
                postData += "&" + URLEncoder.encode("mitab", "UTF-8") + "=" 
                    + URLEncoder.encode( mtb, "UTF-8");
                
                URL url = new URL( rmgrURL );
                URLConnection conn = url.openConnection();
                conn.setDoOutput( true );
                OutputStreamWriter wr = 
                    new OutputStreamWriter( conn.getOutputStream() );
                wr.write(postData);
                wr.flush();

                // Get the response
                BufferedReader rd = 
                    new BufferedReader( new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                }
                wr.close();
                rd.close();
            } catch (Exception e) {
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
        }       
    }
}
