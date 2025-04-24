package Utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ReadInput {
    private static Scanner scanner = new Scanner(System.in);

    public static String readStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter a non-empty string.");
            }
        }
    }

    public static int readIntInput(String prompt) {

        int input = -1;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                input = Integer.parseInt(line.trim());
                if (input < 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid positive integer.");
            }
        }
        return input;
    }

    public static int[] readInputRange(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");

                if (parts.length != 2) {
                    System.out.println("Please enter two integers separated by space.");
                    continue;
                }

                int a = Integer.parseInt(parts[0]);
                int b = Integer.parseInt(parts[1]);

                int min = (a < 0) ? Integer.MIN_VALUE : a;
                int max = (b < 0) ? Integer.MAX_VALUE : b;

                if (min <= max) {
                    return new int[]{min, max};
                } else {
                    System.out.println("Invalid input. The first number should be smaller than or equal to the second.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter valid integers.");
            }
        }
    }
    public static double readDoubleInput(String prompt) {
        double input = -1;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                input = Double.parseDouble(line.trim());
                if (input < 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid positive number.");
            }
        }
        return input;
    }

    public static LocalDate readDateInput(String prompt) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (date == null) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                date = LocalDate.parse(input.trim(), formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format! Please use DD-MM-YYYY.");
            }
        }
        return date;
    }


}
