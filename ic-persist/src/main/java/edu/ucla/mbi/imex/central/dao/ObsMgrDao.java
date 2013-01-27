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
}
