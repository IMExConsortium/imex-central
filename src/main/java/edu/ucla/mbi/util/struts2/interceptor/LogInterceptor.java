package edu.ucla.mbi.util.struts2.interceptor;

/* =============================================================================
 # $HeadURL::                                                                  $
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # LogInterceptor: controlls access to actions/operations
 #         
 #
 #=========================================================================== */
 
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.*;

import java.io.InputStream;
import javax.servlet.ServletContext;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.struts2.action.*;


public class LogInterceptor implements Interceptor{

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";
    public static final String ACL_TGET = "acl_tget";

    private JsonContext logCtx;
    
    public void setLogContext( JsonContext context ) {
        logCtx = context;
    }

    //--------------------------------------------------------------------------

    public void destroy() {}
    public void init() {}

    //--------------------------------------------------------------------------
    
    private void logInitialize ( LogAware action, boolean force ) {
	
        Log log = LogFactory.getLog( this.getClass() );
        log.info( " logCtx(i)=" +logCtx);
        if ( logCtx.getJsonConfigObject() == null || force ) {
	    log.info( " initilizing LOG defs..." );
            String jsonPath =
                (String) logCtx.getConfig().get( "json-config" );
            log.info( "JsonLoggerDef=" + jsonPath );
	    
            if ( jsonPath != null && jsonPath.length() > 0 ) {

                String cpath = jsonPath.replaceAll("^\\s+","" );
                cpath = jsonPath.replaceAll("\\s+$","" );
		
                try {
                    InputStream is = action.
			getServletContext().getResourceAsStream( cpath );
                    logCtx.readJsonConfigDef( is );
		} catch ( Exception e ){
                    log.info( "JsonAclContext reading error" );
                }
            }
            log.info( " logger initialized" );
        } else {
            log.info( " init file missing..." );
        }
        log.info( " logCtx(f)=" +logCtx);
    }
    
    //---------------------------------------------------------------------
    
    public String intercept( ActionInvocation invocation )
        throws Exception {
        
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "Log Interceptor:" );
    
        if ( ! ( invocation.getAction() instanceof LogAware ) ) {
            return invocation.invoke(); // no LogInterceptor support
        }

        // load menu definitions (if needed)
        //----------------------------------
            
        LogAware law = (LogAware) invocation.getAction();
        Map sss = law.getSession();
        
        logInitialize( law, true ); 	    
        
        // action name
        //------------
            
        String actionName = invocation.getInvocationContext().getName();
        log.info( "LOG: action name=" + actionName );
        log.info( "LOG: JsonConfig=" + logCtx.getJsonConfig() );
        
        Map<String,Object> loglst = 
            (Map<String,Object>) logCtx.getJsonConfig().get( "op-log" );
        
        Map<String,Object> alog = 
            (Map<String,Object>) loglst.get( actionName );
        
        if ( alog == null ) return invocation.invoke(); // action not logged

        //----------------------------------------------------------------------
        // logged action/operation  
        //------------------------

        String retval = invocation.invoke();

        //log.info( "LOG:  ruleset=" + acr );

        return retval;
    }
}
