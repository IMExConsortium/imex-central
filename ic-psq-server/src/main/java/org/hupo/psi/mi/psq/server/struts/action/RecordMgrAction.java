package  org.hupo.psi.mi.psq.server.struts.action;

import java.io.InputStream;
import javax.servlet.ServletContext;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.util.ServletContextAware;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ucla.mbi.util.JsonContext;

import org.hupo.psi.mi.psq.server.data.*;
import org.hupo.psi.mi.psq.server.data.derby.*;

public class RecordMgrAction extends ActionSupport
    implements ServletContextAware{
    
    JsonContext psqContext;
    
    //--------------------------------------------------------------------------
    
    public void setPsqContext( JsonContext context ){
        psqContext = context;
    }

    //--------------------------------------------------------------------------
    
    public JsonContext getPsqContext(){
        return psqContext;
    }
    
    //--------------------------------------------------------------------------

    private void initialize() {
        initialize( false );
    }

    //--------------------------------------------------------------------------

    private void initialize( boolean force) {
        
        if ( getPsqContext().getJsonConfig() == null || force ) {

            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing psq context" );
            String jsonPath =
                (String) getPsqContext().getConfig().get( "json-config" );
            log.info( "JsonPsqDef=" + jsonPath );

            if ( jsonPath != null && jsonPath.length() > 0 ) {

                String cpath = jsonPath.replaceAll("^\\s+","" );
                cpath = jsonPath.replaceAll("\\s+$","" );
                
                try {
                    InputStream is =
                        servletContext.getResourceAsStream( cpath );
                    getPsqContext().readJsonConfigDef( is );

                } catch ( Exception e ){
                    log.info( "JsonConfig reading error" );
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    String op="";
    String mitab="";
    String pid="";
    
    public void setOp( String op ){
        this.op = op;
    }

    public String getPid(){
        return pid;
    }
    
    public void setPid(String pid){
        this.pid=pid;
    }

    public void setMitab(String mitab){
        this.mitab=mitab;
    }
    
    public String getMitab(){
        return mitab;
    }

    //--------------------------------------------------------------------------

    RecordDao  rdao = null;

    public void setRecordDao( RecordDao dao ){
        this.rdao= dao;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public String execute() throws Exception {

        initialize();
        System.out.println("OP="+ op);
        
        if( op != null && op.equals( "add" ) ){
            String[] pida = null;
            String[] mtba = null;
            if( pid != null && mitab != null ){
                try{
                    pida = pid.split("\\n");
                    mtba = mitab.split("\\n");            
                }catch(Exception ex){
                }
            }

            if( pida != null && mtba!= null ){

                for( int i=0; i<pida.length; i++ ){
                    rdao.addRecord( pida[i], mtba[i], null );
                }
            }
        }

        return ActionSupport.SUCCESS;
    }

    //--------------------------------------------------------------------------
    // ServletContextAware interface implementation
    //---------------------------------------------
    private ServletContext servletContext;

    public void setServletContext( ServletContext context){
        this.servletContext = context;
    }

    public ServletContext getServletContext(){
        return servletContext;
    }

}
