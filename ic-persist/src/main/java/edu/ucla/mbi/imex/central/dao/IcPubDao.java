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
import org.hibernate.criterion.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
       
public class IcPubDao extends AbstractDAO implements PublicationDAO {

    public Publication getPublication( int id ) { 
        
        Publication pub = null;

        try {
            pub =  (IcPub) super.find( IcPub.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublication( String title ) { 
        
        Publication pub = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.title = :title ");
            query.setParameter("title", title );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            // log error ?
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublicationByPmid( String pmid ) { 
        
        Publication pub = null;
        if ( pmid == null || pmid.equals("") ) return pub;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.pmid = :pmid ");
            query.setParameter("pmid", pmid );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            // log error ?
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublicationByKey( String key ) { 
        
        Publication pub = null;
        if ( key == null || key.equals("") ) return pub;
        
        try {

            key = key.replaceAll( "\\D+", "" );

            long lkey = Long.parseLong(key);
            
            startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :key ");
            query.setParameter("key", lkey );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            // log error ?
        } catch ( NumberFormatException nfe ){
            // log error ?
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public List<Publication> getPublicationList() {
        
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList"  );
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            
            plst = (List<Publication>) query.list();
            tx.commit();
            HibernateUtil.closeSession();
        } catch ( DAOException dex ) {
            // log exception ?
            dex.printStackTrace();
        } 
        
        System.out.println("plist"+plst);

        return plst;
    }

    //---------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize ) {
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList(block)"  );

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );
            
            plst = (List<Publication>) query.list();
            tx.commit();
            HibernateUtil.closeSession();
        } catch ( DAOException dex ) {
            // log exception ?
            dex.printStackTrace();
        } 
        
        System.out.println("plist" + plst);
        return plst;
    }

    //---------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize,
                                                 String skey,
                                                 boolean asc ) {
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList(block) sort=:" + skey );

        try {
            startOperation();
            
            Criteria crit = session.createCriteria( IcPub.class );
            crit.setFirstResult( firstRecord );
            crit.setMaxResults( blockSize );

            if (skey != null && skey.length() > 0 ) {
                if ( asc ) {
                    crit.addOrder( Order.asc( skey ) );
                } else {
                    crit.addOrder( Order.desc( skey ) );
                }
            }

            //Query query =
            //    session.createQuery( "from IcPub p order by id ");
            //query.setFirstResult( firstRecord );
            //query.setMaxResults( blockSize );
            
            //plst = (List<Publication>) query.list();

            plst = crit.list();
            
            tx.commit();
            HibernateUtil.closeSession();
        } catch ( DAOException dex ) {
            // log exception ?
            dex.printStackTrace();
        }        
        return plst;
    }
    
    //---------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord,
                                                 int blockSize,
                                                 String skey, boolean asc,
                                                 Map<String,String> flt) {
        
        List<Publication> plst = new ArrayList<Publication>();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList(block) sort=:" + skey );
        log.info( "IcPubDao:getPublicationList(block) filt(partner)=:" + 
                  flt.get("partner") );

        try {
            startOperation();
            
            Criteria crit = session.createCriteria( IcPub.class );
            crit.setFirstResult( firstRecord );
            crit.setMaxResults( blockSize );

            if (skey != null && skey.length() > 0 ) {
                if ( asc ) {
                    crit.addOrder( Order.asc( skey ) );
                } else {
                    crit.addOrder( Order.desc( skey ) );
                }
            }

            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }
            
            plst = crit.list();
            
            log.info( "IcPubDao: plst=" + plst); 
            log.info( "IcPubDao: size=" + plst.size()); 
            
            tx.commit();
            HibernateUtil.closeSession();
        } catch ( DAOException dex ) {
            // log exception ?
            dex.printStackTrace(); 
            
        } catch( Exception ex ) {
            // log exception ?
            ex.printStackTrace();
        }
        
        return plst;
    }
    //---------------------------------------------------------------------

    public long getPublicationCount() {

        long count = 0;
        try {
            startOperation();
            Query query = session.createQuery( "select count(p) from IcPub p" );
            count  = (Long) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            // log error ?
        }
        return count;
    }

    //---------------------------------------------------------------------

    public long getPublicationCount(  Map<String,String> flt ) {

        long count = 0;
        try {
            startOperation();
            
            Criteria crit = session.createCriteria( IcPub.class );
            
            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }
            
            List foo = crit.setProjection( Projections.rowCount()).list();
            
            Log log = LogFactory.getLog( this.getClass() );
            count  = ((Integer) foo.get(0) ).longValue() ;
            log.info( "count=" + count );
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            dex.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        }

        return count;
    }

    //---------------------------------------------------------------------

    public void savePublication( Publication publication ) { 
        
        try {
            if ( publication  instanceof IcPub ) {
                super.saveOrUpdate( publication );
            } else {
                super.saveOrUpdate( new IcPub( publication ) );
            }
        }  catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------
    
    public void updatePublication( Publication publication ) { 
        
        try {
            super.saveOrUpdate( publication );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------

    public void deletePublication( Publication publication ) { 
        try {
            super.delete( publication );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    
    //---------------------------------------------------------------------
    
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

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public IcPub getPublicationByImexId( long imex ) { 
        
        IcPub pub = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :imex ");
            query.setParameter( "imex", imex );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession();
        } catch( DAOException dex ) {
            // log error ?
        }
        return pub; 
    }
    
}
