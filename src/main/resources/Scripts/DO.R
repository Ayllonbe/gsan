setwd(dirname(rstudioapi::getSourceEditorContext()$path))
tm <- read.table("../static/integration/human_disease_textmining_full.tsv",sep="\t")

tm <- tm[tm$V6>=3,]
tm <- cbind(tm,"V8"=rep("IEA",length(tm$V1)))

exp <- read.table("../static/integration/human_disease_experiments_full.tsv",sep="\t")
#exp <- exp[grepl("DOID:",exp$V3)==TRUE,]
exp <- cbind(exp,"V8"=rep("EXP",length(exp$V1)))
kno <- read.table("../static/integration/human_disease_knowledge_full.tsv",sep="\t")
#kno <- kno[grepl("DOID:",kno$V3)==TRUE,]
kno <- cbind(kno,"V8"=rep("CUR", length(kno$V1)))

gaf <- rbind(exp,kno)

genes <- c(as.character(gaf$V2))

length(unique(genes))

genes <- unique(genes)

library(org.Hs.eg.db)
hs <- org.Hs.eg.db
my.symbols <- c("ANKRD62P1-PARP4P3")
res <- select(hs, 
       keys = genes,
       columns = c("ENTREZID", "SYMBOL"),
       keytype = "SYMBOL")


nres <- res[is.na(res[,2])!=TRUE,]

gaf2 <- gaf[gaf$V2%in%nres$SYMBOL,]


gaf2 <- cbind(rep("Disease",length(gaf2$V2)),rep("-",length(gaf2$V2)),
              as.character(gaf2$V2),
              rep("-",length(gaf2$V2)),
              as.character(gaf2$V3),rep("-",length(gaf2$V2)),as.character(gaf2$V8), rep("-",length(gaf2$V2)), rep("DO",length(gaf2$V2)))
gaf2 <- gaf2[grepl("DOID:",gaf2[,5])==TRUE,]


length(unique(gaf2[,3]))

write.table(gaf2,"../static/integration/gene_hsa2doidNOIEA.gaf", quote = F,row.names = F,col.names = F,sep= "\t")




keys <- c("ACCNUM", "ALIAS","ENSEMBL","ENSEMBLPROT" , "ENSEMBLTRANS" ,"ENTREZID","ENZYME","GENENAME","OMIM","REFSEQ" ,"UCSCKG","UNIGENE","UNIPROT" )
keyunis <- head(keys(org.Hs.eg.db,keytype= "SYMBOL"))
for(x in 1: length(keys)) {
  k <- keys[x]
   
  print( select(org.Hs.eg.db, keys=keyunis, columns = c("SYMBOL",k), keytype="SYMBOL"))
}
