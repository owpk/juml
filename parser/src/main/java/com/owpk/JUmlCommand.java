package com.owpk;

import java.nio.file.Path;

import com.owpk.core.javaimpl.JavaExtractorImpl;
import com.owpk.drawio.UmlDrawEngine;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "juml", description = "...", mixinStandardHelpOptions = true)
public class JUmlCommand implements Runnable {

    @Option(names = { "-s", "--source" }, description = "Source file/dir", required = true)
    Path source;

    @Option(names = { "-t", "--target" }, description = "Target drawio file", required = true)
    Path target;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(JUmlCommand.class, args);
    }

    public void run() {
        System.out.println("Source: " + source);
        System.out.println("Target: " + target);

        var extractor = new JavaExtractorImpl(source);
        var drawio = new UmlDrawEngine(extractor, target);
        drawio.createDrawio();
    }
}
