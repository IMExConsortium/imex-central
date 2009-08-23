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

import edu.ucla.mbi.util.*;

public class DataComment {
    
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    //---------------------------------------------------------------------

    GregorianCalendar datestamp;
    
    GregorianCalendar getDatestamp() {
        return datestamp;
    }

    void setDatestamp(GregorianCalendar datestamp) {
        this.datestamp = datestamp;
    }

    //---------------------------------------------------------------------

    private User author;
    
    public User getAuthor() {
        return author;
    }

    public void setAuthor( User author ) {
        this.author = author;
    }
    
    //---------------------------------------------------------------------

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }
}
