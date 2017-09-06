package com.harshil;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IconThemeCommandGenerator extends BaseCommandGenerator {

    private static final String THEME_PAGE_URL = "http://www.noobslab.com/p/themes-icons.html";

    public static void main(String args[]) {
        Set<String> aptAddCommandsSet = null, aptGetCommandsSet = null;
        try {
            Document rootPage = Jsoup.connect(THEME_PAGE_URL).get();
            Element iconsSection =
                    rootPage.getElementsByClass("entry-content").first().getElementsByTag("table").last();

            Map<String, Set<String>> themeCommands = processThemesTable(iconsSection);
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
