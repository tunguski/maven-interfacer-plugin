package pl.matsuo.interfacer.core;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public class ParsingContext {

  final ClassLoader classLoader;
  final CombinedTypeSolver typeSolver;
  final ParserConfiguration parserConfiguration;
  final JavaParser javaParser;

  public ParsingContext(
      List<String> compileClasspathElements,
      @NonNull File scanDirectory,
      @NonNull File interfacesDirectory) {
    classLoader = ClasspathInterfacesScanner.getCompileClassLoader(compileClasspathElements);
    typeSolver = createTypeSolver(scanDirectory, interfacesDirectory, classLoader);
    parserConfiguration = new ParserConfiguration();
    parserConfiguration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
    javaParser = new JavaParser(parserConfiguration);
  }

  public CombinedTypeSolver createTypeSolver(
      File scanDirectory, File interfacesDirectory, ClassLoader classLoader) {
    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ClassLoaderTypeSolver(classLoader));
    combinedTypeSolver.add(new JavaParserTypeSolver(scanDirectory.toPath()));
    combinedTypeSolver.add(new JavaParserTypeSolver(interfacesDirectory.toPath()));
    return combinedTypeSolver;
  }
}
