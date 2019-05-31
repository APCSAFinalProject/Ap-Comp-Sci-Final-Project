import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Write a description of class MyWorld here.
 * 
 * @author Aaron Saporito, Adam Rutledge, Aiden G, Luke Arsenalt, and Anthony
 * @version 1.1
 */
public class MyWorld extends World
{
    private ArrayList<String> mapcode;

    private boolean[] cleared;
    private int currentRoom;
    private int level;

    private GreenfootSound backgroundMusic;
    /**
     * Constructor for objects of class MyWorld. Generates the map, starts the music, and adds initial enemies.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 600, 1);
        level = 1;
        addObject(new Player(), 300, 300);
        mapcode = new ArrayList<String>();
        mapcode.add("0,0");
        generateCells();
        addDoors();
        addBossDoor();
        cleared = new boolean[mapcode.size()];
        updateImage(findConfig(findDoors(mapcode.get(0))));
        currentRoom = 0;
        spawnEnemies();

        backgroundMusic = new GreenfootSound("Bgm.mp3");
        backgroundMusic.setVolume(30);
        backgroundMusic.playLoop();
    }
    
    /**
     * While an act method is not typical in a world class, it is useful for this game because it allows us to
     * constantly update the stats we are displaying. Displays the current level, how much money the player has
     * and how much health the player has.
     */
     public void act()
    {
        if(getObjects(Player.class).size() != 0)
        {
            Player player = getObjects(Player.class).get(0);
            
            displayStats();
            
            String healthDisplay = "Health: " + Math.max(player.getCurrentHealth(), 0);
            String balanceDisplay = "Gold: " + Math.abs(player.getBalance());
            String levelDisplay = "Level: " + level;
            
            showText(healthDisplay, 55, 585);
            showText(balanceDisplay, 55, 565);
            showText(levelDisplay, 55, 545);
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
     * The next step after generateCells in the process of generating the map. This takes all of the rooms, gets the coordinates
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
     * were made to find the correct layout. This is used in the method updateImage. In the updateImage, the
     * configuration is called a "simpleRoomCode" because the information about a room is simplified into just
     * number of doors and a rotation.
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
    
    /**
     * Updates the background based on what simpleRoomCode is entered in. A simpleRoomCode consists of two values separated
     * by a comma. The first value is how many doors there are. 1 Means 1 door, 2 means 2 doors, etc. It can
     * also be 2S (as in 2 Special) which means two doors that are across from eachother. The second value is
     * how much you need to rotate the basic image for that many doors in order for it to match the layout of the
     * doors. For example, the image for two doors has a door to the north and to the west. If the room you are
     * entering contains two doors to the north and to the east, the background image must be rotated 90 degrees
     * so that instead of north and west, it's doors are to the north and to the east. In this scenario, the second
     * value would be 90. 
     * @param simpleRoomCode A code consisting of a type and rotation value representing a room. It is called simpleRoomCode
     * because a normal room code stores coordinates and every single door value, and whether the room contains a boss door.
     * A simpleRoomCode removes coordinates and all door values, and just holds information about what the room looks like.
     * A simpleRoomCode is only useful for this method.
     */
    public void updateImage(String simpleRoomCode)
    {
        int type = Integer.parseInt(simpleRoomCode.substring(0,1));
        GreenfootImage img;
        int rotation = 0;
        if(simpleRoomCode.length() > 1 && !simpleRoomCode.substring(0,2).equals("2S"))
        {
            img = new GreenfootImage(type + "Door.png");
            rotation = Integer.parseInt(simpleRoomCode.substring(2));
        }
        else if(simpleRoomCode.length() == 1)
        {
            img = new GreenfootImage("4Door.png");
        }
        else
        {
            img = new GreenfootImage("2DoorAcr.png");
            rotation = Integer.parseInt(simpleRoomCode.substring(3));
        }
        img.rotate(rotation);
        setBackground(img);
    }
    
    /**
     * Used for advancing the player into the next room. Takes in whichever door the player has stepped through
     * and based on that determines which room they are going to. If whichDoor is 0, that means the player moves
     * north, so a new substring is created starting with the character "N," which means the north door. It then
     * finds the number that follows the character N. This number is the index in mapcode that contains the
     * room code for whatever room is to the north. It then sets the current room to that index, and updates
     * the background based on whatever the new room is. This class is also responsible for spawning enemies.
     * It checks whether the room that the player just entered is cleared or not, and if it isn't, it spawns in
     * new enemies.
     * @param whichDoor is an integer representing which door the player stepped through. 0 = north, 1 = east,
     * 2 = south, 3 = west.
     */
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
    
    /**
     * @returns the index in mapcode that the current room resides at.
     */
    public int getRoom()
    {
        return currentRoom;
    }
    
    /**
     * Helpful for seeing the coordinate of the current room.
     * @returns the roomCode of the current room, containing coordinates and all doors.
     */
    public String getRoomCode()
    {
        return mapcode.get(currentRoom);
    }
    
    /**
     * Helpful for seeing the entire map, shows the coordinates and doors for every room.
     * @returns all items in the mapcode ArrayList.
     */
    public ArrayList<String> getRooms()
    {
        return mapcode;
    }
    
    /**
     * Spawns a random number of enemies between 1 and 4, does not let them spawn near the player.
     * Does this by generating a random X and Y value, checking if they are near the player, and if so
     * regenerating them.
     */
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
    
    /**
     * Called by the boss door when the player steps on it. First tests to see if there are any enemies,
     * and if so does not let the player go through the door. If there are no enemies, it moves the player
     * to the boss room, removes the door, and spawns one of three bosses randomly. 
     */
    public void enterBossRoom()
    {
        if(getObjects(Enemy.class).size() == 0)
        {
            setBackground("images/BossRoom.png");
            Player a = getObjects(Player.class).get(0);
            a.setLocation(300,500);
            removeObject(getObjects(BossDoor.class).get(0));
            int whichBoss = (int) (Math.random() * 3);
            if(whichBoss == 0)
            {
                Boss0 boss = new Boss0(level * 5);
                addObject(boss, 300, 100);
            }
            else if(whichBoss == 1)
            {
                Boss1 boss = new Boss1(level * 5);
                addObject(boss, 300, 100);
            }
            else
            {
                Boss2 boss = new Boss2(level * 5);
                addObject(boss, 300, 100);
            }
        }
    }
    
    /**
     * addBossDoor selects a random room in the map, then adds "B" to it's string representation
     * meaning that this room contains the boss door. When selecting a random number, it adds 1
     * automatically so that it is never 0. If the number is 0, that means the boss door is in the same
     * room as the starting location, which is not desired. To make sure the random number now never goes
     * out of bounds, 1 is subtracted from mapcode.size(), making the range from the second item in
     * mapcode to the last item in mapcode.
     */
    public void addBossDoor()
    {
        int rand = 1 + (int) (Math.random() * (mapcode.size()-1));
        mapcode.set(rand, mapcode.get(rand) + "B");
    }
    
    /**
     * Sets the value at currentRoom in cleared to true. This is called when the player kills all enemies
     * in a room. This makes it so when you return to a room after clearing it, enemies do not spawn again.
     */
    public void clearLevel()
    {
        cleared[currentRoom] = true;
    }
    
    /**
     * Tests to see if the current room has been cleared already, if true enemies will not spawn again.
     * @returns whether the current room is cleared.
     */
    public boolean getCleared()
    {
        return cleared[currentRoom];
    }
    
    /**
     * Not used by the program but can be called outside to see where the bossroom is, useful for
     * troubleshooting.
     * @returns the room code for whatever room contains the boss door.
     */
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
    
    /**
     * Called after a boss is defeated. Takes the player to a shop where they can purchase stats.
     */
    public void enterShop()
    {
        GreenfootImage img = new GreenfootImage("Shop.png");
        Player player = getObjects(Player.class).get(0);
        
        setBackground(img);
        player.setLocation(300, 300);
        player.enterShop();
        player.heal();
    }
    
    /**
     * Displays what the player's current stats are when in the shop.
     */
    public void displayStats()
    {
        Player player = getObjects(Player.class).get(0);
        
        if(player.isInShop())
        {
            showText("Current Health: " + player.getMaxHealth(), 100, 120);
            showText("Current Speed: " + player.getSpeed(), 300, 120);
            showText("Current Damage: " + player.getDamage(), 500, 120);
        }
        else
        {
            showText("", 100, 120);
            showText("", 300, 120);
            showText("", 500, 120);
        }
    }
    
    /**
     * Called after exiting the shop. Brings the player to the next level, where difficulty is increased.
     * Generates a new random map.
     */
    public void nextLevel()
    {
        level++;
        mapcode = new ArrayList<String>();
        mapcode.add("0,0");
        generateCells();
        addDoors();
        addBossDoor();
        cleared = new boolean[mapcode.size()];
        updateImage(findConfig(findDoors(mapcode.get(0))));
        currentRoom = 0;
        spawnEnemies();
        backgroundMusic.stop();
        backgroundMusic.play();
    }
    
    /**
     * Called when the player dies. Prints scores/level on the screen along with
     * a death message background. Removes all entities from the game, stops the
     * background music, plays a death tune, and removes the HUD text. 
     */
    public void deathSequence()
    {
        List<Actor> actors = getObjects(Actor.class);
        for(Actor a : actors)
        {
            removeObject(a);
        }
        GreenfootImage deathScreen = new GreenfootImage("DeathScreen.png");
        setBackground(deathScreen);
        String levelDisplay = "Level: " + level;
        showText("", 55, 585);
        showText("", 55, 565);
        showText("", 55, 545);
        showText(levelDisplay, 300, 585);
        backgroundMusic.stop();
        GreenfootSound deathTune = new GreenfootSound("Gameover.mp3");
        deathTune.play();
    }
}
