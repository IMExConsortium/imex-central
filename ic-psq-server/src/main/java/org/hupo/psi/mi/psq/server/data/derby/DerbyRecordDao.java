package org.hupo.psi.mi.psq.server.data.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Clob;
import java.sql.PreparedStatement;

import java.util.*;

import edu.ucla.mbi.util.JsonContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.server.data.*;

public class DerbyRecordDao implements RecordDao{

    Connection dbcon = null;

    public DerbyRecordDao(){
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch( Exception ex ){
            ex.printStackTrace();
        }
    }

    JsonContext context = null;
    
    public void setContext( JsonContext context ){
        this.context = context;
    }
    
    private void connect(){

        if( dbcon == null ){

            Log log = LogFactory.getLog( this.getClass() );
            log.info( "DerbyRecordDao(connect): ");
            
            if( context != null && context.getJsonConfig() != null ){
                try{
                    String derbydb = (String) context.getJsonConfig().get("derby-db");
                    dbcon = DriverManager.getConnection("jdbc:derby:" + derbydb + ";create=true");                
                                    
                } catch( Exception ex ){
                    ex.printStackTrace();
                }
                
                try{
                    Statement st = dbcon.createStatement();
                    st.setQueryTimeout(5);
                    ResultSet rs = st.executeQuery( " select count(*) from record" );
                    while( rs.next() ){
                        log.info( "   record count= " + rs.getInt(1) );
                    }
                } catch( Exception ex ){
                    // missing table ?
                    log.info( "   creating record table" ); 
                    create();
                }
            }
        }
    }
    
    private void create(){
        try{
            Statement statement = dbcon.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            
            statement.executeUpdate( "create table record (id varchar(256), mitab clob, mif clob)");
            statement.executeUpdate( "create index record on record (id)" );
        } catch( Exception ex ){
            ex.printStackTrace();
        }
    }

    public void shutdown(){
        if( dbcon != null ){      
            try{
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch( Exception ex ){
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "DerbyRecordDao(shutdown): " + ex);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------


    public void setRecord( String id, String mitab, String mif ){




    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------


    public void addRecord( String id, String record, String format ){
        connect();
        try{
            PreparedStatement pst = dbcon
                .prepareStatement( "insert into record (id, mitab, mif) values (?,?,?)" );
            
            pst.setString( 1, id );
            if( format!=null && format.equals("mif") ){
                pst.setString( 2, "" );
                pst.setString( 3, record );
            } else {
                pst.setString( 2, record );
                pst.setString( 3, "" );
            }
            pst.executeUpdate();
             
        }catch( Exception ex ){
            ex.printStackTrace();
        }

    }

    //--------------------------------------------------------------------------

    public String getRecord( String id, String format ){
        connect();
        String record = "";

        try{
            PreparedStatement pst = dbcon
                .prepareStatement( "select id, mitab, mif from record where id = ?" );
            
            pst.setString( 1, id );
            ResultSet rs =  pst.executeQuery();
            while( rs.next() ){
                Clob rc = rs.getClob( 2 );
                record = rc.getSubString( 1L, new Long(rc.length()).intValue() );
            } 
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        return record;
    }

    //--------------------------------------------------------------------------
    
    public List<String> getRecordList( List<String> id, String format ){
        
        connect();
        List<String> recordList = new ArrayList<String>();
        
        try{
            PreparedStatement pst = dbcon
                .prepareStatement( "select id, mitab, mif from record where id = ?" );
            
            for( Iterator<String> i = id.iterator(); i.hasNext(); ){
                pst.setString( 1, i.next() );
                ResultSet rs =  pst.executeQuery();
                
                while( rs.next() ){
                    Clob rc = rs.getClob(2);
                    String record = rc.getSubString( 1L, new Long(rc.length()).intValue() );
                    recordList.add( record );
                }
            }
        } catch( Exception ex ){
            ex.printStackTrace();
        }

        return recordList;
    }
}