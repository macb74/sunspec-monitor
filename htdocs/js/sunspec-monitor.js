function powerChart(chart, values) {
	myChart = new Chart(chart, {
		type : 'line',
		data : {
			labels: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],
			datasets: [{
				label: false,
				fill: false,
				lineTension: 0.1,
				backgroundColor: "rgba(75,192,192,0.4)",
				borderColor: "#31708f",
				borderCapStyle: 'butt',
				borderDash: [],
				borderDashOffset: 0.0,
				borderJoinStyle: 'miter',
				pointBorderColor: "#31708f",
				pointBackgroundColor: "#fff",
				pointBorderWidth: 1,
				pointHoverRadius: 5,
				pointHoverBackgroundColor: "rgba(75,192,192,1)",
				pointHoverBorderColor: "rgba(220,220,220,1)",
				pointHoverBorderWidth: 2,
				pointRadius: 1,
				pointHitRadius: 10,
				data: values,
				cubicInterpolationMode: "monotone",
				spanGaps: false
			}],
		},
		options : {
			responsive: true,
			maintainAspectRatio: false,
			scales: {
				xAxes: [{
					display: false
					}]
				},
				legend: {
					display: false,
					labels: {
						display: false
					}
				}
			}
		});
	return myChart;
}


function refreshChart(chart, values) {
	chart.data.datasets[0].data = values;
	console.log(values);
	chart.update();
}


function getSunspecData() {
	var jqxhr = $.getJSON( "/modbus/" );
	jqxhr.done(function( data ) {	
		$( '#I_DC_Power' ).text(Math.round(data.I_DC_Power * getDivisor(data.I_DC_Power_Scale)));
		$( '#M_AC_Power' ).text(Math.round(data.M_AC_Power * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power1' ).text(Math.round(data.M_AC_Power1 * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power2' ).text(Math.round(data.M_AC_Power2 * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power3' ).text(Math.round(data.M_AC_Power3 * getDivisor(data.M_AC_Power_Scale)));
		
		diff();
		
		data1.push($( '#M_AC_Power1' ).text());
		if(data1.length > 21) { data1.shift(); }

		data2.push($( '#M_AC_Power2' ).text());
		if(data1.length > 21) { data1.shift(); }
		
		data3.push($( '#M_AC_Power3' ).text());
		if(data1.length > 21) { data1.shift(); }
		
		refreshChart(chart1, data1 );
		refreshChart(chart2, data2 );
		refreshChart(chart3, data3 );
		setTimeout( function() { getSunspecData() }, 5000);
	});
}

function snap() {
	$( '#I_DC_Power_snap').text($( '#I_DC_Power').text());
	$( '#M_AC_Power_snap').text($( '#M_AC_Power').text());
	$( '#M_AC_Power1_snap').text($( '#M_AC_Power1').text());
	$( '#M_AC_Power2_snap').text($( '#M_AC_Power2').text());
	$( '#M_AC_Power3_snap').text($( '#M_AC_Power3').text());
	diff();
}

function diff() {
	$( '#I_DC_Power_diff').text($( '#I_DC_Power_snap').text() - $( '#I_DC_Power').text());
	$( '#M_AC_Power_diff').text($( '#M_AC_Power_snap').text() - $( '#M_AC_Power').text());
	$( '#M_AC_Power1_diff').text($( '#M_AC_Power1_snap').text() - $( '#M_AC_Power1').text());
	$( '#M_AC_Power2_diff').text($( '#M_AC_Power2_snap').text() - $( '#M_AC_Power2').text());
	$( '#M_AC_Power3_diff').text($( '#M_AC_Power3_snap').text() - $( '#M_AC_Power3').text());
}

function getDivisor(i) {
	d = 1;
	switch(i) {
	    case -1:
	        d = 0.1;
	        break;
	    case -2:
	        d = 0.01;
	        break;
	}
	return d
}
