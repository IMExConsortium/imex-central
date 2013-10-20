YAHOO.namespace( "mbi.imgedit" );

YAHOO.mbi.imgedit = {

    coldef:[{ key: "image", label: "Image", width:600},
           // { key: "global", label: "Portal", formatter: "checkbox", width: 80 },
           // { key: "skin", label: "Skin", formatter: "checkbox", width: 80 },
            { key: "edit", label: "", formatter: "myImageEdit" }
           ],
    images: {},
    mds: null,

    build: function( o ){ 

        var res = YAHOO.lang.JSON.parse( o.responseText );
        var el = o.argument.el;  
        var site = o.argument.site;  
        var skin = o.argument.skin;  

        YAHOO.mbi.imgedit.images.items = [];        

        if( res.imageMap["portal"] !== undefined ){
           YAHOO.mbi.imgedit.addRows( res.imageMap["portal"], 
                                      true, false, site, skin );
        }
        if( res.imageMap["skin-current"] !== undefined ){
           YAHOO.mbi.imgedit.addRows( res.imageMap["skin-current"], 
                                      false, true,site, skin );
        }
        if( res.imageMap["site-current"] !== undefined ){
           YAHOO.mbi.imgedit.addRows( res.imageMap["site-current"], 
                                      false, false, site, skin );
        }
        if( res.imageMap["skin"] !== undefined ){
            YAHOO.mbi.imgedit.addRows( res.imageMap["skin"], 
                                       true, true, site, skin );
        }

        YAHOO.mbi.imgedit.mds = new YAHOO.util.DataSource( YAHOO.mbi.imgedit.images );

        YAHOO.mbi.imgedit.mds.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        YAHOO.mbi.imgedit.mds.responseSchema = { resultsList: "items",
                                                 fields: [ {key: "image"},
                                                           {key: "global"},
                                                           {key: "skin"},
                                                           {key: "edit"}]
                                               };
        
        var myConfig = {
            draggableColumns: true,
            selectionMode:"standard",
            height:"10em",
            width:"100%"
        };

        var myImageEdit = function( elCell, oRecord, oColumn, oData ){
            var path = oData.path;
            var name = oData.name;
            var portal = oData.portal;
            var skin = oData.skin;
            var fname = path+"/"+name;
            var ignore = /^\/(.+)$/;

            if( ignore.test(fname) ){
                var res = ignore.exec(fname);
                fname = res[1];
            }
           
            var display = 'YAHOO.mbi.modal.iview(\'' + fname + '\'); return false';
            elCell.innerHTML = '<a href=\"\" onclick=\"'+ display +'\" >view</a>';

        };

        YAHOO.widget.DataTable.Formatter.myImageEdit = myImageEdit; 


        var myImgTable = new YAHOO.widget.ScrollingDataTable(
            el, 
            YAHOO.mbi.imgedit.coldef, 
            YAHOO.mbi.imgedit.mds,
            myConfig);            
    },

    addRows: function( rows, glob, skin, site, skn ){
        var path = rows.path;
        var images = rows.images;
        if( images !== undefined ){
            
            for( var i = 0; i < images.length; i++ ){        
                var row = { "image": images[i].name,
                            "global": glob ,
                            "skin": skin ,
                            "edit": { path: path, name: images[i].name, 
                                      portal: glob , skin: skin }
                          };
                YAHOO.mbi.imgedit.images.items.push( row );
            }
        }
    },

    init : function( site, skin, el ){
        var imgCallback = { cache:false, timeout: 5000, 
                            success:YAHOO.mbi.imgedit.build,
                            argument:{ el:el, site:site, skin:skin } };                  
        YAHOO.util.Connect.asyncRequest( 'GET', 
                                         'image?site=' + site 
                                         + '&skn=' + skin, imgCallback );
    }
};

