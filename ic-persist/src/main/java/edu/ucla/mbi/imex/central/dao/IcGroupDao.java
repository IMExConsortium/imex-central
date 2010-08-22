package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
 *===========================================================================
 *
 * GroupDAO:
 *
 *========================================================================= */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.imex.central.*;

public class IcGroupDao extends AbstractDAO implements GroupDao {
    
    public Group getGroup( int id ) { 
        
        Group group = (IcGroup) super.find( IcGroup.class, id ); 
        return group; 
    }

    
    //---------------------------------------------------------------------

    public Group getGroup( String label ) {
        
        Group group = null;
        Session session =
            HibernateUtil.getSessionFactory().openSession();
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
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
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
            //HibernateUtil.closeSession();
            session.close();
        }

        return cnt;
    }
    
    //---------------------------------------------------------------------

    public List<User> getUserList( Group group ) {

        // NOTE: use sparingly - potentially can load all users
        
        List<User> ulst = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
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
            //HibernateUtil.closeSession();
            session.close();
        }

        return  ulst;
    }
}
