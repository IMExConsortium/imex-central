package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *                                                                             $
 * EditAction - page/menu editing                                              $
 *                                                                             $
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.json.*;

import edu.ucla.mbi.util.context.*;
import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

public class EditAction extends EditSupport {

    // operations
    //-----------

    public static final String PAGE_ATT_STORE = "pageAttStore";
    public static final String PAGE_SRC_STORE = "pageSrcStore";
    public static final String ITEM_ATT_STORE = "itemAttStore";
    public static final String ITEM_SUB_ADD = "itemSubAdd";

    
    // return codes
    //-------------
    
    public static final String PAGE = "page";
    public static final String MENU = "menu";
    
    
    // configuration 
    //--------------
    
    Map<String,Object> pageCnf;
    Map<String,Object> menuCnf;
    
    
    Log log = LogFactory.getLog( this.getClass() );
    
    private void initialize() {
	
	// page context
	//-------------
	
	pageCnf = getPageContext().getJsonConfig();
	
	if ( pageCnf == null ) {
	    log.info( "initilize" );
	    String jsonPath = 
		(String) getPageContext().getConfig().get( "json-config" );
	    log.debug( "JsonPageDef=" + jsonPath );
	
	    if ( jsonPath != null && jsonPath.length() > 0 ) {
		
		String cpath = jsonPath.replaceAll("^\\s+","" );
		cpath = jsonPath.replaceAll("\\s+$","" );

		try {
		    InputStream is = 
			getServletContext().getResourceAsStream( cpath );
		    getPageContext().readJsonConfigDef( is );
		    
		    pageCnf = getPageContext().getJsonConfig();
		    
		} catch ( Exception e ){
		    log.info( "JsonConfig reading error (pageContext)" );
		}
	    }
	}
	
	// menu context
	//-------------
	
        menuCnf = getMenuContext().getJsonConfig();
	
	if ( menuCnf == null ) {
	    log.debug( " initilizing page defs..." );
	    String jsonPath = 
		(String) getPageContext().getConfig().get( "json-config" );
	    log.debug( "JsonPageDef=" + jsonPath );
	    
	    if ( jsonPath != null && jsonPath.length() > 0 ) {
		
		String cpath = jsonPath.replaceAll("^\\s+","" );
		cpath = jsonPath.replaceAll("\\s+$","" );
		
		try {
		    InputStream is = 
			getServletContext().getResourceAsStream( cpath );
		    getPageContext().readJsonConfigDef( is );
		    
		    menuCnf = getPageContext().getJsonConfig();
		    
		} catch ( Exception e ){
		    log.info( "JsonConfig reading error (menuContex)" );
		}
	    }
	}
    }
    
    
    private Map getItemDef( List<Integer> menuSel ) {

	Map menu= (Map) ((List) menuCnf.get("menu")).get( 0 );
	
        if ( getMenuSel() != null ) {
            for( int i = 0; i < getMenuSel().size(); i++ ) {
		
                List smenu = (List) menu.get( "menu" );
                if ( smenu != null ||
                     smenu.size() > getMenuSel().get( i ) - 1 ) {

                    if ( getMenuSel().get( i ) > 0 ) {
                        menu = 
			    (Map) smenu.get( getMenuSel().get( i ) - 1 );
                    }
                }
            }
        }
	return menu;
    }

    
    private void saveContext( JsonContext context ) 
	throws IOException {

	String jsonConfigFile =
	    (String) context.getConfig().get( "json-config" );
	
	String srcPath =
	    getServletContext().getRealPath( jsonConfigFile );
	log.debug( " srcPath=" + srcPath );
	
	File sf = new File( srcPath );
	PrintWriter spw = new PrintWriter( sf );
	context.writeJsonConfigDef( spw );
	spw.close();
    }


