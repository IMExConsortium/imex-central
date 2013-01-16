package edu.ucla.mbi.imex.central.ws.v20.server;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # IcentralPortImpl - ImexCentral SOAP port implementation
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

import java.util.*;
import java.util.regex.*;
import java.text.DecimalFormat;
import java.io.InputStream;

import javax.annotation.*;         

import edu.ucla.mbi.util.struts.*;
import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;
import edu.ucla.mbi.imex.central.ws.v20.*;

@WebService(serviceName = "ics20", 
            portName = "icp20", 
            endpointInterface = "edu.ucla.mbi.imex.central.ws.v20.IcentralPort", 
            targetNamespace = "http://imex.mbi.ucla.edu/icentral/ws",
            wsdlLocation = "/WEB-INF/wsdl/icentral-2.0.wsdl") 

public class IcentralPortImpl implements IcentralPort {

    @Resource 
        WebServiceContext wsContext;
    
    //--------------------------------------------------------------------------
    
    private static String WS_ACTION ="ws-v20";
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

    static DatatypeFactory dtf;
    static {
        try {
            dtf = DatatypeFactory.newInstance();
        } catch( DatatypeConfigurationException dce ) {
            // should not happen
        }
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
    
    public void initialize( boolean force ){
        
        if ( aclv.getAclContext().getJsonConfig() == null || force ) {
            
            Log log = LogFactory.getLog( this.getClass() );
            log.info( " initilizing acl context" );
            String jsonPath =
                (String) aclv.getAclContext().getConfig().get( "json-config" );
            log.info( "JsonAclDef=" + jsonPath );

            if ( jsonPath != null && jsonPath.length() > 0 ) {
                
                String cpath = jsonPath.replaceAll("^\\s+","" );
                cpath = jsonPath.replaceAll("\\s+$","" );

                try {
                    InputStream is =
                        ApplicationContextProvider.getResourceAsStream( cpath );
                    aclv.getAclContext().readJsonConfigDef( is );
                    
                } catch ( Exception e ){
                    log.info( "JsonConfig reading error" );
                }
            }
        }
    }
    
    public void initialize(){
        initialize( false );
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
        createPublication( Holder<edu.ucla.mbi.imex.central.ws.v20.Publication> 
                           publication ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: createPublication" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v20.Publication 
        createPublicationById( Identifier id ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: createPublicationById" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        
        log.debug( " login=" + c.getLogin() );
        log.debug( " pass=" + c.getPass() );
        log.debug( " identifier=" + id );
        
        log.debug( " entry manager=" +  entryManager);
        log.debug( " credentials test=" +  c.test() );
        
        if ( ! c.test() ) throw Fault.AUTH;
    
        User usr = c.loggedUser();
    
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
    
    public edu.ucla.mbi.imex.central.ws.v20.Publication 
        getPublicationById( Identifier id )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationById" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( id == null ) throw Fault.ID_MISSING;

        User usr = c.loggedUser();
        
        String ns = id.getNs();
        String ac = id.getAc();
            
        log.debug( " ns=" + ns + " ac=" + ac );
        
        IcPub icp = getIcPub( ns, ac );
        
        if( icp != null ) {
            aclVerify( WS_ACTION, WS_SRC, usr, icp );
            return buildPub( icp );
        }
        aclVerify( WS_ACTION, WS_SRC, usr );
        throw Fault.NO_RECORD;
    }

    
    //--------------------------------------------------------------------------
    
    public void getPublicationByOwner( String owner,
                                       Integer firstRec,
                                       Integer maxRec,
                                       Holder<PublicationList> publicationList,
                                       Holder<Long> lastRec )
        throws IcentralFault {

        // NOTE: only first owner considered
        //----------------------------------

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "IcentralPortImpl: getPublicationByOwner" );
        log.debug( "IcentralPortImpl: firstRec=" + firstRec 
                   + " maxRec=" + maxRec);
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User usr = c.loggedUser();
        
        aclVerify( WS_ACTION, WS_SRC, usr );

        if( owner == null )  throw Fault.ID_MISSING;
      
        User user = entryManager.getUserContext()
            .getUserDao().getUser( owner );
        
        if( user == null ) throw Fault.USR_UNKNOWN;       
        
        long pubCnt = entryManager.getPubCountByOwner( user );
        lastRec.value = new Long( pubCnt );

        if( maxRec != null && maxRec.intValue() > 0 ){
            List<IcPub> icPubList 
                = entryManager.getPublicationByOwner( user, 
                                                      firstRec, maxRec );
            
            if( icPubList == null || icPubList.size() == 0 ) 
                throw Fault.NO_RECORD;
            publicationList.value = buildPublicationList( icPubList );
      
            log.debug( " owner: " + owner
                       + " count: " + icPubList.size()
                       + " last: " + pubCnt );
        }
    }

    //--------------------------------------------------------------------------
    
    public void getPublicationByStatus( String status,
                                        Integer firstRec,
                                        Integer maxRec,
                                        Holder<PublicationList> publicationList,
                                        Holder<Long> lastRec)
        throws IcentralFault { 

        // NOTE: only first owner considered
        //----------------------------------
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "IcentralPortImpl: getPublicationByStatus" );
        log.debug( "IcentralPortImpl: firstRec=" + firstRec 
                   + " maxRec=" + maxRec);
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User usr = c.loggedUser();

        aclVerify( WS_ACTION, WS_SRC, usr );
        
        if( status == null )  throw Fault.ID_MISSING;

        DataState state = entryManager.getWorkflowContext()
            .getWorkflowDao().getDataState( status );

        log.debug( " status: " + state );
        if( state == null ) throw Fault.STAT_UNKNOWN;
        
        long pubCnt = entryManager.getPubCountByStatus( state );
        lastRec.value = new Long( pubCnt );
        
        if( maxRec != null && maxRec.intValue() > 0 ){

            List<IcPub> icPubList 
                = entryManager.getPublicationByStatus( state,
                                                       firstRec, maxRec );
                   
            if( icPubList == null || icPubList.size() == 0 ) 
                throw Fault.NO_RECORD;
            publicationList.value = buildPublicationList( icPubList );
        
            log.debug( " status: " + state.getName() 
                       + " count: " + icPubList.size() 
                       + " last: " + pubCnt );
        }
    }

    //--------------------------------------------------------------------------

    public void queryPublication( String query, 
                                  Integer firstRec, Integer maxRec,
                                  Holder<PublicationList> publicationList,
                                  Holder<Long> lastRec )
        throws IcentralFault {
    
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: queryPublication" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User usr = c.loggedUser();
        aclVerify( WS_ACTION, WS_SRC, usr );

        throw Fault.UNSUP;
    }
    
    //--------------------------------------------------------------------------
    
    public void updatePublication
        ( Identifier identifier,
          Holder<edu.ucla.mbi.imex.central.ws.v20.Publication> publication )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: updatePublication" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User usr = c.loggedUser();
        aclVerify( WS_ACTION, WS_UPD, usr );
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    
    public edu.ucla.mbi.imex.central.ws.v20.Publication 
        updatePublicationIdentifier( Identifier id, Identifier newId )
        throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: updatePublicationIdentifier" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( id == null ) throw Fault.ID_MISSING;

        User usr = c.loggedUser();

        String ns = id.getNs();
        String ac = id.getAc();

        log.debug( " ns=" + ns + " ac=" + ac );

        IcPub icp = getIcPub( ns, ac );

        if( icp == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }
        aclVerify( WS_ACTION, WS_UPD, usr, icp );
        
        String nNs = newId.getNs();
        String nAc = newId.getAc();

        if( nNs == null || nAc == null ) throw Fault.ID_MISSING;
        
        IcPub nIcp = getIcPub( nNs, nAc );

        if( nIcp != null ) throw Fault.ID_DUP;

        // icp ok, new id unique
        //----------------------

        if( nNs.equals("pmid") ){
            icp.setPmid(nAc);
        }

        if( nNs.equals("doi") ){
            icp.setDoi(nAc);
        }

        if( nNs.equals("jint") ){
            icp.setJournalSpecific( nAc );
        }
 
        IcPub uIcPub = entryManager
            .updateIcPubIdentifiers( icp, c.loggedUser(), icp );
        return buildPub( uIcPub );
               
    }

