
YAHOO.namespace("imex");

YAHOO.imex.userprefmgr = {
    traverse:  function (o,func) {
        var string = '';
        for (var i in o) {                                 
            if(i =="option-def")
            {
                html += "\n<ul>\n"
                for(var j = 0; j < o.options.length; j++)
                {
                    string += "<li>" + func.apply(this,[j,o["option-def"][o.options[j]], o["options"]]); 
                    //going to step down in the object tree!!
                    string += this.traverse(o["option-def"][o.options[j]],func) + "</li>\n";
                }
                
                html+="</ul>\n"
            }
        }
        return string
    },
    
    init: function( init ){
        var Success = function(response)
         {
            var process = function (key,value, options) {
                if(typeof value.value != "undefined")
                {
                    if(value.value )
                    {
                        var checkboxT = '<input type="checkbox" checked><strong>True</strong></input>';
                        var checkboxF = '<input type="checkbox">False</input>';
                    }
                    else
                    {
                        var checkboxT = '<input type="checkbox">True</input>';
                        var checkboxF = '<input type="checkbox" checked><strong>False</strong></input>';
                    }
                    return  value.label + ": " + checkboxT + " " +  checkboxF;
                }
            }

            console.log("sucess");
            var testing;
            testing = YAHOO.lang.JSON.parse(response.responseText);
            testing = YAHOO.lang.JSON.parse(testing.preferences);
            console.log(testing);     

            var form = document.getElementById('userprefmgr');
            var html = '';
            //var html = '<form id="userprefmgrForm" name="userprefmgrForm" action="/icentral/userprefmgr" method="post">';
            //html += '<input type="hidden" name="op.update" value="update" id="userprefmgr_op" />'; 
            html += YAHOO.imex.userprefmgr.traverse(testing,process);
            console.log (html);
            form.innerHTML = html + form.innerHTML ;
             
                             
         };
        var Fail = function ( o ) {
            alert( "AJAX Error update failed: id=" + o.argument.id ); 
        };
        var callback = { cache:false, timeout: 5000, 
                         success: Success,
                         failure: Fail
                         }; 
                         
        try{
            YAHOO.util.Connect
            .asyncRequest( 'GET', 
                           //'userprefmgr?id=' + init.loginid +'&op.view=update', 
                           'userprefmgr?id=30' +'&op.view=true', 
                           callback );        
        } catch (x) {
            alert("AJAX Error:"+x);
        }
    },
    update: function(  ){
        console.log("In function update");
    }
}
