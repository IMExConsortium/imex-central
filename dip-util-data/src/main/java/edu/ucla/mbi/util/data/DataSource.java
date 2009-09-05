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

public class DataSource implements DataAclAware {
    
    DataSource() {}

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
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

    private GregorianCalendar creationTime;

    public void setCrt( GregorianCalendar  crt) {
        this.creationTime = crt;
    }

    public GregorianCalendar getCrt() {
        return this.creationTime;
    }
    
    //---------------------------------------------------------------------

    Set<User>  adminUsrSet;

    public Set<User> getAdminUsers() {
        if ( adminUsrSet == null ) {
            adminUsrSet = new HashSet<User>();
        }
        return adminUsrSet;
    }

    public void setAdminUsers( Set<User> users ) {
        this.adminUsrSet = users;
    }

    //---------------------------------------------------------------------

    Set<Group> adminGroupSet;

    public Set<Group> getAdminGroups() {
        if ( adminGroupSet == null ) {
            adminGroupSet = new HashSet<Group>();
        }
        return adminGroupSet;
    }
    
    public void setAdminGroups( Set<Group> groups ) {
        this.adminGroupSet = groups;
    }

    //---------------------------------------------------------------------
    // DataAclAware
    //-------------

    public boolean testAcl( Set<String> ownerMatch,
                            Set<String> adminUserMatch,
                            Set<String> adminGroupMatch ) {
        try{
            Log log = LogFactory.getLog( this.getClass() );
            log.info( "ACL Test: owner= " + ownerMatch +
                      "\n           ausr= " + adminUserMatch +
                      "\n           agrp= " + adminGroupMatch );

            if ( ownerMatch == null && adminUserMatch == null &&
                 adminGroupMatch == null ) return true;

            // owner match
            //------------

            if ( ownerMatch != null ) {
                if ( ownerMatch.contains( getOwner() ) ) {
                    log.info( "ACL Test: owner matched" );
                    return true;
                }
            }

            log.info( "ACL Test: no owner match");

            // admin user match
            //-----------------

            if ( adminUserMatch != null ) {
                for( Iterator<User> oi = getAdminUsers().iterator();
                     oi.hasNext(); ) {

                    String usr = oi.next().getLogin();
                    if ( adminUserMatch.contains( usr ) ) {
                        log.info( "ACL Test: ausr matched" );
                        return true;
                    }
                }
            }
            log.info( "ACL Test: no ausr match");

            // admin group match
            //------------------

            if ( adminGroupMatch != null ) {

                for( Iterator<Group> gi = getAdminGroups().iterator();
                     gi.hasNext(); ) {

                    String grp = gi.next().getLabel();
                    if ( adminGroupMatch.contains( grp ) ) {
                        log.info( "ACL Test: agrp matched" );
                        return true;
                    }
                }
            }

            log.info( "ACL Test: no agrp match");
            return false;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
