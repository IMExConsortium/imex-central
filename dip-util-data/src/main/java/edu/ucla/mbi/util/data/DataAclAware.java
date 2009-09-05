package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/central/trunk/dip-util-#$
 * $Id:: DataItem.java 27 2009-08-30 01:19:45Z lukasz                      $
 * Version: $Rev:: 27                                                      $
 *==========================================================================
 *
 * DataAclAware - access control interface
 *
 ======================================================================== */

import java.util.*;

public interface DataAclAware {

    public boolean testAcl( Set<String> ownerMatch, 
                            Set<String> adminUserMatch, 
                            Set<String> adminGroupMatch );   
}