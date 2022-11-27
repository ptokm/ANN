package ann;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Frame extends JFrame {
    private final String HOME_PAGE = "<html><h2>Home page</h2></html>";
    private final String ABOUT_PAGE = "<html><h2>About page</h2></html>";
    private final MenuBar menuBar;
    private final Menu menuMenu;
    private final MenuItem[] menuItems;
    private static JLabel label;
    
    Frame(String title) {
        // Configuration of display window
        super(title);
        setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        // A flow layout arranges components in a left-to-right flow, 
        // much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        // Menu configuration
        menuBar = new MenuBar();
        
        menuMenu = new Menu("MENU");
        menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("Home");
        menuItems[1] = new MenuItem("About");
        for (short i=0; i<menuItems.length; i++) {
            menuMenu.add(menuItems[i]);
        }
        menuBar.add(menuMenu);
        setMenuBar(menuBar);
      
        label = new JLabel();
        setTextLabel("<html><h2>Welcome!</h2></html>");
        this.add(label);
    }
    
    private static void setTextLabel(String text){
        label.setText(text);
    }
    
     // Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home" -> setTextLabel(this.HOME_PAGE);
                case "About" -> setTextLabel(this.ABOUT_PAGE);
            }
        }
        else
            super.action(event,obj);
        
        return true;
    }
}
