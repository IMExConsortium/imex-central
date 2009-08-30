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

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    //---------------------------------------------------------------------

    private DataSource source;

    public DataSource getSource() {
        return source;
    }

    public void setSource( DataSource source ) {
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

    private GregorianCalendar creationTime;

    public void setCrt( GregorianCalendar  crt) {
        this.creationTime = crt;
    }

    public GregorianCalendar getCrt() {
        return this.creationTime;
    }

    public String getCreateDateString(){
        if( creationTime == null ) {
            return "----/--/--";
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append( creationTime.get(Calendar.YEAR) );
        sb.append( "/");

        sb.append( creationTime
                   .get( Calendar.MONTH ) < 9 ? "0" : "" ); 
        sb.append( creationTime.get( Calendar.MONTH ) + 1);
        sb.append( "/" );

        sb.append( creationTime
                   .get( Calendar.DAY_OF_MONTH ) < 10 ? "0" : "" );
        sb.append( creationTime.get( Calendar.DAY_OF_MONTH) );
        
        return sb.toString();
    }

    public String getCreateTimeString(){

        if( creationTime == null ) {
            return "--:--:--";
        } 

        StringBuffer sb = new StringBuffer();
        sb.append( creationTime
                   .get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "" );
        sb.append( creationTime.get(Calendar.HOUR_OF_DAY) );
        sb.append( ":");
        
        sb.append( creationTime
                   .get(Calendar.MINUTE) < 10 ? "0" : "" );
        sb.append( creationTime.get(Calendar.MINUTE) );
        sb.append( ":");
        
        sb.append( creationTime
                   .get(Calendar.SECOND) < 10 ? "0" : "" );
        sb.append( creationTime.get(Calendar.SECOND) );
        return sb.toString();
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