package edu.ucla.mbi.util.data.dao;

/* =========================================================================
 # $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-util-s#$
 # $Id:: UserDao.java 429 2009-08-14 01:44:32Z lukasz                      $
 # Version: $Rev:: 429                                                     $
 #==========================================================================
 #
 # WorkflowDAO - workflow control persistence
 #
 #======================================================================= */

import java.util.*;
import edu.ucla.mbi.util.data.*;

public interface WorkflowDAO {

    public DataState getDataState( int id );
    public DataState getDataState( String title );
    public List<DataState> getDataStateList();
    public long getDataStateCount();

    public void saveDataState( DataState state );
    public void updateDataState( DataState state );
    public void deleteDataState( DataState state );

    public Transition getTransition( int id );
    public Transition getTransition( String name );
    public List<Transition> getTransitionList();
    public long getTransitionCount();

    public void saveTransition( Transition trans );
    public void updateTransition( Transition trans );
    public void deleteTransition( Transition trans );



}
