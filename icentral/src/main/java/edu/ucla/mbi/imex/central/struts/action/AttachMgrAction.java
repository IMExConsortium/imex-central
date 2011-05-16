package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * AttachAction action
 *                
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import java.util.*;


import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;

import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class AttachMgrAction extends ManagerSupport {
    
    private static final String JSON = "json";    
    private static final String ACL_PAGE = "acl_page";
    private static final String ACL_OPER = "acl_oper";
    
    ////------------------------------------------------------------------------
    /// Entry Manager
    //---------------
    
    private EntryManager entryManager;

    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }

    public EntryManager getEntryManager() {
        return this.entryManager;
    }
    
    ////--------------------------------------------------------------------------
    ///  TracContext
    //--------------

    private TracContext tracContext;

    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }

    public TracContext getTracContext() {
        return this.tracContext;
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
    // results
    //--------
    
    private List attlist = null;
    
    public List getAttach() {
        if( attlist == null ){
            attlist = new ArrayList();
        }
        return attlist;
    }
    
    private Map attmeta = null;
    
    public Map getAttachMeta(){   
        if( attmeta == null ){
            attmeta = new HashMap<String,Object>();
        }
        return attmeta;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public String execute() throws Exception {

        
        attlist = new ArrayList();

        Log log = LogFactory.getLog( this.getClass() );
        log.debug(  "id=" + getId() + " op=" + getOp() );

        if ( tracContext.getPubDao() == null ||
             tracContext.getAdiDao() == null ) return JSON;
        
        if( getOp() == null ) return JSON;

        IcPub icpub = null;

        if(  getId() > 0 ) {           
            icpub = entryManager.getIcPub( getId() );
        }
        
        if( icpub == null ) return JSON;
        
        for ( Iterator<String> i = getOp().keySet().iterator();
              i.hasNext(); ) {
            
            String key = i.next();
            String val = getOp().get(key);

            if ( val != null && val.length() > 0 ) {
                
                if ( key.equalsIgnoreCase( "calg" ) ) {
                    // get all comments
                    //-----------------
                    attlist = getCommByRoot( icpub );
                }

                if ( key.equalsIgnoreCase( "cidg" ) ) {

                    // get comment by id
                    //------------------
                    
                    if ( getOpp() == null ) return JSON;
                    String sid = getOpp().get( "cid" );
                    int cid = -1;
                    try{
                        cid = Integer.parseInt( sid );
                    } catch (Exception ex){
                        // skip format error 
                    }
                    
                    Map icom = getCommById( cid );
                    if( icom != null ){
                        attlist = new ArrayList();
                        attlist.add( icom );
                    }
                    return JSON;
                }
                
                if ( key.equalsIgnoreCase( "eca" ) ) {
                    // get all comments

                    if ( getOpp() == null ) return JSON;
                    
                    String sub = getOpp().get( "encs" );
                    String bdy = getOpp().get( "encb" );
                    
                    log.debug(  "sub=" + sub + " bdy=" + bdy );
                    
                    return addComment( icpub, sub, bdy );
                }

                if ( key.equalsIgnoreCase( "ccnt" ) ) {

                    // count all comments
                    //-------------------
                    long attCnt = countCommByRoot( icpub );

                    getAttachMeta().put( "total", attCnt );
                }
                
            }
        }

        return JSON;
    }

    //--------------------------------------------------------------------------

    
    private String  addComment( IcPub icpub, String subject, String body ){
        
        Log log = LogFactory.getLog( this.getClass() );
        
        Integer usr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + usr );

        log.debug( " login id=" + usr );
        if ( usr == null )  return ACL_OPER;

        User owner =
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null )  return ACL_OPER;
        log.debug( " owner set to: " + owner );
        
        IcComment icCom = new IcComment( owner, icpub,
                                         subject, body);
        icCom.setOwner( owner );
        
        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();
        adiDao.saveAdi( icCom );
        
        return JSON;
    }

    //--------------------------------------------------------------------------

    private Map getCommById( int id ){
        
        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();
        
        AttachedDataItem cadi = adiDao.getAdi( id );
        
        if( cadi != null && cadi instanceof IcComment ){
            IcComment icom = (IcComment) cadi;
            return buildCommentMap( icom );
        }
        return null;
    }
    
    //--------------------------------------------------------------------------

    private List getCommByRoot( IcPub icpub ){
        
        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();

        List<AttachedDataItem> adiList =
            adiDao.getAdiListByRoot( icpub );

        List clist = new ArrayList();
        
        for( Iterator<AttachedDataItem> 
                 ii = adiList.iterator(); ii.hasNext(); ){
        
            AttachedDataItem cadi = ii.next();
            if( cadi instanceof IcComment ){
                IcComment ic = (IcComment) cadi;
                clist.add( buildCommentMap(ic) );
            }
        }

        return clist;
    }

    //--------------------------------------------------------------------------
    

    private long  countCommByRoot( IcPub icpub ){

        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();
        
        long cnt = adiDao.countIcCommByRoot( icpub );
        
        return cnt;
    }

    //--------------------------------------------------------------------------

    private Map buildCommentMap( IcComment ic ){
        
        Map<String,Object> cmap = new HashMap<String,Object>();
        String sub = ic.getLabel();
        String bdy = ic.getBody();
        String crt = String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", ic.getCrt() );
        String aut = ic.getOwner().getLogin();

        cmap.put("id",ic.getId() );
        cmap.put("root",ic.getRoot().getId());
        cmap.put("subject",sub);
        cmap.put("body",bdy);
        cmap.put("date",crt);
        cmap.put("author",aut);
        
        return cmap;
    }    
}