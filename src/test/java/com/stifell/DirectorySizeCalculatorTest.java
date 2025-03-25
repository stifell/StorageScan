package com.stifell;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author stifell on 25.03.2025
 */
public class DirectorySizeCalculatorTest {
    @Test
    public void testCalculateFolderSizeOnDiskC() {
        DirectorySizeCalculator calculator = new DirectorySizeCalculator();
        Path diskCPath = Paths.get("C:\\");
        long startTime = System.currentTimeMillis();

        HashMap<String, Long> sizes = calculator.calculateFolderSize(diskCPath);

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Время выполнения расчёта размеров для диска C: " + elapsedSeconds + " с.");

        assertNotNull(sizes, "Результат не должен быть null");
        String diskCKey = diskCPath.toString();
        assertTrue(sizes.containsKey(diskCKey), "Результат должен содержать ключ для " + diskCKey);
    }
}
