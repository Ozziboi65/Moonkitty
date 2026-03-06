
package com.moonkitty.Util;

import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FileIO {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    public static void ExtractFromJar(String resPath, String outRelativePath) {
        LOGGER.info("Checking for Files...");

        Path outputPath = FabricLoader.getInstance().getGameDir().resolve(outRelativePath);

        if (Files.exists(outputPath)) {
            LOGGER.info("File Alredy exists, returning. outputPath: " + outputPath);
            return;
        }

        try (InputStream in = FileIO.class.getResourceAsStream("/" + resPath)) {

            if (in == null) {
                LOGGER.warn("Input Stream is Null, Maybe file doesnt exist?");
                return;
            }

            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.copy(in, outputPath);
            LOGGER.info("Finished Copying OutputPath: " + outputPath);

        } catch (Exception e) {
            LOGGER.error("exception while trying to extract file from jar to mc dir Exception: "
                    + e);
        }
    }

    public static InputStream InputStreamFromFile(String InputPath) {
        LOGGER.info("Trying to get Stream from file: " + InputPath);

        Path FilePath = FabricLoader.getInstance().getGameDir().resolve(InputPath);
        LOGGER.info("Resolved Path: " + FilePath);

        if (!Files.exists(FilePath)) {
            LOGGER.warn("File Could Not Be Found! Returning, Path: " + FilePath);
            return null;
        }

        try {
            return Files.newInputStream(FilePath);
        } catch (Exception e) {
            LOGGER.error("exception while trying to read a file! exception: " + e);
            return null;
        }
    }
}