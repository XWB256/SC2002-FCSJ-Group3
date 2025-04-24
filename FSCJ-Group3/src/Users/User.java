package Users;

import Loaders.ApplicantLoader;
import Loaders.HDBManagerLoader;
import Loaders.HDBOfficerLoader;
import Utilities.Utility;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class User {
    private String name;
    private String NRIC;
    private int age;
    private String password;

    private MaritalStatus maritalStatus;
    public enum MaritalStatus {
        SINGLE,
        MARRIED
    }

    public User(String name, String NRIC, int age, String password, MaritalStatus maritalStatus) {
        this.name = name;
        this.NRIC = NRIC;
        this.age = age;
        this.password = password;
        this.maritalStatus = maritalStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNRIC() {
        return NRIC;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getNRIC(), user.getNRIC());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNRIC());
    }

    public static <T extends User> List<T> updateUser(T updated, List<T> records) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).equals(updated)) {
                records.set(i, updated);
                break;
            }
        }

        return records;
    }

    public <T extends User> void changePassword(T user){

        Scanner sc = new Scanner(System.in);

        while (true){

            System.out.print("Enter 1 to proceed changing password or 0 to return: ");
            String choice = sc.nextLine().trim();
            if(!choice.equals("1")) break;

            System.out.print("Enter your new Password: ");
            String newPassword1 = sc.nextLine();
            if(!Utility.validatePassword(newPassword1)) continue;

            System.out.print("Confirm your new Password: ");
            String newPassword2 = sc.nextLine();
            if (newPassword1.equals(newPassword2)){

                System.out.println("Confirm to change? (Y/n)");
                choice = sc.nextLine().trim();
                if(!choice.equalsIgnoreCase("y")) continue;

                user.setPassword(newPassword1);
                String role = user.getClass().getSimpleName();
                switch (role){
                    case "Applicant" -> ApplicantLoader.saveToApplicantCSV((Applicant) user);
                    case "HDBOfficer" -> HDBOfficerLoader.saveToOfficerCSV((HDBOfficer) user);
                    case "HDBManager" -> HDBManagerLoader.saveToManagerCSV((HDBManager) user);
                }
                System.out.println("Password Successfully Changed!");
                break;
            }
            else {
                System.out.println("Passwords don't match. Try Again");
            }
        }
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "\nNRIC: " + NRIC +
                "\nAge: " + age +
                "\nMarital Status: " + maritalStatus +
                "\nPassword: " + password;
    }
}
