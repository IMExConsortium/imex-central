package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * WikiParseAction action
 *                
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionSupport;

import java.util.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;

import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

import info.bliki.wiki.model.*;

public class WikiParseAction extends ActionSupport {
    
    private static final String JSON = "json";    
        
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    private String txt="";
    private String html="";
    

    public void setTxt( String txt ){
        this.txt=txt;
    }

    public String getTxt() {
        return this.txt;
    }
    
    public String getHtml(){
        return this.html;
    }

    //--------------------------------------------------------------------------


    public String execute() throws Exception {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " WikiParseAction:" + txt );
        
        if( txt != null ) {

            WikiModel wikiModel = new WikiModel( "img/${image}", 
                                                 "page?id=${title}" );
            html = wikiModel.render( txt );
            
        }
        return JSON;        
    }
}