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

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
       
public class IcPublicationDao extends AbstractDAO implements PublicationDAO {

    public Publication getPublication( int id ) { 
        
        Publication pub = null;

        try {
            pub =  (IcPublication) super.find( IcPublication.class, id );
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
            pub = (IcPublication) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return pub; 
    }
    

    //---------------------------------------------------------------------

    public List<Publication> getPublicationList() {
        
        List<Publication> plst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcPublication p order by id ");
            
            plst = (List<Publication>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
        } 
        return plst;
    }

    //---------------------------------------------------------------------

    public long getPublicationCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize ) {
        return null;
    }

    //---------------------------------------------------------------------

    public void savePublication( Publication publication ) { 
        try {          
            super.saveOrUpdate( new IcPublication( publication ) );
        } catch ( DAOException dex ) {
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

}

