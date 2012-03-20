package org.hupo.psi.mi.psq.server.index.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;

import org.hupo.psi.mi.psq.server.index.*;

public class SolrIndex implements Index{
    
    String url = "http://10.1.1.206:8080/ic-psq-server/solr/psq-01";
    
    public SolrIndex(){

        // configuration info should be set here
        
    }

    public ResultSet query( String query ){

        ResultSet rs = new ResultSet();

        try{
            SolrServer solr = new CommonsHttpSolrServer( url );
            
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", query);

            try{            
                QueryResponse response = solr.query( params );
                System.out.println( "response = " + response );

                SolrDocumentList res = response.getResults();
                rs.setResultList( res );
                
            } catch( SolrServerException sex ){
                sex.printStackTrace();
            }
        } catch( MalformedURLException mex ){
            mex.printStackTrace();
        }
        return rs;

    }
}