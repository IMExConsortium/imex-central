package edu.ucla.mbi.imex.central;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcPub - a traceable publication
 *
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.util.data.*;

public class IcPub extends Publication {
    
    IcPub() { }
    
    public IcPub( Publication pub ) {

        this.setPmid( pub.getPmid() );
        
        this.setTitle( pub.getTitle() );
        this.setAuthor( pub.getAuthor() );
        this.setAbstract( pub.getAbstract() );
        
        this.setExpectedPubDate( pub.getExpectedPubDate() );
        this.setPubDate( pub.getPubDate() );
        this.setReleaseDate( pub.getReleaseDate() );

        this.setOwner( pub.getOwner() );
        this.setSource( pub.getSource() );
        this.setState( pub.getState() );
        
        this.setContactEmail( pub.getContactEmail() );

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( new Date() );
        this.setCrt( calendar );
    }

    //private String imexId = "";
    
    //public void setImexId( String imexId ) {
    //    this.imexId = imexId;
    //}

    private IcKey key;

    public IcKey getIcKey() {
        return key;
    }

    public void setIcKey( IcKey key ) {
        this.key = key;
    }



    public String getImexId() {

        if( key == null ) {
            return "N/A";
        }
        
        return key.getAccession();
    }

    //---------------------------------------------------------------------    

    private Set<ImexEntry> imexEntrySets;

    public void setImexSets( Set<ImexEntry> sets ){
        imexEntrySets = sets;
    }

    public Set<ImexEntry>  getImexSets(){
        return imexEntrySets;
    }

}

