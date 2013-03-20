package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * SorelDao: Access to subject-object relationship data
 *
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class SorelDao extends AbstractDAO implements ObsMgrDao {
    
    public void addSORel( DataItem subject, User observer ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( '\n' + "got to addSORel with subject = " + subject 
                   + " and  observer = " + observer );

        // NOTE: MUST check if old record present as neither observer nor
        //       subject are primary record ID 
        
        SORel oldRelationship = getSORel( observer, subject );
        if( oldRelationship == null ){
            SORel newRelationship = new SORel( subject, observer );
            log.debug( newRelationship );
            super.saveOrUpdate( newRelationship );
        }
    }

    public void dropSORel( DataItem subject, User observer ){
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( '\n' + "got to dropSOrel with subject = " 
                  + subject + " and  observer = " + observer );

        // NOTE: MUST check if old record present as neither observer nor
        //       subject are primary record ID
        
        SORel oldRelationship = getSORel( observer, subject );
        if( oldRelationship != null ){
            log.debug( oldRelationship );
            super.delete( oldRelationship );
        }
    }
    
    //--------------------------------------------------------------------------

    public SORel getSORel( User observer, DataItem subject ){

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getWatchStatus(HQL) uid=" + subject.getId()
                   + " sid=" + observer.getId() );
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        SORel watchStatus = null;
        
        try {
            Query query =
                session.createQuery( "select sor from SORel as sor where "
                                     + " sor.subject = :subject " 
                                     + " and sor.observer = :observer" );

            query.setParameter( "subject", subject );
            query.setParameter( "observer", observer );

            watchStatus = (SORel) query.uniqueResult();
            
        } catch ( HibernateException e ) {
            log.info( e );
            handleException( e );
            // log exception ?
            e.printStackTrace();
            
        } finally {
            session.close();
        }

        log.debug( "watchStatus=" + watchStatus );
        return watchStatus;
    }

    //--------------------------------------------------------------------------
    // List queries
    // ------------

    public List<User> getObserverList( DataItem subject ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getObserverList(HQL) id=" + subject.getId() );
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<User> olst = null;

        try {
            Query query =
                session.createQuery( "select sor.observer "
                                     + " from SORel as sor where "
                                     + " sor.subject = :subject " );

            query.setParameter( "subject", subject );
            
            olst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            log.info(e);
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        System.out.println("olist"+olst);

        return olst;
    }

    //--------------------------------------------------------------------------
    
    public List<DataItem> getSubjectList( User observer ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getSubjectList(HQL) id=" + observer.getId()  );

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<DataItem> slst = null;

        try {
            Query query =
                session.createQuery( "select sor.subject "
                                     + " from SORel as sor where "
                                     + " sor.observer = :observer " );

            query.setParameter( "observer", observer );
            
            slst = (List<DataItem>) query.list();
            tx.commit();
                
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return slst;
    }

    //--------------------------------------------------------------------------

    public List<DataItem> getSubjectList( User observer, 
                                          int firstRecord, int blockSize,
                                          String skey, boolean asc ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getSubjectList(crit) id=" + observer.getId() );

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<DataItem> slst = null;

        try {

            /*
            Query query =
                session.createQuery( "select sor.subject "
                                     + " from SORel sor where "
                                     + " sor.observer = :observer " );
            query.setParameter( "observer", observer );
            slst = (List<DataItem>) query.list();
            */

            Criteria crit = session.createCriteria( SORel.class );
            crit.add( Restrictions.eq( "observer.id", observer.getId() ) );                                      
                      
            if( firstRecord >= 0 && blockSize > 0 ){
                crit.setFirstResult( firstRecord );
                crit.setMaxResults( blockSize );
            }

            if( skey != null && skey.length() > 0 ){

                crit = crit.createCriteria( "subject" );
                
                if( asc ){
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.asc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.asc( skey ) );
                    }
                }else{
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.desc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.desc( skey ) );
                    }
                }
            }

            List<SORel> plst = (List<SORel>) crit.list();
            
            if( plst != null ){
                slst = new ArrayList<DataItem>();
 
                for( Iterator<SORel> si =  plst.iterator();
                     si.hasNext(); ){
                    slst.add( si.next().getSubject() ); 
                }
            }            
            tx.commit();
                
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return slst;
    }


    //--------------------------------------------------------------------------

    public List<DataItem> getSubjectList( User observer, 
                                          int firstRecord, int blockSize,
                                          String skey, boolean asc,
                                          Map<String,String> flt ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getSubjectList(crit) id=" + observer.getId() );

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<DataItem> slst = null;

        try {

            /*
            Query query =
                session.createQuery( "select sor.subject "
                                     + " from SORel sor where "
                                     + " sor.observer = :observer " );
            query.setParameter( "observer", observer );
            slst = (List<DataItem>) query.list();
            */

            Criteria crit = session.createCriteria( SORel.class );
            if( firstRecord >= 0 && blockSize > 0 ){
                crit.setFirstResult( firstRecord );
                crit.setMaxResults( blockSize );
            }

            crit = crit.createCriteria( "subject" );

            if( skey != null && skey.length() > 0 ){
                
                if( asc ){
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.asc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.asc( skey ) );
                    }
                }else{
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.desc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.desc( skey ) );
                    }
                }
            }

            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }
            
            List<SORel> plst = (List<SORel>) crit.list();
            
            if( plst != null ){
                slst = new ArrayList<DataItem>();
 
                for( Iterator<SORel> si =  plst.iterator();
                     si.hasNext(); ){
                    slst.add( si.next().getSubject() ); 
                }
            }            
            tx.commit();
                
        } catch ( HibernateException e ) {

            e.printStackTrace();
            handleException( e );
            // log exception ?
           
        } finally {
            session.close();
        }
        
        return slst;
    }

    //--------------------------------------------------------------------------
    // Count queries
    //--------------

    public long getSubjectCount( User observer ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getSubjectCount(HQL) id=" + observer.getId() );

        long count = 0;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query =
                session.createQuery( "select count(sor.subject) "
                                     + " from SORel as sor where "
                                     + " sor.observer = :observer " );
            
            query.setParameter( "observer", observer );  
            count  = (Long) query.uniqueResult();
            
            tx.commit();
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        log.debug( "count=" + count );
        
        return count;
    }
    
    //--------------------------------------------------------------------------

    public long getSubjectCount( User observer, Map<String,String> flt ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getSubjectCount(crit) id=" + observer.getId() );

        long count = 0;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            
            Criteria crit = session.createCriteria( SORel.class );
            crit = crit.createCriteria( "subject" );

            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }

            List foo = crit.setProjection( Projections.rowCount()).list();
            count  = ((Long) foo.get(0) ).longValue() ;
            log.debug( "count=" + count );

            tx.commit();            
        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            // log exception ?

        } finally {
            session.close();
        }
        log.debug( "count=" + count );
        
        return count;
    }
        
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    
    private  Criteria addFilter( Criteria crit, Map<String,String> flt ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        if( flt.get("status") != null && 
            !flt.get("status").equals("") ){
            
            crit.createAlias( "state", "st" )
                .add( Restrictions.eq( "st.name",
                                       flt.get( "status" ) 
                                       ) );
        }
        
        if( flt.get("partner") != null && 
            !flt.get("partner").equals("") ){
            
            if( flt.get("partner").equals("Unassigned"))
            {
                crit.add( Restrictions.isEmpty( "adminGroups" ) );
            } 
            else
            {
                crit.createAlias( "adminGroups", "ag" )
                    .add( Restrictions.eq( "ag.label",
                                           flt.get( "partner" )
                                           ) );
            }
        }
        
        if( flt.get("editor") != null && 
            !flt.get("editor").equals("") ){
            crit.createAlias( "adminUsers", "au" )
                .add( Restrictions.eq( "au.login",
                                       flt.get( "editor" )
                                       ) );
        }

        if( flt.get( "owner" ) != null && 
            !flt.get("owner").equals("") ){
            crit.createAlias( "owner", "ow" )
                .add( Restrictions.eq( "ow.login",
                                       flt.get( "owner" )
                                       ) );
        }

        if( flt.get( "cflag" ) != null && 
            !flt.get("cflag").equals("") ){
                      
            DetachedCriteria critt = DetachedCriteria.forClass( IcComment.class );
                        
            critt.createAlias("icFlag","flg")
                .add(  Restrictions.eq( "flg.name", flt.get( "cflag" ) ) )
                .setProjection( Projections.property("root.id") );
            
            crit.add( Property.forName("id").in(critt) );
            
            log.debug("Flag crt: " + flt.get( "cflag") );
        }
        return crit;
    }
}
