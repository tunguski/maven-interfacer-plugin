package pl.matsuo.interfacer.model.ifc;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import pl.matsuo.interfacer.model.ref.MethodReference;
import pl.matsuo.interfacer.model.tv.TypeVariableReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static pl.matsuo.interfacer.util.CollectionUtil.filterMap;
import static pl.matsuo.interfacer.util.CollectionUtil.firstNotNull;

public abstract class AbstractIfcResolve implements IfcResolve {

  @Override
  public Map<String, String> matches(ClassOrInterfaceDeclaration declaration) {
    List<MethodReference> methods = getMethods();
    if (methods.isEmpty()) {
      return null;
    }

    List<Map<String, String>> typeVariableMappings = getTypeVariableMappings(declaration, methods);

    if (typeVariableMappings.size() != methods.size()) {
      return null;
    }

    AtomicBoolean incompatible = new AtomicBoolean();
    Map<String, String> result = new HashMap<>();
    typeVariableMappings.forEach(
        mapping -> {
          mapping.forEach(
              (key, value) -> {
                if (result.containsKey(key) && !result.get(key).equals(value)) {
                  incompatible.set(true);
                }
              });
          result.putAll(mapping);
        });

    return incompatible.get() ? null : result;
  }

  protected List<Map<String, String>> getTypeVariableMappings(
      ClassOrInterfaceDeclaration declaration, List<MethodReference> methods) {
    Map<String, TypeVariableReference> typeVariables = typeVariables();
    return filterMap(
        methods, method -> findMatchingMethodTypeVariables(declaration, typeVariables, method));
  }

  private Map<String, String> findMatchingMethodTypeVariables(
      ClassOrInterfaceDeclaration declaration,
      Map<String, TypeVariableReference> typeVariables,
      MethodReference method) {
    return firstNotNull(
        declaration.getMethodsByName(method.getName()),
        methodDeclaration -> method.matches(methodDeclaration, typeVariables));
  }

  protected abstract Map<String, TypeVariableReference> typeVariables();
}
