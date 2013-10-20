YAHOO.namespace("imex");

YAHOO.imex.autocom = {

    init: function( e, obj ) {
        
        var acInp = obj.inp;
        var acCnt = obj.cnt;
        var acOpt = obj.opt;
        
        // create DataSource

        var oDS = new YAHOO.util.LocalDataSource(
            YAHOO.imex.autocom.testArray );

        var dts = new YAHOO.util.XHRDataSource("acom?op."+acOpt+"=ac&opp.q="); 
        dts.responseType = YAHOO.util.DataSource.TYPE_JSON; 
        dts.responseSchema = {
            resultsList: "acom",
            fields : ["name"]};
        
        // instantiate the AutoComplete

        var acom = new YAHOO.widget.AutoComplete( acInp, acCnt, dts );
        acom.prehighlightClassName = "yui-ac-prehighlight";
        acom.useShadow = true;
        acom.generateRequest = function( query ){
            return query + "%25";
        };
    }
};

