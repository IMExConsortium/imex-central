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
import java.util.regex.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import javax.xml.ws.WebServiceContext;


import java.io.IOException;


import javax.annotation.Resource;
//import javax.xml.ws.handler.MessageContext;



import javax.ws.rs.core.Context;
import org.apache.cxf.jaxrs.ext.MessageContext;


import javax.jws.WebService;

import org.apache.cxf.transport.http.AbstractHTTPDestination;


import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.util.data.*;

public class IcentralRestImpl implements IcentralRest{


    // Struts 2.x
    // @Resource HttpServletResponse httpResponse; 
    // @Resource private WebServiceContext wsContext;

    // Struts 3.x
    
    @Context private MessageContext messageContext;

    private EntryManager eman;
    private Map imexUrlMap;
    private Map imexDtaMap;

    private HttpServletResponse httpResponse;

     
    public IcentralRestImpl(){}

    public void setEntryManager( EntryManager manager ){
        this.eman = manager;
    }

    public void setImexUrlMap( Map imexUrl ){
        this.imexUrlMap = imexUrl;
    }
    
    public void setImexDtaMap( Map imexUrl ){
        this.imexDtaMap = imexUrl;
    }
    
    public void initialize(){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl: initialize" );

        // do nothing
    }

    public Object getRecordByAcc( String ns, String acc, String mode ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl(getRecordByAcc):" +
                  " ns=" + ns + " acc=" + acc + " mode=" + mode );
        
        String imexAc = null;
        String pmidAc = null;
        String imexDB = null;
        String imexUrl = null;
        
        if( ns != null && ns.equalsIgnoreCase("imex") ){
            imexAc = acc;                                               
            Pattern p = Pattern.compile( "(IM-\\d+)(-\\d+)?" );
            Matcher m = p.matcher( acc );
            if( m.matches() ){
                acc = m.group( 1 );
            }
        }

        String imx = (String) getImexAcc( ns, acc);
        log.debug( "IcentralRestImpl(getRecordByAcc): ns=" + ns + " acc=" +acc+ " imx=" + imx );

        if( imx != null && !imx.equals( "{}" ) ){
            try{
                JSONObject jo = new JSONObject( imx ); 
                JSONObject es = jo.getJSONArray( "entryset-list" )
                    .getJSONObject(0); 
            
                if( imexAc == null ){
                    imexAc = es.getString( "acc" );
                }
                if( pmidAc == null ){
                    pmidAc = es.getString( "pmid" );
                }
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
                if( imexAc != null ){
                    imexUrl = imexUrl.replace("%%ACC%%", imexAc );
                }
                if( pmidAc != null ){
                    imexUrl = imexUrl.replace("%%PMID%%", pmidAc );
                }
                return imexUrl;
            } catch( Exception ex ){}
        }

        if( mode.equals( "redirect" ) ){
            
            try{
                log.info("MCX:" +  messageContext);

                // Struts 2.x

                //MessageContext ctx = wsContext.getMessageContext();
                //HttpServletRequest request = (HttpServletRequest) 
                //    ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
                
                // Struts 3.x

                MessageContext ctx = messageContext;

                httpResponse = (HttpServletResponse) 
                    ctx.get(AbstractHTTPDestination.HTTP_RESPONSE);
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getRecordByAcc): EX=" + ex );
            }
            

            log.debug( "IcentralRestImpl(getRecordByAcc): redirect: httpResponse=" + httpResponse );

            log.info( "IcentralRestImpl(getRecordByAcc): redirect: ac=" + imexAc );
            log.info( "IcentralRestImpl(getRecordByAcc): redirect: url=" + imexUrl );

