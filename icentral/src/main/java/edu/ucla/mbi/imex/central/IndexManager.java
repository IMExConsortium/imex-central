package edu.ucla.mbi.imex.central;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # IndexManager - businness logic of indexer  
 #                 
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;

import java.util.*;

import java.io.*;
import java.util.regex.PatternSyntaxException;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.*;

//import org.elasticsearch.client.*;
//import org.elasticsearch.action.index.*;
//import org.elasticsearch.common.xcontent.*;

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.dao.*;

public class IndexManager {
    
    private static Log log; 

    public IndexManager() {
        log = LogFactory.getLog( this.getClass() );
        log.info( "IndexManager: creating manager" );
    }

    //---------------------------------------------------------------------
    //  TracContext
    //--------------

    private TracContext tracContext;
    
    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }
    
    public TracContext getTracContext() {
        return this.tracContext;
    }

    //---------------------------------------------------------------------
    //  WorkflowContext
    //-----------------
    
    private IcWorkflowContext wflowContext;

    public void setWorkflowContext( IcWorkflowContext context ) {
        this.wflowContext = context;
    }

    public IcWorkflowContext getWorkflowContext() {
        return this.wflowContext;
    }

    //---------------------------------------------------------------------
    //  UserContext
    //--------------
    
    private UserContext userContext;

    public void setUserContext( UserContext context ) {
        this.userContext = context;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }

    //---------------------------------------------------------------------
    // KeyContext
    //-----------

    private KeyspaceContext keyspaceContext;

    public void setKeyspaceContext( KeyspaceContext context ) {
        this.keyspaceContext = context;
    }

    public KeyspaceContext getKeyspaceContext() {
        return this.keyspaceContext;
    }
    
    //---------------------------------------------------------------------
    // ElasticSearch Index Url
    //------------------------

    private String esIndexUrl;

    public void setEsIndexUrl( String url ) {
        this.esIndexUrl = url;
    }

    public String getEsIndexUrl() {
        if( esIndexUrl == null ){
            esIndexUrl ="";
        }
        return esIndexUrl;
    }

    
    //---------------------------------------------------------------------
    //  ElasticSearch Active Flag
    //---------------------------

    private boolean esIndexActive = false;;

    public void setEsIndexActive( boolean url ) {
        this.esIndexActive = url;
    }

    public boolean getEsIndexActive() {
        return esIndexActive;
    }

    public boolean isIndexActive() {
        return esIndexActive;
    }

    
    //---------------------------------------------------------------------
    
    boolean debug = false;

    public boolean getDebug() {
        return debug;
    }
    
    public void setDebug( boolean debug ) {
        this.debug = debug;
    }
    
    //---------------------------------------------------------------------

    public void initialize(){
        //Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryManager: initializing" );
    }

    public void cleanup(){
        //Log log = LogFactory.getLog( this.getClass() );
        log.info( "EntryManager: cleanup called" );
    }

    //---------------------------------------------------------------------
    // Operations
    //---------------------------------------------------------------------
    // index publication record
    //-------------------------
    
    public void indexIcPub( int id ) {
        
        //Log log = LogFactory.getLog( this.getClass() );
        log.info( " index pub -> id=" + id );
        
        try{
            IcPub pub =  (IcPub) tracContext.getPubDao()
                .getPublication( id );

            List<AttachedDataItem> adiList =  tracContext.getAdiDao()
                .getAdiListByRoot( pub );

            String jpub = icpub2json( pub, adiList );
                       
            if( ! getEsIndexUrl().equals("") ){
                esIndex("publication", id, jpub);
            }

            log.info( "DONE" );
        } catch( Exception ex ){ 
            ex.printStackTrace();
            log.info( "FAILED" );
            //return null;
        }
    }


    public List<Integer> getIcPubList( int count ){
        
        
        
        return null;
    }

    public List<Integer> getLastIcPub( int count ){

        List<Integer> resList = new ArrayList<Integer>();

        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = getEsIndexUrl() + "/publication/_search?size="+
                + count
                +"&_source=id,timestamp&sort=timestamp:asc";
            log.debug("EsIndex URL=" + url);
            HttpGet get = new HttpGet(url);

            get.addHeader("Accept", "application/json");
            get.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            
            CloseableHttpResponse httpResponse = httpclient.execute( get );
            int statusCode = httpResponse.getStatusLine().getStatusCode(); 
            log.debug( "Status: " + statusCode );
            HttpEntity content = httpResponse.getEntity();
            BufferedReader in = new BufferedReader(new InputStreamReader(content.getContent()));
            String line = null;
            
            StringBuffer jdoc=new StringBuffer();;
            while((line = in.readLine()) != null) {
                //log.debug( "Response: " + line );
                jdoc.append(line);
            }
            
            httpclient.close();

            JSONObject doc = new JSONObject( jdoc.toString() );                        
            JSONObject hits = (JSONObject) doc.get("hits");
            JSONArray hitList =  hits.getJSONArray("hits"); 
            for( int ih = 0; ih < hitList.length(); ih++ ){
                JSONObject hit = (JSONObject) hitList.get(ih);
                JSONObject src = (JSONObject) hit.get("_source");
                resList.add(new Integer((String)src.get("id")));
            }
            log.debug("res(i):" + resList);            
            return resList;
        }catch( Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    private int esIndex( String type, int id, String jdoc){ 
        
        try{

            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = getEsIndexUrl() + "/publication/" + id;
            log.debug("EsIndex URL=" + url);
            HttpPut put = new HttpPut(url);

            put.addHeader("Accept", "application/json");
            put.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                
            StringEntity entity = new StringEntity( jdoc, "UTF-8" );                
            put.setEntity( entity );
 
            CloseableHttpResponse httpResponse = httpclient.execute( put );
            int statusCode = httpResponse.getStatusLine().getStatusCode(); 
            log.debug( "Status: " + statusCode );
            HttpEntity content = httpResponse.getEntity();
            BufferedReader in = new BufferedReader(new InputStreamReader(content.getContent()));
            String line = null;
            while((line = in.readLine()) != null) {
                log.debug( "Response: " + line );
            }

            httpclient.close();

            return statusCode;
        }catch( Exception ex){
            return -1;
        }
    }

    //---------------------------------------------------------------------
    // index attached data record
    //---------------------------

    public void indexIcAdi( int id ) {

        //Log log = LogFactory.getLog( this.getClass() );
        log.info( " get adi -> id=" + id );
        
        try{

            //IcPub pub =  (IcPub) tracContext.getPubDao()
            //    .getPublication( id );

            //List<AttachedDataItem> adiList=  tracContext.getAdiDao()
            //    .getAdiListByRoot( pub );
            
        } catch( Exception ex ) {
            //return null;
        }

    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------


    public String icpub2json( IcPub ipub, List<AttachedDataItem> adiList ){

        //Log log = LogFactory.getLog( this.getClass() );
        
        Map<String,Object> mpub = new HashMap<String,Object>();

        mpub.put( "partner", "" );

        if( ipub.getPmid() != null && ! ipub.getPmid().equals("") ){
            mpub.put("pmid",ipub.getPmid());
        }
        if( ipub.getDoi() != null && ! ipub.getDoi().equals("") ){
            mpub.put("doi",ipub.getDoi());
        }

        if( ipub.getJournalSpecific() != null 
            && ! ipub.getJournalSpecific().equals("") ){
            mpub.put( "jid", ipub.getJournalSpecific() );
        }

        if( ipub.getId() != null ){
            mpub.put( "id", ipub.getId().toString() );
        }

        if( ipub.getImexId() != null 
            && ! ipub.getImexId().equals("") ){
            mpub.put( "imexid", ipub.getImexId() );
        }

        if( ipub.getTitle() != null 
            && ! ipub.getTitle().equals("") ){
            mpub.put("title",ipub.getTitle());
        }

        if( ipub.getAuthor() != null 
            && ! ipub.getAuthor().equals("") ){
            mpub.put("author",ipub.getAuthor());
        }

        if( ipub.getAbstract() != null 
            && ! ipub.getTitle().equals("") ){
            mpub.put("abstract",ipub.getTitle());
        }


        if( ipub.getSource() !=null 
            && ipub.getSource() instanceof Journal ){
            
            // journal record id
            mpub.put( "journal_id", 
                      ipub.getSource().getId() );
            
            // nlmid  
            mpub.put( "journal_nlmid", 
                      ((Journal) ipub.getSource()).getNlmid() );
            
            // title 
            mpub.put("journal_title", 
                     ((Journal) ipub.getSource()).getTitle() );
            
        }

        if( ipub.getYear() != null 
            && ! ipub.getYear().equals("") ){

            mpub.put("year",ipub.getYear());
        }

        if( ipub.getVolume() != null 
            && ! ipub.getVolume().equals("") ){

            mpub.put("volume",ipub.getVolume());
        }

        if( ipub.getIssue() != null 
            && ! ipub.getTitle().equals("") ){

            mpub.put("issue",ipub.getIssue());
        }

        if( ipub.getPages() != null 
            && ! ipub.getPages().equals("") ){

            mpub.put("pages",ipub.getPages());
        }

        if( ipub.getExpectedPubDateStr() != null 
            && ! ipub.getExpectedPubDateStr().equals("")
            && ! ipub.getExpectedPubDateStr().equals("0000/00/00")
            ) {

            mpub.put("xpubdate",ipub.getExpectedPubDateStr());
        }

        if(ipub.getPubDateStr() != null 
           && ! ipub.getPubDateStr().equals("")
           && ! ipub.getPubDateStr().equals("0000/00/00")
           ){

            mpub.put("pubdate",ipub.getPubDateStr());
        }

        if(ipub.getReleaseDateStr() != null 
           && ! ipub.getReleaseDateStr().equals("")
           && ! ipub.getReleaseDateStr().equals("0000/00/00")
           ) {

            mpub.put("reldate",ipub.getReleaseDateStr());
        }
        
        if( ipub.getState() != null ){

            mpub.put( "state",ipub.getState().getName() );
        }

        if(ipub.getStage() != null ){

            mpub.put( "stage",ipub.getStage().getName() );
        }

        if( ipub.getOwner() != null ){

            mpub.put( "owner", ipub.getOwner().getLogin() );
        }

        if( ipub.getContactEmail() != null 
            && ! ipub.getContactEmail().equals("") ){

            mpub.put( "contact", ipub.getContactEmail() );
        }
        
        if(ipub.getCreateDateString() != null 
           && ! ipub.getCreateDateString().equals("----/--/--")
           && ! ipub.getCreateDateString().equals("0000/00/00") ) {

            mpub.put("cretime",ipub.getCreateDateString());

        }

        if( ipub.getModDateStr() != null 
            && ! ipub.getModDateStr().equals("----/--/--")
            && ! ipub.getModDateStr().equals("0000/00/00") ) {

            mpub.put( "modtime", ipub.getModDateStr() );            
            
        }

        if( ipub.getActDateStr() != null 
            && ! ipub.getActDateStr().equals("----/--/--")
            && ! ipub.getActDateStr().equals("0000/00/00") ) {
            
            mpub.put( "acttime", ipub.getActDateStr() );

        }
        
        
        // add admin users
        //----------------

        if( ipub.getAdminUsers() != null ){

            List aul = new ArrayList();

            for( Iterator iau = ipub.getAdminUsers().iterator(); 
                 iau.hasNext(); ){

                User cau = (User) iau.next();
                
                aul.add( cau.getLogin() );
                if( cau.getFirstName() != null 
                    && ! sanitize( cau.getFirstName() ).equals("") 
                    && cau.getLastName() != null 
                    && ! sanitize( cau.getLastName() ).equals("") ){
                    
                    aul.add( cau.getFirstName() + " " + cau.getLastName() );
                }
                
                if( cau.getEmail() != null
                    && ! sanitize( cau.getEmail() ).equals("") ){
                    
                    aul.add( cau.getEmail());
                }
            }

            if( aul.size() > 0){
                mpub.put( "curator", aul );
            }            
        }
        
        // add admin groups 
        //-----------------

        if( ipub.getAdminGroups() != null ){

            List agl = new ArrayList();
            
            for( Iterator iag = ipub.getAdminGroups().iterator(); 
                 iag.hasNext(); ){

                Group cag = (Group) iag.next();
            
                if( cag.getLabel() != null
                    && ! sanitize( cag.getLabel() ).equals("") ){    
                    agl.add( cag.getLabel());
                }
              
                if( cag.getName() != null
                    && ! sanitize( cag.getName() ).equals("") ){

                    agl.add( cag.getName());                    


                    // partner
                    //--------
                    
                    if( cag.getName().indexOf("Database") > 0 ){ 
                        mpub.put( "partner", sanitize( cag.getLabel() ) );
                    }
                }                
            }

            if( agl.size() > 0){
                mpub.put( "curator_group", agl );
            }
        }
        
        // add attached stuff
        //-------------------

        icadi2json( mpub, adiList);
        
        // add timestamp
        //----------------
        
        mpub.put( "timestamp", 
                  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));

        try{
            JSONObject  jpub = new JSONObject( mpub );
            return jpub.toString(2);
        } catch( JSONException ex){
            ex.printStackTrace();
            return null;
        }   
    }

    public  void icadi2json( Map mpub, List<AttachedDataItem> adiList ){

        
        
        // comment list 
        //-------------

        if( mpub.get("comment") == null ){
            mpub.put( "comment", new ArrayList()) ;
        }
        List comment = (List) mpub.get( "comment"); 

        // attachement list
        //-----------------

        if( mpub.get("attachement") == null ){
            mpub.put( "attachement", new ArrayList() );
        }
        List attachement = (List) mpub.get( "attachement"); 
        
        
        // score list
        //-----------

        if( mpub.get("score") == null ){
            mpub.put( "score", new ArrayList() );
        }

        List score = (List) mpub.get( "score"); 
        Map<String,IcScore> smap = new HashMap<String,IcScore>();
        
        GregorianCalendar  laststamp =  new GregorianCalendar(1970, 1, 1);
        
        for( Iterator<AttachedDataItem> i = adiList.iterator(); i.hasNext(); ){

            AttachedDataItem adi = i.next();
            Map<String, Object> adim = new HashMap<String, Object>();

            if( adi.getCrt().compareTo(laststamp ) > 0 ){
                laststamp = adi.getCrt();
            }
            
            if( adi instanceof IcComment){
                
                IcComment c = (IcComment) adi;

                adim.put("subject", c.getSubject());

                if( c.getBody() != null &&  c.getBody() != ""){
                    adim.put("body", c.getBody());
                }

                adim.put("author", c.getOwner().getLogin() );
                adim.put("date", c.getCreateDateString() );

                if( c.getIcFlag() !=null ){
                    adim.put("flag", c.getIcFlag().getName() );
                }

                comment.add(adim);
                continue;
            }

            if( adi instanceof IcAttachment){
                
                IcAttachment c = (IcAttachment) adi;

                adim.put("subject", c.getSubject());

                if( c.getBody() != null &&  c.getBody() != ""){
                    adim.put("body", c.getBody());
                }

                switch( c.getFormat() ){
                case IcAttachment.MIF25: 
                    adim.put("format", "MIF25" );
                    break;

                case IcAttachment.MITAB: 
                    adim.put("format", "MITAB" );
                    break;
                    
                default:
                    adim.put("format", "TEXT" );
                }

                adim.put("author", c.getOwner().getLogin() );
                adim.put("date", c.getCreateDateString() );

                if( c.getIcFlag() !=null ){
                    adim.put("flag", c.getIcFlag().getName() );
                }
                
                attachement.add(adim);                
                continue;
            }

            if( adi instanceof IcScore){
                
                log.info( " adding IcScore " + adi );                

                IcScore cs = (IcScore) adi;
                
                //String name = cs.getName();
                //GregorianCalendar tstamp = cs.getCrt();

                if( ! smap.containsKey( cs.getName() ) ){
                    smap.put( cs.getName(), cs );
                } else{
                    if( cs.getCrt().after( smap.get( cs.getName() ).getCrt() ) ){

                        smap.put( cs.getName(), cs );
                    }
                }
            }            
        }

        log.info( " smap size " + smap.size() );
        
        if( smap.size() > 0 ){
            
            for( Iterator<String> ski = smap.keySet().iterator(); 
                 ski.hasNext(); ){
                
                Map csm = new HashMap();
            
                IcScore cscr = smap.get( ski.next() ); 
            
                csm.put("name", cscr.getName() );
                csm.put("value", cscr.getValue() );
                csm.put("author", cscr.getOwner().getLogin() );
                csm.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                        .format(((GregorianCalendar) cscr.getCrt()).getTime()));

                ((List)mpub.get("score")).add( csm );
            }
            
        }
    }

    //--------------------------------------------------------------------------
    //---------------------------- ******* -------------------------------------
    
    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize,
                                                 String skey, 
                                                 boolean asc ){
    
        return getPublicationList( firstRecord, blockSize,
                                   skey, asc, null );
        
    }
    
    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize,
                                                 String skey, boolean asc,
                                                 Map<String,String> flt ){

        Log log = LogFactory.getLog( this.getClass() );
        
        String query = buildEsQuery( firstRecord, blockSize,
                                     skey, asc, flt  );
        
        List<Publication> plst = new ArrayList<Publication>();
        
        try{
            List<Integer> idList = getPubIdList( query );
            
            for(Iterator<Integer> ii = idList.iterator(); ii.hasNext(); ){
                IcPub iPub =  (IcPub) tracContext.getPubDao()
                    .getPublication( ii.next() );
                
                plst.add( iPub );
            }
            
        }catch( Exception ex){
            //ex.printStackTrace();
        }
        
        return plst;
    }

    //--------------------------------------------------------------------------

    public long getPublicationCount(){
    
        return getPublicationCount( null ) ;
    }

    
    public long getPublicationCount( Map<String,String> flt ){
        
        String query = buildEsQuery( 0, 1, null, false, flt  );
        
        return getPubIdCount( query );
        
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    private String buildEsQuery( int firstRecord, 
                                 int blockSize,
                                 String skey, 
                                 boolean asc,
                                 Map<String,String> flt  ){

        Log log = LogFactory.getLog( this.getClass() );
        
        // valid sort keys:
        
        // +pmid, +imex +author +crtime mdtime (mod time)  

        String qtmp = "{\"query\":{\"bool\":{\"must\":{\"match_all\":{}}," +
            "\"filter\":[%%FLT%%]}},\"sort\":[%%SRT%%],"+
            "\"_source\":[\"id\"]," +
            "\"from\": %%FR%%,\"size\": %%SZ%%}";

        //String qscr = "double m = 0; for( obj in params._source.score){ " +
        //" if( obj.name=='%%SNAME%%'){ m = obj.value;}} return m";
        

        String qsrt_simple = "{\"%%SNAME%%\":\"%%SDIR%%\"}";

        String qsrt_script = "{\"_script\" : {\"type\" : \"string\"," +
            "\"script\" : {\"lang\": \"painless\", \"source\": " + 
            "\"double m = 0; for(obj in params._source.score){ " +
            " if( obj.name=='%%SNAME%%'){ m = obj.value;}} return m\" " +
            "},\"order\" : \"%%SDIR%%\"}}";

        String fromStr = "0";
        String sizeStr = "25";
        String sortStr = "";
        String fltrStr = "";
        
        // start record
        //-------------

        fromStr = String.valueOf( firstRecord );

        // block size
        //-----------

        sizeStr = String.valueOf( blockSize );
        
        // sort field/direction
        //---------------------
        
        String sortFld = skey;

        log.debug( "skey :" + skey + ":");
        
        if( sortFld != null ){
            if( skey.equals("id")){
                sortStr = "{\"" + skey + "\":\"%%SDIR%%\"}";
            } else if( skey.equals("imex") ){                    
                sortStr = "{\"" + skey + "id.keyword\":\"%%SDIR%%\"}";
            } else if( skey.equals("modDate") ){                    
                sortStr = "{\"modtime\":\"%%SDIR%%\"}"; 
            } else if( skey.equals("actDate") ){                    
                sortStr = "{\"modtime\":\"%%SDIR%%\"}";
            } else if( skey.equals("crt") ){                    
                sortStr = "{\"cretime\":\"%%SDIR%%\"}";
                
            } else if( skey.endsWith(".value") ){
                
                sortStr = qsrt_script.replaceAll( "%%SNAME%%",
                                                  skey.replaceAll(".value",""));
                
            } else {
                sortStr = "{\"" + skey + ".keyword\":\"%%SDIR%%\"}";
            }
            
            log.debug( "sortStr: " + sortStr);
            
            if( asc ){
                sortStr =  sortStr.replaceAll("%%SDIR%%", "asc");
            } else {
                sortStr =  sortStr.replaceAll("%%SDIR%%", "desc");
            }
            
            log.debug( "sortStr: " + sortStr);

        }

        // filters
        //--------
        
        if( flt != null && flt.size() > 0 ){
            
            StringBuffer sb = new StringBuffer(" ");

            for( Iterator ii = flt.keySet().iterator(); ii.hasNext(); ){
                
                String key = (String) ii.next();
                String value = (String) flt.get(key);
                
                if( key != null && key.equals("status")){
                    key = "state";
                }

                if( key != null && key.equals("jid")){
                    key = "journal_id";
                }
                
                if( value != null && !value.equals("")){
                    sb.append( "{\"term\":{\""+key+"\":\""+value.toLowerCase()+"\"}},");
                }
            }
            fltrStr = sb.substring(0, sb.length()-1 );            
            log.debug("fltrStr:" + fltrStr +":");            
        }
        
        String query = qtmp.replace("%%FR%%",fromStr).replace("%%SZ%%",sizeStr)
            .replace("%%SRT%%",sortStr).replace("%%FLT%%",fltrStr);

        log.debug("query:" + query);

        
        return query;
    }    

    //---------------------- ******    -----------------------------------------
    
    private String esQueryResult( String query ){

        Log log = LogFactory.getLog( this.getClass() );
        
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = getEsIndexUrl() 
                + "/publication/_search";  
            
            log.debug("EsIndex URL=" + url);
            log.debug("EsIndex query=" + query);
            HttpPost post = new HttpPost(url);
            
            post.addHeader("Accept", "application/json");
            post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            
            StringEntity entity = new StringEntity( query, "UTF-8" );                
            post.setEntity( entity );


            CloseableHttpResponse httpResponse = httpclient.execute( post );
            int statusCode = httpResponse.getStatusLine().getStatusCode(); 
            log.debug( "Status: " + statusCode );
            HttpEntity cnt = httpResponse.getEntity();
            BufferedReader in 
                = new BufferedReader(new InputStreamReader(cnt.getContent()));
            String line = null;
            
            StringBuffer jdoc=new StringBuffer();;
            while( (line = in.readLine()) != null) {
                jdoc.append(line);
            }
            
            httpclient.close();
            
            return jdoc.toString();

        } catch(Exception ex){
        }
            
        return null;

    }
    
    //----------------------------------------------------------------------------

    private List<Integer> getPubIdList( String query ){
        
        Log log = LogFactory.getLog( this.getClass() );
        List<Integer> idList = new ArrayList<Integer>();
        
        try{
            JSONObject doc = new JSONObject( esQueryResult( query ) );                        
            JSONObject hits = (JSONObject) doc.get("hits");
            JSONArray hitList =  hits.getJSONArray("hits"); 
            for( int ih = 0; ih < hitList.length(); ih++ ){
                JSONObject hit = (JSONObject) hitList.get(ih);
                JSONObject src = (JSONObject) hit.get("_source");
                idList.add(new Integer((String)src.get("id")));
            }
            log.debug("res(i):" + idList);
        } catch(Exception ex){
        }
        return idList;        
    }

    //----------------------------------------------------------------------------
    
    private int getPubIdCount( String query ){
        Log log = LogFactory.getLog( this.getClass() );
        try{
            JSONObject doc = new JSONObject( esQueryResult( query ) );                        
            JSONObject hits = (JSONObject) doc.get("hits");
             
            return  hits.getInt("total"); 
             
        } catch(Exception ex){
        }
        return 0;        
    }
    
    //----------------------------------------------------------------------------
    
    private String sanitize( String str ){

        if( str != null ){
            try{
                str = str.replaceAll("^\\s+","");
                str = str.replaceAll("\\s+$","");
            } catch( Exception ex ){
                // should not happen   
            }
        } else {
            str = "";
        }
        return str;
    }
}
