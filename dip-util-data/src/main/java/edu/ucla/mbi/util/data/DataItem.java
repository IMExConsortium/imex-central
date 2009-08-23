package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * DataItem - a traceable unit of data
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.*;

public class DataItem {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    //---------------------------------------------------------------------

    private DataSource source;

    public DataSource getSource() {
        return source;
    }

    void setSource( DataSource source ) {
        this.source = source;
    }
    
    //---------------------------------------------------------------------

    User owner;

    public User getOwner() {
        return owner;
    }

    public void setOwner( User owner ) {
        this.owner = owner;
    }

    //---------------------------------------------------------------------

    Set<User>  adminUsrSet;

    public Set<User> getAdminUsers() {
        return adminUsrSet;
    }

    public void setAdminUsers( Set<User> users ) {
        this.adminUsrSet = users;
    }

    //---------------------------------------------------------------------

    private Set<Group> adminGroupSet;

    public Set<Group> getAdminGroups() {
        return adminGroupSet;
    }

    public void setAdminGroups( Set<Group> groups ) {
        this.adminGroupSet = groups;
    }

    //---------------------------------------------------------------------

    DataState state;
    
    public DataState getState() {
        return state;
    }

    public void setState( DataState state ) {
        this.state = state;
    }
    
    //---------------------------------------------------------------------
    // comments
    //---------

    private Set<DataComment> comments;
    
    public Set<DataComment> getComments() {
        return comments;
    }

    public void setComments( Set<DataComment> comments ) {
        this.comments = comments;
    }
    
}