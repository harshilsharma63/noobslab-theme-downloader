package com.harshil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.ImmutableMap;

abstract class BaseCommandGenerator {

    static Element findOperatingSystemSpecificThemesTable(Element themesSection, String operatingSystemName) {

        int operatingSystemHeaderIndex = -1;

        List<Element> titles =
                themesSection
                        .select(".post-body.entry-content")
                        .first()
                        .getElementsByTag("center")
                        .first()
                        .getElementsByTag("h3");

        for (int i = 0; i < titles.size(); ++i) {
            if (titles.get(i).text().startsWith(operatingSystemName)) {
                operatingSystemHeaderIndex = i;
                break;
            }
        }

        if (operatingSystemHeaderIndex == -1) {
            throw new IllegalArgumentException("Could not found operating system: [" + operatingSystemName + "]");
        }

        int themesTableIndex = 2 * operatingSystemHeaderIndex + 1;
        return themesSection.getElementsByTag("table").get(themesTableIndex);
    }

    static Map<String, Set<String>> processThemesTable(Element themesTable) {
        Set<String> aptAddCommandsSet = new HashSet<>();
        Set<String> aptGetCommandsSet = new HashSet<>();

        for (Element row : themesTable.getElementsByTag("tr")) {
            for (Element cell : row.getElementsByTag("td")) {
                System.out.println("Processing theme '" + cell.text());
                String themePageLink = cell.getElementsByTag("a").first().attr("href");
                Document themePage = null;

                for (int i = 0; i < 5 && themePage == null; ++i) {
                    if (i > 0) {
                        System.out.println("Retrying theme '" + cell.text());
                    }
                    try {
                        themePage = Jsoup.connect(themePageLink).get();
                    } catch (IOException e) {
                        System.out.println(
                                "Error encountered while processing theme: [" + cell.text() + "]. Reason: [" + e + "]");
                    }
                }

                if (themePage != null) {
                    Element commandsTable = themePage.getElementsByClass("code-table").first();
                    Elements commands = commandsTable.getElementsByTag("tr");
                    String command;
                    for (int i = 1; i < commands.size(); ++i) {
                        command = commands.get(i).text();
                        switch (command.substring(0, 14)) {
                            case "sudo add-apt-r":
                                aptAddCommandsSet.add(command);
                                break;
                            case "sudo apt-get i":
                                aptGetCommandsSet.add(command);
                                break;
                        }
                    }
                }
            }
        }

        return new ImmutableMap.Builder<String, Set<String>>()
                .put("aptAddCommands", aptAddCommandsSet)
                .put("aptGetCommands", aptGetCommandsSet)
                .build();
    }
}
