package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 *                                                                             $
 * IcAdiDao:                                                                   $
 *                                                                             $
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

public class IcAdiDao extends AbstractDAO implements AdiDao {

    //public IcAdiDao(){ super(); };

    //public IcAdiDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

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
    
    public IcFlag getIcFlag( String name ){
        
        IcFlag flag = null;

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcAdiDao->getIcFlag: name=" + name  );

        if( name == null ) return flag;
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query =
                session.createQuery( "from IcFlag f where " +
                                     " f.name = :name ");
            query.setParameter("name", name );
            List<IcFlag> icFL = (List<IcFlag>) query.list();

            if( icFL != null && icFL.size() == 1 ){  
                flag = icFL.get(0);
                log.info( "getgetIcFlag by name: id=" + flag.getId() );
            }
            tx.commit();
        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            // log error ?                                                                                                                                                                                                        
        } finally {
            //HibernateUtil.closeSession();                                                                                                                                                                                       
            session.close();
        }

 
        //IcFlag flag = (IcFlag) super.find( IcFlag.class,
        //                                   new Integer( id ) );
        
        log.info( "IcAdiDao->getIcFlag: name=" + name + " ::DONE"  );
        return flag;
    }       


    //--------------------------------------------------------------------------

    public List<String> getScoreNameList( String grp ){

        Log log = LogFactory.getLog( this.getClass() );
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<String> scrlst = new ArrayList<String>();; 
        
        try {
            
            Query query;

            if( grp !=null){

                query = session
                    .createQuery( "select distinct a.name "
                                  + "from AttachedDataItem a where "
                                  + " a.class = IcScore and "
                                  + "  substring( a.name, 0, :len ) = :grp "
                                  + "order by a.name" );
                
                query.setParameter("grp", grp );
                query.setParameter("len", grp.length() + 1);
                
            } else {
                query = session
                    .createQuery( "select distinct a.name  "
                                  + "from AttachedDataItem a where "
                                  + " a.class = IcScore "
                                  + "order by a.name" );                
            }
            scrlst = (List<String>) query.list();

            log.debug( "getScoreList: size=" + scrlst.size() );
            
            tx.commit();
        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            // log error ?
        } finally {
            session.close();
        }
        
        return scrlst;

    }

    
    //--------------------------------------------------------------------------

    public List<AttachedDataItem> getAdiListByRoot( DataItem root ){

        List<AttachedDataItem> alst = null;
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query =
                session.createQuery( "from AttachedDataItem a where " +
                                     " a.root = :root order by a.crt desc");
            query.setParameter("root", root );
            
            alst = (List<AttachedDataItem>) query.list();

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "getAdiListByParent:" + alst.size() );


            
            tx.commit();
        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            // log error ?
        } finally {
            session.close();
        }
        return alst;
    }

    //--------------------------------------------------------------------------

    public List<Score> getScoreListByRoot( DataItem root ){

        List<Score> alst = null;
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query =
                session.createQuery( "from AttachedDataItem a where " +
                                     " a.class = IcScore and " +
                                     " a.root = :root ");
            query.setParameter("root", root );
            
            alst = (List<Score>) query.list();

            Log log = LogFactory.getLog( this.getClass() );
            log.debug( "getScoreListByParent:" + alst.size() );
            
            tx.commit();
        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            // log error ?
        } finally {
            session.close();
        }
        
        Map<String,Score> sMap = new HashMap<String,Score>();

        for( Iterator<Score> ii = alst.iterator(); ii.hasNext(); ){ 

            Score cs = ii.next();
            if( ! sMap.containsKey( cs.getName() ) ){
                sMap.put( cs.getName(), cs );
            } else{
                if( cs.getCrt().after( sMap.get( cs.getName() ).getCrt() ) ){
                    sMap.put( cs.getName(), cs );
                }
            } 
        }
        
        return new ArrayList<Score>( sMap.values() );
    }


    //--------------------------------------------------------------------------

    public long countIcCommByRoot( DataItem root ){
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
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
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
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

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
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

    public void deleteAdi( int aid ){

        AttachedDataItem adi = this.getAdi( aid );
        if( adi != null ){
            super.delete( adi );
        }
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