    //--------------------------------------------------------------------------
    
    
    public edu.ucla.mbi.imex.central.ws.v20.Publication 
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
        log.debug( " ns=" + ns + " ac=" + ac );
       
        IcPub icp = getIcPub( ns, ac );
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
        
        IcPub icPub = entryManager
            .updateIcPubState( icp, c.loggedUser(), state );
        return buildPub( icPub );
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v20.Publication 
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
        log.debug( " ns=" + ns + " ac=" + ac + "create=" + create );

        IcPub icp = getIcPub( ns, ac );
        
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

    public edu.ucla.mbi.imex.central.ws.v20.Publication 
        updatePublicationAdminUser( Identifier identifier,
                                    String operation,
                                    String user) throws IcentralFault {

        edu.ucla.mbi.imex.central.ws.v20.Publication retPub = null;
        
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
               
        String ns = identifier.getNs();
        String ac = identifier.getAc();
        
        IcPub icp = getIcPub( ns, ac );
        
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
                IcPub icpub = entryManager
                    .delAdminUsers( icp, c.loggedUser(), udel ); 
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
                IcPub icpub = entryManager
                    .addAdminUser( icp, c.loggedUser(), usr );
                return buildPub( icpub );
            } else {
                throw Fault.USR_UNKNOWN;
            }
        }
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.v20.Publication 
        updatePublicationAdminGroup( Identifier identifier,
                                     String operation,
                                     String group ) throws IcentralFault {
        
        edu.ucla.mbi.imex.central.ws.v20.Publication retPub = null;

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
        
        String ns = identifier.getNs();
        String ac = identifier.getAc();
        
        IcPub icp = getIcPub( ns, ac );
        
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
                IcPub icpub = entryManager
                    .delAdminGroups( icp, c.loggedUser(),gdel ); 
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
                IcPub icpub = entryManager
                    .addAdminGroup( icp, c.loggedUser(), grp );
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

    public void addAttachment( Identifier parent,
                               Holder<Attachment> attachment)    
        throws IcentralFault{
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: addAttachment" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        User usr = c.loggedUser();
        
        // get parent
        //-----------
        
        String ns = parent.getNs();
        String ac = parent.getAc();

        if( ns == null || ac== null ) throw Fault.ID_MISSING;
        IcPub icParent = getIcPub( ns, ac );

        if ( icParent == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }

        // build attachment
        //-----------------
        
        Attachment iAtt = attachment.value;
        
        String attType = iAtt.getType();
        String attLabel = iAtt.getSubject();
        String attBody = iAtt.getBody();
        
        log.info( "type: "+attType+"  label: "+attLabel+ "   body: " +attBody);

        User owner = entryManager.getUserContext()
            .getUserDao().getUser( c.getLogin() );
        log.debug( " attachment owner: " + owner );

        if( attType != null 
            && attType.toLowerCase().equals( "txt/comment" ) ){
                
            IcComment icCom = new IcComment( owner, icParent, 
                                             attLabel, attBody);

            icCom.setOwner( owner ); 
            //icCom.setRoot( icParent ); 
            
            icCom.setLabel( attLabel );
            icCom.setBody( attBody );

            
            IcAdiDao adiDao = (IcAdiDao) 
                entryManager.getTracContext().getAdiDao();
            adiDao.saveAdi( icCom );

            Attachment nAtt = 
                buildAttachment( icCom.getId(), icParent, null, attType, 
                                 attLabel, attBody, owner );
            
            attachment.value= nAtt;
        }

        if( attType != null 
            && attType.toLowerCase().equals( "txt/data" ) ){
                
            IcAttachment icAtt = new IcAttachment( owner, icParent, 
                                                   attLabel, attBody);

            icAtt.setOwner( owner ); 
            //icCom.setRoot( icParent ); 
            
            icAtt.setLabel( attLabel );
            icAtt.setBody( attBody );

            
            IcAdiDao adiDao = (IcAdiDao) 
                entryManager.getTracContext().getAdiDao();
            adiDao.saveAdi( icAtt );

            Attachment nAtt = 
                buildAttachment( icAtt.getId(), icParent, null, attType, 
                                 attLabel, attBody, owner );
            
            attachment.value= nAtt;
        }
        
    }

    //--------------------------------------------------------------------------    

    public void queryAttachment( String query,
                                 Integer firstRec, Integer maxRec,
                                 Holder<AttachmentList> attachmentList,
                                 Holder<Long> lastRec )
        throws IcentralFault{
    
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: queryAttachment" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        throw Fault.UNSUP;
    }


    public Attachment dropAttachment( Identifier identifier )
        throws IcentralFault{
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: dropAttachment" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        throw Fault.UNSUP;
    }
    
    public void getAttachmentByParent( Identifier parent,
                                       String type,
                                       Integer firstRec, Integer maxRec,
                                       Holder<AttachmentList> attachmentList,
                                       Holder<Long> lastRec )
        throws IcentralFault {
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getAttachmentByParent" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        User usr = c.loggedUser();
        
        // get parent
        //-----------
        
        String ns = parent.getNs();
        String ac = parent.getAc();

        log.info( "type: "+type+"  ns: "+ns+ "   ac: " +ac);
        
        if( ns == null || ac== null ) throw Fault.ID_MISSING;
        IcPub icParent = getIcPub( ns, ac );

        if ( icParent == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }

        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();
        
        List<AttachedDataItem> adiList = 
            adiDao.getAdiListByRoot( icParent ); 
        
        if( adiList != null && adiList.size() > 0 ){
            AttachmentList al = buildAttachmentList( adiList, type );
            attachmentList.value = al;
            if( al== null || al.getAttachment().size() == 0 ){
                throw Fault.NO_RECORD;
            } 
        } else {
            throw Fault.NO_RECORD;
        }
    }
    
    //--------------------------------------------------------------------------

    public Attachment getAttachmentById( Identifier identifier )
        throws IcentralFault{

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getAttachmentById" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        User usr = c.loggedUser();
        
        String ns = identifier.getNs();
        String ac = identifier.getAc();
        log.info( "IcentralPortImpl: ns=" + ns + " ac=" + ac);

        if( ns==null || !ns.equals( "ic" ) ){
            throw Fault.INVALID_ID;    
        } 

        int id=-1;
        
        try{
            Pattern p = Pattern.compile( "IC-0+([1-9][0-9]*)\\D+" );                
            Matcher m = p.matcher( ac );

            if( m.matches() ){
                String sid = m.group(1);
                log.info( "IcentralPortImpl: sid=" +sid);
                id = Integer.parseInt( sid );
            }
        } catch(Exception ex){
            throw Fault.INVALID_ID;
        }
        
        if( id <= 0 ){ 
            throw Fault.INVALID_ID;
        }
        
        log.info( "IcentralPortImpl: id=" + id);

        IcAdiDao adiDao = (IcAdiDao)
            entryManager.getTracContext().getAdiDao();
        AttachedDataItem adi =  adiDao.getAdi( id );

        if ( adi == null ){
            aclVerify( WS_ACTION, WS_SRC, usr );
            throw Fault.NO_RECORD;
        }

        aclVerify( WS_ACTION, WS_UPD, usr, adi );
        log.info( "IcentralPortImpl: adi id=" + adi.getId());
        
        Attachment nAtt = buildAttachment( adi );
        return nAtt;

    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // utilities
    //----------

    private IcPub getIcPub( String ns, String ac )
        throws IcentralFault {
        
        IcPub icp = null;
        if( ns == null || ac == null ) throw Fault.ID_MISSING;

        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();
        
        if( ns.equals( "pmid" ) ) {
            icp = (IcPub) pubDao.getPublicationByPmid( ac );
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
        }
        
        if( ns.equals( "doi" ) ) {
            icp = (IcPub) pubDao.getPublicationByDoi( ac );
        }

        if( ns.equals( "jint" ) ) {
            icp = (IcPub) pubDao.getPublicationByJint( ac );
        }
        
        return icp;
    }
    
    //--------------------------------------------------------------------------

    private edu.ucla.mbi.imex.central.ws.v20.Publication buildPub( IcPub icp ) {
        
        edu.ucla.mbi.imex.central.ws.v20.Publication 
            pub = of.createPublication();

        // identifiers
        //------------

        if( icp.getDoi() != null && 
            !icp.getDoi().equals("") ){
            
            Identifier doi = of.createIdentifier();
            doi.setNs( "doi" );
            doi.setAc( icp.getDoi() );
            pub.getIdentifier().add( doi );
        }

        if( icp.getPmid() != null 
            && !icp.getPmid().equals("") ){
            
            Identifier pmid = of.createIdentifier();
            pmid.setNs( "pmid" );
            pmid.setAc( icp.getPmid() );
            pub.getIdentifier().add( pmid );
        }

        if( icp.getJournalSpecific() != null 
            && !icp.getJournalSpecific().equals("") ){
            
            Identifier jint = of.createIdentifier();
            jint.setNs( "jint" );
            jint.setAc( icp.getJournalSpecific() );
            pub.getIdentifier().add( jint );
        }
        
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

    private edu.ucla.mbi.imex.central.ws.v20.PublicationList
        buildPublicationList( List<IcPub> pubList ){

        edu.ucla.mbi.imex.central.ws.v20.PublicationList
            pl = of.createPublicationList();
        
        for( Iterator<IcPub> 
                 ii = pubList.iterator(); ii.hasNext(); ){
            
            IcPub icpub = ii.next();
            
            edu.ucla.mbi.imex.central.ws.v20.Publication cpub 
                = buildPub( icpub );
            pl.getPublication().add( cpub );
        
        }
        return pl;
    }

    //--------------------------------------------------------------------------

    private edu.ucla.mbi.imex.central.ws.v20.Attachment 
        buildAttachment( int id, IcPub parent,
                         AttachedDataItem attAdi,
                         String attType, String attLabel, String attBody,
                         User owner ) {
        
        edu.ucla.mbi.imex.central.ws.v20.Attachment 
            att = of.createAttachment();

        Identifier aId = of.createIdentifier();
        aId.setNs( "ic" );
        
        DecimalFormat df = new DecimalFormat("000000000000");
        
        aId.setAc( "IC-"+df.format( new Long(id) )+"-ATT" );
        
        att.setIdentifier(aId);
        
        Identifier pId = of.createIdentifier();
        pId.setNs( "pmid" );
        pId.setAc( parent.getPmid() );
        
        att.setParent(pId);
       
        
        att.setType( attType );
        att.setSubject( attLabel );
        att.setBody( attBody );

        att.setOwner( owner.getLogin() );

        if( attAdi != null && attAdi.getCrt() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( attAdi.getCrt() );
            } catch( Exception ex ) {
                //ignore
            }
            if ( xmlDate != null ) {
                att.setCreationDate( xmlDate );
            }
        }

        return att;
    }

    //--------------------------------------------------------------------------

    private edu.ucla.mbi.imex.central.ws.v20.Attachment 
        buildAttachment( AttachedDataItem adi ) {
        
        DecimalFormat df = new DecimalFormat("000000000000");

        edu.ucla.mbi.imex.central.ws.v20.Attachment 
            att = of.createAttachment();
        
        

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: buildAttachment: " + adi.getId() );



        Identifier aId = of.createIdentifier();
        aId.setNs( "ic" );      
        aId.setAc( "IC-" + df.format( new Long( adi.getId() ) ) + "-ATT"  );
        att.setIdentifier( aId );
        
        Identifier pId = of.createIdentifier();
        pId.setNs( "pmid" );
        pId.setAc( ((IcPub) adi.getRoot()).getPmid() );
        att.setParent(pId);
       
        att.setType( "txt/comment" );
        att.setSubject( ((IcComment) adi).getLabel() );
        att.setBody( ((IcComment) adi).getBody() );
        att.setOwner( adi.getOwner().getLogin() );

        XMLGregorianCalendar xmlDate = null;
        try{
            xmlDate = dtf.
                newXMLGregorianCalendar( adi.getCrt() );
        } catch( Exception ex ) {
            //ignore
        }
        if ( xmlDate != null ) {
            att.setCreationDate( xmlDate );
        }
        
        return att;
    }

    //--------------------------------------------------------------------------

    private edu.ucla.mbi.imex.central.ws.v20.AttachmentList
        buildAttachmentList( List<AttachedDataItem> adl,
                             String type ){

        edu.ucla.mbi.imex.central.ws.v20.AttachmentList
            atl = of.createAttachmentList();
        
        for( Iterator<AttachedDataItem> 
                 ii = adl.iterator(); ii.hasNext(); ){
        
            AttachedDataItem cadi = ii.next();

            if( type != null && 
                type.equals( "txt/comment" ) && 
                cadi instanceof edu.ucla.mbi.util.data.Comment ){
                
                Comment c = (edu.ucla.mbi.util.data.Comment) cadi;

                Attachment catt = 
                    buildAttachment( c.getId().intValue(), 
                                     (IcPub) c.getRoot(),
                                     c,
                                     "txt/comment", 
                                     c.getLabel(), c.getBody(),
                                     c.getOwner() ); 
            
                atl.getAttachment().add( catt );
            }
        }
        return atl;
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
