package org.hupo.psi.mi.psq.server.index.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;

import java.util.Map;

import org.hupo.psi.mi.psq.server.index.*;
import org.hupo.psi.mi.psq.server.data.*;

import edu.ucla.mbi.util.JsonContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SolrIndex implements Index{
        
    String coreUrl = null;
    JsonContext context = null;
    
    public void setContext( JsonContext context ){
        this.context = context;
    }

    public SolrIndex(){}
    
    public SolrIndex( JsonContext context ){
        this.context = context;
        initialize( true );
    }
    
    public void initialize(){
        initialize( false );
    }
    
    public void initialize( boolean force ){
        
        if( force || coreUrl == null ){

            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing SolrIndex" );
            
            if( context != null && context.getJsonConfig() != null ){
                Map jCon =  context.getJsonConfig(); 

                log.info( "    solr-url=" + jCon.get("solr-url") );
                log.info( "    solr-url=" + jCon.get("solr-core") );
                
                coreUrl = (String) jCon.get("solr-url");
                if( jCon.get("solr-core") != null ){
                    coreUrl+= (String) jCon.get("solr-core");
                }                
            }
        }
    }
    
    //--------------------------------------------------------------------------
    
    public ResultSet query( String query ){

        ResultSet rs = new ResultSet();

        try{
            if( coreUrl == null ){ initialize(); }
            if( coreUrl != null ){

                SolrServer solr = new CommonsHttpSolrServer( coreUrl );
                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set( "q", query );

                try{            
                    QueryResponse response = solr.query( params );
                    System.out.println( "response = " + response );

                    SolrDocumentList res = response.getResults();
                    rs.setResultList( res );

                    

                } catch( SolrServerException sex ){
                    sex.printStackTrace();
                }
            }
        } catch( MalformedURLException mex ){
            mex.printStackTrace();
        }
        return rs;
    }
}