package com.github.backpaper0.doma;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class DomainXmlAdapterProcessorTest {

    @Test
    public void test() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key",
                        Files.readAllLines(Paths.get(getClass().getResource("/Key.txt").toURI()))))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(
                        JavaFileObjects.forSourceLines("com.example.KeyAdapter", Files.readAllLines(
                                Paths.get(getClass().getResource("/KeyAdapter.txt").toURI()))));
    }

    @Test
    public void factoryMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key",
                        Files.readAllLines(Paths
                                .get(getClass().getResource("/Key_factoryMethod.txt").toURI()))))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        Files.readAllLines(Paths.get(
                                getClass().getResource("/KeyAdapter_factoryMethod.txt").toURI()))));
    }

    @Test
    public void accessorMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key",
                        Files.readAllLines(Paths
                                .get(getClass().getResource("/Key_accessorMethod.txt").toURI()))))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        Files.readAllLines(Paths.get(getClass()
                                .getResource("/KeyAdapter_accessorMethod.txt").toURI()))));
    }

    @Test
    public void generics() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forSourceLines("com.example.Key",
                        Files.readAllLines(
                                Paths.get(getClass().getResource("/Key_generics.txt").toURI()))))
                .processedWith(new DomainXmlAdapterProcessor()).compilesWithoutError().and()
                .generatesSources(JavaFileObjects.forSourceLines("com.example.KeyAdapter",
                        Files.readAllLines(Paths
                                .get(getClass().getResource("/KeyAdapter_generics.txt").toURI()))));
    }
}
