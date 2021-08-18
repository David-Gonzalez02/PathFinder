
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class PathFinder {

    //FRAME
    JFrame frame;
    //GENERAL VARIABLES
    private int cells = 10;
    private int delay = 75;
    private double propDense = .35;
    private double totDense = (cells*cells)*.35;
    private int startx = -1;
    private int starty = -1;
    private int endx = -1;
    private int endy = -1;
    private int tool = 0;
    private int checks = 0;
    private int length = 0;
    private int algChoice = 0;
    private int WIDTH = 950;
    private final int HEIGHT = 775;
    private final int MSIZE = 700;
    private int CSIZE = MSIZE/cells;
    //UTIL ARRAYS
    private String[] algorithms = {"Dijkstra","A*"};
    private String[] tools = {"Start Point","End Point","Obstacle", "Eraser"};
    //STRING
    private String status = "Preparing";
    //BOOLEANS
    private boolean solving = false;
    //UTIL
    Node[][] map;
    Algorithm Alg = new Algorithm();
    Random r = new Random();
    //SLIDERS
    JSlider sizeSL = new JSlider(1,5,2);
    JSlider delaySL = new JSlider(0,500,delay);
    JSlider obstaclesSL = new JSlider(1,100,50);
    //LABELS
    JLabel algL = new JLabel("Algorithms");
    JLabel toolL = new JLabel("Toolbox");
    JLabel sizeL = new JLabel("Size:");
    JLabel cellsL = new JLabel(cells+"x"+cells);
    JLabel delayL = new JLabel("Delay:");
    JLabel msL = new JLabel(delay+"ms");
    JLabel obstacleL = new JLabel("Dens:");
    JLabel densityL = new JLabel(obstaclesSL.getValue()+"%");
    JLabel statusL = new JLabel("Status: " + status);
    JLabel checkL = new JLabel("Nodes Checked: "+checks);
    JLabel lengthL = new JLabel("Final Path Length: "+length);
    //BUTTONS
    JButton searchB = new JButton("Search");
    JButton resetB = new JButton("Reset");
    JButton genMapB = new JButton("Generate Obstacles");
    JButton clearMapB = new JButton("Clear Map");

    //DROP DOWN
    JComboBox algorithmsBx = new JComboBox(algorithms);
    JComboBox toolBx = new JComboBox(tools);
    //PANELS
    JPanel toolP = new JPanel();
    JPanel statusP = new JPanel();
    //CANVAS
    Map canvas;

    public static void main(String[] args) {	//MAIN METHOD
        new PathFinder();
    }

    public PathFinder() {	//CONSTRUCTOR
        clearMap();
        initialize();
    }

    public void resetMap() {	//RESET MAP
        for(int x = 0; x < cells; x++) {
            for(int y = 0; y < cells; y++) {
                Node current = map[x][y];
                if(current.getType() == 4 || current.getType() == 5)	//CHECK TO SEE IF CURRENT NODE IS EITHER CHECKED OR FINAL PATH
                    map[x][y] = new Node(3,x,y);	//RESET IT TO AN EMPTY NODE
            }
        }
        if(startx > -1 && starty > -1) {	//RESET THE START AND END
            map[startx][starty] = new Node(0,startx,starty);
            map[startx][starty].setHops(0);
        }
        if(endx > -1 && endy > -1)
            map[endx][endy] = new Node(1,endx,endy);
        reset();	//RESET VARIABLES
    }

    public void generateObstacles() {	//GENERATE MAP
        clearMap();	//CREATE CLEAR MAP TO START
        for(int i = 0; i < totDense; i++) {
            Node current;
            do {
                int x = r.nextInt(cells);
                int y = r.nextInt(cells);
                current = map[x][y];
            } while(current.getType()==2);	//CHECK IF NODE IS A WALL
            current.setType(2);	//SET NODE TO BE A WALL
        }
    }

    public void clearMap() {	//CLEAR MAP
        endx = -1;	//RESET
        endy = -1;
        startx = -1;
        starty = -1;
        map = new Node[cells][cells];	//CREATE NEW MAP OF NODES
        for(int x = 0; x < cells; x++) {
            for(int y = 0; y < cells; y++) {
                map[x][y] = new Node(3,x,y);	//SET ALL NODES TO EMPTY
            }
        }
        reset();	//RESET SOME VARIABLES
    }



    private void initialize() {	//INITIALIZE THE GUI ELEMENTS
        frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WIDTH,HEIGHT);
        frame.setTitle("Path Finder");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        int space = 25;
        int buff = 45;

        toolP.setLayout(null);
        toolP.setBounds(10,10,210,600);

        searchB.setBounds(25,space, 150, 25);
        toolP.add(searchB);
        space+=buff;

        resetB.setBounds(25,space,150,25);
        toolP.add(resetB);
        space+=buff;

        genMapB.setBounds(25,space, 150, 25);
        toolP.add(genMapB);
        space+=buff;

        clearMapB.setBounds(25,space, 150, 25);
        toolP.add(clearMapB);
        space+=40;

        toolL.setBounds(40,space,120,25);
        toolP.add(toolL);
        space+=25;

        toolBx.setBounds(40,space,120,25);
        toolP.add(toolBx);
        space+=buff;

        algL.setBounds(40,space,120,25);
        toolP.add(algL);
        space+=25;

        algorithmsBx.setBounds(40,space, 120, 25);
        toolP.add(algorithmsBx);
        space+=40;

        sizeL.setBounds(15,space,40,25);
        toolP.add(sizeL);
        sizeSL.setMajorTickSpacing(10);
        sizeSL.setBounds(50,space,100,25);
        toolP.add(sizeSL);
        cellsL.setBounds(160,space,40,25);
        toolP.add(cellsL);
        space+=buff;

        obstacleL.setBounds(15,space,100,25);
        toolP.add(obstacleL);
        obstaclesSL.setMajorTickSpacing(5);
        obstaclesSL.setBounds(50,space,100,25);
        toolP.add(obstaclesSL);
        densityL.setBounds(160,space,100,25);
        toolP.add(densityL);
        space+=buff;

        delayL.setBounds(15,space,50,25);
        toolP.add(delayL);
        delaySL.setMajorTickSpacing(5);
        delaySL.setBounds(50,space,100,25);
        toolP.add(delaySL);
        msL.setBounds(160,space,40,25);
        toolP.add(msL);
        space+=buff;

        checkL.setBounds(15,space,150,25);
        toolP.add(checkL);
        space+=buff;

        lengthL.setBounds(15,space,150,25);
        toolP.add(lengthL);
        space+=buff;

        frame.getContentPane().add(toolP);

        statusP.setLayout(null);
        statusP.setBounds(250,0,525,30);

        statusL.setBounds(250,0,150,25);
        statusP.add(statusL);
        frame.getContentPane().add(statusP);

        canvas = new Map();
        canvas.setBounds(230, 35, MSIZE+1, MSIZE+1);
        frame.getContentPane().add(canvas);

        searchB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                if((startx > -1 && starty > -1) && (endx > -1 && endy > -1))
                    solving = true;
                status = "Searching!";
            }
        });
        resetB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                Update();
            }
        });
        genMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateObstacles();
                Update();
            }
        });
        clearMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMap();
                Update();
            }
        });
        algorithmsBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                algChoice = algorithmsBx.getSelectedIndex();
                Update();
            }
        });
        toolBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tool = toolBx.getSelectedIndex();
            }
        });
        sizeSL.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cells = sizeSL.getValue()*10;
                clearMap();
                reset();
                Update();
            }
        });
        delaySL.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delay = delaySL.getValue();
                Update();
            }
        });
        obstaclesSL.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                propDense = (double)obstaclesSL.getValue()/100;
                Update();
            }
        });

        startSearch();
    }

    public void startSearch() {	//START STATE
        if(solving) {
            switch(algChoice) {
                case 0:
                    Alg.Dijkstra();
                    break;
                case 1:
                    Alg.AStar();
                    break;
            }
        }
        pause();
    }

    public void pause() {	//PAUSE STATE
        int i = 0;
        while(!solving) {
            i++;
            if(i > 500)
                i = 0;
            try {
                Thread.sleep(1);
            } catch (Exception e) {}
        }
        startSearch();	//START STATE
    }

    public void Update() {	//UPDATE ELEMENTS OF THE GUI
        totDense = (cells*cells)*propDense;
        CSIZE = MSIZE/cells;
        canvas.repaint();
        cellsL.setText(cells+"x"+cells);
        msL.setText(delay+"ms");
        lengthL.setText("Final Path Length: "+length);
        densityL.setText(obstaclesSL.getValue()+"%");
        statusL.setText("Status: "+status);
        checkL.setText("Nodes Checked: "+checks);
    }

    public void reset() {	//RESET METHOD
        solving = false;
        status = "Preparing";
        length = 0;
        checks = 0;
    }

    public void delay() {	//DELAY METHOD
        try {
            Thread.sleep(delay);
        } catch (Exception e) {}
    }

    class Map extends JPanel implements MouseListener, MouseMotionListener{	//MAP CLASS

        public Map() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {	//REPAINT
            super.paintComponent(g);
            for(int x = 0; x < cells; x++) {	//PAINT EACH NODE IN THE GRID
                for(int y = 0; y < cells; y++) {
                    switch(map[x][y].getType()) {
                        case 0:
                            g.setColor(Color.GREEN);
                            break;
                        case 1:
                            g.setColor(Color.RED);
                            break;
                        case 2:
                            g.setColor(Color.BLACK);
                            break;
                        case 3:
                            g.setColor(Color.WHITE);
                            break;
                        case 4:
                            g.setColor(Color.YELLOW);
                            break;
                        case 5:
                            g.setColor(Color.CYAN);
                            break;
                        case 6:
                            g.setColor(Color.PINK);
                            break;
                    }
                    g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);

                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                int x = e.getX()/CSIZE;
                int y = e.getY()/CSIZE;
                Node current = map[x][y];
                if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
                    current.setType(tool);
                Update();
            } catch (Exception z) {}
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            resetMap();	//RESET THE MAP WHENEVER CLICKED
            try {
                int x = e.getX()/CSIZE;	//GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
                int y = e.getY()/CSIZE;
                Node current = map[x][y];
                switch(tool ) {
                    case 0: {	//START NODE
                        if(current.getType()!=2) {	//IF NOT WALL
                            if(startx > -1 && starty > -1) {	//IF START EXISTS SET IT TO EMPTY
                                map[startx][starty].setType(3);
                                map[startx][starty].setHops(-1);
                            }
                            current.setHops(0);
                            startx = x;	//SET THE START X AND Y
                            starty = y;
                            current.setType(0);	//SET THE NODE CLICKED TO BE START
                        }
                        break;
                    }
                    case 1: {//END NODE
                        if(current.getType()!=2) {	//IF NOT WALL
                            if(endx > -1 && endy > -1)	//IF END EXISTS SET IT TO EMPTY
                                map[endx][endy].setType(3);
                            endx = x;	//SET THE END X AND Y
                            endy = y;
                            current.setType(1);	//SET THE NODE CLICKED TO BE END NODE
                        }
                        break;
                    }
                    default:
                        if(current.getType() != 0 && current.getType() != 1)
                            current.setType(tool);
                        break;
                }
                Update();
            } catch (Exception z) {}	//EXCEPTION HANDLER
        }

        @Override
        public void mouseReleased(MouseEvent e) {}
    }

    class Algorithm {	//ALGORITHM CLASS

        //DIJKSTRA WORKS BY PROPAGATING OUTWARDS UNTIL IT FINDS THE END AND THEN WORKING ITS WAY BACK TO GET THE PATH
        //IT USES A PRIORITY QUE TO KEEP TRACK OF NODES THAT IT NEEDS TO EXPLORE
        //EACH NODE IN THE PRIORITY QUE IS EXPLORED AND ALL OF ITS NEIGHBORS ARE ADDED TO THE QUE
        //ONCE A NODE IS EXPLORED IT IS DELETED FROM THE QUE
        //AN ARRAYLIST IS USED TO REPRESENT THE PRIORITY QUE
        //A SEPERATE ARRAYLIST IS RETURNED FROM A METHOD THAT EXPLORES A NODES NEIGHBORS
        //THIS ARRAYLIST CONTAINS ALL THE NODES THAT WERE EXPLORED, IT IS THEN ADDED TO THE QUE
        //A HOPS VARIABLE IN EACH NODE REPRESENTS THE NUMBER OF NODES TRAVELED FROM THE START
        public void Dijkstra() {
            ArrayList<Node> priority = new ArrayList<>();	//CREATE A PRIORITY QUE
            priority.add(map[startx][starty]);	//ADD THE START TO THE QUE
            while(solving) {
                if(priority.size() <= 0) {	//IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
                    solving = false;
                    status = "No Path Found";
                    Update();
                    break;
                }
                int hops = priority.get(0).getHops()+1;	//INCREMENT THE HOPS VARIABLE
                ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
                if(explored.size() > 0) {
                    priority.remove(0);	//REMOVE THE NODE FROM THE QUE
                    priority.addAll(explored);	//ADD ALL THE NEW NODES TO THE QUE
                    Update();
                    delay();
                } else {	//IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
                    priority.remove(0);
                }
            }
        }

        //A STAR WORKS ESSENTIALLY THE SAME AS DIJKSTRA CREATING A PRIORITY QUE AND PROPAGATING OUTWARDS UNTIL IT FINDS THE END
        //HOWEVER ASTAR BUILDS IN A HEURISTIC OF DISTANCE FROM ANY NODE TO THE END
        //THIS MEANS THAT NODES THAT ARE CLOSER TO THE END WILL BE EXPLORED FIRST
        //THIS HEURISTIC IS BUILT IN BY SORTING THE QUE ACCORDING TO HOPS PLUS DISTANCE UNTIL THE END
        public void AStar() {
            ArrayList<Node> priority = new ArrayList<>();
            priority.add(map[startx][starty]);
            while(solving) {
                if(priority.size() <= 0) {
                    solving = false;
                    status = "No Path Found";
                    Update();
                    break;
                }
                int hops = priority.get(0).getHops()+1;
                ArrayList<Node> explored = exploreNeighbors(priority.get(0),hops);
                if(explored.size() > 0) {
                    priority.remove(0);
                    priority.addAll(explored);
                    Update();
                    delay();
                } else {
                    priority.remove(0);
                }
                aStarSort(priority);	//SORT THE PRIORITY QUE
            }
        }

        public ArrayList<Node> aStarSort(ArrayList<Node> sort) {	//SORT PRIORITY QUE
            int c = 0;
            while(c < sort.size()) {
                int sm = c;
                for(int i = c+1; i < sort.size(); i++) {
                    if(sort.get(i).getEuclidDist()+sort.get(i).getHops() < sort.get(sm).getEuclidDist()+sort.get(sm).getHops())
                        sm = i;
                }
                if(c != sm) {
                    Node temp = sort.get(c);
                    sort.set(c, sort.get(sm));
                    sort.set(sm, temp);
                }
                c++;
            }
            return sort;
        }

        public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//EXPLORE NEIGHBORS
            ArrayList<Node> explored = new ArrayList<>();	//LIST OF NODES THAT HAVE BEEN EXPLORED
            for(int a = -1; a <= 1; a++) {
                for(int b = -1; b <= 1; b++) {
                    int xbound = current.getX()+a;
                    int ybound = current.getY()+b;
                    if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
                        Node neighbor = map[xbound][ybound];
                        if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
                            explore(neighbor, current.getX(), current.getY(), hops);	//EXPLORE THE NODE
                            explored.add(neighbor);	//ADD THE NODE TO THE LIST
                        }
                    }
                }
            }
            return explored;
        }

        public void explore(Node current, int lastx, int lasty, int hops) {	//EXPLORE A NODE
            if(current.getType()!=0 && current.getType() != 1)	//CHECK THAT THE NODE IS NOT THE START OR END
                current.setType(4);	//SET IT TO EXPLORED
            current.setLastNode(lastx, lasty);	//KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
            current.setHops(hops);	//SET THE HOPS FROM THE START
            checks++;
            if(current.getType() == 1) {	//IF THE NODE IS THE END THEN BACKTRACK TO GET THE PATH
                backtrack(current.getLastX(), current.getLastY(),hops);
            }
        }

        public void backtrack(int lx, int ly, int hops) {	//BACKTRACK
            length = hops;
            while(hops > 1) {	//BACKTRACK FROM THE END OF THE PATH TO THE START
                Node current = map[lx][ly];
                current.setType(5);
                lx = current.getLastX();
                ly = current.getLastY();
                hops--;
            }
            solving = false;
            status = "Completed!";
        }
    }

    class Node {

        // 0 = start, 1 = END, 2 = wall, 3 = empty, 4 = checked, 5 = finalpath
        private int cellType;
        private int hops;
        private int x;
        private int y;
        private int lastX;
        private int lastY;
        private double dToEnd = 0;

        public Node(int type, int x, int y) {	//CONSTRUCTOR
            cellType = type;
            this.x = x;
            this.y = y;
            hops = -1;
        }

        public double getEuclidDist() {		//CALCULATES THE EUCLIDIAN DISTANCE TO THE END NODE
            int xdif = Math.abs(x-endx);
            int ydif = Math.abs(y-endy);
            dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
            return dToEnd;
        }

        public int getX() {return x;}		//GET METHODS
        public int getY() {return y;}
        public int getLastX() {return lastX;}
        public int getLastY() {return lastY;}
        public int getType() {return cellType;}
        public int getHops() {return hops;}

        public void setType(int type) {cellType = type;}		//SET METHODS
        public void setLastNode(int x, int y) {lastX = x; lastY = y;}
        public void setHops(int hops) {this.hops = hops;}
    }
}
