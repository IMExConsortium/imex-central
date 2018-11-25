package edu.ucla.mbi.imex.central.index.es;


/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcESI:
 *
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;

import org.json.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;



public class IcESI  {


    public IcESI(){
    

    }

    public void index( Publication pub){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcESI->saveOrUpdate: id(pub)=" + pub.getId()  );

        if( pub instanceof IcPub ){
            //String jpub = icpub2json( (IcPub) pub );  
            //log.info( jpub );
        } else {
            log.info( "not IcPub instance" );
        }
     
    }

    public String saveOrUpdate( AttachedDataItem adi ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcESI->saveOrUpdate: id(adi)=" + adi.getId()  );
       
        String jadi = "";

        return jadi;

    }

    public void rebuild(boolean pub, boolean adi){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcESI->rebuild: pub=" + pub + "  adi=" + adi );        

    }
    

}
