#!/usr/bin/env Rscript

# test if there is at least one argument: if not, return an error
#args = commandArgs(trailingOnly=TRUE)


library("optparse")
library(clValid)
library("fastcluster")

option_list = list(
  
  make_option(c("-f", "--file"), type="character", default=NULL, 
              help="dataset file name", metavar="character"),  
  make_option("--outFolder", type="character", default="/tmp",
              help="output folder name [default= %default]", metavar="character"),
  make_option(c("-o", "--outFile"), type="character", default="out.txt", 
              help="output file name [default= %default]", metavar="character"),
  make_option(c("-mh", "--methodHclust"), type="character", default="average",
              help="method hclust name [default= %default]", metavar="character")
  #make_option(c("-nc", "--numberclust"), type="numeric", default=20,
  #            help="method hclust name [default= %default]", metavar="character")
  # make_option(c("-c", "--cutOff"), type="character", default="",
  #             help="Cut Off method name [default= %default]", metavar="character")
  
  
);

opt_parser = OptionParser(option_list=option_list);
opt = parse_args(opt_parser);


if (is.null(opt$file)){
  print_help(opt_parser)
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
}

# Create the working path
time.obj =paste(gsub(" ","_", as.character(Sys.time())))
main = opt$outFolder 
path = main #file.path(main,time.obj)
#dir.create(path,showWarnings=FALSE)
setwd(path)
require(Hmisc,warn.conflicts =FALSE,quietly=TRUE)
require(cluster,warn.conflicts =FALSE,quietly=TRUE)
require(vegan,warn.conflicts =FALSE,quietly=TRUE)


mydata <- read.csv(opt$file,header=T,sep=";",row.names=1)
print(length(mydata))
dist.obj <- as.dist(1-mydata)

D<-mydata
D<-1-as.matrix(D)
N <- nrow(D);
go.names<-rownames(D)
descp.names<-rownames(D)
row.names(D)<-NULL
colnames(D)<-NULL



#install.packages("data.tree")
library(data.tree)

#Returns the data corresponding to the leaves of node (data tree)
get.elements<-function(node){
  res<-sapply(node$leaves,function(x){x$value});
  names(res)<-NULL;
  return(res);
}

# Transform partition of elements P into a vector contains clusters ids
as.partition.indexes<-function(P,n){
  indexes.part<-numeric(n);
  for(i in 1:length(P)){
    indexes.part[P[[i]]]<-i;
  }
  return(indexes.part);
}
#########################################################################################
# Compute the similarity quality measure (Pons, 2008)
pons.similarity<-function(cut,dist){
  k<-max(cut);
  si<-0.;
  for(i in 1:k){
    ind<-which(cut==i)
    si<-si+sum(D[ind,ind])
  }
  return(-k/nrow(D)-si/sum(D))
}
# Compute the  additive silhouette = sum_{i} ( B_i - A _ i)/max(B_i,A_i) 
#                       with B_i = sum_{j not in C(i)} dist[i,j] /(N-|C(i)|)
#                       and A_i = sum_{j in C(i)} dist[i,j] /(|C(i)|)
# where N : data size
#       C(i) : cluster of data i
add.silhouette<-function(cut,dist){
  if(min(cut)==max(cut)){return(-1.)}
  si.x<-0.;
  for(u in 1:length(cut)){
    elems_x<-which(cut==cut[u]);
    if(length(elems_x)>1){
      ai<-sum(dist[u,elems_x])/length(elems_x);
      bi<-sum(dist[u,-elems_x])/(length(cut)-length(elems_x));
      #d_out<-sort(dist[u,-elems_x]);
      #n.elem.out<-min(length(elems_x),length(d_out));
      #bi<-sum(d_out[1:n.elem.out])/n.elem.out;
      si.x<-si.x+(bi-ai)/max(ai,bi);
    }
  }
  return(si.x/length(cut));
}
## Compute the silhouette quality measure
library(cluster)
cut.silhouette<-function(cut,dist){
  if(min(cut)==max(cut)){return(-1.)}
  if(max(cut)==length(cut)){return(0.)}
  return(summary(silhouette(cut,dist=as.dist(D)))$avg.width);
}

