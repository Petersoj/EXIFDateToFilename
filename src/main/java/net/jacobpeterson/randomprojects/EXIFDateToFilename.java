package net.jacobpeterson.randomprojects;

import com.beust.jcommander.ParameterException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EXIFDateToFilename {

    /**
     * The entry point of application.
     *
     * @param cliArguments the input arguments
     */
    public static void main(String[] cliArguments) throws Exception {
        final Arguments arguments = new Arguments(cliArguments);
        try {
            arguments.parse();
        } catch (ParameterException exception) {
            System.err.println("An error occurred while parsing arguments.");
            exception.usage();
            return;
        } catch (IllegalArgumentException exception) {
            System.err.printf("An illegal argument was given: %s\n", exception.getMessage());
            return;
        } catch (Exception exception) {
            System.err.printf("An exception occurred while parsing the given arguments: %s\n", exception.getMessage());
            return;
        }

        if (arguments.printHelp()) {
            arguments.getJCommander().usage();
            return;
        }

        // Validate path
        if (arguments.getPath() == null) {
            System.err.println("No path was given.");
            return;
        }
        final File path = new File(arguments.getPath());
        if (!path.exists()) {
            System.err.println("The given path does not exist.");
            return;
        }

        if (path.isFile()) {
            renameFile(path);
        } else if (path.isDirectory()) {
            Files.walk(path.toPath(), 1)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(imageFile -> {
                        try {
                            renameFile(imageFile);
                        } catch (Exception ignored) {}
                    });
        } else {
            System.err.println("Unknown path");
        }
    }

    public static void renameFile(File imageFile) throws Exception {
        // Extract creation date and rename file
        final Metadata imageMetadata = ImageMetadataReader.readMetadata(imageFile);
        for (Directory directory : imageMetadata.getDirectories()) {
            if (directory.getName().equalsIgnoreCase("exif ifd0")) {
                for (Tag tag : directory.getTags()) {
                    if (tag.getTagName().equalsIgnoreCase("date/time")) {
                        final LocalDateTime imageDateTime = LocalDateTime.parse(tag.getDescription(),
                                DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));

                        String originalImageExtension = "";
                        int extensionIndex = imageFile.getName().lastIndexOf('.');
                        if (extensionIndex > 0) {
                            originalImageExtension = imageFile.getName().substring(extensionIndex);
                        }

                        final String newFilename = imageDateTime.format(
                                DateTimeFormatter.ofPattern("yyyy–MM–dd • HH.mm.ss")) + originalImageExtension;
                        final File newFile = new File(imageFile.getParent(), newFilename);
                        if (newFile.exists()) {
                            System.err.printf("File %s Already exists.\n", newFile.getPath());
                        } else {
                            imageFile.renameTo(newFile);
                        }

                        System.out.println("Renamed file " + imageFile.getName() + " to " + newFile.getName());
                        return;
                    }
                }
            }
        }
    }
}
