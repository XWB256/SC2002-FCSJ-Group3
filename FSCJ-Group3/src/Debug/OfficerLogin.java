package Debug;

import Menus.HDBOfficerMenu;
import Users.HDBOfficer;
import Users.User;

public class OfficerLogin {
    public static void main(String[] args) {
        HDBOfficer officer = new HDBOfficer("Daniel", "T2109876H", 36, User.MaritalStatus.SINGLE, "password");
        new HDBOfficerMenu(officer).run();
    }
}
