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
        this.setDoi( pub.getDoi() );
        this.setJournalSpecific( pub.getJournalSpecific() );
        
        this.setTitle( pub.getTitle() );
        this.setAuthor( pub.getAuthor() );
        this.setAbstract( pub.getAbstract() );
        
        this.setExpectedPubDate( pub.getExpectedPubDate() );
        this.setPubDate( pub.getPubDate() );
        this.setReleaseDate( pub.getReleaseDate() );

        this.setOwner( pub.getOwner() );
        this.setSource( pub.getSource() );
        this.setState( pub.getState() );

        this.setActUser( pub.getOwner() );
        this.setModUser( pub.getOwner() );
        
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

    //--------------------------------------------------------------------------


    //--------------------------------------------------------------------------
    
    private Set<ImexEntry> imexEntrySets;

    public void setImexSets( Set<ImexEntry> sets ){
        imexEntrySets = sets;
    }

    public Set<ImexEntry>  getImexSets(){
        return imexEntrySets;
    }

    //--------------------------------------------------------------------------
    //  last activity
    //---------------
    //
    
    private GregorianCalendar actDate;
    
    public void setActDate( GregorianCalendar date ) {
        this.actDate = date;
    }
    
    public void setActDate() {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( new Date() );
        
        this.actDate = calendar;
    }
    


    public GregorianCalendar getActDate() {
        return actDate;
    }
    
    public String getActDateStr() {
        return getDateStr( actDate );
    }
    
    public void setActDateStr( String date ) {
        //this.releaseDate = date;
    }

    private User actUser;
    
    public void setActUser( User user ){
        this.actUser = user;

    }

    public User getActUser(){

        return actUser;
    }


    //--------------------------------------------------------------------------
    //  last modification
    //-------------------

    private GregorianCalendar modDate;
    
    public void setModDate( GregorianCalendar date ) {
        this.modDate = date;
    }

    public void setModDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( new Date() );

        this.modDate = calendar;
    }

    
    public GregorianCalendar getModDate() {
        return modDate;
    }
    
    public String getModDateStr() {
        return getDateStr( modDate );
    }

    public void setModDateStr( String date ) {
        //this.releaseDate = date;
    }
    
    private User modUser;

    public void setModUser( User user ){
        this.modUser = user;
    }

    public User getModUser(){
        return modUser;
    }


    //--------------------------------------------------------------------------

    private String getDateStr( GregorianCalendar date) {
        
        if( date != null ) {
            return dateStr( date.get(Calendar.YEAR),
                            date.get(Calendar.MONTH),
                            date.get(Calendar.DAY_OF_MONTH),

                            date.get(Calendar.HOUR_OF_DAY),
                            date.get(Calendar.MINUTE),
                            date.get(Calendar.SECOND)
                            );
        } else {
            return "0000/00/00 00:00:00";
        }
    }

    private String dateStr( int year, int month, int day,
                            int hour, int minute, int second) {

        
        StringBuffer date = new StringBuffer( Integer.toString( year ) );
        
        date.append( month < 9 ? "/0" : "/" );
        date.append( Integer.toString( month + 1 ) );

        date.append( day < 10 ? "/0" : "/" );
        date.append( Integer.toString( day ) );

        date.append( hour < 10 ? " 0" : " " );
        date.append( Integer.toString( hour ));

        date.append(  minute < 10 ? ":0" : ":");
        date.append( Integer.toString( minute ));

        date.append( second < 10 ? ":0" : ":");
        date.append( Integer.toString( second ));


        return date.toString();
    }




}

