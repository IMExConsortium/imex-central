package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
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
        
        DataState pub = (DataState) super.find( IcDataState.class, id );
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
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            HibernateUtil.closeSession();
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
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            HibernateUtil.closeSession();
        }
        return slst;
    }

    //---------------------------------------------------------------------

    public long getDataStateCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public void saveDataState( DataState state ) { 

        super.saveOrUpdate( new IcDataState( state ) );
            
    }
    
    //---------------------------------------------------------------------
    
    public void updateDataState( DataState state ) { 

        super.saveOrUpdate( state );
    }
    
    //---------------------------------------------------------------------

    public void deleteDataState( DataState state ) { 
    
        super.delete( state );
    }
    
    //--------------------------------------------------------------------------
    // Transition
    //-----------

    public Transition getTrans( int id ) { 
        
        Transition trans = (Transition) super.find( IcTransition.class, id );
        return trans; 
    }
    
    //--------------------------------------------------------------------------

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
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            HibernateUtil.closeSession();
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
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            HibernateUtil.closeSession();
        }
        
        return tlst;
    }

    //--------------------------------------------------------------------------

    public List<Transition> getAllowedTransList( DataState state ) {
        
        List<Transition> tlst = null;
        
        try {
            startOperation();
            Query query =
                session.createQuery( "from IcTransition t order by id " +
                                     " where t.fromState = :state ");
            query.setParameter("state", state );
            
            tlst = (List<Transition>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            HibernateUtil.closeSession();
        }
        
        return tlst;
    }

    //---------------------------------------------------------------------

    public long getTransCount() {
        return 0;
    }

    //---------------------------------------------------------------------

    public void saveTrans( Transition trans ) { 
        
        super.saveOrUpdate( new IcTransition ( trans ) );
    }
    
    //---------------------------------------------------------------------
    
    public void updateTrans( Transition trans ) { 
        
        super.saveOrUpdate( trans );        
    }
    
    //---------------------------------------------------------------------

    public void deleteTrans( Transition trans ) { 
        super.delete( trans );
    }
}

