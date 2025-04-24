package Utilities;

import Classes.Project;

import java.util.List;

public class Display {
    static int width = 90;
    public static final String BOLD = "\033[1m";
    public static final String UNDERLINE = "\033[4m";
    public static final String RESET = "\033[0m";

    public static void displayHeader(String content) {
        int contentLength = content.length();
        int padding = (width - contentLength) / 2;
        String centeredContent = " ".repeat(padding) + BOLD + content + RESET+ " ".repeat(width - contentLength - padding);
        System.out.println(centeredContent);
        System.out.println("=".repeat(width));
    }

    public static void displaySubHeader(String content){
        System.out.println(BOLD + UNDERLINE + content + RESET);
    }

    public static void displayFullProjectDetails(Project p) {
        System.out.println(p);
    }

    public static void displaySeperator() {
        System.out.println("=".repeat(width));
    }

    public static void displaySelection(String content) {
        System.out.println("[SELECTION] " + content);
    }
    // New generic table display function
    public static void displayTable(List<String[]> rows, List<String> headers) {
        int columnCount = headers.size();
        int columnWidth = width / columnCount;

        // Display Header
        for (String header : headers) {
            int padding = (columnWidth - header.length()) / 2;
            String centeredHeader = " ".repeat(padding) + BOLD + header + RESET + " ".repeat(columnWidth - header.length() - padding);
            System.out.print(centeredHeader);
        }
        System.out.println();
        displaySeperator();

        // Display Rows
        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                String content = row[i];
                int padding = (columnWidth - content.length()) / 2;
                String centeredContent = " ".repeat(padding) + content + " ".repeat(columnWidth - content.length() - padding);
                System.out.print(centeredContent);
            }
            System.out.println();
        }

        displaySeperator();
    }
}
