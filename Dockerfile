FROM ubuntu:bionic
RUN apt-get update \ 
    && apt-get install -y --no-install-recommends \ 
    software-properties-common \
    ed \
    less \
    locales \
    vim-tiny \
    wget \
    ca-certificates \  
    openjdk-8-jdk\
    maven

## Configure default locale, see https://github.com/rocker-org/rocker/issues/19
RUN echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
	&& locale-gen en_US.utf8 \
	&& /usr/sbin/update-locale LANG=en_US.UTF-8

ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8

## This was not needed before but we need it now
ENV DEBIAN_FRONTEND noninteractive

# Now install R and littler, and create a link for littler in /usr/local/bin
# Also set a default CRAN repo, and make sure littler knows about it too
# Note 1: we need wget here as the build env is too old to work with libcurl (we think, precise was)
# Note 2: r-cran-docopt is not currently in c2d4u so we install from source
RUN apt-get update \
        && apt-get install -y --no-install-recommends \
                 littler \
 		 r-base \
 		 r-base-dev \
 		 r-recommended \
                 r-cran-stringr \
                 r-cran-rcpp \
        && echo 'options(repos = c(CRAN = "https://cran.rstudio.com/"), download.file.method = "libcurl")' >> /etc/R/Rprofile.site \
 	&& ln -s /usr/lib/R/site-library/littler/examples/install.r /usr/local/bin/install.r \
 	&& ln -s /usr/lib/R/site-library/littler/examples/install2.r /usr/local/bin/install2.r \
 	&& ln -s /usr/lib/R/site-library/littler/examples/installGithub.r /usr/local/bin/installGithub.r \
 	&& ln -s /usr/lib/R/site-library/littler/examples/testInstalled.r /usr/local/bin/testInstalled.r \
 	&& install.r docopt \
 	&& rm -rf /tmp/downloaded_packages/ /tmp/*.rds \
        && rm -rf /var/lib/apt/lists/*

ADD requeriments.txt Rdep.txt
RUN while read line;\
	do echo "install.packages(\"$line\", repos=\"https://pbil.univ-lyon1.fr/CRAN/\")" | R --no-save;\
	done < Rdep.txt
ENV MAVEN_OPTS="-Xmx8096m -XX:MaxPermSize=4048m"
WORKDIR /usr/gsan/
ADD . /usr/gsan/
RUN mvn clean && mvn package -DskipTests
EXPOSE 8282
CMD java -jar target/app.jar

