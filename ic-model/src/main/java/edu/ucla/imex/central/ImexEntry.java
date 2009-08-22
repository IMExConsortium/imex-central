package edu.ucla.mbi.imex.central;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * IcPublication - a traceable publication
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.GregorianCalendar;

import edu.ucla.mbi.util.data.*;

public class ImexEntry extends DataItem {
    
    ImexEntry() {}
    
    //---------------------------------------------------------------------
    
    private GregorianCalendar datestamp;
    
    public void setDatestamp( GregorianCalendar date ) {
        this.datestamp = date;
    }

    public GregorianCalendar getDatestamp() {
        return datestamp;
    }
    
    //---------------------------------------------------------------------
    
    private String imexId;

    public String getImexId() {
        return imexId;
    }

    public void setImexId( String id ) {
        imexId = id;
    }

    //---------------------------------------------------------------------

    private int imexVersion;
    
    public int getImexVersion() {
        return imexVersion;
    }

    public void setImexVersion( int version ) {
        imexVersion = version;
    }

    //---------------------------------------------------------------------
    
    private String mifData;
    
    public String getMifData() {
        return mifData;
    }

    public void setMifData( String mifData ) {
        this.mifData = mifData;
    }

    //---------------------------------------------------------------------

    private String mifVersion;
    
    public String getMifVersion() {
        return mifVersion;
    }

    public void setMifVersion( String version ) {
        mifVersion = version;
    }    
}

