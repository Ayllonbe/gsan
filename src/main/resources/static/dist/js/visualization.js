function visual(data){


var gs2 = data.GS2;
//var reduceterm = data.Reduce;
var genes = data.GeneSet;
var Annotgenes = data.AnnotatedGeneSet;
var representative = data.representatives;
var scp = data.scp;
var dic = data.terms;

scp.sort(function(a,b){
	return dic[a].IC - dic[b].IC
});
var recoverGenes = new Set();

representative.forEach(function(x){
dic[x].geneSet.forEach(function(g){
  recoverGenes.add(g);
});
})

var scpDic = [];
scp.forEach(function(x){
scpDic.push(dic[x]);

});


//console.log("GeneSet SCP " + recoverGenes.size);
gauge(gs2,"#gs2");
gauge(recoverGenes.size/genes.length,"#recoverGenes");
//gauge(reduceterm,"#reduce");
gauge(recoverGenes.size/Annotgenes.length,"#recoverAnnotGenes");




barplot(scpDic,recoverGenes);

var h = ["Id","Name","Onto","IC","CoverNumber","Synthetic"]
var r = [];
representative.forEach(function(x){
  //console.log(dic[x]);
  var cells = [];
  cells.push(x);
  cells.push(dic[x].name);
  cells.push(dic[x].onto);
  cells.push(Math.floor(dic[x].IC*100)/100);
  cells.push(dic[x].geneSet.length);

  if(scp.includes(x)){
	  cells.push(true);
  }else{
	  cells.push(false);
  }

r.push(cells);
})

$("#tableResult").append('<H3>There are '+representative.length+' representative terms and '+scp.length +' synthetic terms annotating ' +recoverGenes.size +' out of '+genes.length+' genes</H3>');
var table = new Table()

//sets table data and builds it
table
    .setHeader(h)
    .setData(r)
    .setTableClass('table table-striped table-bordered')
    .setTableID('tableRep')
    .build()

$("#tableResult").append('<button class="btnCsv" style="float:right;font-family: sans-serif;font-size:18px;"><i class="fa fa-download"></i>&nbsp;Export CSV</button>');

motvis(dic,representative,genes,data.tree);

window.onresize = function(event) {

	motvis(dic,representative,genes,data.tree);
};

function gauge(value,id) {
var gauge = gaugeChart()
.width(150)
.height(110)
.innerRadius(150/4)
.outerRadius((150 / 4) +20);

d3.select(id).datum([value]).call(gauge);



};
}
//console.log(json);