## Find the best partition as an horizontal cut of hierarchical clustering hc
## according to quality function q_fun
find.best.horinz.cut<-function(hc,D,N_max,q_fun = pons.similarity ,plot=TRUE){
  N<-min(nrow(D)-1,N_max);
  si.val<-numeric(N);
  for(i in 1:N){
    cut<- cutree(hc, k = i);
    si.val[i]<- q_fun(cut,D);
  }
  if(plot){
    plot(1:N,si.val,type='l');
    abline(v=which(si.val==max(si.val)),col='red')
  }
  return(list("q.best"=max(si.val),"k.best"=which(si.val==max(si.val))))
}

#########################################################################################
## Compute the marginal similarity of cluster corresponding to leaves of node.
pons.similarity.gain<-function(node,D){
  n<-nrow(D);
  comm<-get.elements(node);
  max.dist<-sum(D)
  in.dist<-sum(D[comm,comm])
  return(-1./n-in.dist/max.dist);
}

## Compute the marginal additive silhouette of cluster corresponding to leaves of node.
add.silhouette.gain<-function(node,D){
  if(node$isRoot){return(-1.)}
  if(node$isLeaf){return(0.)}
  elems_x<-get.elements(node);
  si.x=0.;
  for(u in elems_x){
    ai<-sum(D[u,elems_x])/length(elems_x);
    bi<-sum(D[u,-elems_x])/(nrow(D)-length(elems_x));
    si.x<-si.x+(bi-ai)/max(ai,bi);
  }
  return(si.x/nrow(D));
}

## Compute the best non-horinzontal cut in dendogram rooted in node
## according to the marginal quality measure "gain_fun"
find.best.nonhorinz.cut<-function(node,D,gain_fun = pons.similarity.gain){
  gain.c<-0;
  P<-list();
  
  for(c in node$children){
    rec.res<-find.best.nonhorinz.cut(c,D,gain_fun);
    gain.c<-gain.c+rec.res$gain
    P<-c(P,rec.res$P);
  }
  gain.node<-gain_fun(node,D);
  if(node$isLeaf || gain.node>gain.c){
    P<-list();
    P[[node$pathString]]=get.elements(node);
    return(list("gain"=gain.node,"P"=P,"in.cut"=node));
  }else{
    return(list("gain"=gain.c,"P"=P));
  }
}
#########################################################################################
## Compute the marginal similarity of cluster corresponding to leaves of node
## decomposed as h(P) and l(P) see. [Pons, 2008]
multiscale.pons.similarity.gain<-function(node,D){
  n<-nrow(D);
  comm<-get.elements(node);
  max.dist<-sum(D)
  in.dist<-sum(D[comm,comm])
  return(list("H"=-1./n,"L"=-in.dist/max.dist));
}

## Compute the marginal additive silhouette of cluster corresponding to leaves of node.
## decomposed as h(P) and l(P) see. [Pons, 2008]
multiscale.add.silhouette.gain<-function(node,D){
  if(node$isRoot){return(list("H"=-1.,"L"=0));}
  if(node$isLeaf){return(list("H"=0.,"L"=0.))}
  elems_x<-get.elements(node);
  ai.x=0.;
  bi.x=0.;
  for(u in elems_x){
    ai<-sum(D[u,elems_x])/length(elems_x);
    bi<-sum(D[u,-elems_x])/(nrow(D)-length(elems_x));
    ai.x<-ai.x+ai/max(ai,bi);
    bi.x<-bi.x+bi/max(ai,bi);
  }
  return(list("H"=bi.x/nrow(D),"L"= -ai.x/nrow(D)));
}

