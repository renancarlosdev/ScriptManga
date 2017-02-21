import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import db.MySQLMangaDAOImpl;
import db.UpdateFromHistory;

import objetos.Manga;

import java.beans.*;
import java.util.List;
import java.util.Random;
 
public class ProgressBarDemo extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
 
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	try{	
        		
                //Inicialização de variáveis
        		float progress = 0;                
                setProgress(0);
        		MySQLMangaDAOImpl msql = MySQLMangaDAOImpl.INSTANCE;
        		
                //Pega todos mangas
        		List<Manga> mangas = msql.getAllMangas();
        		
                //Inicialização de variáveis
        		final float progressRate = (float)100/mangas.size();        		
        		UpdateFromHistory uFH = new UpdateFromHistory();
    			

    			for(Manga m : mangas)
    			{
    				
                    //Verifica se a página e capitulo do manga atual no banco de dados é mais atual do que
                    // a que está no histórico                     
    				String s[] = uFH.research(m.getKeyword(),m.getUltimoCap(),m.getPagina());
    				
                    //Se a string não for null, as informações do banco, em relação ao historico do browser
                    //estão desatualizada
    				if(s != null)
    				{    					
    					m.setUltimoCap(Integer.parseInt(s[0]));
    					m.setPagina(Integer.parseInt(s[1]));
    					m.setDiaHorario(s[2]);
    					m.setUrl(s[3]);
    					
                        //Atualizar no banco
    					msql.updateManga(m);
    					System.out.println("updated");
    				}
    				
    				System.out.println(progress+" - "+progressRate);
    				progress+=progressRate;    					
					setProgress((int)progress);
    			}
    			setProgress(100);
    		}
    		catch(Exception exc){exc.printStackTrace();}
        	
        	            
         
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
        }
    }
 
    public ProgressBarDemo() {
        super(new BorderLayout());
 
        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
    }
 
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of task.\n", task.getProgress()));
        } 
    }
 
 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ProgressBarDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBarDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}