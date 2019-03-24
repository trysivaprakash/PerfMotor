document.addEventListener('DOMContentLoaded', function() {

  var headSection = document.getElementById("perf-motor-ui");

  var htmlContent = '<div class="logo-area-outer-styleme"><div class="header-styleme"><table class="logo-table-styleme"><tr><td><img src="per-motor-logo.jpg" alt="Perf-Motor Logo" class="logo-styleme"></td><td valign="bottom"><div class="product-name-styleme">PERF MOTOR</div></td></tr></table>'
      + '</div></div>'

  var confDetails = '<div class="config-styleme">'
      + '<h4 id="myAppHeader" class="opblock-tag">Performance Testing Configuration</h4>'
	  + '<span><div id="myConfigTableDiv" class="opblock opblock-conf"><table>'
      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method">At Once Users</span>'
      + '</div></td>'
      + '<td><input type="text" id="nbrOfReq" value="100"/></td></tr>'
      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method">Constant Users Per Sec</span>'
      + '</div></td>'
      + '<td><input type="text" id="nbrOfLoops" value="10"/></td></tr>'
      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method">Feeder</span>'
      + '</div></td>'
      + '<td><input type="file" id="fileinput" onchange="readSingleFile(event)" name="csv feeder file"/></td></tr>'
      + '</table></div></span></div>';

  headSection.innerHTML = htmlContent + confDetails;

}, false);
