package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * Journal
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

public class Journal extends DataSource {

    public Journal() {}
    
    //---------------------------------------------------------------------
    
    String title;
    
    public void setTitle( String title ) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    //---------------------------------------------------------------------

    String nlmid;

    public void setNlmid( String nlmid ) {
        this.nlmid = nlmid;
    }

    public String getNlmid() {
        return nlmid;
    }


    //---------------------------------------------------------------------

    String comments;
    
    public void setComments( String comments ) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }


    //---------------------------------------------------------------------
    
    String websiteURL;

    public void setWebsiteUrl( String url ) {
        this.websiteURL = url;
    }

    public String getWebsiteUrl() {
        return websiteURL;
    }
}
