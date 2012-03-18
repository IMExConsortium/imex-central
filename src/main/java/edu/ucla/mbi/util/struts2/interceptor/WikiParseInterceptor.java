package edu.ucla.mbi.util.struts2.interceptor;

/* =============================================================================
 # $HeadURL::                                                                  $
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #                                                                             $
 # WikiParseInterceptor: pre/postprocesses menu selection                      $
 #                                                                             $
 #=========================================================================== */

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import java.io.InputStream;
import javax.servlet.ServletContext;

import edu.ucla.mbi.util.*;

public class WikiParseInterceptor implements Interceptor{

    public void destroy() { }

    public void init() {

    }

    private static List<Integer> selDefault;

    public String intercept( ActionInvocation invocation )
        throws Exception {

        Log log = LogFactory.getLog( this.getClass() );

        if ( invocation.getAction() instanceof WikiParseAware ) {

            WikiParseAware action = (WikiParseAware) invocation.getAction();

            //------------------------------------------------------------------
            // load menu definitions (if needed)
            //----------------------------------

            //menuInitialize( action );
            
            invocation.addPreResultListener( new PreResultListener() {
                    public void beforeResult( ActionInvocation invocation,
                                              String resultCode ) {
                        //menuProcess( invocation );
                    }
                } );
        }

        String result = invocation.invoke();
        return result;
    }
}
