package edu.ucla.mbi.util.data.dao;

/* =========================================================================
 # $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-util-s#$
 # $Id:: UserDao.java 429 2009-08-14 01:44:32Z lukasz                      $
 # Version: $Rev:: 429                                                     $
 #==========================================================================
 #
 # PublicationDAO 
 #     
 #======================================================================= */

import java.util.*;
import edu.ucla.mbi.util.data.*;

public interface PublicationDAO {

    public Publication getPublication( int id );
    public Publication getPublication( String title );
    public Publication getPublicationByPmid( String pmid );
    
    public List<Publication> getPublicationList();
    public List<Publication> getPublicationList( int firstRecord, int blockSize );
    public long getPublicationCount();

    public void savePublication( Publication publication );
    public void updatePublication( Publication publication );
    public void deletePublication( Publication publication );
    
}
