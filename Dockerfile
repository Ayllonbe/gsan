FROM openjdk:8
RUN apt update&&\
    apt install --no-install-recommends -y r-base\
     && apt-get install --no-install-recommends -y maven\
     && rm -rf /var/lib/apt/lists/*
COPY requeriments.txt Rdep.txt
RUN while read line;\
	 do wget "http://cran.r-project.org/src/contrib/$line";\
	 R CMD INSTALL "$line";\
	 rm "$line";\
	 done < Rdep.txt && apt remove -y wget && apt -y remove curl

ENV MAVEN_OPTS="-Xmx3096m -XX:MaxPermSize=2048m"
WORKDIR /usr/gsan/mavenProject
COPY . /usr/gsan/mavenProject/
RUN mvn clean && mvn package -DskipTests && mv target/app.jar ../app.jar
WORKDIR /usr/gsan
RUN rm -r mavenProject && apt-get --purge remove -y maven
CMD java -jar target/app.jar
EXPOSE 8282

