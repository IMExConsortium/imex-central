package edu.ucla.mbi.util.data.dao;

/* =========================================================================
 # $HeadURL:: https://lukasz@imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-util-s#$
 # $Id:: UserDao.java 429 2009-08-14 01:44:32Z lukasz                      $
 # Version: $Rev:: 429                                                     $
 #==========================================================================
 #
 # JournalDAO
 #
 #======================================================================= */

import java.util.*;
import edu.ucla.mbi.util.data.*;

public interface JournalDAO {

    public Journal getJournal( int id );
    public Journal getJournal( String title );
    public Journal getJournalByNlmid( String nlmid );
    public List<Journal> getJournalList();
    public List<Journal> getJournalList( int firstRecord, int blockSize );
    public long getJournalCount();

    public void saveJournal( Journal journal );
    public void updateJournal( Journal journal );
    public void deleteJournal( Journal journal );

}


