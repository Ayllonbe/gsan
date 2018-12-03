#install.packages("httr")
#Require the package so you can use it



GSAnGetCsv <- function(query,ontology=c("BP"),organism="hsa",
         useIEA = TRUE,ic="Mazandu",percentile=25,
         semSim="aic",simRepCom=0, repTermCovering=1,
         minSupGen=-1){
  require("httr") 
# IC: Seco, Zhou, Sanchez and Mazandu
# Organism: hsa, zfin, sgd, eco, mgi, tair
# Ontology: BP, MF, CC
# Semantic Similarity: aic, simrel, lin, resnik, pekarStaab, distanceFunction, ganesan, nUnivers

  
ic_list = list()
ic_list["Seco"] = 0
ic_list["Zhou"] = 1
ic_list["Sanchez"] = 2
ic_list["Mazandu"] = 3

org_list = list()
org_list["hsa"] = "homo_sapiens"
org_list["zfin"] = "danio_rerio"
org_list["sgd"] = "saccharomyces_cerevisiae"
org_list["eco"] = "escherichia_coli"
org_list["mgi"] = "mus_musculus"
org_list["tair"] = "arabidopsis_thaliana"

onto_list = list()
onto_list["BP"] = "GO:0008150"
onto_list["MF"] = "GO:0003674"
onto_list["CC"] = "GO:0005575"

prok = F
if(organism=="eco"){
 prok = T 
}
  
str <- "http://localhost:8282/gsanCsv"
query = paste(query,collapse=",")
ont = c()
for(x in 1:length(ontology)){
  ont <- c(ont,onto_list[ontology[x]])
}
ont <- paste(ont,collapse=",")


if(minSupGen<0){
  minSupGen <- floor(sqrt(abs(length(query)/10-1)))+2
}

str <- paste(str,"?query=",query,"&ontology=",ont,"&useIEA=",useIEA,
             "&ic_incomplete=",ic_list[ic],"&percentile=",percentile,"&organism=",org_list[organism],
             "&simRepValue=",simRepCom,"&covering=",repTermCovering,
             "&minGeneSupport=",minSupGen,"&prokaryote=",prok,sep = "")


a <- GET(str)
at <- content(a, "text",encoding = "UTF-8")
return(read.csv(text=at))
}

test <- GSAnGetCsv(query = c("CCL2","DCN","LIF","PLAU","IL6","MGP","COL1A2","MMP9","THBD","MMP2","IL8","MMP1"))
