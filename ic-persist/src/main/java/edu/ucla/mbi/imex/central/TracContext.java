package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
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

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;


import edu.ucla.mbi.imex.central.dao.*;


public class TracContext extends JsonContext {

    private IcPubDao pubDao;

    public IcPubDao getPubDao() {
        return pubDao;
    }

    public void setPubDao( IcPubDao dao ) {
        pubDao = dao;
    }

    //--------------------------------------------------------------------------

    private IcJournalDao journalDao;

    public IcJournalDao getJournalDao() {
        return journalDao;
    }

    public void setJournalDao( IcJournalDao dao ) {
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

    private IcAdiDao icAdiDao;

    public IcAdiDao getAdiDao() {
        return icAdiDao;
    }

    public void setAdiDao( IcAdiDao dao ) {
        icAdiDao = dao;
    }

    //--------------------------------------------------------------------------

    private IcWorkflowDao icWorkflowDao;

    public IcWorkflowDao getWorkflowDao() {
        return icWorkflowDao;
    }

    public void setWorkflowDao( IcWorkflowDao dao ) {
        icWorkflowDao = dao;
    }

    //--------------------------------------------------------------------------
    
    private SorelDao sorelDao;
    
    public SorelDao getSorelDao() {
        return sorelDao;
    }
    
    public void setSorelDao( SorelDao dao ) {
        sorelDao = dao;
    }


    //--------------------------------------------------------------------------
    
    private EorelDao eorelDao;
    
    public EorelDao getEorelDao() {
        return eorelDao;
    }
    
    public void setEorelDao( EorelDao dao ) {
        eorelDao = dao;
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
