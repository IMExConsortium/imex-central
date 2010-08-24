package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id:: EntryMgrAction.java 169 2010-05-26 15:17:24Z lukasz                   $
 * Version: $Rev:: 169                                                         $
 *==============================================================================
 *
 * JournalMgrAction - web interface to journal management
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;
import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class JournalMgrAction extends ManagerSupport {

    private final String NOJOU = "notfound";
    private final String JEDIT = "jedit";
    private final String JSON = "json";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";
    
    ////------------------------------------------------------------------------
    /// Entry Manager
    //--------------

    private EntryManager entryManager;
    
    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }
    
    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    //--------------------------------------------------------------------------
    //  TracContext
    //--------------

    private TracContext tracContext;
    
    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }
    
    public TracContext getTracContext() {
        return this.tracContext;
    }

    //--------------------------------------------------------------------------
    //  WorkflowContext
    //-----------------
    
    private WorkflowContext wflowContext;

    public void setWorkflowContext( WorkflowContext context ) {
        this.wflowContext = context;
    }

    public WorkflowContext getWorkflowContext() {
        return this.wflowContext;
    }


    //--------------------------------------------------------------------------
    // GroupAll list
    //--------------

    public List<Group> getGroupAll(){
        
        if ( getUserContext().getGroupDao() != null ) {
            return getUserContext().getGroupDao().getGroupList();
        }
        return null;
    }
        
    //--------------------------------------------------------------------------
    //  IcJournal
    //-----------
    
    private IcJournal journal = null;

    public void setJournal( IcJournal journal ) {
	this.journal = journal;
    }
    
    public IcJournal getJournal(){
	return this.journal;
    }
    
    //--------------------------------------------------------------------------
    
    public List<IcJournal> getJournalList(){
        
        if ( tracContext.getJournalDao() == null ) return null;
        
        List<Journal> jl = tracContext.getJournalDao().getJournalList();
        if ( jl == null ) return null;

        List<IcJournal> ijl = new ArrayList<IcJournal>();
        for ( Iterator<Journal> ii = jl.iterator(); ii.hasNext(); ) {
            IcJournal jj = (IcJournal) ii.next();
            ijl.add( jj );
        }
        return ijl;
    }

    
    //--------------------------------------------------------------------------
    // Records
    //--------

    private Map<String,Object> records = null;

    public void setRecords( Map<String,Object> records ) {
        this.records = records;
    }
    
    public Map<String,Object> getRecords(){
        return this.records;
    }
    
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "id=" + getId() + " journal=" + journal + " op=" + getOp() ); 
        
        if ( tracContext.getJournalDao() == null ) return SUCCESS;
        
        if ( getId() > 0 && journal == null ) {
            log.debug( "setting journal=" + getId() );            
            journal = entryManager.getIcJournal( getId() );  
            return SUCCESS;
        }
        
        if( getOp() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {

                //--------------------------------------------------------------
                //--------------------------------------------------------------
                // journal operations
                //-------------------
                
                if ( key.equalsIgnoreCase( "jsrc" ) ) {
                    if ( getJournal() == null ) return SUCCESS;
                    String nlmid = getJournal().getNlmid();
                    return searchJournal( nlmid );
                }

                //--------------------------------------------------------------
    
                if ( key.equalsIgnoreCase( "jadd" ) ) {
                    if ( getOpp() == null ) return SUCCESS;
                    String nlmid = getOpp().get( "jadd" );
                    return addJournal( nlmid );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jdel" ) ) {
                    return deleteJournal( journal );
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jldel" ) ) {

                    if ( getOpp() == null ) return SUCCESS;
                    
                    String udel = getOpp().get( "del" );

                    if ( udel != null ) {
                        List<Integer> uidl =
                            new ArrayList<Integer>();
                        try {
                            udel = udel.replaceAll("\\s","");
                            String[] us = udel.split(",");

                            for( int ii = 0; ii <us.length; ii++ ) {
                                uidl.add( Integer.valueOf( us[ii] ) );
                            }
                        } catch ( Exception ex ) {
                            // should not happen
                        }
                        return deleteJournalList( uidl );
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jpup" ) ) {
                    return updateJournalProperties( getId(), journal );
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jauadd" ) ) {
                    System.out.print("jauadd");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String ulogin = getOpp().get( "jauadd" );
                    try {
                        return addJournalAdminUser( getId(), ulogin );
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                        nfe.printStackTrace();
                    }
                    return SUCCESS;
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "jagadd" ) ) {
                    System.out.print("jagadd");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String sgid = getOpp().get( "jagadd" );
                    try {
                        int gid = Integer.parseInt( sgid );
                        return addJournalAdminGroup( getId(), gid );
                        
                    } catch( NumberFormatException nfe ) {
                        // abort on error
                    }
                    return SUCCESS;
                }

                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jaudel" ) ) {
                    System.out.print("jaudel");
                    if ( getOpp() == null ) return SUCCESS;
                    
                    String udel = getOpp().get( "jaudel" );
                    
                    if ( getId() > 0 && udel != null ) {
                        try {
                             List<Integer> uidl =
                                 new ArrayList<Integer>();
                             String[] us = udel.split(",");

                             for( int ii = 0; ii <us.length; ii++ ) {
                                 uidl.add( Integer.valueOf( us[ii] ) );
                             }
                             return delJournalAdminUsers( getId(), uidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }   
                    } else {
                        journal = entryManager.getIcJournal( getId() );
                        setId( journal.getId() );
                    }
                    return SUCCESS;
                }
                
                //--------------------------------------------------------------

                if ( key.equalsIgnoreCase( "jagdel" ) ) {
                    System.out.print("jagdel");
                    if ( getOpp() == null ) return SUCCESS;

                    String gdel = getOpp().get( "jagdel" );

                    if ( getId() > 0 && gdel != null ) {
                        try {
                             List<Integer> gidl =
                                 new ArrayList<Integer>();
                             String[] gs = gdel.split(",");

                             for( int ii = 0; ii <gs.length; ii++ ) {
                                 gidl.add( Integer.valueOf( gs[ii] ) );
                             }
                             return delJournalAdminGroups( getId(), gidl );
                        } catch ( Exception ex ) {
                            // should not happen
                        }   
                    } else {
                        journal = entryManager.getIcJournal( getId() );
                        setId( journal.getId() );
                    }
                    return SUCCESS;
                }

                if ( key.equalsIgnoreCase( "jpg" ) ) {
                    if ( getOpp() == null ) {
                        return this.getIcJournalRecords();
                    }
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    String flt= getOpp().get( "flt" );

                    return this.getIcJournalRecords( max, off, 
                                                     skey, sdir, flt );
                }
            }
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // validation
    //-----------
    
    public void validate() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "validate" );
       
        //boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                    
                    log.debug( " op=" + val);
                    
                    //----------------------------------------------------------

                    if ( key.equalsIgnoreCase( "jsrc" ) ) {
                        if( getJournal() == null 
                            || getJournal().getNlmid() == null ) {
                            addFieldError( "journal.nlmid",
                                           "NLMID field cannot be empty." );
                        } else {
                            String nlmid = getJournal().getNlmid();
                            try {
                                nlmid = nlmid.replaceAll( "\\s", "" );
                            } catch( Exception ex ) {
                                // should not happen
                            }
                            if( nlmid.length() == 0  ) {
                                addFieldError( "journal.nlmid",
                                               "NLMID field cannot be empty." );
                            }
                        }

                        break;
                    }
                }
            }
        }
        
        //if ( loadUserFlag && getId() > 0 ) {
        //    user = getUserContext().getUserDao().getUser( getId() );
        //    setBig( false );
        //}
        
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // operations: Journal
    //----------------------
    
    public String searchJournal( String nlmid ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " search journal -> nlmid=" + nlmid );
        
        IcJournal oldJnrl =
            entryManager.getIcJournalByNlmid( nlmid );

        if ( oldJnrl != null ) {
            journal = oldJnrl;
            setId( oldJnrl.getId() );
            return JEDIT;
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------

    public String addJournal( String nlmid ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( " new jrnl -> nlmid=" + nlmid );
        
        // test if already in 
        //-------------------
        
        IcJournal oldJnrl = 
            entryManager.getIcJournalByNlmid( nlmid );
        
        if ( oldJnrl != null ) {
            journal = oldJnrl;
            setId( oldJnrl.getId() );
            return JEDIT;
        }
        
        // ACL target control NOTE: implement if/as needed
        //------------------------------------------------                
        /* if ( ownerMatch != null && ownerMatch.size() > 0 ) { } */
        
        Integer usr = (Integer) getSession().get( "USER_ID" );        
        if ( usr == null ) return ACL_OPER;
        log.debug( " login id=" + usr );

        User owner = 
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null ) return ACL_OPER;
        log.debug( " owner set to: " + owner );
        
        IcJournal newJnrl = entryManager.addIcJournal( nlmid, owner );
        if ( newJnrl != null ) {
            journal = newJnrl;
            setId( newJnrl.getId() );
            return JEDIT;
        }
        return SUCCESS;
    }

    //--------------------------------------------------------------------------

    public String deleteJournal( Journal journal ) {
        /*
          NOTE: must check dependencies before removal 
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    private String deleteJournalList( List<Integer> ournals ) {
        /*

        NOTE: must check dependencies before removal

        for ( Iterator<Integer> ii = states.iterator();
              ii.hasNext(); ) {
            
            int gid = ii.next();
            DataState s = wflowContext.getWorkflowDao()
                .getDataState( gid );
                                     
            log.debug( " delete state -> id=" + s.getId() );
            wflowContext.getWorkflowDao().deleteDataState( s );                
        }
        */
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String updateJournalProperties( int id, Journal jrnl ) {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "update journal: id=" + id );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal == null || journal == null ) return SUCCESS;
        
        entryManager.updateIcJournal( oldJournal, jrnl );
        
        journal = entryManager.getIcJournal( id );
        setId( journal.getId() );
        
        return JEDIT;
    }

    //---------------------------------------------------------------------

    public String addJournalAdminGroup( int id, int grp ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add JAG: id=" + id + " ag= " + grp );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        Group agrp = getUserContext().getGroupDao().getGroup( grp );

        if ( oldJournal != null && agrp != null ) {
            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                entryManager.addAdminGroup( oldJournal, agrp );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------
    
    public String delJournalAdminGroups( int id, List<Integer> gidl ) {
    
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop JAG: id=" + id + " aglist= " + gidl );
        
        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal != null && gidl != null ) {
            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {

                entryManager.delAdminGroups( oldJournal, gidl );

                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        
        setId( 0 );
        return SUCCESS;
    }
    
    //---------------------------------------------------------------------
    
    public String addJournalAdminUser( int id,  String ulogin ) {
                        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "add JAG: id=" + id + " au= " + ulogin );
                
        IcJournal oldJournal = entryManager.getIcJournal( id );
        User ausr = getUserContext().getUserDao().getUser( ulogin );
        
        if ( oldJournal != null && ausr != null ) {

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                entryManager.addAdminUser( oldJournal, ausr );
            
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }

    //---------------------------------------------------------------------

    public String delJournalAdminUsers( int id, List<Integer> uidl ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "drop EAG: id=" + id + " aglist= " + uidl );

        IcJournal oldJournal = entryManager.getIcJournal( id );
        if ( oldJournal != null && uidl != null ) {

            if ( oldJournal.testAcl( ownerMatch, adminUserMatch, 
                                     adminGroupMatch ) ) {
                
                log.debug( "ACL test passed");
                
                entryManager.delAdminUsers( oldJournal, uidl );
                
                journal = entryManager.getIcJournal( id );
                setId( journal.getId() );
                return JEDIT;
            }
            return ACL_OPER;
        }
        setId( 0 );
        return SUCCESS;
    }
     

    //---------------------------------------------------------------------
    // record list operations
    //-----------------------

    /*
      {"recordsReturned":25, 
       "totalRecords":1397, 
       "startIndex":0, 
       "sort":null, 
       "dir":"asc", 
       "pageSize":10, 
       "records":[{"id":"0", "title":"...", "author":"...",
                   "imexid":"...","pmid":"...",
                   "owner":"","status:"","date":"..."},
                  {...}]
       }
    */
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    public String getIcJournalRecords() {
        return this.getIcJournalRecords( "", "", "", "", "" );
    }
    
    public String getIcJournalRecords( String max, String off, 
                                       String skey, String sdir, 
                                       String flt ) {
        
        if ( tracContext.getJournalDao() == null ) return null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "getIcJournalRecords"  );
        
        int first = 0;
        int blockSize = 10; // NOTE: initialize for defaults ?
        
        if ( off != null ) {
            try {
                first = Integer.parseInt( off );
            } catch ( NumberFormatException nex ) {
                // ignore == use default
            }
        }

        if ( max != null ) {
            try {
                blockSize = Integer.parseInt( max );
            } catch ( NumberFormatException nex ) {
                // ignore == use default
            }
        }

        List<Journal> jl = 
            tracContext.getJournalDao().getJournalList( first, blockSize );
        long total =  
            tracContext.getJournalDao().getJournalCount();

        // buid record map
        //----------------
        
        records = new HashMap<String,Object>();
        records.put("recordsReturned", jl.size() );
        records.put("totalRecords", total );
        records.put("startIndex", first );
        records.put("sort", skey );
        records.put("dir", sdir );
        records.put("pageSize", max );

        List<Map<String,Object>> rl = new ArrayList<Map<String,Object>> ();
        records.put("records", rl );

        for( Iterator<Journal> ii = jl.iterator(); ii.hasNext(); ) {
            IcJournal ij = (IcJournal) ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ij.getId() );
            r.put( "nlmid", ij.getNlmid() );
            r.put( "title", ij.getTitle() );
            r.put( "owner", ij.getOwner().getLogin() );
            r.put( "date", ij.getCreateDateString() );
            r.put( "time", ij.getCreateTimeString() );
            rl.add( r );
        }
        return JSON;
    }

    //--------------------------------------------------------------------------

    private GregorianCalendar parseDate( String date ) {
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "parseDate: " + date );
        
        // FORMAT: 2004/[0]3/12
        //----------------------

        try{
            Pattern p = Pattern.compile("(\\d{4}?)/(\\d\\d?)/(\\d\\d?)");
            Matcher m = p.matcher(date);
            if (  m.matches() ) {

                String year = m.group(1);
                String month = m.group(2);
                String day = m.group(3);
                
                log.debug( "Y=" + year + " M=" + month + " D=" + day );
                
                GregorianCalendar dateGC = 
                    new GregorianCalendar( Integer.parseInt( year ),
                                           Integer.parseInt( month )-1,
                                           Integer.parseInt( day ) );
                
                return dateGC;                
            }
        } catch( Exception ex ) {
            // ignore == parse to null
        }
        return null;        
    }
}
