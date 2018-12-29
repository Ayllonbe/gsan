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


console.log("GeneSet SCP " + recoverGenes.size);
gauge(gs2,"#gs2");
var rg = Annotgenes.length/genes.length;
gauge(rg,"#recoverGenes");
$(".rg").append('<p>'+(Math.round(rg*100))+ '% of genes are annotated in the used GOA file.</p>');

//gauge(reduceterm,"#reduce");
var rag = recoverGenes.size/Annotgenes.length;
$(".rag").append('<p>'+(Math.round(rag*100))+ '% of genes covered by GSAn.</p>');
gauge(rag,"#recoverAnnotGenes");
$(".gs2").append('<p>The groupwise similarity between genes of the set is '+(Math.round(gs2*100))+ '%</p>');




barplot(scpDic,recoverGenes);

var h = ["GOID","Name","Ontology","IC","Covered genes","Synthetic"]
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

$("#tableResult").append('<H3>GSAn retained '+representative.length+' terms, '+scp.length +' of them being synthetic<br>  ' +recoverGenes.size +' out of '+genes.length+' genes are covered</H3>');
var table = new Table()

//sets table data and builds it
table
    .setHeader(h)
    .setData(r)
    .setTableClass('table table-striped table-bordered')
    .setTableID('tableRep')
    .build()

$("#tableResult").append('<button class="btnCsv" style="float:right;font-family: sans-serif;font-size:18px;"><i class="fa fa-download"></i>&nbsp;Export CSV</button>');

//motvis(dic,representative,genes,data.tree);
var circulardiv = document.getElementById("circular"),
treediv = document.getElementById("treeHIDDEN"),
width = circulardiv.clientWidth,
height = circulardiv.clientHeight,
pack = d3.pack().size([width*0.9 , height*0.9]).padding(0.4);
motvis(dic,representative,genes,data.tree,width,height,pack,circulardiv,treediv);

  $(window).resize(function(){
    circulardiv = document.getElementById("circular"),
    treediv = document.getElementById("treeHIDDEN");
    if(width!==circulardiv.clientWidth || height!==circulardiv.clientHeight){
      console.log("hola");
      width = circulardiv.clientWidth,
      height = circulardiv.clientHeight;
      motvis(dic,representative,genes,data.tree,width,height,pack,circulardiv,treediv);
    }

});


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
