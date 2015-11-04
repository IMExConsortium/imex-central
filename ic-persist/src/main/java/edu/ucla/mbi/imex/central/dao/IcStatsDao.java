package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcStatsDao: entry statistics 
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
       
public class IcStatsDao extends AbstractDAO {

    //public IcStatsDao(){ super();}

    //public IcStatsDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

    public static final Integer PARTNERID = new Integer( 15 ); // set as bean param
    
    public Map<DataState,Long> getCountAll() { 
        
        String qStr = "select p.state.id, count( distinct p ) " + 
            " from IcPub p group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();
            
            if( res.size()>0 ) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put( state, cnt );
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountAll): no counts"  );
            }
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }

    //--------------------------------------------------------------------------

    public Map<DataState,Long> getAccCountAll() { 
        
        String qStr = "select p.state.id, count( distinct p ) " + 
            " from IcPub p where not(p.icKey is null) group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();
            
            if( res.size()>0 ) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Long stateId = (Long) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put( state, cnt );
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountAll): no counts"  );
            }
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }

    //--------------------------------------------------------------------------
    
    public Map<Group,Map<DataState,Long>> 
        getCountByPartner( String pipeline ){
        
        return null;
    }

    public Map<Group,Map<DataState,Long>> 
        getAccCountByPartner( String pipeline ){

        
        return null;
    }
    
    //--------------------------------------------------------------------------

    public Map<Group,Map<DataState,Long>> getAccCountByPartner() { 
        
        String qStr = "select grp.id, p.state.id, count( distinct p ) " + 
            " from IcPub p join p.adminGroups grp join grp.roles role " +
            "  where not(p.icKey is null) and role.id= :pid " +
            "  group by grp.id, p.state.id";
        
        Log log = LogFactory.getLog( this.getClass() );
        
        Map<Group,Map<DataState,Long>> 
            resmap = new HashMap<Group,Map<DataState,Long>>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );

            log.info( "pid" + IcStatsDao.PARTNERID);
            
            query.setParameter( "pid", IcStatsDao.PARTNERID );
            List  res = query.list();
            log.info( "res=" + res);
            if( res.size()>0 ){
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer partnerId = (Integer) ir[0]; 
                    Integer stateId = (Integer) ir[1];
                    Long cnt = (Long) ir[2]; 
                    
                    Group partner = (IcGroup) 
                        super.find( IcGroup.class, partnerId );
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    if( partner != null ) {

                        if( resmap.get(partner) == null ) {
                            Map<DataState,Long> 
                                pst = new HashMap<DataState,Long>();
                            resmap.put( partner, pst );
                        }
                        resmap.get(partner).put(state,cnt);
                    }
                }
            } else {
                
                log.info( "IcStatsDao(getCountByPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }

        return resmap;
    }
    
   //--------------------------------------------------------------------------
    
    public Map<Group,Map<DataState,Long>> getCountByPartner() { 
        
        String qStr = "select grp.id, p.state.id, count( distinct p ) " + 
            " from IcPub p join p.adminGroups grp join grp.roles role " +
            "  where role.id= :pid group by grp.id, p.state.id";
        
        Map<Group,Map<DataState,Long>> 
            resmap = new HashMap<Group,Map<DataState,Long>>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
      
            Query query = session.createQuery( qStr );
            query.setParameter( "pid", IcStatsDao.PARTNERID );

            //XX 
            List  res = query.list();
            
            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer partnerId = (Integer) ir[0]; 
                    Integer stateId = (Integer) ir[1];
                    Long cnt = (Long) ir[2]; 
                    
                    Group partner = (IcGroup) 
                        super.find( IcGroup.class, partnerId );
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    if( partner != null ) {

                        if( resmap.get(partner) == null ) {
                            Map<DataState,Long> 
                                pst = new HashMap<DataState,Long>();
                            resmap.put( partner, pst );
                        }
                        resmap.get(partner).put(state,cnt);
                    }
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountByPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }

        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public Map<DataState,Long> getCountNoPartner() { 

        String qStr = "select p.state.id, count(distinct p) " +
            " from IcPub p where p.adminGroups.size = 0 " +
            " group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();

            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put(state,cnt);
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountNoPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public Map<DataState,Long> getAccCountNoPartner() { 

        String qStr = "select p.state.id, count(distinct p) from IcPub p " +
            " where not(p.icKey is null) and p.adminGroups.size = 0 " +
            " group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();

            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put(state,cnt);
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountNoPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public void testQuery( String testQuery ) { 
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( testQuery );
            
            List  res = query.list();

            if(res.size()>0) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    for(int j = 0; j < ir.length; j++ ) {
                        System.out.print(" " + ir[j] );
                    }
                    System.out.println("");
                }
                System.out.println("Size: " + res.size() + "res(0)=" + res.get(0) );
            } else {
                System.out.println("M/T");
            }
            tx.commit();
            System.out.println("Session closed normally");
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
    }

    //--------------------------------------------------------------------------
    /*
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
    */
}
