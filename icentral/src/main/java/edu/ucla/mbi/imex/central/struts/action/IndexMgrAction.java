package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IndexMgrAction - web interface to index management
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

public class IndexMgrAction extends ManagerSupport implements LogAware{

    private final String NOPUB = "notfound";
    private final String PUBEDIT = "pubedit";
    private final String PUBNEW = "pubnew";
    private final String JSON = "json";
    private final String INPUT = "input";

    public static final String ACL_PAGE = "acl_page";
    public static final String ACL_OPER = "acl_oper";

    public static final String EDITOR = "CURATOR";
    public static final String PARTNER = "IMEX PARTNER";

    private Map<String,String> filter = null;
    
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
    /// Index Manager
    //---------------

    private IndexManager indexManager;
    
    public void setIndexManager( IndexManager manager ) {
        this.indexManager = manager;
    }
    
    public IndexManager getIndexManager() {
        return this.indexManager;
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
    // status
    //------

    private int statCode = 0;
    private String statMessage = "OK";
    
    public int getStatusCode(){
        return this.statCode;
    }

    public String getStatusMessage(){
        return this.statMessage;
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
    // ES URL
    //-------

    public String getEsUrl(){
        return getIndexManager().getEsIndexUrl();
    }

    public boolean getEsActive(){
        return getIndexManager().getEsIndexActive();
    }

    //--------------------------------------------------------------------------

    public String execute() throws Exception{

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  " op=" + getOp() + " opp=" + getOpp()); 
	
        if ( tracContext.getPubDao() == null ) return SUCCESS;

        Integer iusr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + iusr );
        
        User luser = null;
        if( iusr != null) {
            luser = getUserContext().getUserDao().getUser( iusr.intValue() );
            log.debug( " user set to: " + luser );
        }
        
        if( getOp() == null ) return SUCCESS;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);
            
            log.debug(  "op=" + key + "  val=" + val );

            if ( key.equalsIgnoreCase( "status" ) ){

                

                    return SUCCESS;                    
            }
                
            if ( key.equalsIgnoreCase( "idxid" ) ){
                
                String idStr = getOpp().get( "recid" );

                try{                    
                    int id = Integer.parseInt( idStr );
                    IcPub pub = getEntryManager().getIcPub( id );
                    
                    if( pub != null ){
                        getIndexManager().indexIcPub( id );
                    }

                    getIndexManager().getLastIcPub( 10 );

                }catch( Exception ex ){
                
                    return SUCCESS;
                }

            }

            if ( key.equalsIgnoreCase( "idxnold" ) ){

                String cntStr = getOpp().get( "nrec" );
                
                try{                    
                    int cnt = Integer.parseInt( cntStr );
                   
                    List<Integer> plist =  getIndexManager().getLastIcPub(cnt);

                    for(Iterator<Integer> ii = plist.iterator(); ii.hasNext(); ){

                        IcPub pub = entryManager.getIcPub( (ii.next()).intValue() );
                        
                        if( pub != null ){
                            getIndexManager().indexIcPub( pub.getId() );                            
                        }
                    }
                }catch( Exception ex ){
                    return SUCCESS;
                }                
            }

            if ( key.equalsIgnoreCase( "idxrng" ) ){

                String minStr = getOpp().get( "recmin" );
                String maxStr = getOpp().get( "recmax" );

                try{                    
                    int minId = Integer.parseInt( minStr );
                    int maxId = Integer.parseInt( maxStr );
               
                    for( int cid = minId; cid <=maxId; cid ++ ){
                        try{
                            IcPub pub = getEntryManager().getIcPub( cid );
                            if( pub != null ){
                                getIndexManager().indexIcPub( cid );
                            }
                        }catch( Exception dbx ){
                            return SUCCESS;
                        }
                    }
                }catch( Exception ex ){
                    
                    return SUCCESS;
                }
            }

            if ( key.equalsIgnoreCase( "idxall" ) ){

                try{                    
                    //IcPub pub = entryManager.getIcPub( id );
                    
                    //if( pub != null ){
                        // index me
                    
                    //}
                }catch( Exception ex ){
                
                    return SUCCESS;
                }
            }

            if ( key.equalsIgnoreCase( "seturl" ) ){

                try{                    

                    String esurl = getOpp().get( "esurl" );

                    if( esurl != null ){
                        esurl = esurl.replaceAll( " ","");
                    }
                    
                    if( esurl.length() > 0 ){
                        getIndexManager().setEsIndexUrl( esurl );
                        log.info(" New ES URL: " +  esurl );
                    }
                    
                }catch( Exception ex ){
                
                    return SUCCESS;
                }
            }

        }
        return SUCCESS;
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // validation
    //-----------
    
    public void validateXXX() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: validate" );
        /*
        //boolean loadUserFlag = false;
        
        if( getOp() != null ) {
            for ( Iterator<String> i = getOp().keySet().iterator();
                  i.hasNext(); ) {
                
                String key = i.next();
                String val = getOp().get(key);

                if ( val != null && val.length() > 0 ) {
                    
                    log.debug( " op=" + val);
                    if ( key.equalsIgnoreCase( "esrc" ) ) {

                        if ( getOpp() != null 
                             && getOpp().get( "imex" ) != null 
                             && ! getOpp().get( "imex" ).equals("") ) {
                            break;
                        }

                        if( getPub() == null || getPub().getPmid() == null ) {
                            addFieldError( "pub.pmid",
                                           "PMID field cannot be empty." );
                            
                        } else {
                            String pmid = getPub().getPmid();
                            try {
                                pmid = pmid.replaceAll( "\\s", "" );
                            } catch( Exception ex ) {
                                // should not happen
                            }
                            log.debug( " pmid=" + pmid +"<");
                            if( pmid.length() == 0  ) {
                                log.debug( "  empty PMID field" );
                                addFieldError( "pub.pmid",
                                               "PMID field cannot be empty." );
                            }
                        }

                        break;
                    }
                    
                    //----------------------------------------------------------
                    
                    if ( key.equalsIgnoreCase( "eatu" ) ) {
                        
                        String auth = null;
                        String title = null;
                        
                        if( getPub() != null ) {

                            auth = getPub().getAuthor();
                            title = getPub().getTitle();
                            
                            if (auth != null ) {
                                auth = auth.replaceAll( "^\\s+", "" );
                                auth = auth.replaceAll( "\\s+$", "" );
                            } else {
                                auth = "";
                            }
                            getPub().setAuthor( auth );

                            if ( title != null ) {
                                title = title.replaceAll( "^\\s+", "" );
                                title = title.replaceAll( "\\s+$", "" );
                            } else {
                                title= "";
                            }
                            getPub().setTitle( title );

                        }
                        
                        if( auth == null || auth.length() == 0 ) {
                            addFieldError( "pub.author",
                                           "Author field cannot be empty." );
                        }
                        
                        if( title == null || title.length() == 0 ) {
                            addFieldError( "pub.title",
                                           "Title field cannot be empty." );
                        }
                        
                        break;
                    }                                        
                }
            }
        }
        */
    }
    
    /*
    private boolean aclTargetValidate( Publication pub ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryMgrAction: aclTargetValidate"  );
        
        return pub.testAcl( ownerMatch, adminUserMatch, adminGroupMatch);
    }
    */
    
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