            try{
                if( imexAc != null ){
                    imexUrl = imexUrl.replace("%%ACC%%", imexAc );   
                }
                if( pmidAc != null ){
                    imexUrl = imexUrl.replace("%%PMID%%", pmidAc );
                }
                httpResponse.sendRedirect( imexUrl ); 
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getRecordByAcc): EX=" + ex );
            }
            return null;
        }

        if( mode.equals( "icentral" ) ){
            
            try{

                // Struts 2.x
                
                //MessageContext ctx = wsContext.getMessageContext();
                //HttpServletRequest request = (HttpServletRequest) 
                //    ctx.get(AbstractHTTPDestination.HTTP_REQUEST);

                // Struts 3.x

                MessageContext ctx = messageContext;
                httpResponse = (HttpServletResponse) 
                    ctx.get(AbstractHTTPDestination.HTTP_RESPONSE);
                
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getRecordByAcc): EX=" + ex );
            }

            
            IcPub icp = eman.getIcPubByNsAc( "imex", imexAc );
            
            if( icp != null ){
                String icUrl 
                    = "https://imexcentral.org/icentral/pubedit?id=";
                icUrl = icUrl + icp.getId() + "#pubedit=tab1";
                try{
                    httpResponse.sendRedirect( icUrl ); 
                } catch(IOException ex){
                    log.info( "IcentralRestImpl(getRecordByAcc): EX=" + ex );
                }
                return null;
            }
        }
        return ns + ":" + acc + ":" + mode;
    }
    
    public Object getImexAcc( String ns, String acc ){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralRestImpl(getRecordByAcc):" +
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
            
            if( icp.getState().getName().equals("RELEASED") ){
                String retStr = "{'entryset-list':["+
                    "{'acc':'"+icp.getImexId()+"',"+
                    " 'pmid':'"+icp.getPmid()+"',"+
                    "'curated-by':[{'name':'"+imexDB+"'}]"+
                    "}]}";

                try{
                    return retStr.replaceAll( "'", "\"" );
                } catch(Exception ex){
                    return retStr;                    
                }
            }
        } 
        return "{}";
    }

    public Object getDataFileByAcc( String ns, String acc, String format,
                                    String mode ){

        // /imex/dta/mif27/IM-1234-1?ns=imex?mode=redirect
        // redirect to psicquic... 
        
        Log log = LogFactory.getLog( this.getClass() );
        
        log.info( "IcentralRestImpl(getDataFileByAcc):" +
                  " ns=" + ns + 
                  " acc=" + acc + 
                  " format=" + format + 
                  " mode=" + mode);
        
        String imexAc = null;
        String pmidAc = null;
        String imexDB = null;
        String imexUrl = null;
        
        if( ns != null && ns.equalsIgnoreCase("imex") ){
            imexAc = acc;                                               
            Pattern p = Pattern.compile( "(IM-\\d+)(-\\d+)?" );
            Matcher m = p.matcher( acc );
            if( m.matches() ){
                acc = m.group( 1 );
            }
        }

        String imx = (String) getImexAcc( ns, acc);
        log.info( "IcentralRestImpl(getDataFileByAcc): ns=" + ns + " acc=" +acc+ " imx=" + imx );

        if( imx != null && !imx.equals( "{}" ) ){
            try{
                JSONObject jo = new JSONObject( imx ); 
                JSONObject es = jo.getJSONArray( "entryset-list" )
                    .getJSONObject(0); 
            
                if( imexAc == null ){
                    imexAc = es.getString( "acc" );
                }
                if( pmidAc == null ){
                    pmidAc = es.getString( "pmid" );
                }
                imexDB = es.getJSONArray( "curated-by" )
                    .getJSONObject(0).getString("name");
            }catch(JSONException jx ){
                log.info( "IcentralRestImpl(getDataFileByAcc): jx=" + jx );
            }
            log.info( "IcentralRestImpl(getDataFileByAcc):" +
                      " imexAc=" + imexAc + " imexDB=" + imexDB);

            Map formatMap = (Map) imexDtaMap.get( imexDB );

            imexUrl = (String) formatMap.get( format );
            log.info( "IcentralRestImpl(getDataFileByAcc): "+
                      "URL="+imexUrl );
        }

        if( imexAc == null || imexDB == null || imexUrl == null){
            return ns + ":" + acc + ":" + mode;
        }
        
        if( mode.equals( "url" ) ){
            try{
                if( imexAc != null ){
                    imexUrl = imexUrl.replace("%%ACC%%", imexAc );
                }
                if( pmidAc != null ){
                    imexUrl = imexUrl.replace("%%PMID%%", pmidAc );
                }
                return imexUrl;
            } catch( Exception ex ){}
        }

        if( mode.equals( "redirect" ) ){
            
            try{
                log.info("MCX:" +  messageContext);

                // Struts 2.x

                //MessageContext ctx = wsContext.getMessageContext();
                //HttpServletRequest request = (HttpServletRequest) 
                //    ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
                
                // Struts 3.x

                MessageContext ctx = messageContext;

                httpResponse = (HttpServletResponse) 
                    ctx.get(AbstractHTTPDestination.HTTP_RESPONSE);
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getDataFileByAcc): EX=" + ex );
            }            

            log.debug( "IcentralRestImpl(getDataFileByAcc): redirect: httpResponse=" + httpResponse );

            log.info( "IcentralRestImpl(getDataFileByAcc): redirect: ac=" + imexAc );
            log.info( "IcentralRestImpl(getDataFileByAcc): redirect: url=" + imexUrl );

            try{
                if( imexAc != null ){
                    imexUrl = imexUrl.replace("%%ACC%%", imexAc );   
                }
                if( pmidAc != null ){
                    imexUrl = imexUrl.replace("%%PMID%%", pmidAc );   
                }
                httpResponse.sendRedirect( imexUrl ); 
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getDataFileByAcc): EX=" + ex );
            }
            return null;
        }

        if( mode.equals( "icentral" ) ){
            
            try{

                // Struts 2.x
                
                //MessageContext ctx = wsContext.getMessageContext();
                //HttpServletRequest request = (HttpServletRequest) 
                //    ctx.get(AbstractHTTPDestination.HTTP_REQUEST);

                // Struts 3.x

                MessageContext ctx = messageContext;
                httpResponse = (HttpServletResponse) 
                    ctx.get(AbstractHTTPDestination.HTTP_RESPONSE);
                
            } catch( Exception ex ){
                log.info( "IcentralRestImpl(getDataFileByAcc): EX=" + ex );
            }

            
            IcPub icp = eman.getIcPubByNsAc( "imex", imexAc );
            
            if( icp != null ){
                String icUrl 
                    = "https://imexcentral.org/icentral/pubedit?id=";
                icUrl = icUrl + icp.getId() + "#pubedit=tab1";
                try{
                    httpResponse.sendRedirect( icUrl ); 
                } catch(IOException ex){
                    log.info( "IcentralRestImpl(getDataFileByAcc): EX=" + ex );
                }
                return null;
            }
        }
        return ns + ":" + acc + ":" + mode;

    }
}
