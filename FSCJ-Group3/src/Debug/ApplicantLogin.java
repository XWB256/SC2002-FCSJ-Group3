package Debug;

import Menus.ApplicantMenu;
import Users.Applicant;
import Users.User;

public class ApplicantLogin {
    public static void main(String[] args) {
        Applicant applicant = new Applicant("Grace", "S9876543C", 37, User.MaritalStatus.MARRIED, "password");
        new ApplicantMenu(applicant).run();
    }
}
