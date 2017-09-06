package com.harshil;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SystemThemeCommandGenerator extends BaseCommandGenerator {

    private static final String THEME_PAGE_URL = "http://www.noobslab.com/p/themes-icons.html";

    private static final String DEFAULT_TARGET_OPERATING_SYSTEM = "Ubuntu 16.04";

    public static void main(String args[]) {
        final String targetOperatingSystem;
        if (args.length > 1) {
            targetOperatingSystem = args[1];
        } else {
            targetOperatingSystem = DEFAULT_TARGET_OPERATING_SYSTEM;
        }

        Set<String> aptAddCommandsSet = null, aptGetCommandsSet = null;
        try {
            Document rootPage = Jsoup.connect(THEME_PAGE_URL).get();
            Element themesSection = rootPage.getElementsByClass("entry-content").first();
            themesSection = findOperatingSystemSpecificThemesTable(themesSection, targetOperatingSystem);

            Map<String, Set<String>> themeCommands = processThemesTable(themesSection);
            aptAddCommandsSet = themeCommands.get("aptAddCommands");
            aptGetCommandsSet = themeCommands.get("aptGetCommands");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("\n\n" + "****************\n" + "apt-add commands\n" + "****************");
            if (aptAddCommandsSet != null) {
                aptAddCommandsSet.forEach(System.out::println);
            }

            System.out.println("\n\n" + "****************\n" + "apt-get commands\n" + "****************");
            if (aptGetCommandsSet != null) {
                aptGetCommandsSet.forEach(System.out::println);
            }
        }
    }

}
