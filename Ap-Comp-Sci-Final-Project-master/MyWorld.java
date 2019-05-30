import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{
    private ArrayList<String> mapcode;
    private boolean[] cleared;
    private int currentRoom;
    private int level;
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 600, 1);
        mapcode = new ArrayList<String>();
        mapcode.add("0,0");
        generateCells();
        addDoors();
        cleared = new boolean[mapcode.size()];
        updateImage(findConfig(findDoors(mapcode.get(0))));
        currentRoom = 0;
        level = 1;
        addObject(new Player(), 300, 300);
        //setBackground(new GreenfootImage("images/map_01.png"));
        spawnEnemies();
    }
    
     public void act()
    {
        if(getObjects(Player.class).size() != 0)
        {
            Player player = getObjects(Player.class).get(0);
            String healthDisplay = "Health: " + player.getHealth();
            String balanceDisplay = "Gold: " + player.getBalance();
        
            int healthDisplayX = 5 * healthDisplay.length();
            int balanceDisplayX = 5 * balanceDisplay.length();;
            
            showText(healthDisplay, 55, 585);
            showText(balanceDisplay, 55, 565);
        }
    }
    
    /**
     * Creates a map for the game. The initial random number creates an aproximate number of rooms
     * (though due to the nature of the creation, this number cannot be exact). The algorithm starts
     * with one room, which will always be at coordinate 0,0. It then goes through and adds an
     * adjacent room to the ArrayList of rooms. Then, it does the entire process again, adding an adjacent
     * room to every room in the list until the list is longer than numRooms elements. After each iteration,
     * a double for loop removes any duplicate rooms from the ArrayList, meaning none will overlap. 
     * The result is that mapcode is filled with a network of room coordinates that all connect.
     */
    public void generateCells()
    {
        int numRooms = 10 + (int) (Math.random() * 8);
        
        while(mapcode.size() <= numRooms)
        {
            int size = mapcode.size();
            
            for(int i = 0; i < size; i++)
            {
                String str = mapcode.get(i);
                Scanner s = new Scanner(str).useDelimiter("\\s*,\\s*");
                int x = s.nextInt();
                int y = s.nextInt();
                s.close();
                int rand = (int) (Math.random() * 4);
                if(rand == 0)
                {
                    mapcode.add((x + 1) + "," + y);
                }
                else if(rand == 1)
                {
                    mapcode.add((x - 1) + "," + y);
                }
                else if(rand == 2)
                {
                    mapcode.add(x + "," + (y + 1));
                }
                else
                {
                    mapcode.add(x + "," + (y - 1));
                }
            }
        }
        
        /*A Set collection does not allow duplicates. By putting all values of
         * mapcode into a set and then adding them back into mapcode, all
         * duplicates are removed.
         */
        
        Set<String> set = new HashSet<>(mapcode);
        mapcode.clear();
        mapcode.addAll(set);
    }
    
    /**
     * The next step in the process of generating the map. This takes all of the rooms, gets the coordinates
     * in each, and figures out if a room has any adjacent to it. When it does find a room adjacent, it determines
     * what side it is adjacent on, and adds a code onto the string for a door. A door code might look like:
     * "N5" meaning that there is a door on the north side of the room that connects to the room at index 5. 
     * A completed code could look like "1,2,E5,S2,W3" the first two numbers, 1 and 2, are the coordinates of the
     * room itself. After that, each combination of letter and number is where a door is and what room it leads to.
     * The resultant strings will be used in two ways. First, to determine what image to use for the background
     * in this room. Second, to determine what room to load when the player enters a doorway.
     */
    public void addDoors()
    {
        for(int i = 0; i < mapcode.size(); i++)
        {
            Scanner s = new Scanner(mapcode.get(i)).useDelimiter("\\s*,\\s*");
            int firstX = s.nextInt();
            int firstY = s.nextInt();
            s.close();
            for(int x = 0; x < mapcode.size(); x++)
            {
                Scanner a = new Scanner(mapcode.get(x)).useDelimiter("\\s*,\\s*");
                int nextX = a.nextInt();
                int nextY = a.nextInt();
                a.close();
                if(firstX - nextX == 0 && firstY - nextY == -1)
                {
                    mapcode.set(i, mapcode.get(i) + ",N" + x);
                }
                else if(firstX - nextX == -1 && firstY - nextY == 0)
                {
                    mapcode.set(i, mapcode.get(i) + ",E" + x);
                }
                else if(firstX - nextX == 0 && firstY - nextY == 1)
                {
                    mapcode.set(i, mapcode.get(i) + ",S" + x);
                }
                else if(firstX - nextX == 1 && firstY - nextY == 0)
                {
                    mapcode.set(i, mapcode.get(i) + ",W" + x);
                }
            }
        }
    }
    
    /**
     * This method is mostly a setup for findConfig. It translates a code such as: "1,1,N3,W5,S1" into a boolean
     * array that tells only of there is a door somewhere. With the example given, the boolean array would be
     * [true, false, true, true]. Index 0 is North, index 1 is east, and so on. If there is a door in the north,
     * index 0 is set to true. This is used in findConfig to determine what image to use for the background in
     * this room. 
     */
    public boolean[] findDoors(String roomcode)
    {
        boolean[] doors = {false, false, false, false};
        
        if(roomcode.contains("N"))
        {
            doors[0] = true;
        }
        if(roomcode.contains("E"))
        {
            doors[1] = true;
        }
        if(roomcode.contains("S"))
        {
            doors[2] = true;
        }
        if(roomcode.contains("W"))
        {
            doors[3] = true;
        }
        
        return doors;
    }
    
    /**
     * Creates a string with the information on what kind of room is to be displayed and what rotation it should
     * be put into. The method first determines how many rooms there are, then tests every possible layout for
     * that room against what the room actually is, and then adds 90 multiplied by how many 90 degree rotations
     * were made to find the correct layout. 
     * @returns a string with two pieces of information separated by a comma. The first piece of information is
     * what type of room to use. "1" means a room with 1 door. "2" means a room with two adjacent doors. "2S"
     * means a room with two doors opposite from eachother, and so on. The second piece tells how much the room
     * is rotated compared to the basic model. 0 means keep the standard image, 90 means rotate 90 degrees, etc. 
     */
    public String findConfig(boolean[] doors)
    {
        String str = "";
        int numDoors = 0;
        for(boolean a : doors)
        {
            if(a)
            {
                numDoors++;
            }
        }
        if(numDoors == 1)
        {
            str += "1";
            for(int i = 0; i < 4; i++)
            {
                boolean[] temp = new boolean[4];
                temp[i] = true;
                /*While this looks strange, it is the only way to compare two
                 * boolean arrays. arr1.equals(arr2), arr1.compareTo(arr2), and
                 * arr1 == arr2 all fail.
                 */
                if(Arrays.equals(temp, doors))
                {
                    str += "," + (i * 90);
                }
            }
        }
        else if(numDoors == 2)
        {
            str += "2";
            for(int i = 0; i < 4; i++)
            {
                boolean[] temp = new boolean[4];
                temp[i] = true;
                temp[(i+1) % 4] = true;
                if(Arrays.equals(temp, doors))
                {
                    str += "," + ((i+1) * 90);
                }
            }
            for(int i = 0; i < 2; i++)
            {
                boolean[] temp = new boolean[4];
                temp[i] = true;
                temp[i+2] = true;
                if(Arrays.equals(temp, doors))
                {
                    str += "S," + (i * 90);
                }
            }
        }
        else if(numDoors == 3)
        {
            str += "3";
            for(int i = 0; i < 4; i++)
            {
                boolean[] temp = new boolean[4];
                Arrays.fill(temp, Boolean.TRUE);
                temp[i] = false;
                if(Arrays.equals(temp, doors))
                {
                    str += "," + (i * 90);
                }
            }
        }
        else
        {
            str += "4";
        }
        
        return str;
    }
    
    public void updateImage(String roomCode)
    {
        int type = Integer.parseInt(roomCode.substring(0,1));
        GreenfootImage img;
        int rotation = 0;
        if(roomCode.length() > 1 && !roomCode.substring(0,2).equals("2S"))
        {
            img = new GreenfootImage(type + "Door.png");
            rotation = Integer.parseInt(roomCode.substring(2));
        }
        else if(roomCode.length() == 1)
        {
            img = new GreenfootImage("4Door.png");
        }
        else
        {
            img = new GreenfootImage("2DoorAcr.png");
            rotation = Integer.parseInt(roomCode.substring(3));
        }
        img.rotate(rotation);
        setBackground(img);
    }
    
    public void nextRoom(int whichDoor)
    {
        String roomCode = mapcode.get(currentRoom);
        if(whichDoor == 0)
        {
            /*Creates a new string, removing everything before the character N.
             * This means that the very first integer in the string is the one
             * directly after the "N" character. This sets us up to then find
             * the first integer, and make that the room we must go to.
             */
            String str = roomCode.substring(roomCode.indexOf("N"));
            Scanner s = new Scanner(str);
            /* A delimiter for a scanner means something that separates tokens
             * the scanner must look for. if a string is "a/d/f/g" and a scanner
             * uses "/" as a delimiter, a, d, f, and g become tokens that the
             * scanner can find. By using the delimiter \D+, everything that is
             * not an integer becomes a delimiter. Therefore, the only tokens the
             * scanner can find will be integers.
             */
            s.useDelimiter("\\D+");
            /* Now, when we call nextInt(), the first int in the string is
             * returned, which we know to be the one directly after "N". We set
             * the current room to wherever the north door leads to.
             */
            currentRoom = s.nextInt();
            updateImage(findConfig(findDoors(mapcode.get(currentRoom))));
            if(mapcode.get(currentRoom).contains("B"))
            {
                addObject(new BossDoor(), 450, 150);
            }
            else if(getObjects(BossDoor.class).size() > 0)
            {
                for(BossDoor b : getObjects(BossDoor.class))
                {
                    removeObject(b);
                }
            }
        }
        else if(whichDoor == 1)
        {
            String str = roomCode.substring(roomCode.indexOf("E"));
            Scanner s = new Scanner(str);
            s.useDelimiter("\\D+");
            currentRoom = s.nextInt();
            updateImage(findConfig(findDoors(mapcode.get(currentRoom))));
            if(mapcode.get(currentRoom).contains("B"))
            {
                addObject(new BossDoor(), 450, 150);
            }
            else if(getObjects(BossDoor.class).size() > 0)
            {
                for(BossDoor b : getObjects(BossDoor.class))
                {
                    removeObject(b);
                }
            }
        }
        else if(whichDoor == 2)
        {
            String str = roomCode.substring(roomCode.indexOf("S"));
            Scanner s = new Scanner(str);
            s.useDelimiter("\\D+");
            currentRoom = s.nextInt();
            updateImage(findConfig(findDoors(mapcode.get(currentRoom))));
            if(mapcode.get(currentRoom).contains("B"))
            {
                addObject(new BossDoor(), 450, 150);
            }
            else if(getObjects(BossDoor.class).size() > 0)
            {
                for(BossDoor b : getObjects(BossDoor.class))
                {
                    removeObject(b);
                }
            }
        }
        else
        {
            String str = roomCode.substring(roomCode.indexOf("W"));
            Scanner s = new Scanner(str);
            s.useDelimiter("\\D+");
            currentRoom = s.nextInt();
            updateImage(findConfig(findDoors(mapcode.get(currentRoom))));
            if(mapcode.get(currentRoom).contains("B"))
            {
                addObject(new BossDoor(), 450, 150);
            }
            else if(getObjects(BossDoor.class).size() > 0)
            {
                for(BossDoor b : getObjects(BossDoor.class))
                {
                    removeObject(b);
                }
            }
        }
        if(!cleared[currentRoom])
        {
            spawnEnemies();
        }
    }
    
    public int getRoom()
    {
        return currentRoom;
    }
    
    public String getRoomCode()
    {
        return mapcode.get(currentRoom);
    }
    
    public ArrayList<String> getRooms()
    {
        return mapcode;
    }
    
    public void spawnEnemies()
    {
        if(!cleared[currentRoom])
        {
            int numEnemies = (int) (Math.random() * 4) + 1;
            Player player = getObjects(Player.class).get(0);
            int playerX = player.getX();
            int playerY = player.getY();
            int i = 0;
            while(i < numEnemies)
            {
                /*
                 * Creates a random stat value. The lowest this value can be
                 * is whatever the level currently is. The highest this value
                 * can be is two times the current level. As time goes on, enemies
                 * get gradually harder and more spread apart in their power.
                 */
                int stats = level + (int) (Math.random() * level);
                Enemy enemy = new Enemy(stats);
                int x = -1;
                int y = -1;
                while(x == -1)
                {
                    int rand = (int) (Math.random() * 600);
                    if(rand <= playerX - 100 || rand >= playerX + 100)
                    {
                        x = rand;
                    }
                }
                while(y == -1)
                {
                    int rand = (int) (Math.random() * 600);
                    if(playerY - 100 >= rand || rand >= playerY + 100)
                    {
                        y = rand;
                    }
                }
                addObject(enemy, x, y);
                i++;
            }
        }
    }
    
    public void enterBossRoom()
    {
        if(getObjects(Enemy.class).size() == 0)
        {
            Player a = getObjects(Player.class).get(0);
            a.setLocation(300,500);
            removeObject(getObjects(BossDoor.class).get(0));
        }
    }
    
    public void addBossDoor()
    {
        int rand = (int) (Math.random() * mapcode.size());
        mapcode.set(rand, mapcode.get(rand) + "B");
    }
    
    public void clearLevel()
    {
        cleared[currentRoom] = true;
    }
    
    public boolean getCleared()
    {
        return cleared[currentRoom];
    }
    
    public String getBossRoom()
    {
        String str = "";
        for(String a : mapcode)
        {
            if(a.contains("B"))
            {
                str = a;
            }
        }
        return str;
    }
    
    public void testShopRoom()
    {
        GreenfootImage img = new GreenfootImage("shop.png");
        Player player = getObjects(Player.class).get(0);
        
        setBackground(img);
        player.setLocation(300, 300);
        player.enterShop();
    }
    
    public void displayStats()
    {
        Player player = getObjects(Player.class).get(0);
        
        if(player.isInShop())
        {
            showText("Current Health: " + player.getHealth(), 100, 120);
            showText("Current Damage: " + player.getHealth(), 100, 120);
            showText("Current Speed: " + player.getHealth(), 100, 120);
        }
    }
}