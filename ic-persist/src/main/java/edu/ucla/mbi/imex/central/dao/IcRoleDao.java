package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
 *===========================================================================
 *
 * RoleDAO:
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

public class IcRoleDao extends AbstractDAO implements RoleDao {


    //public IcRoleDao(){ super();}

    //public IcRoleDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

    public Role getRole( int id ) { 
        
        Role grp = (IcRole) super.find( IcRole.class, id );
        return grp; 
    }
    

    //---------------------------------------------------------------------

    public Role getRole( String name ) { 
        
        Role role = null;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcRole u where " +
                                     " u.name = :name ");
            query.setParameter("name", name );
            query.setFirstResult( 0 );
            role = (IcRole) query.uniqueResult();
            tx.commit();
        
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return role; 
    }
    

    //---------------------------------------------------------------------

    public List<Role> getRoleList() {
        
        List<Role> rlst = null;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcRole r order by id ");
            
            rlst = (List<Role>) query.list();
            tx.commit();
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return rlst;
    }

    public List<Role> getRoleList( int firstRecord, int blockSize ) { 
        
        List<Role> rolel = null;

	Log log = LogFactory.getLog( this.getClass() );
	log.debug("getRoleList: firstRecord=" + firstRecord);
	log.debug("getRoleList: blockSize=" + blockSize);
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = 
                session.createQuery( "from IcRole r order by id ");
            query.setFirstResult( firstRecord - 1 );
            query.setMaxResults( blockSize );
            
            rolel = (List<Role>) query.list();
            tx.commit();
            System.out.println( firstRecord+ " :: " + 
                                blockSize + " :: " + rolel.size() );
        } catch( DAOException dex ) {
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return rolel;
    }


    //---------------------------------------------------------------------

    public long getRoleCount(){

        long count = 0;

        Log log = LogFactory.getLog( this.getClass() );

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();

            Query query =
                session.createQuery( "select count(*) from IcRole");
            
            count = (Long) query.uniqueResult();
            
            log.info("Total roles=" + count);
            
        } catch( DAOException dex ) {
            // log error ?
            log.info(dex);
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return count;
    }

    //---------------------------------------------------------------------

    public void saveRole( Role role ) { 
                  
        super.saveOrUpdate( new IcRole( role ) );
    }
    

    //---------------------------------------------------------------------
    
    public void updateRole( Role role ) { 

        super.saveOrUpdate( role );
    }
    
    
    //---------------------------------------------------------------------

    public void deleteRole( Role role ) { 
    
        super.delete( role );
    }
    
    //---------------------------------------------------------------------
    // usage/count: Users
    //-------------------

    public long getUserCount( Role role ) {
        
        long cnt = 0;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();

        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "select count(u) from IcUser u" +
                                     " join u.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            cnt = (Long) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return cnt;
    }
    
    //---------------------------------------------------------------------

    public List<User> getUserList( Role role ) {

        // NOTE: use sparingly - potentially can load all users

        List<User> ulst = null;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "select u from IcUser u" +
                                     " join u.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            ulst = (List<User>) query.list();
            tx.commit();
            //HibernateUtil.closeSession();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return ulst;
    }

    
    //---------------------------------------------------------------------
    // usage/count: Groups
    //--------------------

    public long getGroupCount( Role role ) {
        
        long cnt = 0;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "select count(g) from IcGroup g" +
                                     " join g.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            cnt = (Long) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return cnt;
    }

    //---------------------------------------------------------------------

    public List<Group> getGroupList( Role role ) {

        // NOTE: use sparingly - potentially can load all users

        List<Group> ulst = null;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "select g from IcGroup g" +
                                     " join g.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            ulst = (List<Group>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return ulst;
    }
}
