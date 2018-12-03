FROM openjdk:8
RUN apt-get update \ 
    && apt-get install -y --no-install-recommends \ 
    ed \ 
    less \ 
    locales \ 
    vim-tiny \ 
    wget \
    ca-certificates \
    fonts-texgyre \
    maven\
    && rm -rf /var/lib/apt/lists/* ## Configure default locale
RUN echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \ 
    && locale-gen en_US.utf8 \
    && /usr/sbin/update-locale LANG=en_US.UTF-8 

ENV LC_ALL en_US.UTF-8 
ENV LANG en_US.UTF-8
## Use Debian unstable via pinning -- new style via APT::Default-Release
RUN echo "deb http://deb.debian.org/debian sid main" > /etc/apt/sources.list.d/debian-unstable.list \
    && echo 'APT::Default-Release "testing";' > /etc/apt/apt.conf.d/default
ENV R_BASE_VERSION 3.5.1
## Now install R and littler, and create a link for littler in /usr/local/bin 
RUN apt-get update \ 
    && apt-get install -t unstable -y --no-install-recommends \
    littler \ 
    r-cran-littler \ 
    r-base=${R_BASE_VERSION}-* \ 
    r-base-dev=${R_BASE_VERSION}-* \
    r-recommended=${R_BASE_VERSION}-* \ 
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
