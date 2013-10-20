package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * EntryViewerAction - Action supporting a table view of entry list
 *
 ============================================================================ */
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;
import java.util.GregorianCalendar;
import java.util.Calendar;

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;
import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;

public class EntryViewerAction extends ManagerSupport implements LogAware{

    private final String NOPUB = "notfound";
    private final String PUBEDIT = "pubedit";
    private final String PUBNEW = "pubnew";
    private final String JSON = "json";
    private final String INPUT = "input";

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

    ////------------------------------------------------------------------------
    /// Watch Manager
    //--------------

    private WatchManager watchManager;
    
    public void setWatchManager( WatchManager manager ) {
        this.watchManager = manager;
    }
    
    public WatchManager getWatchManager() {
        return this.watchManager;
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
    // IcPub
    //------

    private IcPub icpub = null;
    
    public void setPub( IcPub pub ) {
        this.icpub = pub;
    }

    public IcPub getPub(){
        return this.icpub;
    }
    
    //--------------------------------------------------------------------------
    
    public List<IcPub> getPublicationList(){

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: getPublicationList"  );
        
        if ( tracContext.getPubDao() == null ) return null;

        log.debug( "getPublicationList: pubDao ok..."  );
        
        List<Publication> pl = tracContext.getPubDao().getPublicationList();

        log.debug( "publist=" + pl );
        
        if ( pl == null ) return null;
        
        List<IcPub> ipl = new ArrayList<IcPub>();
        for ( Iterator<Publication> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub jj = (IcPub) ii.next();
            ipl.add( jj );
        }
        return ipl;
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
    // PMID
    //-----
    
    private String pmid = null;

    public void setPmid( String pmid ) {
        this.pmid = sanitize( pmid );
    }
    
    public String getPmid(){
        return this.pmid;
    }
    
    //--------------------------------------------------------------------------
    // format
    //-------

    private String format = null;

    public void setFormat( String format ) {
        this.format = format;
    }
    
    public String getFormat(){
        return this.format;
    }

    //--------------------------------------------------------------------------
    
    Map<String,Object> flags = null;

    public Map<String,Object> getFlags(){
        return this.flags;
    }
    
    //--------------------------------------------------------------------------
    // target states
    //--------------
    /*
    private List<String> targetStates = null;
    
    public void setTargetStates( List<String> states) {
        this.targetStates = states;
    }

    public List<String> getTargetStates() {
        return this.targetStates;
    }
    */
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " icpub=" + icpub + " op=" + getOp() ); 
        
        if ( tracContext.getPubDao() == null ) return SUCCESS;

        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null && iusr.intValue() > 0 ){
            luser = getUserContext().getUserDao().getUser( iusr.intValue() );
            log.debug( " user set to: " + luser );
        }
        
        if( getPmid() != null ) {
            log.debug( "no ic pub record pmid=" + getPmid() +"<" );
            Publication nicp = entryManager.getPubByPmid( getPmid() );

            log.debug("nicp="+nicp);

            if ( nicp != null ) {
                icpub = new IcPub( nicp );
                return PUBNEW;
            } else {
                addActionError( "No publication found." ) ;
                icpub = new IcPub(new Publication());
                icpub.setPmid(getPmid());
                return NOPUB;
            }
        }
        
        if( getId() <= 0 && getOp() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( val != null && val.length() > 0 ) {

                //--------------------------------------------------------------
                // initialize
                //-----------
                
                if ( key.equalsIgnoreCase( "init" ) ) {
                    return SUCCESS;                    
                }
                
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "esrc" ) ) {

                    String imex = null ;
                    if ( getOpp() != null ) {
                        String ns  = getOpp().get( "ns" );
                        String ac  = getOpp().get( "ac" );

                        return searchIcPub( ns, ac );

                    }
                    return searchIcPub( icpub, imex );
                }

                //--------------------------------------------------------------
                
                /*
                                
                if ( key.equalsIgnoreCase( "etsl" ) ) {
                    if( icpub != null ) {
                        return getTargetStates( icpub, val );
                    }
                }
                */
                //--------------------------------------------------------------
                
