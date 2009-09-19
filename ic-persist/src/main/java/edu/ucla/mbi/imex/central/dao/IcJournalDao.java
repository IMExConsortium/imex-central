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

        long count = 0;
        try {
            startOperation();
            Query query = 
                session.createQuery( "select count(j) from IcJournal j" );
            count  = (Long) query.uniqueResult();
            tx.commit();

        } catch( DAOException dex ) {
            // log error ?
        }
        return count;
    }

    //---------------------------------------------------------------------

    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize ) {
        List<Journal> jlst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcJournalDao:getJournalList(block)"  );

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcJournal p order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );

            jlst = (List<Journal>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
            dex.printStackTrace();
        }

        System.out.println("jlist" + jlst);
        return jlst;
    }
    
    //---------------------------------------------------------------------

    public void saveJournal( Journal journal ) { 
        try {          
            if ( journal instanceof IcJournal ) {
                super.saveOrUpdate( journal );
            } else {
                super.saveOrUpdate( new IcJournal( journal ) );
            }
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

