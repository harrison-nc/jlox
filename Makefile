target=target/jlox-1.0-SNAPSHOT.jar

run: $(target)
	@java -jar $(target) $(file)

$(target):
	@make build

build: pom.xml
	@mvn package

test: pom.xml
	@mvn test

clean: pom.xml
	@mvn clean

