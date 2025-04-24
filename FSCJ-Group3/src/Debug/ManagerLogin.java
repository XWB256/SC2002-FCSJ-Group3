package Debug;

import Menus.HDBManagerMenu;
import Users.HDBManager;
import Users.User;

public class ManagerLogin {
    public static void main(String[] args) {
        HDBManager manager = new HDBManager("Jessica", "S5678901G", 37, User.MaritalStatus.MARRIED, "password");
        new HDBManagerMenu(manager).run();
    }
}
