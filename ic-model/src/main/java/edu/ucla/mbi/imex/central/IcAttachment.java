package edu.ucla.mbi.imex.central;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * Attachment  
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

       
import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;



public class IcAttachment extends AttachedDataItem {

    public static final int TEXT = 0;
    public static final int MIF25 = 1;
    public static final int MITAB = 2;
    
    public IcAttachment() {}
   
    public IcAttachment( User owner, IcPub root, String label, String body ){
        this( owner, root, label, body, "TEXT" );
    }

    public IcAttachment( User owner, IcPub root, String label,
                         String body, String bodyTp ){

        this.setOwner( owner );
        this.setRoot( root );

        this.setLabel( label );
        this.setBody( body );

        if( bodyTp != null ){
            if( bodyTp.equalsIgnoreCase( "TEXT" ) ){
                this.setText();
            }
            
            if( bodyTp.equalsIgnoreCase( "MIF25" ) ){
                this.setMif25();
            }
         
            if( bodyTp.equalsIgnoreCase( "MITAB" ) ){
                this.setMitab();
            }
        }
    }

    //--------------------------------------------------------------------------
    
    String label = "";
    
    public void setLabel( String label ) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
   
    // NOTE: alias for the label field
    // -------------------------------

    public void setSubject( String subject ) {
        this.label = subject;
    }

    public String getSubject() {
        return label;
    }

    //--------------------------------------------------------------------------

    String body = null;

    public void setBody( String body ){
        this.body = body;
    }

    public String getBody(){
        return body;
    }

    //--------------------------------------------------------------------------

    int format = 0;   // 0 - TEXT; 1 - MIF25; 2 - MITAB28; 

    public void setFormat( int format ){
        this.format = format;
    }

    public int getFormat(){
        return this.format;
    }

    //--------------------------------------------------------------------------

    public void setText(){
        format = IcAttachment.TEXT;
    }

    public void setMif25(){
        format = IcAttachment.MIF25;
    }

    public void setMitab(){
        format = IcAttachment.MITAB;
    }

    public boolean isText(){
        return (format == IcAttachment.TEXT);
    }

    public boolean isMif25(){
        return (format == IcAttachment.MIF25);
    }

    public boolean isMitab(){
        return (format == IcAttachment.MITAB);
    }
        
    public String getDataType(){
        if( format == IcAttachment.TEXT ) return "TEXT";
        if( format == IcAttachment.MIF25 ) return "MIF25";
        if( format == IcAttachment.MITAB ) return "MITAB";
        return "TEXT";
    }

    //--------------------------------------------------------------------------

    public IcFlag getIcFlag(){
        return (IcFlag) this.getState();
    }

    public IcAttachment setIcFlag( IcFlag flag ){
        this.setState( flag );
        return this;
    }


}
