package com.github.backpaper0.doma;

import org.junit.Test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class DomainXmlAdapterProcessorTest {

    @Test
    public void test() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key", "package com.example;", "",
                        "@org.seasar.doma.Domain(valueType = String.class)", "public class Key {",
                        "    private final String value;",
                        "    public Key(String value) { this.value = value; }",
                        "    public String getValue() { return value; }", "}"))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        "package com.example;", "",
                        "public class KeyAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<java.lang.String, com.example.Key> {",
                        "",
                        "    public com.example.Key unmarshal(java.lang.String v) throws Exception {",
                        "        return new com.example.Key(v);", "    }", "",
                        "    public java.lang.String marshal(com.example.Key v) throws Exception {",
                        "        return v.getValue();", "    }", "}"));
    }

    @Test
    public void factoryMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key", "package com.example;", "",
                        "@org.seasar.doma.Domain(valueType = String.class, factoryMethod = \"valueOf\")",
                        "public class Key {", "    private final String value;",
                        "    private Key(String value) { this.value = value; }",
                        "    public String getValue() { return value; }",
                        "    public static Key valueOf(String text) { return new Key(text); }",
                        "}"))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        "package com.example;", "",
                        "public class KeyAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<java.lang.String, com.example.Key> {",
                        "",
                        "    public com.example.Key unmarshal(java.lang.String v) throws Exception {",
                        "        return com.example.Key.valueOf(v);", "    }", "",
                        "    public java.lang.String marshal(com.example.Key v) throws Exception {",
                        "        return v.getValue();", "    }", "}"));
    }

    @Test
    public void accessorMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key", "package com.example;", "",
                        "@org.seasar.doma.Domain(valueType = String.class, accessorMethod = \"value\")",
                        "public class Key {", "    private final String value;",
                        "    public Key(String value) { this.value = value; }",
                        "    public String value() { return value; }", "}"))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        "package com.example;", "",
                        "public class KeyAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<java.lang.String, com.example.Key> {",
                        "",
                        "    public com.example.Key unmarshal(java.lang.String v) throws Exception {",
                        "        return new com.example.Key(v);", "    }", "",
                        "    public java.lang.String marshal(com.example.Key v) throws Exception {",
                        "        return v.value();", "    }", "}"));
    }

    @Test
    public void generics() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key", "package com.example;", "",
                        "@org.seasar.doma.Domain(valueType = String.class)",
                        "public class Key<T> {", "    private final String value;",
                        "    public Key(String value) { this.value = value; }",
                        "    public String getValue() { return value; }", "}"))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        "package com.example;", "",
                        "public class KeyAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<java.lang.String, com.example.Key<?>> {",
                        "",
                        "    public com.example.Key<?> unmarshal(java.lang.String v) throws Exception {",
                        "        return new com.example.Key(v);", "    }", "",
                        "    public java.lang.String marshal(com.example.Key<?> v) throws Exception {",
                        "        return v.getValue();", "    }", "}"));
    }
}
