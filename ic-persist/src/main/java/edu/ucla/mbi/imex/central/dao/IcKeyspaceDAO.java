package  edu.ucla.mbi.imex.central.dao;

/* =============================================================================
 # $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/ic-model#     $
 # $Id:: IcGroup.java 10 2009-08-22 14:06:27Z lukasz                           $
 # Version: $Rev:: 10                                                          $
 #==============================================================================
 #                                                                             $
 # IcKeyspaceDAO - keyspace/key operations                                     $
 #                                                                             $
 #=========================================================================== */

import org.hibernate.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;

import java.net.*;
import java.util.*;
import java.sql.*;

public class IcKeyspaceDAO extends AbstractDAO implements KeyspaceDAO {
    
    //--------------------------------------------------------------------------
    // KeyspaceDAO implementation
    //---------------------------
    
    public Keyspace getKeyspace( int id ) {
        return null;
    }

    public Keyspace getKeyspace( String name ) {
        return null;
    }

    public List<Keyspace> getKeyspaceList() {
        return null;
    }
    
    public long getKeyspaceCount() {
        return 0L;
    }
    
    public void saveKeyspace( Keyspace ksp ) {
        
    }

    public void updateKeyspace( Keyspace ksp ) {

    }

    public void deleteKeyspace( Keyspace  ksp ) {

    }
    
    //--------------------------------------------------------------------------
    // key operations
    //---------------

    public void create( IcKey sub ) throws DAOException {
        
	synchronized(this){
	    IcKey maxKey =this.getMaxKey( sub.getKeyspace().getName() );
	    
	    sub.setKeyspace( maxKey.getKeyspace() );
	    sub.setKeyvalue( maxKey.getKeyvalue() + 1 );
	    sub.setCreated( new Timestamp(System.currentTimeMillis()) );
	    sub.setUpdated (new Timestamp(System.currentTimeMillis()) );
	    saveOrUpdate(sub);
	}
    }

    
    //--------------------------------------------------------------------------

    public IcKey create( String keyspace ) throws DAOException {
	
	IcKey sub = new IcKey();
	synchronized( this ) {
	    IcKey maxKey = this.getMaxKey( keyspace );
	    
	    sub.setKeyspace( maxKey.getKeyspace() );
	    sub.setKeyvalue( maxKey.getKeyvalue() + 1 );
	    sub.setCreated( new Timestamp( System.currentTimeMillis()) );
	    sub.setUpdated( new Timestamp( System.currentTimeMillis()) );
	    
	    saveOrUpdate( sub );
	}
	
	if( sub.getKeyspace() != null ) {
	    return sub;
	} else {
	    return null;
	}
    }

    
    //--------------------------------------------------------------------------

    public IcKey find( Long id ) throws DAOException {
	return (IcKey) find( IcKey.class, id );
    }

    public List findAll() throws DAOException {
	return findAll( IcKey.class );
    }
    
    public IcKey get( String keyspace, String accession ) throws DAOException {
        
	IcKey key = null;
	accession = accession.replaceAll( "[^0-9]", "" );
        
	try {

	    long value = Long.parseLong( accession );
	    startOperation();

	    Query query
		= session.createQuery( "select k from Key as k "+
                                       " where k.keyspace.name = :name " +
                                       " and k.value = :value" );
            query.setParameter( "name", keyspace );
            query.setParameter( "value", value );
	    
	    key  = (IcKey) query.uniqueResult();
            tx.commit();
	    
	    HibernateUtil.closeSession();
	    return key;
	    
	} catch (HibernateException e) {
	    handleException(e);
	} finally {
	    HibernateUtil.closeSession();
	}
	throw new DAOException("KeyDAO exception");
    }    
    
    public IcKey getMaxKey( String keyspace ) throws DAOException {
       	
	IcKey key = null;
	
	try {
	    startOperation();
	    
	    Query query	= session
                .createQuery( "select k from Key as k "+
                              " where k.keyspace.name = :kn2 "+
                              " and k.keyvalue = (" +
                              " select max(k1.keyvalue) from Key as k1 "+
                              " where k1.keyspace.name = :kn1)" );
	    query.setParameter( "kn1", keyspace );
	    query.setParameter( "kn2", keyspace );
            
	    key  = (IcKey) query.uniqueResult();
            tx.commit();
	    HibernateUtil.closeSession();
	    return key;
	    
	} catch ( HibernateException e ) {
	    handleException(e);
	} finally {
	    HibernateUtil.closeSession();
	}
	
	throw new DAOException( "KeyDAO exception" );
    }    
}



