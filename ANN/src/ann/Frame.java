package ann;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Frame extends JFrame {
    private final String HOME_PAGE = "<html><h2>Home page</h2></html>";
    private final String ABOUT_PAGE = "<html><h2>About page</h2></html>";
    private final String SOMETHING_WRONG = "<html><h2>Something went wrong..</h2></html>";
    private final String LOAD_TRAIN_DATASET = "<html><h2>Loading dataset..</h2></html>";
    private final String SUCCESS = "<html><h2>Succeed action!</h2></html>";
    private final MenuBar menuBar;
    private final Menu menuMenu, datasetMenu;
    private final MenuItem[] menuItems, datasetItems;
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
        
        datasetMenu = new Menu("LOAD DATASETS");
        datasetItems = new MenuItem[1];
        datasetItems[0] = new MenuItem("ionosphere");
        for (short i=0; i< datasetItems.length; i++)
            datasetMenu.add(datasetItems[i]);
        menuBar.add(datasetMenu);
        
        setMenuBar(menuBar);
      
        label = new JLabel();
        this.setTextLabel("<html><h2>Welcome!</h2></html>");
        this.add(label);
    }
    
    private void setTextLabel(String text){
        label.setText(text);
    }
    
     // Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Home" ->  {
                    this.setTextLabel(this.HOME_PAGE);
                    break;
                }
                case "About" ->  {
                    this.setTextLabel(this.ABOUT_PAGE);
                    break;
                }
                case "ionosphere" ->  {
                    this.setTextLabel(this.LOAD_TRAIN_DATASET);
                    
                    FileOperations fileOperations = new FileOperations();
                    if (!fileOperations.loadDataset("ionosphere.train"))
                        this.setTextLabel(this.SOMETHING_WRONG);
                    else {
                        if (!fileOperations.loadDataset("ionosphere.test"))
                            this.setTextLabel(this.SOMETHING_WRONG);
                        else
                            this.setTextLabel(this.SUCCESS);
                    }
                    
                    break;
                }
            }
        }
        else
            super.action(event,obj);
        
        return true;
    }
    
}
