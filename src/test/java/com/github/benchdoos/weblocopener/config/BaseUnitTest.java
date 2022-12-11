package com.github.benchdoos.weblocopener.config;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@Tag("weblocopener-unit-test")
public abstract class BaseUnitTest {

  @TempDir
  public static Path tempDir;

}
