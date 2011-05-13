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
import org.hibernate.criterion.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;

import edu.ucla.mbi.util.User;
import edu.ucla.mbi.util.Group;
       
public class IcPubDao extends AbstractDAO implements PublicationDAO {

    public Publication getPublication( int id ) { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: id(int)=" + id  );
        
        Publication pub = (IcPub) super.find( IcPub.class, new Integer( id ) );

        log.info( "IcPubDao->getPublication: id=" + id + " ::DONE"  );
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublication( String title ) { 
        
        Publication pub = null;
        
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
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublicationByPmid( String pmid ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: pmid=" + pmid  );

        Publication pub = null;
        if ( pmid == null || pmid.equals("") ) return pub;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.pmid = :pmid ");
            query.setParameter("pmid", pmid );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }

    //---------------------------------------------------------------------

    public Publication getPublicationByDoi( String doi ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: doi=" + doi  );

        Publication pub = null;
        if ( doi == null || doi.equals("") ) return pub;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.doi = :doi ");
            query.setParameter("doi", doi );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }

    //---------------------------------------------------------------------

    public Publication getPublicationByJint( String jint ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: jint=" + jint  );

        Publication pub = null;
        if ( jint == null || jint.equals("") ) return pub;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.journalSpecific = :jint ");
            query.setParameter("jint", jint );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public Publication getPublicationByKey( String key ) { 
        
        Publication pub = null;
        if ( key == null || key.equals("") ) return pub;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            key = key.replaceAll( "\\D+", "" );

            long lkey = Long.parseLong(key);
            
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :key ");
            query.setParameter("key", lkey );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            
        } catch ( NumberFormatException nfe ) {
            // log error ?
        } catch ( HibernateException e ) {
            handleException( e );
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public List<Publication> getPublicationList() {
        
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList"  );
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            
            plst = (List<Publication>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
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

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );
            
            plst = (List<Publication>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
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
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Criteria crit = session.createCriteria( IcPub.class );

            if( firstRecord >= 0 && blockSize > 0 ){
                crit.setFirstResult( firstRecord );
                crit.setMaxResults( blockSize );
            }
            
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
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();                   
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

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
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
        } catch ( HibernateException e ) {
            handleException( e );
            
            // log exception ?
            e.printStackTrace(); 
            
        } catch( Exception ex ) {
            // log exception ?
            ex.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return plst;
    }

    //--------------------------------------------------------------------------

    public long getPublicationCount() {

        long count = 0;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query = session.createQuery( "select count(p) from IcPub p" );
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

    public List<User> getOwners( String qstr ) {

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        List<User> ulst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct p.owner from IcPub p "
                              + " where upper( p.owner.login ) like :q "
                              + " order by p.owner.login" );
            query.setParameter("q", qstr );
            ulst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return ulst;
    }

    //--------------------------------------------------------------------------

    public List<User>  getAdminUsers( String qstr ){

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<User> ulst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct au from IcPub as p"
                              + " join p.adminUsers as au"
                              + " where upper( au.login ) like :q "
                              + " order by au.login" );
            query.setParameter("q", qstr );
            ulst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return ulst;
    }

    //--------------------------------------------------------------------------

    public List<Group>  getAdminGroups( String qstr ){

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<Group> glst = null;

        if( qstr!= null && qstr.toUpperCase().equals("ROLE:PARTNER" ) ){
            try {
                Query query = session
                    .createQuery( "select distinct gr from IcGroup as gr"
                                  + " join gr.roles as rl"
                                  + " where upper( rl.name ) like :q " 
                                  + " order by gr.name" );
                query.setParameter("q", "%PARTNER%" );
                glst = (List<Group>) query.list();
                tx.commit();
            } catch ( HibernateException e ) {
                handleException( e );
                e.printStackTrace();
            } catch( Exception ex ) {
                ex.printStackTrace();
            } finally {
                session.close();
            } 
        } else {
            try {
                Query query = session
                    .createQuery( "select distinct gr from IcGroup as gr"
                                  + " order by gr.name" );
                //query.setParameter("q", qstr );
                glst = (List<Group>) query.list();
                tx.commit();
            } catch ( HibernateException e ) {
                handleException( e );
                e.printStackTrace();
            } catch( Exception ex ) {
                ex.printStackTrace();
            } finally {
                session.close();
            }
        }
        return glst;
    }
    
    //--------------------------------------------------------------------------

    public List<DataState> getStates( String qstr ){
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<DataState> slst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct ds from IcDataState as ds"
                              + " order by ds.name" );
            //query.setParameter( "q", qstr );
            slst = (List<DataState>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return slst;
    }


    //--------------------------------------------------------------------------


    
    public long getPublicationCount(  Map<String,String> flt ) {

        long count = 0;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Criteria crit = session.createCriteria( IcPub.class );
            
            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }
            
            List foo = crit.setProjection( Projections.rowCount()).list();
            
            Log log = LogFactory.getLog( this.getClass() );
            count  = ((Integer) foo.get(0) ).longValue() ;
            log.info( "count=" + count );
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return count;
    }

   

    //--------------------------------------------------------------------------

    public void savePublication( Publication publication ) { 
        
        if ( publication  instanceof IcPub ) {
            super.saveOrUpdate( publication );
        } else {
            super.saveOrUpdate( new IcPub( publication ) );
        }
    }
    
    //---------------------------------------------------------------------
    
    public void updatePublication( Publication publication ) { 
        
        super.saveOrUpdate( publication );
    }
    
    //---------------------------------------------------------------------

    public void deletePublication( Publication publication ) { 
        
        super.delete( publication );
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


        if( flt.get( "owner" ) != null && 
            !flt.get("owner").equals("") ){
            crit.createAlias( "owner", "ow" )
                .add( Restrictions.eq( "ow.login",
                                       flt.get( "owner" )
                                       ) );
        }
        
        return crit;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public IcPub getPublicationByImexId( long imex ) { 
        
        IcPub pub = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :imex ");
            query.setParameter( "imex", imex );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
}
