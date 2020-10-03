target=target/jlox-1.0-SNAPSHOT.jar

build: pom.xml
	@mvn clean
	@mvn package

test: pom.xml
	@mvn test

run: $(target)
	@java -jar --enable-preview $(target) $(file)

ast-gen: $(target)
	@java --module-path target/classes \
 	--enable-preview \
 	-m com.example.lox.jlox/com.example.lox.jlox.tool.GenerateAst \
 	src/main/java/com/example/lox/jlox

ast: $(target)
	@java --module-path target/classes \
	--enable-preview \
	-m com.example.lox.jlox/com.example.lox.jlox.AstPrinter

$(target):
	@make build

clean: pom.xml
	@mvn clean

