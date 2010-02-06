package  edu.ucla.mbi.imex.central;

/* =============================================================================
 # $HeadURL::                                                                  $
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #                                                                             $
 # IcKey                                                                       $
 #                                                                             $
 #=========================================================================== */

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
 
//import org.hibernate.*;
//import org.hibernate.cfg.*;

public class IcKey{
    
    private long id = -1;
    private IcKeyspace kspace = null;
    private long kval = -1;
    private int status = -1;
    private Timestamp created = null;
    private Timestamp updated = null;

    // setters
    //--------
    
    private IcKey setId( long id ) {
	this.id = id;
	return this;
    }

    public IcKey setKeyspace( IcKeyspace kspace ) {
	this.kspace = kspace;
	return this;
    }

    public IcKey setKeyvalue( long value ) {
	this.kval = value;
	return this;
    }
    
    public IcKey setStatus( int status ) {
        this.status = status;
	return this;
    }    

    public IcKey setCreated( Timestamp time ) {
        this.created = time;
	return this;
    }    
    public IcKey setUpdated( Timestamp time ) {
        this.updated = time;
	return this;
    }    
    
    // getters
    //--------

    public long getId() {
       	return this.id;
    }

    public IcKeyspace getKeyspace() {
       	return this.kspace;
    }
    public long getKeyvalue() {
       	return this.kval;
    }

    public int getStatus() {
       	return this.status;
    }

    public Timestamp getCreated() {
	return this.created;
    }

    public Timestamp getUpdated() {
	return this.created;
    }

    // accession
    
    public String getAccession() {
        return kspace.getPrefix() + kval + kspace.getPostfix();
    }
    
}
