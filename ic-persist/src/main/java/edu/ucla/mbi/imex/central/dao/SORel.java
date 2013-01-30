package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * SORel:  subject-observer relationship
 *
 *=========================================================================== */

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.imex.central.*;

public class SORel {

    int id;

    DataItem subject = null;
    User observer = null;

    public SORel(){};
    
    public SORel( DataItem subject, User observer){
        this.subject = subject;
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
    
    public void setSubject( DataItem subject ){
        this.subject = subject;
    }
    
    public DataItem getSubject(){
        return subject;
    }

}