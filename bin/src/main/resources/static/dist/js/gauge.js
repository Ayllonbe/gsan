// Based on reusable chart pattern from https://bost.ocks.org/mike/chart/
function gaugeChart() {
  var margin = {top: 0, right: 65, bottom: 10, left: 65},
      width = 100,
      height = 100,
      arcMin = -Math.PI/2,
      arcMax = Math.PI/2,
      innerRadius = 60,
      polize = 25,
      outerRadius = 80,
      dataDomain = [0., 0.5, 1.0],
      labelPad = 10,
      dataValue = function(d) { return +d; },
      colorScale = d3.scaleLinear(),
      arcScale = d3.scaleLinear(),
      colorOptions = ["#d7191c", "#efef5d", "#1a9641"],
      arc = d3.arc();

  function chart(selection) {
    selection.each(function(data) {
      // Convert data to standard representation greedily;
      // this is needed for nondeterministic accessors.
      data = data.map(function(d, i) { return dataValue(d); });
      arcScale = d3.scaleLinear().domain(dataDomain).range([arcMin, 0, arcMax]);
      colorScale = d3.scaleLinear().domain(dataDomain).range(colorOptions);
      arc = d3.arc().innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(arcMin);

      // Select the svg element, if it exists.
      var svg = d3.select(this).selectAll("svg").data([data]);

      // Otherwise, create the skeletal chart.
      var gEnter = svg.enter().append("svg").append("g");
      var arcGEnter = gEnter.append("g").attr("class", "arc");
      arcGEnter.append("path").attr("class", "bg-arc");
      arcGEnter.append("path").attr("class", "data-arc")
        .datum({endAngle: arcMin, startAngle: arcMin, score: dataDomain[0]})
        .attr("d", arc)
        .style("fill", colorScale(dataDomain[0]))
        .each(function(d) { this._current = d; });
      arcGEnter.append("text").attr("class", "arc-label");


      arcGEnter.selectAll(".lines").data(arcScale.ticks(5).map(function(d) {
        return { score: d };
      })).enter()
        .append("path")
          .attr("class", "lines");
      arcGEnter.selectAll(".ticks").data(arcScale.ticks(5))
        .enter().append("text")
          .attr("class", "ticks");

      // Update the outer dimensions.
      var svg = selection.select("svg");
      svg.attr("viewBox", "0 0 170 100");
      
      //.attr("width", width).attr("height", height);
      // Update the inner dimensions.
      var g = svg.select("g")
          .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

      var arcG = svg.select("g.arc")
        .attr("transform", "translate(" +
          ((width - margin.left - margin.right) / 2) + "," +
          ((height * (2 / 3)) + ")"));

      svg.select("g.arc .bg-arc")
        .datum({endAngle: arcMax})
        .style("fill", "#ddd")
        .attr("d", arc);

      // https://bl.ocks.org/mbostock/1346410
      function arcTween(a) {
        var i = d3.interpolate(this._current, a);
        this._current = i(0);
        return function(t) {
          return arc(i(t));
        };
      }
      var dataArc = svg.select("g.arc .data-arc")
        .datum({score: data[0], startAngle: arcMin, endAngle: arcScale(data[0])})
          .transition()
          .duration(750)
            .style("fill", function(d) { return colorScale(d.score); })
            .style("opacity", function(d) { return d.score < dataDomain[0] ? 0 : 1; })
            .attrTween("d", arcTween);

      var arcBox = svg.select("g.arc .bg-arc").node().getBBox();
      svg.select("text.arc-label")
        .datum({score: data[0]})
        .attr("x", (arcBox.width/2)+arcBox.x)
        .attr("y", 0)
        .style("alignment-baseline", "central")
        .style("text-anchor", "middle")
        .style("font-size", polize+"px")
        .text(function(d) { return d3.format(".2f")(d.score); });

      var markerLine = d3.radialLine()
        .angle(function(d) { return arcScale(d); })
        .radius(function(d, i) {
          return innerRadius + ((i % 2) * ((outerRadius - innerRadius)));
        });

      arcG.selectAll(".lines")
        .attr("d", function(d) { return markerLine([d.score, d.score]); })
        .style("fill", "none")
        .style("stroke-width", 2.5)
        .style("stroke", "#fff");
      arcG.selectAll(".ticks")
        .style("font-size", "12px")
        .style("text-anchor", "middle")
        .attr("x", function(d) { return Math.cos(arcScale(d) + arcMin) * (outerRadius + labelPad); })
        .attr("y", function(d) {
          var yVal = Math.sin(arcScale(d) + arcMin) * (outerRadius + labelPad);
          return yVal < -1 ? yVal : -7;
        }).text(function(d) { return d; });
    });
  }

  chart.margin = function(_) {
    if (!arguments.length) return margin;
    margin = _;
    return chart;
  };

  chart.width = function(_) {
    if (!arguments.length) return width;
    width = _;
    return chart;
  };

  chart.height = function(_) {
    if (!arguments.length) return height;
    height = _;
    return chart;
  };

  chart.innerRadius = function(_) {
    if (!arguments.length) return innerRadius;
    innerRadius = _;
    return chart;
  };

  chart.outerRadius = function(_) {
    if (!arguments.length) return outerRadius;
    outerRadius = _;
    return chart;
  };

  chart.dataDomain = function(_) {
    if (!arguments.length) return dataDomain;
    dataDomain = _;
    return chart;
  };

  chart.colorOptions = function(_) {
    if (!arguments.length) return colorOptions;
    colorOptions = _;
    return chart;
  };

  chart.labelPad = function(_) {
    if (!arguments.length) return labelPad;
    labelPad = _;
    return chart;
  };

  return chart;
}
