<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style>

#pubtab table {
   width: 100%;
}

.pubtab th {
    vertical-align: middle;
    text-align: center;
    font-weight: bold;
     
}

#pubtab .yui-dt-label .yui-dt-sortable {
    vertical-align: middle;
    text-align: center;
    font-weight: bold;
}

.yui-skin-sam .yui-dt th {
    background:#E8E8EA;
    text-align: center;
    vertical-align: middle;
    font-weight: bold;    
}

.yui-skin-sam .yui-dt-liner-null {
    margin: 0px;
    padding: 0px;
    border-width: 0px;
    border-style: none;
}

.yui-skin-sam table.yui-table-inner {
    border-width: 0px;
    border-style: none;
    padding: 0px;
    margin: 0px;
}

.yui-skin-sam .yui-dt th .yui-dt-label {
  text-align: center;
  margin-left: 10px;
}

.yui-skin-sam  .yui-dt-even th.yui-table-inner-top {
    width: 5%;
    padding: 3px; 
    border-style: none none solid none;
    border-width: 0 0 thin 0; 
    border-color: #B8B8B8;
    background: #DDDDDD;
    text-align: right; 
    white-space: nowrap;
}

.yui-skin-sam  .yui-dt-odd th.yui-table-inner-top {
    width: 5%;
    padding: 3px; 
    border-style: none none solid none;
    border-width: 0 0 thin 0; 
    border-color: #B8B8B8;
    background: #D0E2FF;
    text-align: right; 
    white-space: nowrap;
}

.yui-skin-sam  td.yui-table-inner-top {
    padding: 3px;
    border-style: none none solid none;
    border-width: 0 0 thin 0; 
    border-color: #BBBBBB;
}

.yui-skin-sam  tr.yui-dt-first  td.yui-table-inner-top {
    padding: 3px;
    border-style: none none solid none;
    border-width: 0 0 thin 0;
    border-color: #BBBBBB;

}

.yui-skin-sam  .yui-dt-even th.yui-table-inner-bottom {
    padding: 3px;
    border-style: none;
    border-width: 0; 
    border-color: #A8A8A8;
    text-align: right;    
    background: #DDDDDD;
    white-space: nowrap;
}

.yui-skin-sam  .yui-dt-odd th.yui-table-inner-bottom {
    padding: 3px;
    border-style: none;
    border-width: 0; 
    border-color: #A8A8A8;
    background: #D0E2FF;
    text-align: right;  
    white-space: nowrap;
}

.yui-skin-sam tr.yui-dt-first th.yui-table-inner-bottom {
    border-style: none;
    border-width: 0;
    border-color: #A8A8A8;
    text-align: right;
    white-space: nowrap;
}

.yui-skin-sam  td.yui-table-inner-bottom {
    padding: 3px;
    border-style: none;
    border-width: 0; 
    border-color: #A8A8A8;
}

.yui-skin-sam tr.yui-dt-first td.yui-table-inner-bottom {
    border-style: none;
    border-width: 0;
    border-color: #A8A8A8;
}

.yui-skin-sam .yui-dt-center {
  text-align: center;
}

.yui-skin-sam th.yui-dt-col-date {
    border-top-style: solid;
    border-top-width: thin;
    border-color: #C8C8C8;   
}

.yui-skin-sam th.yui-dt-col-owner {
    border-top-style: solid;
    border-top-width: thin;
    border-color: #C8C8C8;
}

.yui-skin-sam div.dt-pag-nav {
    border-top-style: solid;
    border-top-width: thin;
    border-color: #C8C8C8;
}

#myAutoCompleteEditor { 
           width:12em;
           padding-bottom:1.7em; 
} 

.yui-skin-sam td.filter-container {
   vertical-align: middle;
}

.yui-skin-sam em.yui-button-state {
   font-style: normal; 
   display: block; 
   text-align: left; 
   white-space: nowrap; 
   width: 7em; 
   overflow: hidden; 
   text-overflow: ellipsis; 
}

.yui-skin-sam em.yui-button-partner {
   font-style: normal; 
   display: block; 
   text-align: left; 
   white-space: nowrap; 
   width: 9em; 
   overflow: hidden; 
   text-overflow: ellipsis; 
}

</style> 

