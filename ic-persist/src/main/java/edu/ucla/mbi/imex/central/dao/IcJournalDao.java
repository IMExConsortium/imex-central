package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
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

public class IcJournalDao extends AbstractDAO implements JournalDao {

    public Journal getJournal( int id ) { 
        
        Journal journal = (IcJournal) super.find( IcJournal.class, id );
        return journal; 
    }
    

    //---------------------------------------------------------------------

    public Journal getJournalByNlmid( String nlmid ) { 
        
        Journal journal = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.nlmid = :nlmid ");
            query.setParameter( "nlmid", nlmid );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return journal; 
    }

    //---------------------------------------------------------------------

    public Journal getJournal( String title ) { 
        
        Journal journal = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.title = :title ");
            query.setParameter("title", title );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return journal; 
    }

    //--------------------------------------------------------------------------

    public List<Journal> getJournalList() {
        
        List<Journal> jlst = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "from IcJournal j where id > 0 order by id" );
            
            jlst = (List<Journal>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return jlst;
    }

    //--------------------------------------------------------------------------

    public long getJournalCount() {
        
        long count = 0;
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "select count(j) from IcJournal j where id > 0" );
            count  = (Long) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return count;
    }

    //--------------------------------------------------------------------------

    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize ) {
        List<Journal> jlst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcJournalDao:getJournalList(block)"  );

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "from IcJournal p where id > 0 order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );

            jlst = (List<Journal>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        System.out.println("jlist" + jlst);
        return jlst;
    }
    
    //--------------------------------------------------------------------------

    public void saveJournal( Journal journal ) { 
                  
        if ( journal instanceof IcJournal ) {
            super.saveOrUpdate( journal );
        } else {
            super.saveOrUpdate( new IcJournal( journal ) );
        }
    }
    

    //-------------------------------------------------------------------------
    
    public void updateJournal( Journal journal ) { 
        super.saveOrUpdate( journal );
    }
    
    
    //-------------------------------------------------------------------------

    public void deleteJournal( Journal journal ) {    
        super.delete( journal );
    }

}

