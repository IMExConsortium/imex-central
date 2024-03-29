package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 *                                                                             $
 * UserDAO:                                                                    $
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

public class IcUserDao extends AbstractDAO implements UserDao {

    public User getUser( int id ) { 
        
        User user = null;
        
        try {
            user =  (IcUser) super.find( IcUser.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return user;
    }

    
    //---------------------------------------------------------------------
    
    public User getUser( String login ) {
        
        User user = null;

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query =
                session.createQuery( "from IcUser u where " +
                                     " u.login = :login ");
            query.setParameter( "login", login );
            query.setFirstResult( 0 );
            user = (IcUser) query.uniqueResult();
            tx.commit();
        } catch( DAOException dex ) {
            // log error ?
        } finally {
            session.close();
        }

        return user;
    }


    //---------------------------------------------------------------------
    
    public List<User> getUserList() {   
        return null;
    }
    
    public List<User> getUserList( int firstRecord, int blockSize ) { 
       
        List<User> userl = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("getUserList: firstRecord=" + firstRecord);
        log.debug("getUserList: blockSize=" + blockSize);
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            
            Query query = 
                session.createQuery( "from IcUser u order by id ");
            query.setFirstResult( firstRecord );  // -1 ???
            query.setMaxResults( blockSize );
            
            userl = (List<User>) query.list();
            tx.commit();            
        } catch( DAOException dex ) {
            // log error ?
        } finally {
            session.close();
        }
        
        return userl;
    }

    
    //---------------------------------------------------------------------

    public long getUserCount(){ 

        long count = 0;

        Log log = LogFactory.getLog( this.getClass() );
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {            
            Query query =
                session.createQuery( "select count(*) from IcUser");
            
            count = (Long) query.uniqueResult();
            
            log.info("Total users=" + count);

        } catch( DAOException dex ) {
            // log error ?
            log.info(dex);
        } finally {
            session.close();
        }
        
        return count;
    }
    
    public void saveUser( User user ) { 
        super.saveOrUpdate( new IcUser ( user ) );
    }
    
    public void updateUser( User user ) { 
        super.saveOrUpdate( user );
    }


    public void deleteUser( User user ) { 
        super.delete( user );
    }
}
