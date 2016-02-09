package com.github.backpaper0.doma;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.SimpleTypeVisitor7;

public class DomainXmlAdapterModel {

    private final String packageName;
    private final String adapterClassSimpleName;
    private final String boundType;
    private final String valueType;
    private final String factoryMethod;
    private final String accessorMethod;

    public DomainXmlAdapterModel(TypeElement domainClassElement, Elements elements) {
        AnnotationMirror atDomain = getDomainAnnotation(domainClassElement);
        Map<String, AnnotationValue> map = new HashMap<>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elements
                .getElementValuesWithDefaults(atDomain).entrySet()) {
            map.put(entry.getKey().getSimpleName().toString(), entry.getValue());
        }
        this.packageName = elements.getPackageOf(domainClassElement).getQualifiedName().toString();
        this.adapterClassSimpleName = domainClassElement.getSimpleName().toString() + "Adapter";
        this.boundType = domainClassElement.getQualifiedName().toString();
        this.valueType = map.get("valueType").accept(v, null);
        this.factoryMethod = map.get("factoryMethod").accept(v, null);
        this.accessorMethod = map.get("accessorMethod").accept(v, null);
    }

    static AnnotationMirror getDomainAnnotation(TypeElement domainClassElement) {
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

    static SimpleAnnotationValueVisitor7<String, Void> v = new SimpleAnnotationValueVisitor7<String, Void>() {
        @Override
        public String visitType(TypeMirror t, Void p) {
            return t.accept(new SimpleTypeVisitor7<String, Void>() {
                @Override
                public String visitDeclared(DeclaredType t, Void p) {
                    return t.asElement().accept(new SimpleElementVisitor7<String, Void>() {
                        @Override
                        public String visitType(TypeElement e, Void p) {
                            return e.getQualifiedName().toString();
                        }
                    }, null);
                }
            }, null);
        }

        @Override
        public String visitString(String s, Void p) {
            return s;
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public String getAdapterClassSimpleName() {
        return adapterClassSimpleName;
    }

    public String getBoundType() {
        return boundType;
    }

    public String getValueType() {
        return valueType;
    }

    public String getFactoryMethod() {
        return factoryMethod;
    }

    public String getAccessorMethod() {
        return accessorMethod;
    }
}
