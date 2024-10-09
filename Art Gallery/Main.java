

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import krs.Visualiser;

import java.util.Scanner;


public class Main extends JFrame {
    Visualiser inface;
    JSpinner spinner;
    JLabel statusLabel;

    static int noOfVertex = 0;
    static int actType = 0;
    static int n = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the no of vertices: ");
        int n = scanner.nextInt();
        noOfVertex = n;
        scanner.close();

        Main app = new Main();

        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        app.setPreferredSize(new Dimension(800, 600));  // Set preferred size for the JFrame
        app.pack();
        app.setVisible(true);
    }

    public Main() {
        super("Trapezoidation Problem");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
    
        inface = new Visualiser();
        // Removed the border setting for inface
        // inface.setBorder(new BevelBorder(BevelBorder.LOWERED)); // This line is now commented out or removed.
        
        contentPane.add(inface, BorderLayout.CENTER);
    
        Action next = new NextAction(noOfVertex);
        Action zoomin = new ZoomIn();
        Action zoomout = new ZoomOut();
        Action left = new Left();
        Action right = new Right();
        Action up = new Up();
        Action down = new Down();
        
        // Create the toolbar and add actions
        JToolBar toolbar = new JToolBar();
        toolbar.add(next);
        toolbar.add(zoomin);
        toolbar.add(zoomout);
        toolbar.add(left);
        toolbar.add(right);
        toolbar.add(up);
        toolbar.add(down);
        
        // Add the toolbar to the NORTH of the content pane
        contentPane.add(toolbar, BorderLayout.NORTH);
        
        // Create the status label and set its initial text
        statusLabel = new JLabel("Action: Initialize");
        contentPane.add(statusLabel, BorderLayout.SOUTH); // Add status label to SOUTH
    
        pack();
        
    }


    class NextAction extends AbstractAction {
        

        public NextAction(int noOfVertex) {
            super("Next");
            n = noOfVertex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String currentAction = "";

            switch (actType) {
                case 0:
                    inface.clear();
                    inface.initPoints(n);
                    currentAction = "Initializing Points";
                    break;
                case 1:
                    inface.generatePolygon();
                    currentAction = "Generating Polygon";
                    break;
                case 2:
                    inface.trapezoidalization();
                    currentAction = "Performing Trapezoidalization";
                    break;
                case 3:
                    inface.monotonePartition();
                    currentAction = "Performing Monotone Partition";
                    break;
                case 4:
                    inface.triangulatePartitions();
                    currentAction = "Triangulating Partitions";
                    break;
                case 5:
                    inface.dualGraph();
                    currentAction = "Creating Dual Graph";
                    break;
                case 6:
                    inface.threecolor();
                    currentAction = "3-Coloring the Graph";
                    break;
                case 7:
                    System.exit(0);
                    break;
            }

            statusLabel.setText("Action: " + currentAction);
            actType++;
        }
    }

    class ZoomIn  extends AbstractAction {
        public ZoomIn() {
            super("ZoomIn(+)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.zoomIn();
        }
    }
    class ZoomOut  extends AbstractAction {
        public ZoomOut() {
            super("ZoomOut(-)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.zoomOut();
        }
    }
    class Left  extends AbstractAction {
        public Left() {
            super("Left");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.lefT();
        }
    }
    class Right  extends AbstractAction {
        public Right() {
            super("Right");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.righT();
        }
    }
    class Up  extends AbstractAction {
        public Up() {
            super("Up");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.uP();
        }
    }
    class Down  extends AbstractAction {
        public Down() {
            super("Down");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            inface.dowN();
        }
    }
    
    

}
