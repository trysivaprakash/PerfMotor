document.addEventListener('DOMContentLoaded', function() {

  var headSection = document.getElementById("perf-motor-ui");

  var htmlContent = '<div class="logo-area-outer-styleme"><div class="header-styleme">'
      +'<table class="logo-table-styleme"><tr><td><img src="per-motor-logo.jpg" alt="Perf-Motor Logo" class="logo-styleme"></td>'
      +'<td valign="bottom"><div class="product-name-styleme">PERF MOTOR</div></td></tr></table>'
      + '</div></div>'

  var confDetails = '<div class="config-styleme">'
      + '<h4 id="myAppHeader" class="opblock-tag">Performance Testing Configurations</h4>'
	  + '<span><div id="myConfigTableDiv" class="opblock opblock-conf"><table>'

	  + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = "Expected response status for requests">Expected response status</span>'
      + '</div></td>'
      + '<td><input type="text" id="expectedStatus" value="200"/></td></tr>'

	  + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = "Expected maximum response time for requests">Expected max response time</span>'
      + '</div></td>'
      + '<td><input type="text" id="expectedMaxResponseTime" value="500"/> milliseconds</td></tr>'

      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = "Injects a given number of users at once">Users at once</span>'
      + '</div></td>'
      + '<td><input type="text" id="atOnceUsers" value="0"/></td></tr>'

      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = "Injects a given number of users with a linear ramp over a given duration">Ramp Users</span>'
      + '</div></td>'
      + '<td><input type="text" id="rampUsers" value="0"/> over</td><td><input type="text" id="rampUsersOver" value="0 seconds"/></td></tr>'

      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = "Injects users at a constant rate, defined in users per second, during a given duration. Users will be injected at regular intervals">Constant Users Per Second</span>'
      + '</div></td>'
      + '<td><input type="text" id="constantUsersPerSec" value="20"/> during</td><td><input type="text" id="constantUsersPerSecDuring" value="10 seconds"/></td></tr>'

      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method" title = " Injects users from starting rate to target rate, defined in users per second, during a given duration. Users will be injected at regular intervals">Ramp Users Per Second</span>'
      + '</div></td>'
      + '<td><input type="text" id="rampUsersPerSecRate1" value="0"/> to</td><td><input type="text" id="rampUsersPerSecRate2" value="0"/> during</td><td><input type="text" id="rampUsersPerSecDuring" value="0 seconds"/></td></tr>'

      + '<tr><td><div class="opblock-summary">'
      + '<span class="opblock-summary-method">Feeder</span>'
      + '</div></td>'
      + '<td><input type="file" id="fileinput" onchange="readSingleFile(event)" name="csv feeder file"/></td></tr>'
      + '</table></div></span></div>';

  headSection.innerHTML = htmlContent + confDetails;

}, false);