## Compute the height "alpha" at which each cluster in data.tree node
## does not belong to the best partition 
## according to the multiscale quality measure gain_fun
compute.multiscale.clustering.height<-function(node,D,gain_fun = multiscale.pons.similarity.gain){
  H.c<-0;
  L.c<-0;
  max.alpha.c=0.;
  for(c in node$children){
    rec.res<-compute.multiscale.clustering.height(c,D,gain_fun);
    H.c<-H.c+rec.res$H;
    L.c<-L.c+rec.res$L
    max.alpha.c=max(max.alpha.c,c$alpha);
  }
  gain.node<-gain_fun(node,D);
  h.n<-gain.node$H;
  l.n<-gain.node$L;
  if(node$isLeaf){
    node$alpha<-0;
  }else{
    node$alpha<-max((L.c-l.n)/((h.n-H.c)-(l.n-L.c)),max.alpha.c);
    #node$alpha<-(L.c-l.n)/((h.n-H.c)-(l.n-L.c));
  }
  return(list("H"=h.n,"L"=l.n));
}

## Compute the contribution of cluster node 
## to the best partition at height alpha  see. [Pons, 2008]
node.scale.relevance<-function(node,alpha){
  if(node$isRoot){return(0.)}
  a_min=node$alpha;
  a_max=node$parent$alpha;
  if(a_min==a_max){return(0.)}
  if(alpha<=a_min || alpha>a_max ){return(0.)}
  ra<-(a_max-a_min)/2.+2.*(a_max-alpha)*(alpha-a_min)/(a_max-a_min);
  return(ra);
}

## Compute the avegrage scale relevance for a given vector of alpha values
##  see. [Pons, 2008]
scale.relevance<-function(node,alphas){
  n<-node$leafCount;
  n.elems<-dt$Get(function(x) x$leafCount);
  sr<-numeric(length(alphas));
  for(i in 1:length(alphas)){
    sr.a<-node$Get(node.scale.relevance,alpha=alphas[i]);
    sr[i]<-sum(n.elems*sr.a)/n;
  }
  return(sr);
}



clust <- hclust(dist.obj,method = opt$methodHclust)
dt<-as.Node(as.dendrogram(clust)) ## Les feuilles de dt  doivent avoir pour 'name' leur position dans la matrice D
order.leaves<-as.numeric(dt$Get("name",filterFun = isLeaf)) ## On récupère l'ordre des feuille dans le dendogramme
dt$Set(label=go.names[order.leaves],filterFun=isLeaf) ## On ajoute à dt le label GO des feuilles 
dt$Set(description=descp.names[order.leaves],filterFun=isLeaf) ## On ajoute à dt la description des feuilles
print(dt,"label","description")

res.bp<-find.best.nonhorinz.cut(dt,D,gain_fun = add.silhouette.gain)
best.add.sil.nonhoriz.cut <- as.partition.indexes(res.bp$P,nrow(D))
names(best.add.sil.nonhoriz.cut) <- go.names
cl <- max(best.add.sil.nonhoriz.cut)
cutree.obj <- best.add.sil.nonhoriz.cut

clusters <- c()

termCl <- c()

for(x in 1:cl){
  namesCluster <- names(cutree.obj[cutree.obj==x])
  termCl<-c(termCl,length(namesCluster))
  print(paste(x,paste(namesCluster,collapse=";"),sep="\t"))
  clusters <- c(clusters,paste(x,0,paste(namesCluster,collapse=";"),sep="\t"))
}
Tcl <- paste("#Cluster Number:",length(termCl))
Tmax <- paste("#Terms Number max in a cluster:",max(termCl))
Tmin <- paste("#Terms Number min in a cluster:",min(termCl))
Tmean <- paste("#Terms Number mean in a cluster:",mean(termCl))


res.clu <- paste(Tcl,Tmax,Tmin,Tmean,sep="\n")
res.clu <- c(res.clu,clusters)

write.table(res.clu,opt$outFile,row.names = FALSE, col.names=FALSE, quote=FALSE)
#write.table(res.clu,"/home/aaron/test.csv",row.names = FALSE, col.names=FALSE, quote=FALSE)
