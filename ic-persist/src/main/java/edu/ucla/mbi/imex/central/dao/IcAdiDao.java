package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcAdiDao:
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

public class IcAdiDao extends AbstractDAO implements AdiDAO {
    
    // AdiDAO
    //--------------------------------------------------------------------------    
    
    public AttachedDataItem getAdi( int id ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcAdiDao->getAdi: id(int)=" + id  );

        AttachedDataItem adi = 
            (AttachedDataItem) super.find( AttachedDataItem.class, 
                                           new Integer( id ) );
        
        log.info( "IcAdiDao->getAdi: id=" + id + " ::DONE"  );
        return adi;

    }

    //--------------------------------------------------------------------------
    
    public IcFlag getIcFlag( int id ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcAdiDao->getIcFlag: id(int)=" + id  );

        if( id <= 0 ) return null;
        
        IcFlag flag = (IcFlag) super.find( IcFlag.class,
                                           new Integer( id ) );
        
        log.info( "IcAdiDao->getIcFlag: id=" + id + " ::DONE"  );
        return flag;
    }       
    
    //--------------------------------------------------------------------------

    public List<AttachedDataItem> getAdiListByRoot( DataItem root ){

        List<AttachedDataItem> alst = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query =
                session.createQuery( "from AttachedDataItem a where " +
                                     " a.root = :root ");
            query.setParameter("root", root );
            
            alst = (List<AttachedDataItem>) query.list();

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "getAdiListByParent:" + alst.size() );


            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return alst;
    }

    //--------------------------------------------------------------------------

    public long countIcCommByRoot( DataItem root ){
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Long lCnt = null;

        try {
            Query query =
                session.createQuery( "select count(*) "
                                     + " from AttachedDataItem a where "
                                     + " a.class = IcComment "
                                     + "  and a.root = :root " );
            query.setParameter( "root", root );
            
            lCnt = (Long) query.uniqueResult() ;

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "countIcCommByRoot:" + lCnt );
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        if( lCnt != null ){
            return lCnt.longValue();
        } 
        return 0L;            
    }

    //--------------------------------------------------------------------------

    public long countIcLogEntryByRoot( DataItem root ){
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Long lCnt = null;

        try {
            Query query =
                session.createQuery( "select count(*) "
                                     + " from AttachedDataItem a where "
                                     + " a.class = IcLogEntry "
                                     + "  and a.root = :root " );
            query.setParameter( "root", root );
            
            lCnt = (Long) query.uniqueResult() ;

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "countIcCommByRoot:" + lCnt );
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        if( lCnt != null ){
            return lCnt.longValue();
        } 
        return 0L;            
    }

    //--------------------------------------------------------------------------

    public List<AttachedDataItem> getAdiListByParent( DataItem parent ){

        List<AttachedDataItem> alst = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query =
                session.createQuery( "from AttachedDataItem a where " +
                                     " a.parent = :parent ");
            query.setParameter("parent", parent );

            alst = (List<AttachedDataItem>) query.list();

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "getAdiListByParent:" + alst.size() );


            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return alst;
    }

    //--------------------------------------------------------------------------

    public void saveAdi( AttachedDataItem  adi ){

        if( adi.getCrt() == null ){
            adi.setCrt( new GregorianCalendar() );
        }

        if ( adi  instanceof AttachedDataItem ) {
            super.saveOrUpdate( adi );
        } else {
            super.saveOrUpdate( adi );
        }
        
    }

    public void updateAdi( AttachedDataItem adi ){
        
        super.saveOrUpdate( adi );

    }

    public void deleteAdi( AttachedDataItem adi ){
        super.delete( adi );
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    private  Criteria addFilter( Criteria crit, Map<String,String> flt ) {
        
        if( flt.get("status") != null && 
            !flt.get("status").equals("") ){
            
            crit.createAlias( "state", "st" )
                .add( Restrictions.eq( "st.name",
                                       flt.get( "status" ) 
                                       ) );
        }
        
        if( flt.get("partner") != null && 
            !flt.get("partner").equals("") ){
            
            crit.createAlias( "adminGroups", "ag" )
                .add( Restrictions.eq( "ag.label",
                                       flt.get( "partner" )
                                       ) );
        }
        
        if( flt.get("editor") != null && 
            !flt.get("editor").equals("") ){
            crit.createAlias( "adminUsers", "au" )
                .add( Restrictions.eq( "au.login",
                                       flt.get( "editor" )
                                       ) );
        }
        
        return crit;
    }

}
