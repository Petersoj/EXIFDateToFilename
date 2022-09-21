package net.jacobpeterson.randomprojects;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * {@link Arguments} contains the arguments parsed from the CLI.
 */
public final class Arguments {

    private final String[] cliArguments;
    private JCommander jCommander;

    @Parameter(names = {"-h", "--help"},
            description = "Prints the usage.")
    private boolean printHelp = false;

    @Parameter(names = {"-f", "--file"},
            description = "The image file path or directory with image files to set the name to its EXIF created " +
                    "image date.")
    private String path;

    /**
     * Instantiates a new {@link Arguments}.
     *
     * @param cliArguments the CLI arguments
     */
    public Arguments(String[] cliArguments) {
        this.cliArguments = cliArguments;
    }

    /**
     * Parses the arguments.
     *
     * @throws ParameterException thrown for {@link ParameterException}s from {@link JCommander}
     */
    public void parse() throws ParameterException {
        jCommander = JCommander.newBuilder()
                .programName("exiffilename")
                .addObject(this)
                .build();
        jCommander.parse(cliArguments);
    }

    public JCommander getJCommander() {
        return jCommander;
    }

    public boolean printHelp() {
        return printHelp;
    }

    public String getPath() {
        return path;
    }
}
