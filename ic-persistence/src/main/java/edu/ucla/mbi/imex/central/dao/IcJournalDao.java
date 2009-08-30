package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/ic-persist#$
 * $Id:: IcRoleDao.java 10 2009-08-22 14:06:27Z lukasz                      $
 * Version: $Rev:: 10                                                       $
 *===========================================================================
 *
 * IcJournalDAO:
 *
 *========================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;

public class IcJournalDao extends AbstractDAO implements JournalDAO {

    public Journal getJournal( int id ) { 
        
        Journal journal = null;

        try {
            journal =  (IcJournal) super.find( IcJournal.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return journal; 
    }
    

    //---------------------------------------------------------------------

    public Journal getJournalByNlmid( String nlmid ) { 
        
        Journal journal = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.nlmid = :nlmid ");
            query.setParameter( "nlmid", nlmid );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return journal; 
    }

    //---------------------------------------------------------------------

    public Journal getJournal( String title ) { 
        
        Journal journal = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.title = :title ");
            query.setParameter("title", title );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return journal; 
    }
    
    //---------------------------------------------------------------------

    public List<Journal> getJournalList() {
        
        List<Journal> jlst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcJournal j order by id ");
            
            jlst = (List<Journal>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
        } 
        return jlst;
    }

    //---------------------------------------------------------------------

    public long getJournalCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize ) {
        return null;
    }
    
    //---------------------------------------------------------------------

    public void saveJournal( Journal journal ) { 
        try {          
            super.saveOrUpdate( new IcJournal( journal ) );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    

    //---------------------------------------------------------------------
    
    public void updateJournal( Journal journal ) { 
        try {
            super.saveOrUpdate( journal );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    
    //---------------------------------------------------------------------

    public void deleteJournal( Journal journal ) { 
        try {
            super.delete( journal );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }

}

