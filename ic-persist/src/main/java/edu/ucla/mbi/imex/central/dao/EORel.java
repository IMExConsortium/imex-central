package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * EORel:  event-observer relationship
 *
 *=========================================================================== */

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.imex.central.*;

public class EORel {

    int id;

    String event = null;
    User observer = null;

    public EORel(){};
    
    public EORel( String event, User observer){
        this.event = event;
        this.observer = observer;
    }

   
    //--------------------------------------------------------------------------
    
    public void setId( int id ){
        this.id = id;
    }

    public int getId(){
        return id;
    }
    //--------------------------------------------------------------------------
    
    public void setObserver( User observer ){
        this.observer = observer;
    }

    public User getObserver(){
        return observer;
    }

    //--------------------------------------------------------------------------
    
    public void setEvent( String event ){
        this.event = event;
    }
    
    public String getEvent(){
        return event;
    }

}