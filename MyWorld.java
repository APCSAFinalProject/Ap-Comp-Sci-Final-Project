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
    private int currentRoom;
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
        currentRoom = 0;
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
            
            for(int i = size - 1; i >= 0; i--)
            {
                for(int x = size - 1; x >= 0; x--)
                {
                    if(x != i && mapcode.get(x).equals(mapcode.get(i)))
                    {
                        mapcode.remove(x);
                    }
                }
            }
        }
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
                if(temp.equals(doors))
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
                if(temp.equals(doors))
                {
                    str += "," + (i * 90);
                }
            }
            for(int i = 0; i < 2; i++)
            {
                boolean[] temp = new boolean[4];
                temp[i] = true;
                temp[i+2] = true;
                if(temp.equals(doors))
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
                if(temp.equals(doors))
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
}
