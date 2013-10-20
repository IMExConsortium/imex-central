package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * ObserverDao:
 *
 *=========================================================================== */

import java.util.List;
import edu.ucla.mbi.util.data.*;

public interface ObsMgrDao {
    
    public List<User> getObserverList( DataItem obsItem );
    public List<DataItem> getSubjectList( User observer );

    public void addSORel( DataItem subject, User observer );
    public void dropSORel( DataItem subject, User observer );

    
    /*
    public List<User> getObserverList( Event obsItem );
    public List<String> getEventList( User observer );

    public void addEORel( String event, User observer );
    public void dropEORel( String event, User observer );
    */

}
