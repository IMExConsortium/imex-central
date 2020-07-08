package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 *                                                                             $
 * GroupDAO:                                                                   $
 *                                                                             $
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;

public class IcGroupDao extends AbstractDAO implements GroupDao {
    
    public Group getGroup( int id ) { 
        
        Group group = (IcGroup) super.find( IcGroup.class, id ); 
        return group; 
    }
    
    //---------------------------------------------------------------------

    public Group getGroup( String label ) {
        
        Group group = null;
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcGroup g where " +
                                     " g.label = :label ");
            query.setParameter( "label", label );
            query.setFirstResult( 0 );
            group = (IcGroup) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return group;
    }


    //---------------------------------------------------------------------

    public List<Group> getGroupList() {
        
        List<Group> glst = (List<Group>) super.findAll( IcGroup.class );

        List<Group> groupl = null;

        Log log = LogFactory.getLog( this.getClass() );
        log.debug("getGroupList: all" );
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            
            Query query = 
                session.createQuery( "from IcGroup g order by name ");
            
            groupl = (List<Group>) query.list();
            tx.commit();
        } catch( DAOException dex ) {
            // log error ?
        } finally {
            session.close();
        }
        
        return groupl;
    }
    
    public List<Group> getGroupList( int firstRecord, int blockSize ) { 
       
        List<Group> groupl = null;

        Log log = LogFactory.getLog( this.getClass() );
        log.debug("getGroupList: firstRecord=" + firstRecord);
        log.debug("getGroupList: blockSize=" + blockSize);
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            
            Query query = 
                session.createQuery( "from IcGroup g order by name ");
            query.setFirstResult( firstRecord - 1 );
            query.setMaxResults( blockSize );
            
            groupl = (List<Group>) query.list();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        } finally {
            session.close();
        }
        
        return groupl;
    }


    //---------------------------------------------------------------------

    public long getGroupCount(){

        long count = 0;

        Log log = LogFactory.getLog( this.getClass() );

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {            
            Query query =
                session.createQuery( "select count(*) from IcGroup");
            
            count = (Long) query.uniqueResult();
            
            log.info("Total groups=" + count);
            
        } catch( DAOException dex ) {
            // log error ?
            log.info(dex);
        } finally {
            session.close();
        }

        return count;
    }
    
    //---------------------------------------------------------------------

    public List<Group> getGroupList( Role role ){
        
        List<Group> glst = null;

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "role=" + role );

        try {

            Query query =
                session.createQuery( "select g from IcGroup g join g.roles as r" +
                                     " where r = :r ");
            query.setParameter( "r", role );
            query.setFirstResult( 0 );
            glst = (List<Group>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ? 
        } finally {
            session.close();
        }
        
        log.debug( "list=" + glst );
        return glst;
    }


    //---------------------------------------------------------------------

    public void saveGroup( Group group ) { 
        super.saveOrUpdate( new IcGroup( group ) );
    }
    

    //---------------------------------------------------------------------
    
    public void updateGroup( Group group ) {   
        super.saveOrUpdate( group );
    }
    
    
    //---------------------------------------------------------------------

    public void deleteGroup( Group group ) { 
        super.delete( group );
    }

    
    //---------------------------------------------------------------------
    // usage/count: Users
    //-------------------

    public long getUserCount( Group group ) {

        long cnt = 0;
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query =
                session.createQuery( "select count(u) from IcUser u" +
                                     " join u.groups as grp" +
                                     " where grp.id = :gid");
            query.setParameter( "gid", group.getId() );
            query.setFirstResult( 0 );
            cnt = (Long) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            session.close();
        }

        return cnt;
    }
    
    //---------------------------------------------------------------------

    public List<User> getUserList( Group group ) {

        // NOTE: use sparingly - potentially can load all users
        
        List<User> ulst = null;
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "select u from IcUser u" +
                                     " join u.groups as grp" +
                                     " where grp.id = :gid");
            query.setParameter( "gid", group.getId() );
            query.setFirstResult( 0 );
            ulst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            session.close();
        }

        return  ulst;
    }
}
