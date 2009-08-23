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

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.imex.central.*;

public class IcRoleDao extends AbstractDAO implements RoleDao {
    
    public Role getRole( int id ) { 
        
        Role grp = null;

        try {
            grp =  (IcRole) super.find( IcRole.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return grp; 
    }
    

    //---------------------------------------------------------------------

    public Role getRole( String name ) { 
        
        Role role = null;
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcRole u where " +
                                     " u.name = :name ");
            query.setParameter("name", name );
            query.setFirstResult( 0 );
            role = (IcRole) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return role; 
    }
    

    //---------------------------------------------------------------------

    public List<Role> getRoleList() {
        
        List<Role> rlst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcRole r order by id ");
            
            rlst = (List<Role>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
        } 
        return rlst;
    }


    //---------------------------------------------------------------------

    public void saveRole( Role role ) { 
        try {          
            super.saveOrUpdate( new IcRole( role ) );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    

    //---------------------------------------------------------------------
    
    public void updateRole( Role role ) { 
        try {
            super.saveOrUpdate( role );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    
    //---------------------------------------------------------------------

    public void deleteRole( Role role ) { 
        try {
            super.delete( role );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }

    //---------------------------------------------------------------------
    // usage/count: Users
    //-------------------

    public long getUserCount( Role role ) {
        
        long cnt = 0;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "select count(u) from IcUser u" +
                                     " join u.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            cnt = (Long) query.uniqueResult();
            tx.commit();

        } catch( DAOException dex ) {
            // log error ?
        }

        return cnt;
    }
    
    //---------------------------------------------------------------------

    public List<User> getUserList( Role role ) {

        // NOTE: use sparingly - potentially can load all users

        List<User> ulst = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "select u from IcUser u" +
                                     " join u.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            ulst = (List<User>) query.list();
            tx.commit();

        } catch( DAOException dex ) {
            // log error ?
        }
        return ulst;
    }

    
    //---------------------------------------------------------------------
    // usage/count: Groups
    //--------------------

    public long getGroupCount( Role role ) {
        
        long cnt = 0;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "select count(g) from IcGroup g" +
                                     " join g.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            cnt = (Long) query.uniqueResult();
            tx.commit();

        } catch( DAOException dex ) {
            // log error ?
        }

        return cnt;
    }

    //---------------------------------------------------------------------

    public List<Group> getGroupList( Role role ) {

        // NOTE: use sparingly - potentially can load all users

        List<Group> ulst = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "select g from IcGroup g" +
                                     " join g.roles as role" +
                                     " where role.id = :rid");
            query.setParameter( "rid", role.getId() );
            query.setFirstResult( 0 );
            ulst = (List<Group>) query.list();
            tx.commit();

        } catch( DAOException dex ) {
            // log error ?
        }

        return ulst;
    }
}