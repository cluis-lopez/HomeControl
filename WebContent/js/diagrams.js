/**
 * 
 */

var dias = {0:"Domingo", 1:"Lunes", 2:"Martes", 3:"Miercoles", 4:"Jueves", 5:"Viernes", 6:"Sabado"};
var meses = {0: "Enero", 1:"Febrero", 2:"Marzo", 3:"Abril", 4:"Mayo", 5:"Junio", 6:"Julio", 7:"Agosto", 8:"Septiembre", 9:"Octubre", 10:"Noviembre", 11:"Diciembre"};


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
	//pintaPrograma(canvas);
}

function pintaChart(datos, canvas){
	var numPoints = datos.length;
	var temps = new Array();
	var targetTemps = new Array();
	var times = new Array();
	var padx = 55;
	var pady = 45;
	
	var maxTemp = 0;
	var minTemp = 100;
	
	for (var i=0; i<numPoints; i++){
		temps[i] = parseFloat(datos[i].currentTemp.replace(',', '.'));
		if (temps[i] > maxTemp)
			maxTemp = temps[i];
		if (temps[i]< minTemp)
			minTemp = temps[i];
		targetTemps[i] = parseFloat(datos[i].targetTemp.replace(',', '.'));
		if (targetTemps[i] != 9999 && targetTemps[i] > maxTemp)
			maxTemp = targetTemps[i];
		if (targetTemps[i] != 9999 && targetTemps[i] < minTemp)
			minTemp = targetTemps[i];
		times[i] = new Date(datos[i].date + " " + datos[i].time);
	}
	
	var lastDate = times[numPoints-1].getDate() + "-"+meses[times[numPoints-1].getMonth()]+"-"+times[numPoints-1].getFullYear();
	
	//El diagrama principal
	var ctx = canvas.getContext("2d");
	ctx.fillStyle = "#FFFFFF";
	ctx.fillRect(0,0,canvas.width,canvas.height);
	
	ctx.strokeStyle = "#000000";
	ctx.setLineDash([]);
	ctx.font = "italic 20px Arial";
	ctx.fillStyle = "#000000";
	ctx.textAlign = "center";
	ctx.fillText("Estado el " + lastDate, canvas.width/2, 20);
	ctx.moveTo(padx, pady);
	ctx.lineTo(padx, canvas.height-pady) // Vertical
	ctx.lineTo(canvas.width-padx,canvas.height-pady); //Horizontal
	ctx.stroke();
	
	// El eje vertical ... solo ponemos 5 marcas
	maxTemp = (Math.floor(maxTemp/10)) * 10 + (maxTemp%10 <5 ? 5 : 10);
	if (maxTemp-minTemp >=5)
		minTemp = (Math.floor(minTemp/10)) * 10 + (minTemp%10 <5 ? 0 : 5);
	else
		minTemp = maxTemp-5;
	var stepUp = (canvas.height - 2 * pady)/5; //5 valores en el eje Y
	ctx.font = "12px Arial";
	ctx.textAlign = "right";
	ctx.fillStyle = "#000000";
	ctx.strokeStyle = "#000000";
	ctx.lineWidthl = 2;
	for (var i=0; i<=5; i++){
		ctx.moveTo(padx, canvas.height-pady - stepUp*i);
		ctx.lineTo(padx-10, canvas.height-pady - stepUp*i);
		ctx.fillText(minTemp + i* ((maxTemp-minTemp)/5) + " \260C", padx - 20, canvas.height-pady - stepUp*i +7);
	}
	ctx.stroke();
	
	// El eje horizontal: ponemos 5 marcas horarias
	ctx.font = "10px Arial";
	ctx.textAlign = "center";
	ctx.fillStyle = "#000000";
	ctx.strokeStyle = "#000000";
	ctx.lineWidth = 2;
	var stepRight = (canvas.width - 2 * padx) / 5;
	var step = parseInt(numPoints / 5);
	for (var i = numPoints - 1; i>=0; i -=step){
		var hour = (times[i].getHours().toString().length == 1 ? "0"+times[i].getHours() : times[i].getHours())
		var min = (times[i].getMinutes().toString().length == 1 ? "0"+times[i].getMinutes() : times[i].getMinutes())
		ctx.moveTo(timeToPixel(times[i]), canvas.height-pady);
		ctx.lineTo(timeToPixel(times[i]), canvas.height-pady+10);
		ctx.fillText(hour + ":" + min, timeToPixel(times[i]), canvas.height-pady+20);
		ctx.stroke();
	}
	
	// La gráfica de la temperatura
	ctx.beginPath();
	ctx.strokeStyle = "#0000FF";
	ctx.lineWidth = 3;
	ctx.moveTo(timeToPixel(times[0]), tempToPixel(temps[0]));
	for (var i = 1; i<numPoints; i++){
		ctx.lineTo(timeToPixel(times[i]), tempToPixel(temps[i]));
	}
	ctx.stroke();
	
	// Temperatura Objetivo
	//Comenzamos con el primer punto != de 9999
	var i;
	for ( i = 0; i< numPoints; i++)
		if (targetTemps[i] != 9999)
			break;
	ctx.beginPath();
	ctx.moveTo(timeToPixel(times[i]), tempToPixel(targetTemps[i]));
	ctx.setLineDash([4, 2]);
	ctx.lineDashOffset = 0;
	ctx.strokeStyle = "#FF0000";
	ctx.lineWidth = 2;
	
	for (i; i<numPoints-1; i++){
		if (targetTemps[i] == 9999)
			continue;
		ctx.moveTo(timeToPixel(times[i]), tempToPixel(targetTemps[i]));
		ctx.lineTo(timeToPixel(times[i+1]), tempToPixel(targetTemps[i]));
	}
	ctx.stroke();
	
	// La gráfica del estado de la caldera
	ctx.fillStyle = "rgba(0, 255, 0, 0.5)";
	step = (canvas.width - 2 * padx)/numPoints;
	for (var i = 0; i<numPoints; i++){
		if (datos[i].state == "1"){
			ctx.fillRect(timeToPixel(times[i]), tempToPixel(maxTemp), step, canvas.height - 2 * pady );
		}
	}
	
	
	function timeToPixel(time){
		//console.log("time2pix :" + parseInt(padx + (canvas.width - 2 * padx) * ((time.getTime() - times[0].getTime()) / (times[numPoints-1].getTime() - times[0].getTime()))));
		return parseInt(padx + (canvas.width - 2 * padx) * ((time.getTime() - times[0].getTime()) / (times[numPoints-1].getTime() - times[0].getTime())));
	}
	
	function tempToPixel(t){
		//console.log("temp2pix : "+ (pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)))));
		return pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)));
	}
}

