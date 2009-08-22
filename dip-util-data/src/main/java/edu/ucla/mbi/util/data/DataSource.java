package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * DataSource - a source of DataItem(s)
 *             
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;

import edu.ucla.mbi.util.*;

public class DataSource {

    DataSource() {}

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    Set<Group> adminGroupSet;

    public Set<Group> getAdminGroups() {
        return adminGroupSet;
    }
    
    public void setAdminGroups( Set<Group> groups ) {
        this.adminGroupSet = groups;
    }

}
