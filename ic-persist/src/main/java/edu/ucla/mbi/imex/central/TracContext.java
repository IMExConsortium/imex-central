package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id:: UserContext.java 465 2009-08-21 02:36:27Z lukasz                      $
 # Version: $Rev:: 465                                                         $
 #==============================================================================
 #                                                                             $
 # TracContext: JSON-based tracker configuration                               $
 #                                                                             $
 #     TO DO:                                                                  $
 #                                                                             $
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.io.*;
import org.json.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;


import edu.ucla.mbi.imex.central.dao.*;


public class TracContext extends JsonContext {

    private PublicationDAO pubDao;

    public PublicationDAO getPubDao() {
        return pubDao;
    }

    public void setPubDao( PublicationDAO dao ) {
        pubDao = dao;
    }

    //--------------------------------------------------------------------------

    private JournalDAO journalDao;

    public JournalDAO getJournalDao() {
        return journalDao;
    }

    public void setJournalDao( JournalDAO dao ) {
        journalDao = dao;
    }


    //--------------------------------------------------------------------------

    private IcStatsDao icStatsDao;

    public IcStatsDao getIcStatsDao() {
        return icStatsDao;
    }

    public void setIcStatsDao( IcStatsDao dao ) {
        icStatsDao = dao;
    }


    //--------------------------------------------------------------------------
    //  NCBI Proxy Access
    //-------------------
    
    private NcbiProxyClient ncbiProxy;

    public void setNcbiProxyClient( NcbiProxyClient proxy ) {
        this.ncbiProxy = proxy;
    }

    public NcbiProxyClient getNcbiProxyClient() {
        return this.ncbiProxy;
    }
    
    //--------------------------------------------------------------------------
    
    public void initialize() { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "TracContext: initializing" );
        
    }
}