function pintaPrograma(data, canvas){
	var colores = {15: "azul", 17: "azul templado", 20: "templado", 22: "guachi", 25: "calor", 30: "tasllocu"};
	var maxTemp =30;
	var minTemp = 10;
	ctx = canvas.getContext("2d");
	ctx.fillStyle = "#FFFFFF";
	ctx.fillRect(0,0,canvas.width,canvas.height);
	padx = 55;
	pady = 45;
	ctx.font = "20px Arial";
	ctx.fillStyle = "#000000";
	ctx.textAlign = "center";
	
	d = new Date().getDay();
	ctx.fillText(dias[d], canvas.width/2, pady+5);
	
	//Linea vertical. 5 marcas enre 10 y 30 grados cada 5 grados
	ctx.moveTo(padx, pady);
	ctx.lineTo(padx, canvas.height-pady) // Vertical
	var stepUp = (canvas.height - 2 * pady)/5; //5 valores en el eje Y
	for (var i=0; i<=5; i++){
		ctx.moveTo(padx, canvas.height-pady - stepUp*i);
		ctx.lineTo(padx-10, canvas.height-pady - stepUp*i);
		ctx.font = "12px Arial";
		ctx.textAlign = "right";
		ctx.fillText(minTemp + i* ((maxTemp-minTemp)/5) + " \260C", padx - 20, canvas.height-pady - stepUp*i +7);
		ctx.fillStyle = "#000000";
		ctx.lineWidth = 2;
		ctx.stroke();
	}
	
	// El eje horizontal: ponemos 24 marcas horarias
	ctx.moveTo(padx, canvas.height-pady);
	ctx.lineTo(canvas.width-padx,canvas.height-pady); //Horizontal
	var stepRight = (canvas.width - 2 * padx) / 24;
	for (var i = 0; i<=24; i++){
		ctx.moveTo(padx + i*stepRight, canvas.height-pady);
		ctx.lineTo(padx + i*stepRight, canvas.height-pady+10);
		ctx.font = "10px Arial";
		ctx.textAlign = "center";
		ctx.fillStyle = "#000000";
		ctx.lineWidth = 2;
		var label = "" + i;
		if (label.length <2)
			label = "0" + label;
		ctx.fillText(label, padx + i*stepRight, canvas.height-pady+20);
		ctx.stroke();
	}
	
	datos = data.calendario.dias[d]; //Array [24][2] con las temperaturas programas para el día
	stepRight = (canvas.width - (2 * padx)) / 48; //48 elementos en el gráfico (24x2)
	ctx.moveTo(padx, canvas.height-pady); // El punto de partida del chart
	ctx.lineWidth = "1";
	ctx.strokeStyle = "black";
	for (var i =0; i< 24; i++){
		ctx.rect(padx + (i * stepRight), (canvas.height-pady) - tempToPixel(datos[i][0]), stepRight, tempToPixel(datos[i][0]));
		ctx.rect(padx + ((i + 1)* stepRight), (canvas.height-pady) - tempToPixel(datos[i][1]), stepRight, tempToPixel(datos[i][1]));
	}
	ctx.stroke();
	
	function tempToPixel(t){
		//console.log("temp2pix : "+ (pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)))));
		return pady + ((canvas.height-2*pady) * ((maxTemp - t) / (maxTemp - minTemp)));
	}
}