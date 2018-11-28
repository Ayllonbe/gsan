FROM openjdk:8
RUN apt update&&\
    apt install --no-install-recommends -y r-base\
     && apt-get install --no-install-recommends -y maven\
     && rm -rf /var/lib/apt/lists/*
ADD requeriments.txt Rdep.txt
RUN while read line;\
	do wget "http://cran.r-project.org/src/contrib/$line";\
	R CMD INSTALL "$line";\
	rm "$line";\
	done < Rdep.txt && apt remove -y wget && apt -y remove curl

ENV MAVEN_OPTS="-Xmx8096m -XX:MaxPermSize=4048m"
WORKDIR /usr/gsan/
ADD . /usr/gsan/
RUN mvn clean && mvn package -DskipTests
EXPOSE 8282
CMD java -jar target/app.jar
