package br.com.ifood.ifoodbackendconnection;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

import static org.springframework.boot.ansi.AnsiColor.DEFAULT;
import static org.springframework.boot.ansi.AnsiColor.GREEN;
import static org.springframework.boot.ansi.AnsiStyle.FAINT;

public class ServiceBanner implements Banner {

    private static final int STRAP_LINE_SIZE = 47;

    private static final String[] BANNER = {
            "\n" +
                    "                                                                                        \n" +
                    ",--. ,---.                 ,--.    ,-----.                ,--.                     ,--. \n" +
                    "|  |/  .-' ,---.  ,---.  ,-|  |    |  |) /_  ,--,--. ,---.|  |,-. ,---. ,--,--,  ,-|  | \n" +
                    "|  ||  `-,| .-. || .-. |' .-. |    |  .-.  \\' ,-.  || .--'|     /| .-. :|      \\' .-. | \n" +
                    "|  ||  .-'' '-' '' '-' '\\ `-' |    |  '--' /\\ '-'  |\\ `--.|  \\  \\\\   --.|  ||  |\\ `-' | \n" +
                    "`--'`--'   `---'  `---'  `---'     `------'  `--`--' `---'`--'`--'`----'`--''--' `---'  \n" +
                    " ,-----.                                      ,--.  ,--.                \n" +
                    "'  .--./ ,---. ,--,--, ,--,--,  ,---.  ,---.,-'  '-.`--' ,---. ,--,--,  \n" +
                    "|  |    | .-. ||      \\|      \\| .-. :| .--''-.  .-',--.| .-. ||      \\ \n" +
                    "'  '--'\\' '-' '|  ||  ||  ||  |\\   --.\\ `--.  |  |  |  |' '-' '|  ||  | \n" +
                    " `-----' `---' `--''--'`--''--' `----' `---'  `--'  `--' `---' `--''--' \n" +
                    "                                                                        \n"
    };


    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        for (String line : BANNER) {
            out.println(line);
        }

        String profiles = " :: " + String.join(", ", environment.getActiveProfiles()) + " :: ";
        String version = "(v1.0)";

        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + profiles.length())) {
            padding += " ";
        }

        out.println(AnsiOutput.toString(GREEN, profiles, DEFAULT, padding, FAINT, version));
        out.println();
    }
}
