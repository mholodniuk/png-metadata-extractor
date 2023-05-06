package com.dev.imageprocessingapi.filehandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileReadingTests {
    private File currentFile;

    @BeforeAll
    public void readFile() {
        currentFile = Paths.get("src","test", "resources", "file.txt").toFile();
    }

    @Test
    public void correctlyReadFile() {
        Assertions.assertNotNull(currentFile);
    }
}