                if ( key.equalsIgnoreCase( "ppg" ) ){

                    log.debug(  "\n\nop=" + getOp() );
                    log.debug(  "opp=" + getOpp() );
                    log.debug(  "luser=" + luser +"\n\n" );

                    if ( getOpp() == null ) {
                        return getIcPubRecords();
                    }
                    
                    String wfl= getOpp().get( "wfl" ) == null ?
                        "" :  getOpp().get( "wfl" );
                    
                    String max= getOpp().get( "max" );
                    String off= getOpp().get( "off" );
                    String skey= getOpp().get( "skey" );
                    String sdir= getOpp().get( "sdir" );
                    
                    String sfv = getOpp().get( "sfv" ) == null ? 
                        "" :  getOpp().get( "sfv" );
                    String pfv = getOpp().get( "pfv" ) == null ?
                        "" :  getOpp().get( "pfv" );;
                    String ofv = getOpp().get( "ofv" ) == null ?
                        "" :  getOpp().get( "ofv" );
                    String efv = getOpp().get( "efv" ) == null ?
                        "" :  getOpp().get( "efv" );
                    String ffv = getOpp().get( "ffv" ) == null ?
                        "" :  getOpp().get( "ffv" );
                
                    if( !wfl.equalsIgnoreCase("true") || luser == null ){
    
                        return getIcPubRecords( max, off, skey, sdir,
                                                sfv, pfv, ofv, efv, ffv );

                    } else {
                        return getWatchedRecords( luser, 
                                                  max, off, skey, sdir,
                                                  sfv, pfv, ofv, efv, ffv );
                    }
                }                
            }
        }
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // validation
    //-----------
  
    private boolean aclTargetValidate( Publication pub ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: aclTargetValidate"  );
        
        return pub.testAcl( ownerMatch, adminUserMatch, adminGroupMatch);
    }
     
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    // operations
    //-----------

    public String searchIcPub( Publication pub, String imex ) {

        Log log = LogFactory.getLog( this.getClass() );
        
        if( pub != null ) {
            log.debug( " search pub -> id=" + pub.getId() +
                       " pmid=" + pub.getPmid() );
        } else {  
            log.debug( " search pub -> imex=" + imex );
        }        

        try {
            imex = imex.replaceAll("\\D+", "" );
        } catch ( Exception ex ) {
            // skip error
        }

        if ( imex != null && !imex.equals("") ) {
            IcPub oldPub = entryManager.getIcPubByIcKey( imex );
            
            if ( oldPub != null ) {
                 
                if( !aclTargetValidate( oldPub ) ) return ACL_OPER;
                
                icpub = oldPub;
                setId( oldPub.getId() );
                return PUBEDIT;
            }

            addActionError( "No publication found" ) ;
            return NOPUB;

        }
        
        if( pub!= null && pub.getPmid() != null ){

            pub.setPmid( sanitize( pub.getPmid() ) );
            
            IcPub oldPub = entryManager.getIcPubByPmid( pub.getPmid() );
            
            if ( oldPub != null ) {

                if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
                    
                icpub = oldPub;
                setId( oldPub.getId() );
                return PUBEDIT;
            }
            
            if( !pub.getPmid().equals("") ) {
                this.setPmid( pub.getPmid() );
                return PUBNEW;
            }
                
            addActionError( "No publication found" ) ;
            return NOPUB;
        } 
        
        return INPUT;

    }

    //------------------------------------------------------------------------------

    public String searchIcPub( String ns, String ac ) {

        Log log = LogFactory.getLog( this.getClass() );

        if( ns == null ||  ac == null ){
            addActionError( "No publication found" ) ;
            return NOPUB; 
        }
        
        IcPub oldPub = entryManager.getIcPubByNsAc( ns, ac );
        
        if ( oldPub != null ) {

            if( ! aclTargetValidate( oldPub ) ) return ACL_OPER;
                
            icpub = oldPub;
            setId( oldPub.getId() );
            return PUBEDIT;
        } else {
            addActionError( "No publication found" ) ;
            return NOPUB;
        }
    }
    
    //--------------------------------------------------------------------------
    /*
    public String getTargetStates( IcPub pub, String mode ) {

        if( ! aclTargetValidate( pub ) ) return ACL_OPER;

        List<String> states = entryManager.getTargetStates( pub, mode );
        targetStates = states;
        return JSON;
    }
    */
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    private boolean getWatchStatus( User usr, Publication pub ){ 
        return watchManager.getWatchStatus( usr, pub );       
    }

    public String getWatchedRecords( User usr ) {
        return this.getWatchedRecords( usr, 
                                       "", "", "", "", "", "", "", "","" );
    }
    
    public String getWatchedRecords( User usr, 
                                     String max, String off,
                                     String skey, String sdir,
                                     String sfv, String pfv,
                                     String ofv, String efv,
                                     String ffv ) {
        

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getWatchedRecords: uid=" + usr.getId() );

        return getIcPubRecords( usr , max, off, skey, sdir, sfv, pfv,
                                ofv, efv, ffv);
        
    }
    
    public String getIcPubRecords() {
        return this.getIcPubRecords( null, "", "", "", "", "", "", "", "","" );
    }
    
    public String getIcPubRecords( String max, String off, 
                                   String skey, String sdir, 
                                   String sfv, String pfv, 
                                   String ofv, String efv,
                                   String ffv ) {
        return getIcPubRecords( null, max, off, skey, sdir, sfv, pfv, 
                                ofv, efv, ffv);
    }

    public String getIcPubRecords( User usr, 
                                   String max, String off, 
                                   String skey, String sdir, 
                                   String sfv, String pfv, 
                                   String ofv, String efv,
                                   String ffv ) {
        
        if ( tracContext.getPubDao() == null ) return null;

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "getPubRecords: pubDao ok >" + sdir + "<"  );
        
        int first = 0;
        int blockSize = 10; // NOTE: initialize for defaults ?
        boolean asc = true;

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
        } else {
            max ="";
        }
        
        if ( sdir != null &&
             ( sdir.equals( "false" )
               || sdir.equals( "desc" ) ) ){
            asc = false;
            sdir ="desc";
        } else {
            sdir ="asc";
        }

        String sortKey ="id";
        
        if( skey != null && !skey.equals("") ){
            if( skey.equals( "pub" ) ){
                sortKey ="author";
            }
            if( skey.equals( "date" ) ){
                sortKey ="crt";
            }
            if( skey.equals( "owner" ) ){
                sortKey ="owner";
            }
            if( skey.equals( "pmid" ) ){
                sortKey ="pmid";
            }
            if( skey.equals( "imexId" ) ){
                sortKey ="imex";
            }
        } else {
            skey = "id";
            sortKey = "id";
        }

        List<Publication> pl = new ArrayList<Publication>();
        long total = 0;
        log.debug( "getPubRecords: " + sfv + " :: " + pfv + " :: " + efv + " :: " + ffv);

        Map<String,String> flt = new HashMap<String,String>();
        flt.put( "status", sfv );
        flt.put( "partner", pfv );
        flt.put( "owner", ofv );
        flt.put( "editor", efv );
        flt.put( "cflag", ffv );
        
        if ( sfv.equals("") && pfv.equals("") && ofv.equals("") 
             && efv.equals("") && ffv.equals("") ){
            
            log.debug( "getPubRecords: unfiltered" );
            
            if( usr == null ){
                pl = tracContext.getPubDao()
                    .getPublicationList( first, blockSize, sortKey, asc );
                total = tracContext.getPubDao().getPublicationCount();
            } else {

                log.debug( "getting list" );
                pl = watchManager
                    .getPublicationList( usr, first, blockSize, sortKey, asc );

                log.debug( "getting count" );
                total = watchManager
                    .getPublicationCount( usr );
                log.debug( "got count" );
            }
            
        } else {
            
            log.debug( "getPubRecords: filtered" );
            
            if( usr == null ){
                pl = tracContext.getPubDao()
                    .getPublicationList( first, blockSize, 
                                         sortKey, asc, flt );            
                
                total = tracContext.getPubDao().getPublicationCount( flt );
            } else {
                pl = watchManager
                    .getPublicationList( usr, first, blockSize, 
                                         sortKey, asc, flt );
                total = watchManager
                    .getPublicationCount( usr, flt );
            }   
        }

        log.debug( "getPubRecords: total=" + total);

        // buid record map
        //----------------
        
        records = new HashMap<String,Object>();
        records.put("recordsReturned", pl.size() );
        records.put("totalRecords", total );
        records.put("startIndex", first );
        records.put("sort", skey );
        records.put("dir", sdir );
        records.put("pageSize", max );
        records.put("filter", flt );

        List<Map<String,Object>> rl = new ArrayList<Map<String,Object>> ();
        records.put("records", rl );

        for( Iterator<Publication> ii = pl.iterator(); ii.hasNext(); ) {
            IcPub ip = (IcPub) ii.next();
            Map<String,Object> r = new HashMap<String,Object>();  
            r.put( "id", ip.getId() );
            r.put( "pmid", ip.getPmid() );
            r.put( "doi", ip.getDoi() );
            r.put( "jintId", ip.getJournalSpecific() );
            r.put( "imexId", ip.getImexId() );
            r.put( "cEmail", ip.getContactEmail() );

            r.put( "title", ip.getTitle() );
            r.put( "author", ip.getAuthor() );
            r.put( "owner", ip.getOwner().getLogin() );
            r.put( "state", ip.getState().getName() );
            r.put( "date", ip.getCreateDateString() );
            r.put( "time", ip.getCreateTimeString() );
            r.put( "editor", "N/A" );
            r.put( "imexDb", "N/A" );

            // set partner
            //------------

            String partner = "";

            Set<Group> gs = ip.getAdminGroups();
            for( Iterator<Group> gi = gs.iterator(); gi.hasNext(); ) {

                Group g = gi.next();
                Set<Role> rs = g.getRoles();

                for( Iterator<Role> ri = rs.iterator(); ri.hasNext(); ) {
                    Role role = ri.next();
                    if( role.getName().toUpperCase().equals(PARTNER) ) {
                        partner += g.getLabel()+":";
                    }
                    //log.debug( "r:" + role.getName() );
                }
                //log.debug( "g:" + g.getLabel() );
                
            }
            if ( !partner.equals("") ) {
                r.put( "imexDb", partner.substring(0,partner.length()-1 ) );
            }
            

            // set editors
            //------------

            String editor = "";

            Set<User> us = ip.getAdminUsers();
            for( Iterator<User> ui = us.iterator(); ui.hasNext(); ) {

                User u = ui.next();
                Set<Role> rs = u.getRoles();

                for( Iterator<Role> ri = rs.iterator(); ri.hasNext(); ) {
                    Role role = ri.next();
                    if( role.getName().toUpperCase().equals(EDITOR) ) {
                        editor += u.getLogin()+":";
                    }
                    //log.debug( "r:" + role.getName() );
                }
                //log.debug( "u:" + u.getLogin() );
                
            }
            if ( !editor.equals("") ) {
                r.put( "editor", editor.substring(0,editor.length()-1 ) );
            }
            
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
                
                if( Integer.parseInt( year ) <1900 ||
                    Integer.parseInt( month ) < 1 ||
                    Integer.parseInt( day ) < 1 ){
                    return null;
                } else {

                    GregorianCalendar dateGC = 
                        new GregorianCalendar( Integer.parseInt( year ),
                                               Integer.parseInt( month )-1,
                                               Integer.parseInt( day ) );


                    return dateGC;                
                }
            }
        } catch( Exception ex ) {

            ex.printStackTrace();
            // ignore == parse to null
        }
        return null;        
    }

    //--------------------------------------------------------------------------
    
    private String sanitize( String str ){

        if( str != null ){
            try{
                str = str.replaceAll("^\\s+","");
                str = str.replaceAll("\\s+$","");
            } catch( Exception ex ){
                
            }
        } else {
            str = "";
        }
        return str;
    }    
}
