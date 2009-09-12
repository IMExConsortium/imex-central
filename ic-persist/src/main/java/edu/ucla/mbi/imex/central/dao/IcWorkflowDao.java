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
       
public class IcWorkflowDao extends AbstractDAO implements WorkflowDAO {
    
    //---------------------------------------------------------------------
    // DataState
    //----------

    public DataState getDataState( int id ) { 
        
        DataState pub = null;
        
        try {
            pub = (DataState) super.find( IcDataState.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return pub; 
    }
    
    //---------------------------------------------------------------------

    public DataState getDataState( String name ) { 
        
        DataState state = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcDataState s where " +
                                     " s.name = :name ");
            query.setParameter("name", name );
            query.setFirstResult( 0 );
            state = (DataState) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return state; 
    }
    
    //---------------------------------------------------------------------

    public List<DataState> getDataStateList() {
        
        List<DataState> slst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcDataState s order by id ");
            
            slst = (List<DataState>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
        } 
        return slst;
    }

    //---------------------------------------------------------------------

    public long getDataStateCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public void saveDataState( DataState state ) { 
        try {          
            super.saveOrUpdate( new IcDataState( state ) );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------
    
    public void updateDataState( DataState state ) { 
        try {
            super.saveOrUpdate( state );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------

    public void deleteDataState( DataState state ) { 
        try {
            super.delete( state );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }

    //---------------------------------------------------------------------
    // Transition
    //-----------

    public Transition getTrans( int id ) { 
        
        Transition trans = null;

        try {
            trans = (Transition) super.find( IcTransition.class, id );
        } catch ( DAOException dex ) {
            // log exception ?
        }
        return trans; 
    }
    
    //---------------------------------------------------------------------

    public Transition getTrans( String name ) { 
        
        Transition trans = null;

        try {
            startOperation();
            Query query =
                session.createQuery( "from IcTransition t where " +
                                     " t.name = :name ");
            query.setParameter("name", name );
            query.setFirstResult( 0 );
            trans = (Transition) query.uniqueResult();
            tx.commit();
            
        } catch( DAOException dex ) {
            // log error ?
        }
        return trans; 
    }
    
    //---------------------------------------------------------------------

    public List<Transition> getTransList() {
        
        List<Transition> tlst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcTransition t order by id ");
            
            tlst = (List<Transition>) query.list();
            tx.commit();
            
        } catch ( DAOException dex ) {
            // log exception ?
        } 
        return tlst;
    }

    //---------------------------------------------------------------------

    public long getTransCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public void saveTrans( Transition trans ) { 
        try {          
            super.saveOrUpdate( new IcTransition ( trans ) );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------
    
    public void updateTrans( Transition trans ) { 
        try {
            super.saveOrUpdate( trans );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
    
    //---------------------------------------------------------------------

    public void deleteTrans( Transition trans ) { 
        try {
            super.delete( trans );
        } catch ( DAOException dex ) {
            // log exception ?
        }
    }
}

