package org.hupo.psi.mi.psq.builder;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # BuildSolrDerbyIndex: populate Solr/Derby-based PSQ index 
 #
 #=========================================================================== */

import java.util.*;
import java.util.zip.*;
import java.net.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.server.index.*;

import org.hupo.psi.mi.psq.server.index.solr.SolrIndex;
import org.hupo.psi.mi.psq.server.store.derby.DerbyStore;

import edu.ucla.mbi.util.JsonContext;

public class BuildSolrDerbyIndex{

    private List<String> solrURL = 
        Arrays.asList( "http://localhost:8080/ic-psq/solr/psq-01" );    

    private String rmgrURL = 
        "http://localhost:8080/ic-psq/recordmgr";
    
    public static final String 
        CONTEXT = "/etc/psq-context-default.json";
    
    public static final String 
        RFRMT = "mif254";
        
    String rfrmt;
    
    String rootDir= null;
    boolean zip = false;

    boolean defCtx = true;

    SolrIndex sIndex = null;
    DerbyStore dStore = null;
    
    //--------------------------------------------------------------------------
    
    JsonContext psqContext;
    
    public void setPsqContext( JsonContext context ){
        psqContext = context;
    } 

    public JsonContext getPsqContext(){
        return psqContext;
    }

    //--------------------------------------------------------------------------
    
    public BuildSolrDerbyIndex( String ctx, String rfrmt,
                                String dir, boolean zip ){
        
        InputStream ctxStream = null;
        try{
            ctxStream = new FileInputStream( ctx );
        } catch( Exception ex ){
            ex.printStackTrace();
        }
        
        defCtx = false;
        this.initialize( ctxStream, rfrmt,  dir, zip );
    }

    public BuildSolrDerbyIndex( InputStream ctxStream, String rfrmt,
                                String dir, boolean zip ){
        this.initialize( ctxStream, rfrmt,  dir, zip );
    }
    
    private void initialize( InputStream ctxStream, String rfrmt,
                             String dir, boolean zip ){
        
        this.zip = zip;
        this.rfrmt = rfrmt;
        this.rootDir = dir;
        
        // get context
        //------------

        psqContext = new JsonContext();

        try{                     
            //psqContext.readJsonConfigDef( new FileInputStream( ctx ) );
            
            psqContext.readJsonConfigDef( ctxStream );
            Map jctx = (Map) getPsqContext().getJsonConfig();
            
            // SOLR Index
            //-----------

            if( ((Map)jctx.get( "index" )).get( "solr" ) != null ){
                sIndex = new SolrIndex( psqContext );

                sIndex.initialize();
                sIndex.connect();
            }            
            
            // Derby data store
            //-----------------

            if( ((Map)jctx.get( "store" )).get( "derby" ) != null ){
                
                dStore = new DerbyStore( psqContext );
                dStore.initialize();
            }
            
        } catch( Exception ex ){
            ex.printStackTrace();
        }        
    }

    //--------------------------------------------------------------------------

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
            
            sIndex.addFile( rfrmt, 
                            file.getCanonicalPath().replace( root, "" ), is );
            
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        
        // transform/add -> datastore
        //---------------------------

        try{
            
            InputStream is = null;
            
            if( zip ){
                ZipFile zf = new ZipFile( file );
                is = zf.getInputStream( zf.entries().nextElement() );
            } else {
                is = new FileInputStream( file );  
            }
            
            dStore.addFile( rfrmt,
                            file.getCanonicalPath().replace( root, "" ), is );
            
        }catch( Exception ex ){
            ex.printStackTrace();
        }       
    }
}
