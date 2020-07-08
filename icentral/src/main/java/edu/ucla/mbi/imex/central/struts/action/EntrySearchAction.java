package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 *
 * EntrySearchAction - Action supporting entry searches
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

public class EntrySearchAction extends ManagerSupport implements LogAware{

    private final String NOPUB = "notfound";
    private final String PUBVIEW = "pubview";
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
    
    private IcWorkflowContext wflowContext;

    public void setWorkflowContext( IcWorkflowContext context ) {
        this.wflowContext = context;
    }

    public IcWorkflowContext getWorkflowContext() {
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
    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " icpub=" + icpub + " op=" + getOp() ); 
        
        if ( tracContext.getPubDao() == null ) return SUCCESS;

        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null) {
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
            log.debug(  "opp=" + getOpp() );

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
                
                if ( key.equalsIgnoreCase( "eflt" ) ) {

                    if( getOpp() != null ){

                        if( getOpp().get("ftype") != null ){

                            if( getOpp().get("ftype").equals("elastic")) {
                                

                            }
                        }

                        
                        log.debug(  "opp=encf val=" + getOpp().get( "encf" ) );
                      
                        if( getOpp().get("encf") != null ){ 
                            
                            if( getOpp().get("encf").equals("-1")) {
                            
                                getOpp().put( "encf","");
                            }
                        }
                    }

                    return PUBVIEW;
                }

                //--------------------------------------------------------------
                                
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
    
    //---------------------------------------------------------------------
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
