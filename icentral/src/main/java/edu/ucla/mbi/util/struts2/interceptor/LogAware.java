package edu.ucla.mbi.util.struts2.interceptor;

/* =========================================================================
 # $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-util-s#$
 # $Id:: AclAware.java 516 2009-09-04 19:36:16Z lukasz                     $
 # Version: $Rev:: 516                                                     $
 #==========================================================================
 #
 # AclAware interface: must be implemented by classes interceptable 
 #           by AclInterceptor
 #
 #======================================================================= */

import java.util.*;
import edu.ucla.mbi.util.*;

import javax.servlet.ServletContext;

public interface LogAware {
    
    public void setOp( Map<String,String> op );
    public Map<String,String> getOp();
    public ServletContext getServletContext();
    public Map getSession();
    //public void setOwnerMatch( Set<String> owner );
    //public void setAdminUserMatch( Set<String> aul );
    //public void setAdminGroupMatch( Set<String> agl );
}

