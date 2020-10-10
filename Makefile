target=target/jlox-1.0-SNAPSHOT.jar

build: pom.xml
	@mvn clean
	@mvn package

test: pom.xml
	@mvn test

run: $(target)
	@java -jar --enable-preview $(target) $(file)

$(target):
	@make build

clean: pom.xml
	@mvn clean

ast: jsh/generateAst.java
	@jsh/generateAst.java jsh/

