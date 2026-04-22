import com.wiseplanner.console.ConsoleUI;
import com.wiseplanner.gui.App;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("--console")) {
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.show();
        }

        else {
            Application.launch(App.class, args);
        }
    }
}