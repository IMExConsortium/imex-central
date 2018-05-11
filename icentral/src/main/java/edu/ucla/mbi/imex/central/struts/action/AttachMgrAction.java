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

import java.io.*;
import java.util.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class AttachMgrAction extends ManagerSupport {
    
    private static final String JSON = "json";    
    private static final String REDIRECT = "redirect";    
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

    ////------------------------------------------------------------------------
    /// Attachment Manager
    //--------------------
    
    private AttachmentManager attachmentManager;

    public void setAttachmentManager( AttachmentManager manager ) {
        this.attachmentManager = manager;
    }

    public AttachmentManager getAttachmentManager() {
        return this.attachmentManager;
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
    private List scrlist = null;
    
    public List getAttach() {
        if( attlist == null ){
            attlist = new ArrayList();
        }
        return attlist;
    }


    public List getScore() {
        if( scrlist == null ){
            scrlist = new ArrayList();
        }
        return scrlist;
    }
    
    private Map attmeta = null;
    private Map scrmeta = null;
    
    public Map getAttachMeta(){   
        if( attmeta == null ){
            attmeta = new HashMap<String,Object>();
        }
        return attmeta;
    }

    public Map getScoreMeta(){   
        if( scrmeta == null ){
            scrmeta = new HashMap<String,Object>();
        }
        return scrmeta;
    }

    private InputStream dstream;

    public InputStream getData() {
        return dstream;
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

               if ( key.equalsIgnoreCase( "halg" ) ) {

                    // get all history log entries
                    //----------------------------
                    attlist = getLogEntryByRoot( icpub );
                }

               if ( key.equalsIgnoreCase( "dalg" ) ) {

                    // get all data attachments
                    //-------------------------
                   
                   List<Map> adl = getADataByRoot( icpub );
                   
                   Map<String,Map> smap = new <String,Map>HashMap();

                   for( Iterator<Map> ii = adl.iterator(); ii.hasNext(); ){
                       
                       Map ci = ii.next();
                       
                       if(ci.containsKey("value")){  // new score to add
                                                     
                           String name = (String) ci.get("name");
                           int id = (int)  ci.get("id");

                           boolean skip = false;

                           if( smap.containsKey(name) ){                           
                               Map cscore = (Map) smap.get( name );
                               
                               if( ((int) cscore.get("id")) > id ){
                                   skip = true;
                               }
                           }
                           
                           if( ! skip  ){
                               smap.put( name, ci );
                           }
                       } else {
                           getAttach().add( ci );
                       }
                   }
                   
                   scrlist = new ArrayList( smap.values() );

                   getAttachMeta().put( "total", getAttach().size() );
                   getScoreMeta().put( "total", getScore().size() );

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
                
                if ( key.equalsIgnoreCase( "aidg" ) ) {

                    // get attached data by id
                    //------------------------
                    
                    if ( getOpp() == null ) return JSON;
                    String sid = getOpp().get( "aid" );
                    int cid = -1;
                    try{
                        cid = Integer.parseInt( sid );
                    } catch (Exception ex){
                        // skip format error 
                    }
                    
                    Map icom = getADataById( cid );
                    if( icom != null ){
                        attlist = new ArrayList();
                        attlist.add( icom );
                    }

                    if( getOpp().get( "rt" ) != null){ 

                        if( getOpp().get( "rt" ).equalsIgnoreCase( "data" ) ){
                            dstream = new StringBufferInputStream( (String) icom.get("body") );
                            
                        }
                        return getOpp().get( "rt" );
                    } else {
                        return JSON;
                    }
                }

                if ( key.equalsIgnoreCase( "eca" ) ) {

                    // get all comments
                    //-----------------
                    if ( getOpp() == null ) return JSON;
                    
                    String sub = getOpp().get( "encs" );
                    String bdy = getOpp().get( "encb" );
                    String bdyTp = getOpp().get( "encbt" );
                    if( bdyTp == null || bdyTp.equals("") ){
                        bdyTp = "WIKI";
                    }
                    String flag = getOpp().get( "encf" );
                    int iflag = -1;
                    try{
                        if( flag != null ){
                            iflag = Integer.parseInt(flag);
                        } 
                    } catch(Exception ex){
                        // ignore formatting errors
                    }
                    log.debug(  "sub=" + sub + " bdy=" + bdy + " sflag="+flag);
                    return addComment( icpub, sub, bdy, bdyTp, iflag );
                }

                if ( key.equalsIgnoreCase( "ccnt" ) ) {

                    // count all comments
                    //-------------------
                    long attCnt = countCommByRoot( icpub );

                    getAttachMeta().put( "total", attCnt );
                }
                
                if ( key.equalsIgnoreCase( "edda" ) ) {

                    // delete data attachment
                    //-----------------------

                    if ( getOpp() == null ) return JSON;

                    String daIdStr = getOpp().get( "daid" );
                    int daId = -1;
                    
                    if( daIdStr != null && !daIdStr.equals("") ){
                        try{
                            daId = Integer.parseInt(daIdStr);
                        } catch( Exception ex ){
                        }
                    }
                    
                    return dropAttachment( icpub, daId );
                    
                }
                
                if ( key.equalsIgnoreCase( "eada" ) ) {

                    // add data attachment
                    //--------------------
                    
                    if ( getOpp() == null ) return SUCCESS;

                    String aName = getOpp().get( "edan" );
                    String fName = getOpp().get( "edafile" );
                    
                    String daTpStr = getOpp().get( "edat" );
                    int daTp = 0;
                    if( daTpStr != null && !daTpStr.equals("") ){
                        try{
                            daTp = Integer.parseInt(daTpStr); 
                        } catch(Exception ex){
                        }
                    } 
                    String flag = getOpp().get( "edaf" );
                    int iflag = -1;
                    try{
                        if( flag != null ){
                            iflag = Integer.parseInt( flag );
                        }
                    } catch(Exception ex){
                        // ignore formatting errors
                    }
                    
                    log.info( "name=" + aName + " type=" + daTp 
                              + " sflag=" + flag + " file=" + fName );                      
                    
                    StringBuffer abody = new StringBuffer();
                    try{
                        FileReader fr = new FileReader( fName );
                        BufferedReader br = new BufferedReader( fr );
                        
                        char[] buffer = new char[4096];
                        
                        while(true) {
                            int read = br.read( buffer, 0, 4096);
                            abody.append( new String( buffer, 0, read ) );

                            if( read < 4096 ){
                                break;
                            }
                        }
                        br.close();
                        
                    }catch(Exception e){
                    }
                    
                    if( abody.length() > 0 ){
                        return addAttachment( icpub, aName, abody.toString(), 
                                       daTp, iflag );

                    }
                    return REDIRECT;
                }                
            }
        }
        return JSON;
    }

    //--------------------------------------------------------------------------

    
    private String  addComment( IcPub icpub, String subject, 
                                String body, String bdyTp, 
                                int iflag ){
        
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
                                         subject, body, "WIKI" );
        icCom.setOwner( owner );

        if( iflag > 0 ){
            IcFlag flag =  attachmentManager.getIcFlag( iflag );
            if( flag != null ){
                icCom.setIcFlag( flag );
            }
        }
        
        attachmentManager.addIcAdi( icCom, owner );

        return JSON;
    }

    //--------------------------------------------------------------------------

    
    private String  addAttachment( IcPub icpub, String subject, 
                                   String body, int bdyTp, int iflag ){
        
        Log log = LogFactory.getLog( this.getClass() );
        
        Integer usr = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + usr );

        log.debug( " login id=" + usr );
        if ( usr == null )  return ACL_OPER;

        User owner =
            getUserContext().getUserDao().getUser( usr.intValue() );
        if ( owner == null )  return ACL_OPER;
        log.debug( " owner set to: " + owner );
        
        IcAttachment icAtt = new IcAttachment( owner, icpub,
                                               subject, body );
        
        if( iflag > 0 ){
            IcFlag flag =  attachmentManager.getIcFlag( iflag );
            if( flag != null ){
                icAtt.setIcFlag( flag );
            }
        }
        
        if( bdyTp > 0 ){
            icAtt.setFormat(bdyTp);
        }
        
        log.info( " DAtt Format: " + icAtt.getFormat() + ":(" + bdyTp + ")" );
        
        attachmentManager.addIcAdi( icAtt, owner );

        return REDIRECT;
    }

    private String dropAttachment( IcPub pub, int aid ){

        Log log = LogFactory.getLog( this.getClass() );

        Integer usrId = (Integer) getSession().get( "USER_ID" );
        log.debug( " login id=" + usrId );

        log.debug( " login id=" + usrId );
        if ( usrId == null )  return ACL_OPER;

        User usr =
            getUserContext().getUserDao().getUser( usrId.intValue() );
        if ( usr == null )  return ACL_OPER;

        attachmentManager.dropIcAdi( aid, usr );        
        return REDIRECT;
    }

    //--------------------------------------------------------------------------

    private Map getADataById( int id ){
        
        AttachedDataItem aadi = attachmentManager.getIcAdi( id );
        
        if( aadi != null && aadi instanceof IcAttachment ){
            IcAttachment icom = (IcAttachment) aadi;
            return buildIcAttachmentMap( icom, true );
        }
        return null;
    }

    //--------------------------------------------------------------------------

    private Map getCommById( int id ){
        
        AttachedDataItem cadi = attachmentManager.getIcAdi( id );
        
        if( cadi != null && cadi instanceof IcComment ){
            IcComment icom = (IcComment) cadi;
            return buildCommentMap( icom );
        }
        return null;
    }


    
    //--------------------------------------------------------------------------

    private List getCommByRoot( IcPub icpub ){
        
        //IcAdiDao adiDao = (IcAdiDao)
        //    entryManager.getTracContext().getAdiDao();

        //List<AttachedDataItem> adiList =
        //    adiDao.getAdiListByRoot( icpub );

        List<AttachedDataItem> adiList 
            = attachmentManager.getAdiListByRoot( icpub );
        
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
    
    private List getLogEntryByRoot( IcPub icpub ){
        
        //IcAdiDao adiDao = (IcAdiDao)
        //    entryManager.getTracContext().getAdiDao();

        //List<AttachedDataItem> adiList =
        //    adiDao.getAdiListByRoot( icpub );

        List<AttachedDataItem> adiList
            = attachmentManager.getAdiListByRoot( icpub );

        List clist = new ArrayList();
        
        for( Iterator<AttachedDataItem> 
                 ii = adiList.iterator(); ii.hasNext(); ){
        
            AttachedDataItem cadi = ii.next();
            if( cadi instanceof IcLogEntry ){
                IcLogEntry ic = (IcLogEntry) cadi;
                clist.add( buildLogEntryMap(ic) );
            }
        }

        return clist;
    }

    //--------------------------------------------------------------------------

    private List<Map> getADataByRoot( IcPub icpub ){
        
        //IcAdiDao adiDao = (IcAdiDao)
        //    entryManager.getTracContext().getAdiDao();

        //List<AttachedDataItem> adiList =
        //    adiDao.getAdiListByRoot( icpub );

        List<AttachedDataItem> adiList
            = attachmentManager.getAdiListByRoot( icpub );

        List clist = new ArrayList();
        
        for( Iterator<AttachedDataItem> 
                 ii = adiList.iterator(); ii.hasNext(); ){
        
            AttachedDataItem cadi = ii.next();
            if( cadi instanceof IcAttachment ){
                IcAttachment ic = (IcAttachment) cadi;
                clist.add( buildIcAttachmentMap( ic, false ) );
            }
            if( cadi instanceof IcScore ){
                IcScore ic = (IcScore) cadi;
                clist.add( buildIcScoreMap( ic ) );
            }
        }

        return clist;
    }

    //--------------------------------------------------------------------------
    

    private long  countCommByRoot( IcPub icpub ){

        //IcAdiDao adiDao = (IcAdiDao)
        //    entryManager.getTracContext().getAdiDao();
        
        //long cnt = adiDao.countIcCommByRoot( icpub );
        
        return attachmentManager.countCommByRoot( icpub ); 
    }

    //--------------------------------------------------------------------------
    
    private long  countLogEntryByRoot( IcPub icpub ){

        //IcAdiDao adiDao = (IcAdiDao)
        //    entryManager.getTracContext().getAdiDao();
        
        //long cnt = adiDao.countIcLogEntryByRoot( icpub );
        
        return attachmentManager.countLogEntryByRoot( icpub );
    }

    //--------------------------------------------------------------------------

    private Map buildCommentMap( IcComment ic ){
        
        Map<String,Object> cmap = new HashMap<String,Object>();
        String sub = ic.getLabel();
        String bdy = ic.getBody();
        String bdyTp = ic.getBodyType();

        String crt = String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", ic.getCrt() );
        String aut = ic.getOwner().getLogin();

        cmap.put("id",ic.getId() );
        cmap.put("root",ic.getRoot().getId());
        cmap.put("subject",sub);
        cmap.put("body",bdy);
        cmap.put("bodyType",bdyTp);
        cmap.put("date",crt);
        cmap.put("author",aut);
        
        if( ic.getIcFlag() != null ){
            cmap.put("flagId",ic.getIcFlag().getId());            
            cmap.put("flagName",ic.getIcFlag().getName());
        }
        return cmap;
    }    
    //--------------------------------------------------------------------------

    private Map buildLogEntryMap( IcLogEntry ic ){
        
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

    //--------------------------------------------------------------------------

    private Map buildIcAttachmentMap( IcAttachment ic, boolean bflag ){
        
        Map<String,Object> cmap = new HashMap<String,Object>();
        String sub = ic.getLabel();

        String crt = String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", ic.getCrt() );
        String aut = ic.getOwner().getLogin();
        String bdyTp = ic.getDataType();

        cmap.put("id",ic.getId() );
        cmap.put("aid",ic.getId() );
        cmap.put("root",ic.getRoot().getId());
        cmap.put("subject",sub);
        cmap.put("date",crt);
        cmap.put("author",aut);
        cmap.put("bodyType", bdyTp );
        if( ic.getIcFlag() != null ){
            cmap.put("flagId",ic.getIcFlag().getId());
            cmap.put("flagName",ic.getIcFlag().getName());
        }

        if( bflag ){
            String bdy = ic.getBody();
            cmap.put("body", bdy );
        }
        return cmap;
    }

    //--------------------------------------------------------------------------

    private Map buildIcScoreMap( IcScore ic ){

        Map<String,Object> cmap = new HashMap<String,Object>();
                
        String crt = String.format( "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", ic.getCrt() );
        String aut = ic.getOwner().getLogin();        

        cmap.put("id",ic.getId() );
        cmap.put("aid",ic.getId() );
        cmap.put("root",ic.getRoot().getId());
        cmap.put("date",crt);
        cmap.put("author",aut);
        cmap.put("name",ic.getName());
        cmap.put("value",ic.getValue());        
        return cmap;        

    }
    
}
