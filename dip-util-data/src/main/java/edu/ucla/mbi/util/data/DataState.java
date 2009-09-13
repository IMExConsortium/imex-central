package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/dip-util-#$
 * $Id:: DataItem.java 12 2009-08-22 14:40:38Z lukasz                      $
 * Version: $Rev:: 12                                                      $
 *==========================================================================
 *
 * DataItem - a traceable unit of data
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.*;

public class DataState {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    //---------------------------------------------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    //---------------------------------------------------------------------

    private String comments;

    public void setComments( String comments ) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }    
}