<h1>Publication Manager</h1>
<s:if test="id > 0">
 <t:insertDefinition name="pubedit"/>
 <br/><br/>
</s:if>
<s:else>
 
<div class="yui-skin-sam" width="100%">
 <center>
  <table width="99%">
   <tr>
    <td><div id="dt-pag-nav"></div></td>
    <td width="95%">&nbsp;</td>
    <th width="1%">Filter:</th>
    <td class="filter-container"><label id="state-button-container"/></td>
    <th width="1%">and</th>
    <td class="filter-container"><label id="partner-button-container"/></td>
    <th width="1%" nowrap>and Editor:</th>
    <td valign="middle">
     <div id="myAutoCompleteEditor">     
      <input id="myEditorInput" type="text"> 
      <div id="myEditorContainer"></div> 
     </div> 
    </td>
   </tr> 
  </table>
 </center>    
 <div id="pubtab" width="100%" class="pubtab"></div>
 <table width="100%" cellpadding="5">
  <s:form theme="simple" action="pubmgr">
   <tr>
    <td align="center" width="5%">
     <s:submit theme="simple" name="op.ldel" value="DROP" />
    </td>
    <td align="right">
     <b>PMID:</b> <s:textfield theme="simple" name="pub.pmid" size="24" maxLength="64"/>
    </td>
    <td colspan="1" align="center" width="5%">
     <s:submit theme="simple" name="op.eadd" value="ADD" />
    </td>
   </tr>
  </s:form>
 </table>
</div>

