package com.github.backpaper0.doma;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.tools.JavaFileObject;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@SupportedAnnotationTypes("org.seasar.doma.Domain")
public class DomainXmlAdapterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            for (TypeElement domainClassElement : ElementFilter
                    .typesIn(roundEnv.getElementsAnnotatedWith(typeElement))) {

                AnnotationMirror domainAnnotation = getDomainAnnotation(domainClassElement);

                String packageName = getPackageName(domainClassElement);
                String adapterClassSimpleName = domainClassElement.getSimpleName() + "Adapter";

                String valueType = getStringElement(domainAnnotation, "valueType");
                String boundType = domainClassElement.getQualifiedName().toString();

                //                String factoryMethod = getStringElement(domainAnnotation, "factoryMethod");
                //                String accessorMethod = getStringElement(domainAnnotation, "accessorMethod");

                try {
                    JavaFileObject file = processingEnv.getFiler()
                            .createSourceFile(packageName + "." + adapterClassSimpleName);
                    try (PrintWriter out = new PrintWriter(file.openWriter())) {
                        out.printf("package %1$s;%n", packageName);
                        out.printf("%n");
                        out.printf("public class %1$s extends %2$s<%3$s, %4$s> {%n",
                                adapterClassSimpleName, XmlAdapter.class.getName(), valueType,
                                boundType);
                        out.printf("%n");
                        out.printf("    public %1$s unmarshal(%2$s v) throws Exception {%n",
                                boundType, valueType);
                        //TODO
                        out.printf("        return new %1$s(v);%n", boundType);
                        out.printf("    }%n");
                        out.printf("%n");
                        out.printf("    public %1$s marshal(%2$s v) throws Exception {%n",
                                valueType, boundType);
                        //TODO
                        out.printf("        return v.getValue();%n");
                        out.printf("    }%n");
                        out.printf("}%n");
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    private AnnotationMirror getDomainAnnotation(TypeElement domainClassElement) {
        ElementVisitor<TypeElement, Void> v = new SimpleElementVisitor7<TypeElement, Void>() {

            @Override
            public TypeElement visitType(TypeElement e, Void p) {
                return e;
            }
        };
        for (AnnotationMirror am : domainClassElement.getAnnotationMirrors()) {
            if (am.getAnnotationType().asElement().accept(v, null).getQualifiedName().toString()
                    .equals("org.seasar.doma.Domain")) {
                return am;
            }
        }
        throw new RuntimeException();
    }

    private String getPackageName(TypeElement domainClassElement) {
        return domainClassElement.getEnclosingElement()
                .accept(new SimpleElementVisitor7<String, Void>() {

                    @Override
                    public String visitPackage(PackageElement e, Void p) {
                        return e.getQualifiedName().toString();
                    }
                }, null);
    }

    private String getStringElement(AnnotationMirror domainAnnotation, String elementName) {
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : domainAnnotation
                .getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(elementName)) {
                return entry.getValue().accept(new SimpleAnnotationValueVisitor7<String, Void>() {

                    @Override
                    public String visitType(TypeMirror t, Void p) {
                        return t.accept(new SimpleTypeVisitor7<String, Void>() {

                            @Override
                            public String visitDeclared(DeclaredType t, Void p) {
                                return t.asElement()
                                        .accept(new SimpleElementVisitor7<String, Void>() {

                                    @Override
                                    public String visitType(TypeElement e, Void p) {
                                        return e.getQualifiedName().toString();
                                    }
                                }, null);
                            }
                        }, null);
                    }
                }, null);
            }
        }
        throw new RuntimeException(elementName);
    }
}
