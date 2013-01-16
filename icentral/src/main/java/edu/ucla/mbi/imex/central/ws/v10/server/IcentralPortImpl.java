package edu.ucla.mbi.imex.central.ws.v10.server;

/* #============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # IcentralPortImpl - ImexCentral ver 1.0 SOAP port implementation
 #
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.codec.binary.Base64;

import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.BindingProvider; 
import javax.xml.ws.Holder;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.xml.ws.soap.Addressing;
import javax.xml.ws.handler.MessageContext; 

import java.io.InputStream;
import java.util.*;
import javax.annotation.*;         


import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;
import edu.ucla.mbi.imex.central.ws.v10.*;

@WebService(serviceName = "ImexCentralService", 
            portName = "ImexCentralPort", 
            endpointInterface = "edu.ucla.mbi.imex.central.ws.v10.IcentralPort", 
            targetNamespace = "http://imex.mbi.ucla.edu/icentral/ws",
            wsdlLocation = "/WEB-INF/wsdl/icentral-1.0.wsdl") 

public class IcentralPortImpl implements IcentralPort {

    @Resource 
        WebServiceContext wsContext;

    private static String WS_ACTION ="ws-v10";
    private static String WS_UPD = "update";
    private static String WS_SRC = "search";
    
    //--------------------------------------------------------------------------
    // Entry Manager
    //--------------

    private EntryManager entryManager;

    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }

    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    //--------------------------------------------------------------------------
    // ACL Validator
    //--------------

    private AclValidator aclv;

    public void setAclValidator( AclValidator validator ){
        aclv = validator;
    }

    public AclValidator getAclValidator(){
        return aclv;
    }


    
    private JsonContext aclContext;

    public void setAclContext( JsonContext context ){
        aclContext = context;
    }

    public JsonContext getAclContext(){
        return aclContext;
    }
    
    
    public void initialize( boolean force ){

        if ( getAclContext().getJsonConfig() == null || force ) {

            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing acl context" );
            String jsonPath =
                (String) getAclContext().getConfig().get( "json-config" );
            log.info( "JsonAclDef=" + jsonPath );

            if ( jsonPath != null && jsonPath.length() > 0 ) {

                String cpath = jsonPath.replaceAll("^\\s+","" );
                cpath = jsonPath.replaceAll("\\s+$","" );

                try {
                    InputStream is =
                        ApplicationContextProvider.getResourceAsStream( cpath );
                    getAclContext().readJsonConfigDef( is );

                } catch ( Exception e ){
                    log.info( "JsonConfig reading error" );
                }
            }
        }
    }
    
    public void initialize(){
        //initialize( false );
    }

    //--------------------------------------------------------------------------

    private void aclVerify( String action, String op, User usr )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "aclVerify: action=" + action + " op=" + op 
                  + " usr=" + usr );
        
        if( ! aclv.verify( action, op,
                           usr.getLogin(), usr.getAllRoleNames(),
                           usr.getGroupNames() ) ) throw Fault.AUTH;
    }
    
    private void aclVerify( String action, String op, User usr, DataItem pub)
        throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "aclVerify: action=" + action + " op=" + op 
                  + " usr=" + usr + " tgt=" + pub );

        if( ! aclv.verify( action, op,
                           usr.getLogin(), usr.getAllRoleNames(),
                           usr.getGroupNames(),
                           pub.getOwner().getLogin(),
                           pub.getAdminUserNames(),
                           pub.getAdminGroupNames() ) ) throw Fault.AUTH;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    static DatatypeFactory dtf;
    static {
        try {
            dtf = DatatypeFactory.newInstance();
        } catch( DatatypeConfigurationException dce ) {
            // should not happen
        }
    }
    
    //--------------------------------------------------------------------------

    private long delay = 30L;

    public void setDelay( long delay ) {
        this.delay = delay;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    static ObjectFactory of = new ObjectFactory();

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // IcentralPort interface
    //-----------------------

    public void 
        createPublication( Holder<edu.ucla.mbi.imex.central.ws.v10.Publication> 
                           publication ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: createPublication" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v10.Publication 
        createPublicationById( Identifier id ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: createPublicationById" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        User usr = c.loggedUser();

        log.debug( " login=" + c.getLogin() );
        log.debug( " pass=" + c.getPass() );
        log.debug( " identifier=" + id );
        
        log.debug( " entry manager=" +  entryManager);
        log.debug( " credentials test=" +  c.test() );
        
        if ( ! c.test() ) throw Fault.AUTH;
        
        String ns = id.getNs();
        String ac = id.getAc();
        
        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        // test if already in
        //-------------------

        IcPub icPub = entryManager.getIcPubByPmid( ac );
        log.debug( " icPub=" + icPub);

        if ( icPub == null ) {

            aclVerify( WS_ACTION, WS_UPD, usr );
            edu.ucla.mbi.util.data.Publication
                newPub = entryManager.getPubByPmid( ac );
            if( newPub != null ) {
                icPub = new IcPub( newPub );
            }
        } else {
            aclVerify( WS_ACTION, WS_UPD, usr, icPub );
        }
        
        if ( icPub != null ) {
            log.info( " icPub=" + icPub + " ID=" + icPub.getId());
            if ( icPub.getId() == null ) {
                
                User owner = entryManager.getUserContext()
                    .getUserDao().getUser( c.getLogin() );
                log.debug( " owner set to: " + owner );
                
                DataState state =
                    entryManager.getWorkflowContext()
                    .getWorkflowDao().getDataState( "NEW" );
                log.debug( " state set to: " + state );
                
                if ( state != null ) {
                    IcPub newPub = entryManager.addIcPub( icPub, owner, state );
                    if ( newPub != null ) {
                        icPub = newPub;
                    }
                }
            }
        } 
        
        if ( icPub != null ) {
            return buildPub( icPub );
        }
        throw Fault.NO_REC_CR;
    }
    
    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationById( List<Identifier> idl )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationById" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( idl== null || idl.size() == 0 ) throw Fault.ID_MISSING;

        User usr = c.loggedUser();

        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();

        PublicationList pl = of.createPublicationList();
        for( Iterator<Identifier> idi = idl.iterator(); idi.hasNext(); ) {
            
            Identifier id = idi.next();
            
            String ns = id.getNs();
            String ac = id.getAc();
            
            log.debug( " ns=" + ns + " ac=" + ac );

            boolean noid = true;
            IcPub icp = null;
            if( ns == null || ac == null ) throw Fault.ID_MISSING;
            
            if( ns.equals( "pmid" ) ) {
                icp = (IcPub) pubDao.getPublicationByPmid( ac );
                log.debug( " icp=" + icp );
                noid = false;
            }
            
            if( ns.equals( "imex" ) ) {
                long lac = 0;
                
                try{
                    ac = ac.replaceFirst( "IM-", "" );
                    ac.replaceFirst( "-\\d$", "" );
                    lac = Long.parseLong( ac ); 
                } catch( Exception ex ) {
                    throw Fault.ID_UNKNOWN;
                }
                
                
                icp = (IcPub) pubDao.getPublicationByImexId( lac );
                log.debug( " icp=" + icp );
                noid = false;
            }

            if( icp != null ) {
                aclVerify( WS_ACTION, WS_SRC, usr, icp );
                pl.getPublication().add( buildPub( icp ) );
            }
                
            if( noid ) throw Fault.ID_UNKNOWN;

        }
        if( pl.getPublication().size() >  0 ) { 
            return pl;
        } else {
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }        
    }
    
    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationByOwner( List<String> owner )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationByOwner" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        User usr = c.loggedUser();

        aclVerify( WS_ACTION, WS_SRC, usr );
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationByStatus( List<String> status )
        throws IcentralFault { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationByStatus" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        User usr = c.loggedUser();
        aclVerify( WS_ACTION, WS_SRC, usr );

        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v10.Publication 
        updatePublicationStatus( Identifier id, String status, String message )
        throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: updatePublicationStatus" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( id == null ) throw Fault.ID_MISSING;
        
        User usr = c.loggedUser();

        String ns = id.getNs();
        String ac = id.getAc();
            
        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;

        log.debug( " ns=" + ns + " ac=" + ac );
       
        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();      
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( ac );
        log.debug( " icp=" + icp );
        
        if ( icp == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }        
        aclVerify( WS_ACTION, WS_UPD, usr, icp );
        
        DataState state = entryManager.getWorkflowContext()
            .getWorkflowDao().getDataState( status );
        
        log.info( " state set to: " + state );
        if( state == null ) throw Fault.STAT_UNKNOWN;
        
        // update status
        //--------------
        
        IcPub icPub = 
            entryManager.updateIcPubState( icp, c.loggedUser(), state );
        return buildPub( icPub );
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v10.Publication 
        getPublicationImexAccession( Identifier id, 
                                     java.lang.Boolean create )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationImexAccession" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if( id == null )  throw Fault.ID_MISSING;
        
        User usr = c.loggedUser();

        String ns = id.getNs();
        String ac = id.getAc();

        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        log.debug( " ns=" + ns + " ac=" + ac + "create=" + create );

        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( ac );       
        
        if ( icp == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }
        aclVerify( WS_ACTION, WS_UPD, usr, icp );

        log.debug( " imexid(old)=" + icp.getImexId() );

        if( ( icp.getImexId() == null || icp.getImexId().equals("N/A") )
            && create ) {
            icp = entryManager.genIcPubImex( icp, c.loggedUser() );
        }

        if( icp.getImexId() == null || icp.getImexId().equals("N/A") ) {
            throw Fault.NO_IMEX;
        }
        log.debug( " imexid(final)=" + icp.getImexId() );
        return buildPub( icp );
    }

    //--------------------------------------------------------------------------

    public void getServerStatus( String depth, Holder<String> version, 
                                 Holder<String> status )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getServerStatus" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) {
        
            try{
                Thread.sleep( delay*1000 );
            } catch ( InterruptedException ie) {
            }
            status.value = "";
        } else {
            status.value = "OK";
        }
        version.value = Icentral.WSVERSION;
    }
    
    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v10.Publication 
        updatePublicationAdminUser( Identifier identifier,
                                    String operation,
                                    String user) throws IcentralFault {

        edu.ucla.mbi.imex.central.ws.v10.Publication retPub = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: updatePublicationAdminUser" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        User lusr = c.loggedUser();
        
        if( operation == null || identifier == null || user == null ) {
            throw Fault.UNSUP;
        }
        
        log.debug( "IcentralPortImpl: identifier.ns=" + identifier.getNs() );
        log.debug( "IcentralPortImpl: identifier.ac=" + identifier.getAc() );
        log.debug( "IcentralPortImpl: operation=" + operation );
        log.debug( "IcentralPortImpl: user=" + user);
        

        // get publication
        //----------------
        
        if( !identifier.getNs().equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( identifier.getAc() );
        
        if ( icp == null ){
            aclVerify( WS_ACTION, WS_SRC, lusr );
            throw Fault.NO_RECORD;
        }
        aclVerify( WS_ACTION, WS_UPD, lusr, icp );
        
        if( operation.toUpperCase().equals("DROP") ) {
         
            log.debug( "IcentralPortImpl: AdminUser DROP");
            
            //------------------------------------------------------------------
            // drop user
            //----------
            
            boolean drop = false;
            
            Set<User> us = icp.getAdminUsers();
            List<Integer> udel = new ArrayList<Integer>();
            
            for( Iterator<User> ui = us.iterator(); ui.hasNext(); ) {
                User u = ui.next();
                if( u.getLogin().equals(user) ) {
                    udel.add( new Integer( u.getId() ) );
                    drop = true;
                }
            }
            
            if( drop ) {
                IcPub icpub = entryManager.delAdminUsers( icp, c.loggedUser(), 
                                                          udel ); 
                return buildPub( icpub ); 
            }
        }

        if( operation != null && operation.toUpperCase().equals("ADD") ) {
            
            log.debug( "IcentralPortImpl: AdminUser ADD");
            
            //------------------------------------------------------------------
            // get user
            //---------
            
            UserDao dao = entryManager.getUserContext().getUserDao();
            IcUser usr = (IcUser) dao.getUser( user );
           
            //------------------------------------------------------------------
            // add user
            //---------

            if( usr != null ) {
                IcPub icpub = entryManager.addAdminUser( icp, c.loggedUser(),
                                                         usr );
                                                         
                return buildPub( icpub );
            } else {
                throw Fault.USR_UNKNOWN;
            }
        }
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v10.Publication 
        updatePublicationAdminGroup( Identifier identifier,
                                     String operation,
                                     String group ) throws IcentralFault {
        
        edu.ucla.mbi.imex.central.ws.v10.Publication retPub = null;

        if( operation == null || identifier == null || group == null ) {
            throw Fault.UNSUP;
        }
        
        Log log = LogFactory.getLog( this.getClass() );
        
        log.info( "IcentralPortImpl: updatePublicationAdminGroup" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User lusr = c.loggedUser();
        
        log.debug( "IcentralPortImpl: identifier.ns=" + identifier.getNs() );
        log.debug( "IcentralPortImpl: identifier.ac=" + identifier.getAc() );
        log.debug( "IcentralPortImpl: operation=" + operation );
        log.debug( "IcentralPortImpl: group=" + group);
        
        
        // get publication
        //----------------
        
        if( !identifier.getNs().equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( identifier.getAc() );
        
        if( icp == null ){
            aclVerify( WS_ACTION, WS_SRC, lusr );
            throw Fault.NO_RECORD;
        }
        aclVerify( WS_ACTION, WS_UPD, lusr, icp );
        
        if( operation.toUpperCase().equals("DROP") ) {
        
            log.debug( "IcentralPortImpl: AdminGroup DROP");
            
            //------------------------------------------------------------------
            // drop group
            //-----------
            
            boolean drop = false;
            
            Set<Group> gs = icp.getAdminGroups();
            List<Integer> gdel = new ArrayList<Integer>();
            
            for( Iterator<Group> gi = gs.iterator(); gi.hasNext(); ) {
                Group g = gi.next();
                if( g.getLabel().equals( group ) ) {
                    gdel.add( new Integer( g.getId() ) );
                    drop = true;
                }
            }
            
            if( drop ) {
                IcPub icpub = entryManager.delAdminGroups( icp, c.loggedUser(),
                                                           gdel ); 
                return buildPub( icpub ); 
            }
        }

        if( operation != null && operation.toUpperCase().equals("ADD") ) {
            
            log.debug( "IcentralPortImpl: AdminGroup ADD");
            
            //------------------------------------------------------------------
            // get group
            //----------
            
            GroupDao dao = entryManager.getUserContext().getGroupDao();
            IcGroup grp = (IcGroup) dao.getGroup( group );
            
            //------------------------------------------------------------------
            // add group
            //----------

            if( grp != null ) {
                IcPub icpub = entryManager.addAdminGroup( icp, c.loggedUser(),
                                                          grp );
                                                          log.info(icpub);
                 if(icpub == null)
					throw Fault.INVALID_OP;
                return buildPub( icpub );
            } else {
                throw Fault.GRP_UNKNOWN;
            }
        }
        
        throw Fault.UNSUP;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // utilities
    //----------

    private edu.ucla.mbi.imex.central.ws.v10.Publication buildPub( IcPub icp ) {
        
        edu.ucla.mbi.imex.central.ws.v10.Publication 
            pub = of.createPublication();

        Identifier pmid = of.createIdentifier();
        pmid.setNs( "pmid" );
        pmid.setAc( icp.getPmid() );
        
        pub.getIdentifier().add( pmid );
        //pub.getIdentifier().setNs( "pmid" );
        //pub.getIdentifier().setAc( icp.getPmid() );
        
        pub.setAuthor( icp.getAuthor() );
        pub.setTitle( icp.getTitle() );
        pub.setPaperAbstract( icp.getAbstract() ) ;
        
        if( icp.getExpectedPubDate() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getExpectedPubDate() );
            } catch( Exception ex ) {
                //ignore
            } 
            if ( xmlDate != null ) {
                pub.setExpectedPublicationDate( xmlDate );
            }
        }
        
        if( icp.getPubDateStr() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getPubDate() );
            } catch( Exception ex ) {
                //ignore
            } 
            if ( xmlDate != null ) {
                pub.setPublicationDate( xmlDate );
            }
        }
        
        if( icp.getReleaseDateStr() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getReleaseDate() );
            } catch( Exception ex ) {
                //ignore
            }

            if ( xmlDate != null ) {
                pub.setReleaseDate( xmlDate );
            }
        }
        //pub.setCreationDate(icp);
        
        pub.setStatus( icp.getState().getName() );
        pub.setImexAccession( icp.getImexId() );
        pub.setOwner( icp.getOwner().getLogin() );

        return pub;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    class Credentials{
        
        String login = null;
        String pass = null;
        
        public String getLogin(){
            return login;
        }
        
        public String getPass(){
            return pass;
        }
        
        public Credentials( MessageContext context ) {
            
            try {
                Map requestHeaders =
                    (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS ) ;
                
                String b64str = (String) 
                    ((List) requestHeaders.get("Authorization")).get(0);
                
                String lpString = 
                    new String( Base64.decodeBase64( b64str.substring(6) ) );

                
                login = lpString.substring( 0, lpString.indexOf( ":" ) );
                pass = lpString.substring( lpString.indexOf( ":" ) + 1 );
            } catch ( Exception e ) {
                // ignore: login/pass left at null
            }
        }

        public boolean test() {
            
            if ( entryManager == null ||  
                 entryManager.getUserContext() == null ) return false;
            
            UserDao dao = entryManager.getUserContext().getUserDao();
            if ( dao == null ) return false;
            
            Log log = LogFactory.getLog( this.getClass() );
            log.info( "Credentials.test: login=" + login );

            if( login == null ) {
                return false;
            }

            User user = dao.getUser( login );
            
            if ( user != null && pass != null ) {
                return user.testPassword( pass );
            } 
            return false;            
        }
        
        public User loggedUser(){
            
            UserDao dao = entryManager.getUserContext().getUserDao();
            if ( dao == null ) return null;
            return dao.getUser( login );
        }
    }
}