<script type="text/javascript">
  YAHOO.util.Event.addListener(window, "load", function() {
    YAHOO.example.NestedHeaders = function() {

     var stateSel = [ 
           { text: "---ANY---", value: "none" }, 
           { text: "NEW", value: "new" }, 
           { text: "Reserved", value: "reserved" },
           { text: "Processed", value: "processed" },
           { text: "Released", value: "released" } 
       ]; 
        
     var stateButton = new YAHOO.widget.Button({  
                               id: "state-button",  
                               name: "state-button", 
                               label: "<em class=\"yui-button-state\">---Status---</em>", 
                               type: "menu",   
                               menu: stateSel,  
                               container: "state-button-container" }); 
    
    var partnerSel = [
           { text: "---ANY---", value: "none" },
           { text: "DIP", value: "dip" },
           { text: "IntAct", value: "intact" },
           { text: "MINT", value: "mint" },
           { text: "MPIDB", value: "mpidb" }
       ];

     var partnerButton = new YAHOO.widget.Button({
                               id: "partner-button",
                               name: "partner-button",
                               label: "<em class=\"yui-button-partner\">---Imex Partner---</em>",
                               type: "menu",
                               menu: partnerSel,
                               container: "partner-button-container" });

     var editorArray = ["lukasz", "skerrien", "hhm", "doe_99"]; 
     var oACS = new YAHOO.util.LocalDataSource( editorArray );  
     oACS.responseSchema = {fields : ["editor"]};

     var oAC = new YAHOO.widget.AutoComplete("myEditorInput", "myEditorContainer", oACS); 
     oAC.prehighlightClassName = "yui-ac-prehighlight"; 
     oAC.useShadow = true; 
         
     this.myPubFormatter = function(elLiner, oRecord, oColumn, oData) { 
          YAHOO.util.Dom.replaceClass(elLiner, "yui-dt-liner", "yui-dt-liner-null"); 
          elLiner.innerHTML = '<table width="100%" class="yui-table-inner">' +
                              '<tr><th class="yui-table-inner-top">Author(s)</th>' +
                              '<td class="yui-table-inner-top">' + oRecord.getData("author") + '</td></tr>' +
                              '<tr><th class="yui-table-inner-bottom">Title</th>' + 
                              '<td class="yui-table-inner-bottom">' + oRecord.getData("title") + '</td></tr></table>';        
     }; 
      
     this.myElinkFormatter = function(elLiner, oRecord, oColumn, oData) {
          YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
          elLiner.innerHTML = '<a href="pubmgr?id=' + oRecord.getData( "id" ) + '">details</a>';
     };
     
     this.myDateFormatter = function(elLiner, oRecord, oColumn, oData) {
          YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
          elLiner.innerHTML = oRecord.getData( "date" ) + '<br/>' + oRecord.getData( "time" );
     };
     
     this.myCenterFormatter = function(elLiner, oRecord, oColumn, oData) {
          YAHOO.util.Dom.addClass(elLiner, "yui-dt-center");
          elLiner.innerHTML = oData; 
     }    
      // Add the custom formatter to the shortcuts 
      YAHOO.widget.DataTable.Formatter.publication = this.myPubFormatter; 
      YAHOO.widget.DataTable.Formatter.elink = this.myElinkFormatter; 
      YAHOO.widget.DataTable.Formatter.crt = this.myDateFormatter; 
      YAHOO.widget.DataTable.Formatter.center = this.myCenterFormatter; 

      // Create the DataSource 
      var myDataSource = new YAHOO.util.DataSource("pubmgr?op.ppg=44&"); 
      myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON; 
      myDataSource.responseSchema = { 
        resultsList: "records.records", 
        fields: ["id","author","title","pmid","imexId","owner","state","date","time"], 
        metaFields: { 
          totalRecords: "records.totalRecords", 
          paginationRecordOffset : "records.startIndex", 
          paginationRowsPerPage : "records.pageSize", 
          sortKey: "records.sort", 
          sortDir: "records.dir" 
        } 
      }; 

      // Create the Paginator
      var myPaginator = new YAHOO.widget.Paginator({
        containers : ["dt-pag-nav"],
        template : "{PreviousPageLink} {CurrentPageReport} {NextPageLink} {RowsPerPageDropdown}",
        pageReportTemplate : "Showing items {startIndex} - {endIndex} of {totalRecords}",
        rowsPerPageOptions : [5,10,20,50]
      });
    
    var myColumnDefs = [
          {key:"del",label:"", formatter:"checkbox", sortable:false, resizeable:false },
          {key:"pub", label:"Publication", sortable:true, resizeable:true, maxAutoWidth:1000, formatter:"publication"},
          {key:"pmid", label:"PMID", sortable:true, resizeable:true, formatter:"center"},
          {key:"imexId", label:"ImexId", sortable:true, resizeable:true, formatter:"center"},
          {key:"state", label:"Status", sortable:true, resizeable:false, formatter:"center"},
          {label:"Submission", 
              children:[
                {key:"date",  label:"Date",sortable:true, resizeable:false, formatter:"crt"},
                {key:"owner", label:"Owner",sortable:true, resizeable:false, formatter:"center"}
              ]
          },
          {key:"detail", label:"",sortable:false, resizeable:true, formatter:"elink"}
        ];

        var myRequestBuilder = function(oState, oSelf) {
           // Get states or use defaults
           oState = oState || {pagination:null, sortedBy:null};
           var sort = (oState.sortedBy) ? oState.sortedBy.key : "id";
           var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "false" : "true";
           var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
           var results = (oState.pagination) ? oState.pagination.rowsPerPage : 10;

          // Build custom request
           return  "opp.skey=" + sort +
                   "&opp.sdir=" + dir +
                   "&opp.off=" + startIndex +
                   "&opp.max=" + results;
        };

    // DataTable configurations
    var myConfig = {
        paginator : new YAHOO.widget.Paginator({
                      containers: ["dt-pag-nav"], 
                      rowsPerPage: 25, 
                      template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE, 
                      rowsPerPageOptions: [5,10,25,50,100], 
                      pageLinks: 5 
                  }), 
        //paginator : new YAHOO.widget.Paginator({ rowsPerPage:5, rowsPerPageOptions : [5,10,20,50] }),        
        initialRequest: "opp.off=0&opp.max=25",
        dynamicData : true,
        draggableColumns: true,
        generateRequest : myRequestBuilder
    };

    // Instantiate DataTable
    var myDataTable = new YAHOO.widget.DataTable(
        "pubtab", myColumnDefs, myDataSource, myConfig
    );
    
    // Show loading message while page is being rendered
    myDataTable.showTableMessage(myDataTable.get("MSG_LOADING"), YAHOO.widget.DataTable.CLASS_LOADING);

    myDataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) { 
              oPayload.totalRecords = oResponse.meta.totalRecords;
              return oPayload; 
          } 
           
    return { 
      ds: myDataSource, 
      dt: myDataTable 
    };

  }();
});
</script>


 <br/>
 <br/>
</s:else>