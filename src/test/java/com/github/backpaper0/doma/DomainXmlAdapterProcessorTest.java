package com.github.backpaper0.doma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class DomainXmlAdapterProcessorTest {

    private final DomainXmlAdapterProcessor processor = new DomainXmlAdapterProcessor();

    @Test
    public void test() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(source("com.example.Key", "/Key.txt")).processedWith(processor)
                .compilesWithoutError().and()
                .generatesSources(source("com.example.KeyAdapter", "/KeyAdapter.txt"));
    }

    @Test
    public void factoryMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(source("com.example.Key", "/Key_factoryMethod.txt")).processedWith(processor)
                .compilesWithoutError().and().generatesSources(
                        source("com.example.KeyAdapter", "/KeyAdapter_factoryMethod.txt"));
    }

    @Test
    public void accessorMethod() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(source("com.example.Key", "/Key_accessorMethod.txt")).processedWith(processor)
                .compilesWithoutError().and().generatesSources(
                        source("com.example.KeyAdapter", "/KeyAdapter_accessorMethod.txt"));
    }

    @Test
    public void generics() throws Exception {
        Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(source("com.example.Key", "/Key_generics.txt")).processedWith(processor)
                .compilesWithoutError().and()
                .generatesSources(source("com.example.KeyAdapter", "/KeyAdapter_generics.txt"));
    }

    private JavaFileObject source(String className, String resource) throws Exception {
        Path file = Paths.get(getClass().getResource(resource).toURI());
        List<String> lines = Files.readAllLines(file);
        return JavaFileObjects.forSourceLines(className, lines);
    }
}
