package com.github.asodja.task.api;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("org.gradle.api.tasks.Input")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GradleParamsAnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("org.gradle.api.tasks.Input");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, List<PropertyGetters>> getters = new HashMap<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof ExecutableElement executableElement) {
                    TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                    getters.computeIfAbsent(typeElement, k -> new ArrayList<>()).add(new PropertyGetters(
                            executableElement.getSimpleName().toString(),
                            "String",
                            "return null;"
                    ));
                }
            }
        }
        getters.forEach(this::generateSourceFile);
        return true;
    }

    private void generateSourceFile(TypeElement typeElement, List<PropertyGetters> getters) {
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).toString();
        String className = typeElement.getSimpleName().toString() + "Generated";
        String sourceCode = generateGetters(getters);

        // Write the source code to a new file
        try (Writer writer = processingEnv.getFiler().createSourceFile(packageName + "." + className).openWriter()) {
            writer.write("""
                    package %s;
                    
                    public class %s {
                        %s
                    }
                    """.formatted(packageName, className, sourceCode));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String generateGetters(List<PropertyGetters> getters) {
        StringBuilder out = new StringBuilder();
        for (PropertyGetters getter : getters) {
            out.append("""
                    public %s %s() {
                        %s
                    }
                    """.formatted(getter.returnType(), getter.name(), getter.body()));
        }
        return out.toString();
    }

    private record PropertyGetters(String name, String returnType, String body) {}
}