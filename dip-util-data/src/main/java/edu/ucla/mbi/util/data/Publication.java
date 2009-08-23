package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * DataItem - a traceable unit of data
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.GregorianCalendar;

public class Publication extends DataItem {
    
    public Publication() {}
    
    //---------------------------------------------------------------------
    // identifiers
    //------------

    private String doi;

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDoi() {
        return doi;
    }

    //---------------------------------------------------------------------

    private String pmid;

    public void setPmid( String pmid ) {
        this.pmid = pmid;
    }

    public String getPmid() {
        return pmid;
    }

    //---------------------------------------------------------------------

    private String journalSpecific;

    public void setJournalSpecific( String journalSpecific ) {
        this.journalSpecific = journalSpecific;
    }

    public String getJournalSpecific() {
        return journalSpecific;
    }


    //---------------------------------------------------------------------
    // publication data
    //-----------------

    private String title;

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    //---------------------------------------------------------------------

    private String author;

    public void setAuthor( String author ) {
        this.author = author; 
    }
    
    public String getAuthor() {
        return author; 
    }

    //---------------------------------------------------------------------

    private String publicationAbstract;
    
    public void setAbstract( String abst ) {
        this.publicationAbstract = abst;
    }

    public String getAbstract() {
        return publicationAbstract;
    }
    
    //---------------------------------------------------------------------
    // release data
    //--------------
    
    private GregorianCalendar pubDate;

    public void setPubDate( GregorianCalendar date ) {
        this.pubDate = date;
    }
    
    public GregorianCalendar getPubDate() {
        return pubDate;
    }
    
    //---------------------------------------------------------------------
    
    private GregorianCalendar expectedPubDate;
    
    public void setExpectedPubDate( GregorianCalendar date ) {
        this.expectedPubDate = date;
    }

    public GregorianCalendar getExpectedPubDate() {
        return expectedPubDate;
    }
    
    //---------------------------------------------------------------------

    private GregorianCalendar releaseDate;

    void setReleaseDate( GregorianCalendar date ) {
        this.releaseDate = date;
    }

    public GregorianCalendar getReleaseDate() {
        return releaseDate;
    }
    
}
