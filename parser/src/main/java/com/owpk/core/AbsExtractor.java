package com.owpk.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import com.owpk.model.ClassUml;

public abstract class AbsExtractor implements SourceCodeExtractor {
    protected final Map<String, ClassUml> sourceEntries = new HashMap<>();
    protected final Queue<ChainEntry> unresolwedChains = new LinkedList<>();
    protected String extension;

    private Path extractPath;

    public static record ChainEntry(String parentType, ClassUml uml) {
    }

    public AbsExtractor(Path extractFromPath, String fileExtension) {
        this.extension = fileExtension;
        this.extractPath = extractFromPath;
        if (!Files.exists(extractFromPath))
            throw new RuntimeException("Path does not exist: " + extractFromPath);
    }

    /**
     * Constructor for AbsExtractor
     * 
     * @param extractFromPath
     *                        path to extract source code from
     * @param fileExtension
     *                        file extension to filter files
     */
    public AbsExtractor(String extractFromPath, String fileExtension) {
        this(Paths.get(extractFromPath), fileExtension);
    }

    @Override
    public List<ClassUml> extractSource() {
        var rawSources = getRawData(extractPath);
        var sources = rawSources.stream()
                .map(it -> getUmlSource(it))
                .flatMap(it -> it.stream())
                .toList();

        while (!unresolwedChains.isEmpty()) {
            var next = unresolwedChains.poll();
            var parent = sourceEntries.get(next.parentType());
            if (parent != null) {
                next.uml.getParent().add(parent);
            } else {
                System.out.println("Unresolved parent type: " + next.parentType());
            }
        }
        return sources;
    }

    /**
     * Get .java files from a directoryPath recursively
     * 
     * @param directoryPath
     *                      directoryPath received by user
     * @return List<Path>
     *
     * @throws IOException
     */
    protected List<String> getRawData(Path dirPath) {

        try {
            return Files.walk(dirPath)
                    .filter(it -> Files.isRegularFile(it) && it.toString()
                            .endsWith(extension))
                    .map(it -> {
                        try {
                            return Files.readString(it);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Optional<ClassUml> getUmlSource(String path) throws RuntimeException;

}
