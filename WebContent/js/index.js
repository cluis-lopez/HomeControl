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
	
	 monitorRefresh();
	
	 function monitorRefresh(){
		 $("#refrescando").css("display", "block");
		 $.get("ControlServlet", function(data, status) {
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
				$("#refrescando").css("display", "none");
			});
		 
		 lastChart();
	 }
	 
	 
	 function lastChart(){
	 	$.get("HistoryServlet?mode=last", function(responseJson) {
			chartdata = responseJson;
			$("#lastchart").html("");
			chart = Morris.Line({
		  	  	element: 'lastchart',
		  	 	data: chartdata,
		  	  	xkey: 'time',
		  	  	ykeys: ['currentTemp'],
		  	  	postUnits: ' ºC',
		  	  	resize: true,
		  	  	labels: ['Temperatura']
		  		});
		});
	};
	 
 });
	