    public String execute() throws Exception {

        log.info( "execute" );
	initialize();
       	
	// decode operation
	//-----------------

	String target = null;
	String opcode = null;
	int itemIdx = -1;
	
	if( getOpr() != null ) {
	    
	    for( Iterator<String> ii = getOpr().keySet().iterator(); 
		 ii.hasNext(); ) {
		String key = ii.next();
		
		if ( key.equals( PAGE_ATT_STORE ) ) {
		    target = "page";
		    opcode = "att_store";
		}

		if ( key.equals( PAGE_SRC_STORE ) ) {
		    target = "page";
		    opcode = "src_store";
		}

		if ( key.equals( ITEM_ATT_STORE ) ) {
		    target = "menu";
		    opcode = "att_store";
		}
		if ( key.equals( ITEM_SUB_ADD ) ) {
		    target = "menu";
		    opcode = "sub_add";
		}
	    }
	}

	if( getDropid() != null ) {
	    target = "menu";
	    opcode = "sub_drop";
	    itemIdx = getDropid().size()-1;
	}

	log.debug( "target=" + target + " opcode=" +opcode);
	
	// dispatch
	//---------

	if ( target != null && target.equals( "page") ) {
	    if ( opcode != null && opcode.equals( "att_store" ) ) {
		return pageAttStore();
	    }
	    if ( opcode != null && opcode.equals( "src_store" ) ) {
		return pageSrcStore();
	    }
	}

	if ( target != null && target.equals( "menu") ) {
	    if ( opcode != null && opcode.equals( "att_store" ) ) {
		return menuAttStore();                
            }
	    if ( opcode != null && opcode.equals( "sub_add" ) ) {
		return menuItemAdd();
            }
	    if ( opcode != null && opcode.equals( "sub_drop" ) ) {
		return menuItemDrop( itemIdx );
            }	    
	}

	// default: menu edit 

	return MENU;
    }


    //---------------------------------------------------------------------
    // store page attributes
    //----------------------
    
    private String pageAttStore() {

	// get pageid 
	//-----------
	
	String id = getPageid();
	String newid = getNewid();
	
	log.debug( "id(old)=" + id + " id(new)=" + newid );

       	if ( id != null  && id.length() > 0 ) {
	
    
	    if( newid != null  && newid.length() > 0 ) {
		
		id = newid;
		setPageid( id );
	    }

	    // sanitize id
	    //------------
	    
	    String idregex = 
		(String) ((Map) pageCnf.get("pageConfig") ).get( "idregex" );
	    
	    try {
		if ( !newid.matches(idregex) ) {
		    return ERROR;
		}
	    } catch( PatternSyntaxException ex ){
		return ERROR;
	    }
	    
	    Map pages = 
		(Map) ((Map) pageCnf.get("pageConfig") ).get( "pages" );
	    Map pageConfig = (Map) pages.get( id );
	    
	    if ( pageConfig == null ) {
		pageConfig = new HashMap();
		pages.put( id, pageConfig );
		pageConfig.put("viewtype", "file-html" );

	    }

	    // update attributes
	    //------------------
	    
	    for( Iterator<String> ii = getPage().keySet().iterator(); 
		 ii.hasNext(); ) {
		
		String key = ii.next();
		Object value = getPage().get( key );

		try{
		    String[] sval = (String[]) value;
		    if ( sval.length > 0 ) {
			if( key.equals("id") ) {
			    id = sval[0];
			    id.replaceAll("\\s","");
			}

			if( key.equals( "viewpath" ) ) {

			    String fpath = sval[0];
			    
			    // sanitize path
			    //--------------

			    String pathregex = 
				(String) ((Map) pageCnf.get("pageConfig") ).get( "pathregex" );
			    
			    try {
				if ( !fpath.matches(pathregex) ) {
				    return ERROR;
				}
			    } catch( PatternSyntaxException ex ){
				log.info("err pathregex="+pathregex);
				return ERROR;
			    }			    
			}
		    }
		    pageConfig.put( key, sval[0] ); 
		    
		} catch( ClassCastException cce ) {
		    // ignore...
		}
	    }
	    
	    // save updated configuration
	    //---------------------------
	    
	    try { 
		saveContext( getPageContext() );
	    } catch(IOException ioe ) {
		log.info( " error: " + ioe );
		return ERROR;
	    }

	    getSession().put( "EDT", "ATR" );
	    return PAGE;
	}
	return ERROR;
    }	

    
    //---------------------------------------------------------------------
    // page source update
    //-------------------
    
