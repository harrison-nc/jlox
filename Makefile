target=target/jlox-1.0-SNAPSHOT.jar

run: $(target)
	@java -jar $(target) $(file)

ast-gen: $(target)
	@java --module-path target/classes \
 	-m com.example.lox.jlox/com.example.lox.jlox.tool.GenerateAst \
 	src/main/java/com/example/lox/jlox

ast: $(target)
	@java --module-path target/classes \
	-m com.example.lox.jlox/com.example.lox.jlox.AstPrinter

$(target):
	@make build

build: pom.xml
	@mvn clean
	@mvn package

test: pom.xml
	@mvn test

clean: pom.xml
	@mvn clean

