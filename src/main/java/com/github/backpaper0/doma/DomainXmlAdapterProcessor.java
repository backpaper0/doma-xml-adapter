package com.github.backpaper0.doma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@SupportedAnnotationTypes("org.seasar.doma.Domain")
public class DomainXmlAdapterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            for (TypeElement domainClassElement : ElementFilter
                    .typesIn(roundEnv.getElementsAnnotatedWith(typeElement))) {
                DomainXmlAdapterModel model = new DomainXmlAdapterModel(domainClassElement,
                        processingEnv.getElementUtils());
                try {
                    JavaFileObject file = processingEnv.getFiler().createSourceFile(
                            model.getPackageName() + "." + model.getAdapterClassSimpleName());
                    try (Writer writer = file.openWriter()) {
                        generate(model, writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static void generate(DomainXmlAdapterModel model, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        out.printf("package %1$s;%n", model.getPackageName());
        out.printf("%n");
        out.printf("public class %1$s extends %2$s<%3$s, %4$s> {%n",
                model.getAdapterClassSimpleName(), XmlAdapter.class.getName(), model.getValueType(),
                model.getBoundType());
        out.printf("%n");
        out.printf("    public %1$s unmarshal(%2$s v) throws Exception {%n", model.getBoundType(),
                model.getValueType());
        if (model.getFactoryMethod().equals("new")) {
            out.printf("        return new %1$s(v);%n", model.getBoundType());
        } else {
            out.printf("        return %1$s.%2$s(v);%n", model.getBoundType(),
                    model.getFactoryMethod());
        }
        out.printf("    }%n");
        out.printf("%n");
        out.printf("    public %1$s marshal(%2$s v) throws Exception {%n", model.getValueType(),
                model.getBoundType());
        out.printf("        return v.%1$s();%n", model.getAccessorMethod());
        out.printf("    }%n");
        out.printf("}%n");
        out.flush();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
