var jqpage = function( init ) {

  var jqpage = {};
 
  jqpage.edt = init.edt;

  jqpage.start = function() {

   $("#edit_acc").tabs("#edit_acc div.pane",
                        {tabs: 'h2'});
   var tapi = $("#edit_acc").tabs();
   switch (jqpage.edt) {
      case "ATR":
        tapi.click(0);
        break;
      case "SRC":
        tapi.click(1);
        break;
      default:
        tapi.click(2);
    }
  };
  return jqpage;
};
