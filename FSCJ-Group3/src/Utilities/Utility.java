package Utilities;

import Classes.Application;
import Classes.OfficerApplication;
import Classes.Project;
import Loaders.ApplicationLoader;
import Loaders.OfficerApplicationLoader;
import Loaders.ProjectLoader;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Utilities.Display.displayHeader;
import static Utilities.Display.displaySeperator;

public class Utility {
    //current date to be used for welcoming
    private static final Scanner scanner = new Scanner(System.in);
    public static final LocalDate CURRENT_DATE_ORIGINAL = LocalDate.now();
    public static final String CURRENT_DATE =String.format("%02d-%02d-%d",
            CURRENT_DATE_ORIGINAL.getDayOfMonth(),
            CURRENT_DATE_ORIGINAL.getMonthValue(),
            CURRENT_DATE_ORIGINAL.getYear());

//    Scanner sc = new Scanner(System.in);

    //Int, double and String related validation-----------------------------------------------
    public static int inputSafeInt(Scanner sc) {
        while (true) {
            try {
                int input = sc.nextInt();
                if (input<0) {
                    throw new Exception();
                }
                sc.nextLine();
                return input;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer.");
                sc.nextLine();
            }
        }
    }

    public static int[] inputRange(Scanner sc) {
        while (true) {
            try {
                int a = sc.nextInt();
                int b = sc.nextInt();
                sc.nextLine();

                int min = (a < 0)? Integer.MIN_VALUE : a;
                int max = (b < 0)? Integer.MAX_VALUE : b;
                if (min <= max) {
                    return new int[] {min,max};
                } else {
                    System.out.println("Invalid input. the former should be smaller than the latter.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                sc.nextLine();
            }
        }
    }

    public static int validateIntRange(Scanner sc, int min, int max) {
        while (true) {
            try {
                int input = sc.nextInt();
                if (input >= min && input <= max) {
                    sc.nextLine();
                    return input;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                sc.nextLine();
            }
        }
    }

    public static String inputNonEmptyString(Scanner sc) {
        while (true) {
            String input = sc.nextLine();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter a non-empty string.");
            }
        }
    }

    public static String replaceEmptyString(String s){
        if(s.isEmpty()){s += "None";}
        return s;
    }

    //Date related validation---------------------------------------------------------
    public static String inputSafeDate(Scanner sc) {
        while (true) {
            String date=Utility.inputNonEmptyString(sc);
            if (validateDateFormat(date)) {
                return date;
            }
        }
    }



    public static boolean validateDateFormat(String date) {

        // Validate date format (DD-MM-YYYY)
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            System.out.println("Invalid date format. Please use DD-MM-YYYY format.");
            return false;
        }

        try {
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            // Basic date validation
            if (day < 1 || day > 31) {
                System.out.println("Invalid day. Must be between 1 and 31.");
                return false;
            }
            if (month < 1 || month > 12) {
                System.out.println("Invalid month. Must be between 1 and 12.");
                return false;
            }
            if (year < CURRENT_DATE_ORIGINAL.getYear()) {
                System.out.println("Invalid year. Cannot be in the past.");
                return false;
            }


            // Validate against current date
            LocalDate Date = LocalDate.of(year, month, day);
            if (Date.isBefore(CURRENT_DATE_ORIGINAL)) {
                System.out.println("Cannot be in the past.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid date format. Please use numbers for DD-MM-YYYY.");
            return false;
        } catch (Exception e) {
            System.out.println("Invalid date. Please check your input.");
            return false;
        }
    }

    public static int compareDate(String date1, String date2) {
        String[] dateParts1 = date1.split("-");
        String[] dateParts2 = date2.split("-");
        int year1 = Integer.parseInt(dateParts1[2]);
        int year2 = Integer.parseInt(dateParts2[2]);
        int month1 = Integer.parseInt(dateParts1[1]);
        int month2 = Integer.parseInt(dateParts2[1]);
        int day1 = Integer.parseInt(dateParts1[0]);
        int day2 = Integer.parseInt(dateParts2[0]);

        if (year1 != year2) {
            return year1 - year2;
        } else if (month1 != month2) {
            return month1 - month2;
        } else if (day1 != day2) {
            return day1 - day2;
        }
        else {
            return 0;
        }
    }

    public static boolean isFutureDate(String date) {
        return compareDate(date, CURRENT_DATE +"-0") > 0;
    }

    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(CURRENT_DATE_ORIGINAL);
    }

    public static boolean isPastDate(String date) {
        return compareDate(date, CURRENT_DATE +"-24") < 0;
    }

    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(CURRENT_DATE_ORIGINAL);
    }

    public static void sortDateList(ArrayList<String> list) {
        list.sort(Utility::compareDate);
    }

    //Removing empty lines in CSV-----------------------------------------------------
    public static void removeEmptyLines(String csvPath) {
        String line;
        List<String> nonEmptyLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    nonEmptyLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
            for (String nonEmptyLine : nonEmptyLines) {
                writer.write(nonEmptyLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeEmptyLines(String[] csvPaths) {
        for (String csvPath : csvPaths) {
            removeEmptyLines(csvPath);
        }
    }

    //Password related----------------------------------------------------------------
    public static boolean validatePassword(String password) {
        if (password == null || password.isEmpty() || password.equals("\n")) {
            System.out.println("Error: Password cannot be empty.");
            return false;
        }
        if (password.length() < 8) {
            System.out.println("Error: Password must be at least 8 characters long.");
            return false;
        }
        return true;
    }

    //Project related-----------------------------------------------------------------
    public static boolean searchProject (String ProjectName){
        return ProjectLoader.loadProjects().stream()
                .anyMatch(project -> project.getProjectName().equals(ProjectName));
    }

    public static <T> void printCurrentList(List<T> currentFilteredList){
        if(currentFilteredList != null) {

            System.out.print("Enter 1 to print current Filtered List (Enter 0 to skip): ");
            String choice = scanner.nextLine().trim();
            displaySeperator();

            if(!choice.equals("1")) {
                return;
            }

            displayHeader("Your current Filtered List");
            currentFilteredList.forEach(
                    project -> {
                        System.out.println(project);
                        displaySeperator();
                    }
            );
        }

    }

    public static Project selectProject(List<Project> projectList){
        Project selectedProject = null;
        do {
            System.out.print("Enter the name of the Project (or 0 to return): ");
            String projectName = scanner.nextLine().trim();

            if (projectName.equals("0")) {
                displaySeperator();
                System.out.println("Returned. ");
                return null;
            }
            selectedProject = projectList.stream()
                    .filter(project -> project.getProjectName().equals(projectName))
                    .findFirst()
                    .orElse(null);

            if (selectedProject == null) {
                System.out.println("Project Not Found. Make sure the project name is exact.");
                displaySeperator();
            }
        } while (selectedProject == null);

        return selectedProject;
    }


}
