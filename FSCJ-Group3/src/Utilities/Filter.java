package Utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static Utilities.Display.displayHeader;
import static Utilities.Display.displaySeperator;

public class Filter {

    private static final Scanner scanner = new Scanner(System.in);

    public static <T extends Searchable> List<T> filterRun(List<T> list){
        List<T> currentList = list;

        while(true){
            System.out.println("\n<<<<< Filter >>>>>");
            System.out.println("1. Default (by alphabet)");
            System.out.println("2. Filter by keywords");
            System.out.println("3. Filter by range");
            System.out.println("4. Reset Filter");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    currentList = defaultFilter(list);
                    printResult(currentList);
                    break;
                case "2":
                    System.out.print("Enter Keywords (separate by space): ");
                    String text = Utility.inputNonEmptyString(scanner);
                    List<String> keywords = List.of(text.split(" "));
                    currentList = filterByKeywords(list, keywords);
                    printResult(currentList);
                    break;
                case "3":
                    System.out.print("Enter two numbers (separate by space) to set range, enter -1 if don't regard a boundary: ");
                    int[] range = Utility.inputRange(scanner);
                    currentList = filterByRange(list,range);
                    printResult(currentList);
                    break;
                case "4":
                    System.out.println("Filter has been reset!");
                    currentList = defaultFilter(list);
                    printResult(currentList);
                    break;
                case "0":
                    //System.out.println("Filter Exited...");
                    System.out.println();
                    return currentList;
                default:
                    System.out.println("Invalid choice, please try again.");
            }

        }
    }

    public static <T extends Searchable> void printResult(List<T> list){
        if(!list.isEmpty()) {
            displaySeperator();
            list.forEach(
                    project -> {
                        System.out.println(project);
                        displaySeperator();
                    }
            );
        }

        else{
            displaySeperator();
            System.out.println("No result found!");
        }
    }

    public static <T extends Searchable> List<T> updateCurrentFilter(List<T> current, List<T> list) {
        if(list==null || list.isEmpty() || current==null || current.isEmpty()) return new ArrayList<>();

        List<T> result = new ArrayList<>();
        for (T item : list) {

            String searchData = item.IDstring();
            if(searchData==null) return new ArrayList<>();

            for (T i : current) {
                if (i.IDstring().equals(searchData)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
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

    public static <T extends Searchable> List<T> defaultFilter(List<T> list) {
        return list.stream()
                .sorted(Comparator.comparing(T::defaultString))
                .collect(Collectors.toList());
    }

    public static <T extends Searchable> List<T> filterByKeywords(List<T> list, List<String> keywords) {

        if(list==null || list.isEmpty()) return new ArrayList<>();

        List<T> result = new ArrayList<>();
        for (T item : list) {
            
            String searchData = item.toSearchableString();
            if(searchData==null) return new ArrayList<>();

            boolean matchesAll = true;
            for (String kw : keywords) {
                if (!searchData.contains(kw.toLowerCase())) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) result.add(item);
        }
        return result;
    }

    public static <T extends Searchable> List<T> filterByRange(List<T> list, int[] range) {

        if(list==null || list.isEmpty()) return new ArrayList<>();

        List<T> result = new ArrayList<>();
        for (T item : list) {

            List<Integer> searchData = item.toSearchableNum();
            if(searchData==null) return new ArrayList<>();

            for (int num : searchData) {
                if (num >= range[0] && num <= range[1]) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }

    public static <T extends Searchable> List<T> filterByPrinciple(List<T> list, Predicate<T> predicate) {

        if(list==null || list.isEmpty()) return new ArrayList<>();

        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /*
    public static void main(String[] args) {
        List<Project> projects = ProjectLoader.loadProjects();
        filterRun(projects);
    }
     */

}
