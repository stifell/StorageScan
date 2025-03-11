package com.stifell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

/**
 * @author stifell on 11.03.2025
 */
public class DirectorySizeCalculator {
    private HashMap<String, Long> sizes;

    HashMap<String, Long> calculateFolderSize(Path path){
        try {
            sizes = new HashMap<>();
            Files.walkFileTree(path, new SimpleFileVisitor<>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        long size = Files.size(file);
                        updateFolderSize(file, size);
                    } catch (IOException e) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });

            return sizes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFolderSize(Path path, Long size){
        String key = path.toString();
        sizes.put(key, size + sizes.getOrDefault(key, 0L));

        Path parent = path.getParent();

        if (parent != null){
            updateFolderSize(parent, size);
        }
    }
}
