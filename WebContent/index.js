	/* Toggle between showing and hiding the navigation menu links when the user clicks on the hamburger menu / bar icon */
	function myFunction() {
	  var x = document.getElementById("myLinks");
	  if (x.style.display === "block") {
	    x.style.display = "none";
	  } else {
	    x.style.display = "block";
	  }
	}

$(document).ready(function() {
	
	 function monitorRefresh(){
		 $("#refrescando").css("display", "block");
		 $.get("ServerTest", function(data, status) {
				$("#estado").text(data.estado);
				$("#currentTemp").text(data.currentTemp);
				$("#currentHum").text(data.currentHum);
				var modo = "";
				switch (data.modeOp) {
				case 0:
					modo = "OFF";
					break;
				case 1:
					modo = "MANUAL";
					break;
				case 2:
					modo = "PROGRAMADO";
					break;
				}
				$("#modeOp").text(modo);
				$("#tempTarget").text(data.tempTarget);
			});
	 }
	 
 });
	