    private String pageSrcStore() {

	// get pageid
        //-----------

        String id = getPageid();
        String newid = getNewid();
	
        log.debug( "id=" + id);
	
        if ( id != null  && id.length() > 0 ) {

            Map pages =
                (Map) ((Map) pageCnf.get("pageConfig") ).get( "pages" );
            Map pageConfig = (Map) pages.get( id );

	    String type = (String) pageConfig.get( "viewtype" );
	    String path = (String) pageConfig.get( "viewpath" );
	    
	    // NOTE: MUST sanitize path here !!!!
	    //-----------------------------------

	    // sanitize path
	    //--------------

	    String pathregex = 
		(String) ((Map) pageCnf.get("pageConfig") ).get( "pathregex" );

	    try {
		if ( !path.matches( pathregex ) ) {
		    return ERROR;
		}
	    } catch( PatternSyntaxException ex ){
		return ERROR;
	    }

	    //viewtype:"file-html",
	    //viewpath:"/pages/news.html"

	    if ( type != null && type.equals("file-html") &&
		 path != null && path.length() > 0 ) {
		
	       	       		
		// store page source
		//------------------
	
		String srcPath = 
		    getServletContext().getRealPath( path );
		log.debug( " srcPath=" + srcPath );
	
		if ( getSource() != null && getSource().length() > 0 ) {
		
		    try {
			File sf = new File( srcPath );
			PrintWriter spw = new PrintWriter( sf );
			spw.print( getSource() );
			spw.close();
		    } catch(IOException ioe ) {
			log.info( " error: " + ioe );
			return ERROR;
		    }
		}
	    }
	}
	getSession().put( "EDT", "SRC" );
	return PAGE;
    }


    //---------------------------------------------------------------------
    // menu item attribute update
    //---------------------------
    
    private String menuAttStore() {
	
	log.debug(" EditAction: menuSel=" + getMenuSel() + 
                  " item=" + getItem() );
	
      	// get item definition
	//--------------------
	
	Map menu = getItemDef( getMenuSel() );
	log.debug(" menu(old)=" + menu );

	// update definition
	//------------------

	if ( getItem() != null ) {
	    
	    Map item = getItem();
	    for ( Iterator ii = item.keySet().iterator(); 
		  ii.hasNext(); ) {
		
		String key = (String) ii.next();
		String val = ((String[]) item.get( key ))[0]; 
		
		if( !key.equals("menu" ) ) { 
		    menu.put( key, val );
		}		
	    }

	    log.debug(" menu(new)=" + menu );
	    
	    // save updated configuration
            //---------------------------
	    
	    try { 
		saveContext( getMenuContext() );
	    } catch(IOException ioe ) {
		log.debug( " error: " + ioe );
		return ERROR;
	    }	    
	}
	return MENU;
    }

    
    //---------------------------------------------------------------------
    // drop subitem
    //-------------
    
    private String  menuItemDrop( int idx ) {

	log.debug(" EditAction: menuSel=" + getMenuSel() +
                  " item=" + getItem() );

        // get item definition
        //--------------------
	
        Map menu = getItemDef( getMenuSel() ); 	
        log.debug(" menu(old)=" + menu );
	
	// get/create submenu
        //-------------------

        List submenu = (List) menu.get("menu");
        if ( submenu != null && submenu.size() > idx) {
            submenu.remove( idx );
	
	    log.debug(" menu(new)=" + menu );

	    // save updated configuration
	    //---------------------------

	    try { 
		saveContext( getMenuContext() );
	    } catch(IOException ioe ) {
		log.info( " error: " + ioe );
		return ERROR;
	    }
	}
	return MENU;
    }
    
    //---------------------------------------------------------------------
    // add subitem
    //------------

    private String menuItemAdd() {

	log.debug(" EditAction: menuSel=" + getMenuSel() +
                  " item=" + getItem() );

        // get item definition
        //--------------------

	Map menu = getItemDef( getMenuSel() );
        log.debug(" menu(old)=" + menu );

	// get/create submenu
	//-------------------

	List submenu = (List) menu.get("menu");
	if ( submenu == null ) {
	    submenu = new ArrayList();  // List<Map> ???
	    menu.put("menu", submenu );
	}

	// create/add new item
	//---------------------
	
	Map newitem = new HashMap();
	newitem.put( "label","NewItem");
	submenu.add( newitem );

	log.debug(" menu(new)=" + menu );
	
	// save updated configuration
	//---------------------------

	try { 
	    saveContext( getMenuContext() );
	} catch(IOException ioe ) {
	    log.debug( " error: " + ioe );
	    return ERROR;
	}
	return MENU;
    }

    //---------------------------------------------------------------------

    public void validate() {
	
	// verify role
	//------------

	Map role = (Map) getSession().get( "USER_ROLE" );

	if ( role != null ) {
	    if ( role.get("administrator") == null ) {
		addActionError("Insufficient priviledges");
	    }		
	} else {
	    addActionError("Insufficient priviledges");
	}

	// missing id (old)
	//-----------------
	
       
	// missing/invalid new page
	// ------------------------


	// invalid file path
	//------------------


    }
}
