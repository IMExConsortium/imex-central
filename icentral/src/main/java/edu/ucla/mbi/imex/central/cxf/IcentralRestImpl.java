package edu.ucla.mbi.imex.central.cxf;
     
/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # RESTful Web service interface
 #
 #=========================================================================== */


import java.util.*;
import javax.ws.rs.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.MessageContext;

import java.io.IOException;
import javax.annotation.Resource;

import javax.jws.WebService;

import org.apache.cxf.transport.http.AbstractHTTPDestination;


import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.util.data.*;

public class IcentralRestImpl implements IcentralRest{

    @Resource
        HttpServletResponse httpResponse; 

    private EntryManager eman;
    private Map imexUrlMap;
     
    public IcentralRestImpl(){}

    public void setEntryManager( EntryManager manager ){
        this.eman = manager;
    }

    public void setImexUrlMap( Map imexUrl ){
        this.imexUrlMap = imexUrl;
    }
    
    public void initialize(){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl: initialize" );

        // do nothing
    }

    public Object getRecordByAcc( String ns, String acc, String mode ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl(getRecordByAcc):" +
                  " ns=" + ns + " acc=" + acc + "mode=" + mode );
        
        String imexAc = null;
        String imexDB = null;
        String imexUrl = null;
        
        String imx = (String) getImexAcc( ns, acc);
        if( imx != null && !imx.equals( "{}" ) ){
            try{
                JSONObject jo = new JSONObject( imx ); 
                JSONObject es = jo.getJSONArray( "entryset-list" )
                    .getJSONObject(0); 
            
                imexAc = es.getString( "acc" );
                imexDB = es.getJSONArray( "curated-by" )
                    .getJSONObject(0).getString("name");
            }catch(JSONException jx ){
                log.info( "IcentralRestImpl(getRecordByAcc): jx=" + jx );
            }
            log.info( "IcentralRestImpl(getRecordByAcc):" +
                      " imexAc=" + imexAc + " imexDB=" + imexDB);

            imexUrl = (String) imexUrlMap.get( imexDB );
            
        }

        if( imexAc == null || imexDB == null || imexUrl == null){
            return ns + ":" + acc + ":" + mode;
        }
        
        if( mode.equals( "url" ) ){
            try{
                imexUrl = imexUrl.replace("%%ACC%%", imexAc );
                return imexUrl;
            } catch( Exception ex ){}
        }

        if( mode.equals( "redirect" ) ){
            try{
                imexUrl = imexUrl.replace("%%ACC%%", imexAc );
                httpResponse.sendRedirect( imexUrl ); 
            } catch( Exception ex ){}
            return null;
        }

        if( mode.equals( "icentral" ) ){
            
            IcPub icp = eman.getIcPubByNsAc( "imex", imexAc );
            
            if( icp != null ){
                String icUrl 
                    = "https://imexcentral.org/icentral/pubedit?id=";
                icUrl = icUrl + icp.getId() + "#pubedit=tab1";
                try{
                    httpResponse.sendRedirect( icUrl ); 
                } catch(IOException ex){}
                return null;
            }
        }
        return ns + ":" + acc + ":" + mode;
    }
    
    public Object getImexAcc( String ns, String acc ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl(getImexAcc):" +
                  " ns=" + ns + " acc=" + acc );
        
        IcPub icp = eman.getIcPubByNsAc( ns, acc );
        
        if( icp != null ){
            String imexDB = "";
            log.info( "IcentralRestImpl(getImexAcc):" +
                      " status=" + icp.getState().getName() );
            
            Set<Group> groups = icp.getAdminGroups();
            for( Iterator<Group> ig = groups.iterator(); ig.hasNext(); ){
                Group g = ig.next();
                Set<Role> roles = g.getRoles();
                for( Iterator<Role> ir = roles.iterator(); ir.hasNext(); ){
                    Role r = ir.next();
                    if( r.getName().equals("IMEx partner") ){
                        imexDB = g.getName();
                        break;
                    }
                }
                if( !imexDB.equals("") ) break;
            }

            log.info( "IcentralRestImpl(getImexAcc):" +
                      " IMEX DB=" + imexDB );
            
            if( icp.getState().getName().equals("RELEASED") ){
                return "{'entryset-list':["+
                    "{'acc':'"+icp.getImexId()+"',"+
                    "'curated-by':[{'name':'"+imexDB+"'}]"+
                    "}]}";
            } 
        } 
        return "{}";
    }
}