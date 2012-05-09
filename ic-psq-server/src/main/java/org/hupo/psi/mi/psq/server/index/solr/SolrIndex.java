package org.hupo.psi.mi.psq.server.index.solr;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # PsqPortImpl: implementation of PSICQUIC 1.1 SOAP service
 #
 #=========================================================================== */

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

    String baseUrl = null;
    String queryUrl = null;
    List<String> shardUrl = null;

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
        
        if( force || queryUrl == null ){

            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing SolrIndex" );
            
            if( context != null && context.getJsonConfig() != null ){
                Map jCon =  context.getJsonConfig(); 

                log.info( "    url=" + jCon.get("url") );
                log.info( "    shard=" + jCon.get("shard") );
                
                baseUrl = (String) jCon.get("url");
                queryUrl = baseUrl + "?";
            
                if( jCon.get("shard") != null ){
                    List shardList = (List) jCon.get("shard");
                    String shardStr= "";
                    for( Iterator is = shardList.iterator(); is.hasNext(); ){
                        String csh = (String) is.next();
                        shardStr += csh + ",";
                        if(shardUrl == null){
                            shardUrl = new ArrayList<String>();
                        }
                        shardUrl.add( csh );
                    }
                    if( !shardStr.equals("") ){
                        shardStr = shardStr.substring( 0, shardStr.length()-1);
                        queryUrl += "shards=" + shardStr + "&";                    
                    }
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

    //--------------------------------------------------------------------------



}