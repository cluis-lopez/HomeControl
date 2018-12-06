/**
 * 
 */

var dias = {0: "Domingo", 1:"Lunes", 2:"Martes", 3:"Miercoles", 4:"Jueves", 5:"Viernes", 6:"Sabado"};
var meses = {0: "Enero", 1:"Febrero", 2:"Marzo", 3:"Abril", 4:"Mayo", 5:"Junio", 6:"Julio", 7:"Agosto", 8:"Septiembre", 9:"Octubre", 10:"Noviembre", 11:"Diciembre"};

/**
 * Edit to match the name of the containers and canvas
 */

$("#cont1").resize(resizeChart($("#cont1"), $("#chart")[0]));
$("#cont2").resize(resizePrograma($("#cont2"), $("#programa")[0]));

function resizeChart(con, canvas){
	canvas.height = 350;
	var aspect = 350/700;
	var width = con.width();
	if (width < 380)
		canvas.width = 380;
	else
		canvas.width = Math.round(con.height() / aspect);
	// pintaChart(canvas);
	// La funcion que genera el gráfico se invoka desde Ajax
}

function resizePrograma(con, canvas){
	canvas.height = 350;
	var aspect = 350/700;
	var width = con.width();
	if (width < 380)
		canvas.width = 380;
	else
		canvas.width = Math.round(con.height() / aspect);
	pintaPrograma(canvas);
}

function pintaChart(canvas){
	var numPoints = hist.length;
	var temps = new Array();
	var times = new Array();
	var states = new Array();
	var padx = 55;
	var pady = 45;
	
	for (var i=0; i<numPoints; i++){
		temps[i] = parseFloat(hist[i].currentTemp);
		times[i] = new Date(hist[i].date + " " + hist[i].time);
		states[i] =hist[i].state;
	}
	
	var lastDate = times[numPoints-1].getDay() + "-"+meses[times[numPoints-1].getMonth()]+"-"+times[numPoints-1].getFullYear();
	
	//El diagrama principal
	var ctx = canvas.getContext("2d");
	ctx.fillStyle = "#FFFFFF";
	ctx.fillRect(0,0,canvas.width,canvas.height);
	ctx.font = "italic 20px Arial";
	ctx.fillStyle = "#000000";
	ctx.textAlign = "center";
	ctx.fillText("Estado hasta el " + lastDate, canvas.width/2, 20);
	ctx.moveTo(padx, pady);
	ctx.lineTo(padx, canvas.height-pady) // Vertical
	ctx.lineTo(canvas.width-padx,canvas.height-pady); //Horizontal
	ctx.stroke();
	
	// El eje vertical ... solo ponemos 5 marcas
	var maxTemp = Math.max.apply(Math, temps);
	var minTemp = Math.min.apply(Math, temps);
	maxTemp = (Math.floor(maxTemp/10)) * 10 + (maxTemp%10 <5 ? 5 : 10);
	if (maxTemp-minTemp >=5)
		minTemp = (Math.floor(minTemp/10)) * 10 + (minTemp%10 <5 ? 0 : 5);
	else
		minTemp = maxTemp-5;
	var stepUp = (canvas.height - 2 * pady)/5; //5 valores en el eje Y
	for (var i=0; i<=5; i++){
		ctx.moveTo(padx, canvas.height-pady - stepUp*i);
		ctx.lineTo(padx-10, canvas.height-pady - stepUp*i);
		ctx.font = "12px Arial";
		ctx.textAlign = "right";
		ctx.fillText(minTemp + i* ((maxTemp-minTemp)/5) + " \260C", padx - 20, canvas.height-pady - stepUp*i +7);
		ctx.fillStyle = "#000000";
		ctx.lineWidthl = 2;
		ctx.stroke();
	}
	
	// El eje horizontal: ponemos 10 marcas horarias
	var stepRight = (canvas.width - 2 * padx) / 10;
	for (var i = 0; i<=10; i++){
		ctx.moveTo(padx + i*stepRight, canvas.height-pady);
		ctx.lineTo(padx + i*stepRight, canvas.height-pady+10);
		ctx.font = "10px Arial";
		ctx.textAlign = "center";
		ctx.fillStyle = "#000000";
		ctx.lineWidthl = 2;
		var label = times[i*parseInt(numPoints/10)].getHours() +":"+times[i*parseInt(numPoints/10)].getMinutes();
		ctx.fillText(label, padx + i*stepRight, canvas.height-pady+20);
		ctx.stroke();
	}
	
	// La gráfica de la temperatura
	for (var i = 0; i<numPoints-1; i++){
		ctx.beginPath();
		ctx.moveTo(timeToPixel(times[i]), tempToPixel(temps[i]));
		ctx.lineTo(timeToPixel(times[i+1]), tempToPixel(temps[i+1]));
		ctx.strokeStyle = "#0000FF";
		ctx.lineWidth = 3;
		ctx.stroke();
	}
	
	// La gráfica del estado de la caldera
	
	function timeToPixel(time){
		//console.log("time2pix :" + parseInt(padx + (canvas.width - 2 * padx) * ((time.getTime() - times[0].getTime()) / (times[numPoints-1].getTime() - times[0].getTime()))));
		return parseInt(padx + (canvas.width - 2 * padx) * ((time.getTime() - times[0].getTime()) / (times[numPoints-1].getTime() - times[0].getTime())));
	}
	
	function tempToPixel(t){
		//console.log("temp2pix : "+ (pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)))));
		return pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)));
	}
}