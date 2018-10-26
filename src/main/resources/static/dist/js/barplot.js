function barplot(data,set){
var canvas = document.createElement('canvas');
var ctx = canvas.getContext("2d");
var font = "14";
ctx.font = font +"px Sans";




// set the dimensions and margins of the graph
var margin = {top: 2*data.length, right: data.length, bottom: 1.5*data.length, left: 2*data.length},
    width =  950//document.getElementById("barplot").offsetWidth +10 - margin.left - margin.right,
    height = (25*data.length)- margin.top - margin.bottom;

var tierceWidth = width/4;
var maxSizeText = 0;
var maxIC = 0;
data.forEach(function(d){
 maxIC =  Math.max(d.IC,maxIC);
  var fontBack = font;
  while(ctx.measureText(d.name).width>(2*tierceWidth-1)){
    font = font *0.99;
    ctx.font = font +"px Sans";
  }
  maxSizeText = Math.max(ctx.measureText(d.name).width,maxSizeText);
  d.wd = ctx.measureText(d.name).width;
  d.font = font;

  font = fontBack;
  ctx.font = font +"px Sans";
})

var posText = maxSizeText;
var posPlot = 2*tierceWidth
width = maxSizeText + 2*tierceWidth;
maxIC = Math.floor(maxIC+1);
// set the ranges
var y = d3.scaleBand()
          .range([height, 0])
          .padding(0.2);

var x = d3.scaleLinear()
          .range([0, tierceWidth]);

// append the svg object to the body of the page
// append a 'group' element to 'svg'
// moves the 'group' element to the top left margin

var tooltip = d3.select('#barplot').append('div')
  .style('position','absolute')
  .style('padding','0 10px')
  .style('opacity',0)
  .attr('class','tooltipBP')
  .attr("transform",
        "translate(" + margin.left + "," + margin.top +50+ ")")
var svg = d3.select("#barplot").append("svg")
.attr("viewBox", "0 0 "+(width +35+ margin.left + margin.right)+" " +(height +50+ margin.top + margin.bottom));
//    .attr("width", "100%")// width +30+ margin.left + margin.right)
//    .attr("height", "auto")//height +50+ margin.top + margin.bottom);
var g=    svg.append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");



  // format the data
  data.forEach(function(d) {
    d.IC = +d.IC;
  });

  // Scale the range of the data in the domains
  y.domain(data.map(function(d) { return d.name; }));
  //y.domain([0, d3.max(data, function(d) { return d.sales; })]);

  // append the rectangles for the bar chart
  // add the x Axis
  /*
  svg.append("g")
      .attr("transform", "translate("+(maxLength+1)+"," + height + ")")
      .call(d3.axisBottom(x));*/

  // add the y Axis
  /*svg.append("g")

  .attr("transform", "translate("+maxLength+"," + 0 + ")")
  .style("font-size",font+"px")
  .call(d3.axisLeft(y));*/

  g.selectAll(".bar")
      .data(data)
    .enter().append("rect")
      .attr("class", "bar1")
      .attr("x", function(d) { return posText + posPlot/2+10; })
      .attr("width", function(d) {return Math.log(d.IC)/Math.log(maxIC) * posPlot/2; } )
      .attr("y", function(d) { return y(d.name); })
      .attr("fill", "#68a9ff" )
      .attr("height", y.bandwidth())
      .on('mousemove', function(d) {
        const [xMouse, yMouse] = d3.mouse(this);
  tooltip
  .style('top', (d3.event.layerY+5) + 'px')
	.style('left', (d3.event.layerX+5) + 'px')
})
      .on('mouseover', function(d) {
        const [xMouse, yMouse] = d3.mouse(this);
  tooltip.transition()
    .style('opacity', .9)
    .style('background', '#E6E6E6')
  tooltip.html( "IC: "+(Math.floor(d.IC*100)/100)  +"<br> Gene set Number: "+d.geneSet.length )
   .style('top', (d3.event.layerY+5) + 'px')
					.style('left', (d3.event.layerX+5) + 'px')
})
.on('mouseout', function(d) {
  tooltip.transition()
    .style('opacity', 0)
});
      g.selectAll(".bar")
          .data(data)
        .enter().append("rect")
          .attr("class", "bar2")
          .attr("x", function(d) { return posText + posPlot/2+10 - d.geneSet.length/set.size * posPlot/2; })
          .attr("width", function(d) {return d.geneSet.length/set.size * posPlot/2; } )
          .attr("y", function(d) { return y(d.name); })
          .attr("fill", "#df9f30" )
          .attr("height", y.bandwidth())
          .on('mousemove', function(d) {
            const [xMouse, yMouse] = d3.mouse(this);
      tooltip
      .style('top', (d3.event.layerY+5) + 'px')
  	.style('left', (d3.event.layerX+5) + 'px')
    })
          .on('mouseover', function(d) {
            const [xMouse, yMouse] = d3.mouse(this);
      tooltip.transition()
        .style('opacity', .9)
        .style('background', '#E6E6E6')
      tooltip.html(  "IC: "+(Math.floor(d.IC*100)/100) +"<br> Gene set Number: "+d.geneSet.length  )
         .style('top', (d3.event.layerY+5) + 'px')
	.style('left', (d3.event.layerX+5) + 'px')
    })
    .on('mouseout', function(d) {
      tooltip.transition()
        .style('opacity', 0)
    });

     g.append("g").selectAll("text").data(data).enter().append("text")
      .attr("x", function(d) { return posText;})
      .attr("y", function(d) { return y(d.name)+  y.bandwidth()/1.5; })
      .attr("text-anchor","end")
      .text(function(d){ return d.name;})
      .attr("font-family", "Sans")
      .attr("font-size",function(d){return d.font+"px";});

// Axis x
      g.append('line')    // attach a line
    .style("stroke", "black")  // colour the line
    .attr("x1", posText +10)     // x position of the first end of the line
    .attr("y1", height)      // y position of the first end of the line
    .attr("x2", posText + posPlot +10)     // x position of the second end of the line
    .attr("y2", height);
// Ticks
    g.append('line')    // attach a line
  .style("stroke", "black")  // colour the line
  .attr("x1", posText +10)     // x position of the first end of the line
  .attr("y1", height-5)      // y position of the first end of the line
  .attr("x2",posText +10)     // x position of the second end of the line
  .attr("y2", height+5);
  g.append('line')    // attach a line
.style("stroke", "black")  // colour the line
.attr("x1", posText + posPlot/2+10)     // x position of the first end of the line
.attr("y1", height-5)      // y position of the first end of the line
.attr("x2", posText + posPlot/2+10)     // x position of the second end of the line
.attr("y2", height+5);
g.append('line')    // attach a line
.style("stroke", "black")  // colour the line
.attr("x1", posText + posPlot +10)     // x position of the first end of the line
.attr("y1", height-5)      // y position of the first end of the line
.attr("x2", posText + posPlot +10)     // x position of the second end of the line
.attr("y2", height+5);

g.append("text")
.attr("x", posText +10+(posPlot/4))
.attr("y", height +30)
.text("Covered genes percentage")
.attr("text-anchor","middle")
.attr("font-family", "Sans")
.attr("font-size","12px");

g.append("text")
.attr("x", posText + posPlot/2+10+(posPlot/4))
.attr("y", height +30)
.text("log(Information Content)")
.attr("text-anchor","middle")
.attr("font-family", "Sans")
.attr("font-size","12px");

g.append("text")
.attr("x", posText +10)
.attr("y", height +20)
.text("100%")
.attr("text-anchor","middle")
.attr("font-family", "Sans")
.attr("font-size","12px");

g.append("text")
.attr("x", posText + posPlot/2+10)
.attr("y", height +20)
.text("0")
.attr("text-anchor","middle")
.attr("font-family", "Sans")
.attr("font-size","12px");
g.append("text")
.attr("x", posText + posPlot+10)
.attr("y", height +20)
.text(maxIC)
.attr("text-anchor","middle")
.attr("font-family", "Sans")
.attr("font-size","12px");




}
