import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends Actor
{
    private GreenfootSound dash;
    private GreenfootSound getHit;
    private GreenfootSound gameover;
    private GreenfootSound bgm;
    private boolean inShop;
    private int speed;
    private int maxHealth;
    private int damage;
    private int health;
    private int attackTimer;
    private int attackCooldownTimer;
    private int balance;
    private int volume;
    private boolean canDoDamage;
    private static int ATTACK_DISTANCE = 70;
    private static int ATTACK_COOLDOWN = 60;
    
    /**
     * The only constructor for player is a non-input constructor. A player
     * is always created at the start of the game with the same stats.
     */
    public Player()
    {
        speed = 3;
        maxHealth = 5;
        damage = 5;
        health = maxHealth;
        attackTimer = 0;
        attackCooldownTimer = 0;
        balance = 0;
        canDoDamage = false;
        dash = new GreenfootSound("Dash.mp3");
        getHit = new GreenfootSound("PlayerAttacked.mp3");
        gameover = new GreenfootSound("Gameover.mp3");
        bgm = new GreenfootSound("Bgm.mp3");
        volume = 50;
        bgm.setVolume(volume);
    }
    
    /**
     * The Act method runs the player. Every tick, whatever is contained within
     * this method is run. 
     */
    public void act() 
    {
        bgm.playLoop();
        hit();
        advance();
        if(attackTimer == 0)
        {
            canDoDamage = false;
            followMouse();
            takeInput();
            
            if(attackCooldownTimer > 0)
            {
                attackCooldownTimer--;
            }
        }
        else
        {
            move(speed * 3);
            attackTimer--;
            setImage(new GreenfootImage("images/player_0.png"));
        }
        if(!((MyWorld) getWorld()).getCleared() && getWorld().getObjects(Enemy.class).size() == 0)
        {
            ((MyWorld) getWorld()).clearLevel();
        }
        die();
    }
    
   
    public void shop()
    {
        if(inShop)
        {
            boolean purchaseReady = true;
            if(purchaseReady == true && this.getX() < 200 && this.getY() < 100)
            {
                removeMoney(10);
                levelUp(0, 1);
                purchaseReady = false;
            }
            else if(purchaseReady == true && this.getX() > 200 && this.getX() < 400 
                && this.getY() < 100)
            {
                removeMoney(10);
                levelUp(1, 1);
                purchaseReady = false;
            }
            else if(purchaseReady == true && this.getX() > 400 && this.getX() < 600
                && this.getY() < 100)
            {
                removeMoney(10);
                levelUp(2, 1);
                purchaseReady = false;
            }

            if(purchaseReady == false && this.getY() > 100)
            {
                purchaseReady = true;
            }
        }
    }
    
    /**
     * Called to declare that the player has entered the shop.
     */
    public void enterShop()
    {
        inShop = true;
    }
    
    /**
     * Called when leaving shop to declare that the player is no longer in the shop.
     */
    public void leaveShop()
    {
        inShop = false;
    }
    
    /**
     * Checks if the player is in the shop
     * 
     * @return True if in shop.
     */
    public boolean isInShop()
    {
        return inShop;
    }
    
    /**
     * Gets the player's health
     * 
     * @return health
     */
    public int getHealth()
    {
        return health;
    }
    
    /**
     * Gets the players speed.
     * 
     * @return speed
     */
    public int getSpeed()
    {
        return speed;
    }
    
    /**
     * Gets the damage.
     * @return damage
     */
    public int getDamage()
    {
        return damage;
    }

    
    /**
     * A method that runs at all times, excluding when the player is doing a dash
     * attack. It gets the position of the mouse on the screen, and points the
     * player towards the mouse.
     */
    public void followMouse()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        if(mouse != null)
        {
            int xDist = mouse.getX() - this.getX();
            int yDist = mouse.getY() - this.getY();
            double angle = Math.atan2(yDist, xDist);
            angle = (angle * 180) / Math.PI;
            this.setRotation((int) angle);
        }
    }
    
    /**
     * Controls all button click input. This includes directional keys for
     * movement and the key to lunge attack. 
     */
    public void takeInput()
    {
        if(Greenfoot.isKeyDown("a") && !Greenfoot.isKeyDown("d"))
        {
            moveLeft(speed);
        }
        
        if(Greenfoot.isKeyDown("w") && !Greenfoot.isKeyDown("s"))
        {
            moveUp(speed);
        }
        
        if(Greenfoot.isKeyDown("s") && !Greenfoot.isKeyDown("w"))
        {
            moveDown(speed);
        }
        
        if(Greenfoot.isKeyDown("d") && !Greenfoot.isKeyDown("a"))
        {
            moveRight(speed);
        }
        
        if(Greenfoot.isKeyDown("space") && attackCooldownTimer == 0)
        {
            dash.play();
            setImage(new GreenfootImage("images/player_1.png"));
            attackTimer = ATTACK_DISTANCE / (speed * 3);
            canDoDamage =  true;
            attackCooldownTimer = ATTACK_COOLDOWN;
        }
        
        
    }
    
    /**
     * Special move method designed to always move the player in a set direction, rather
     * than the move() method, which moves where the player is pointing. These methods
     * achieve movement by taking in an input for how many tiles should be moved, then
     * executing a for loop that many times, each time it tests to see if it can move,
     * then sets its location to one pixel in the direction it needs to go. This
     * particular method always moves in the negative Y direction (Up).
     * 
     * @param speed The amount to move
     */
    public void moveUp(int speed)
    {
        for(int i = 0; i < speed; i++)
        {  
            if(getY()>0)
            {
                setLocation(getX(), getY() - 1);
            }
        }
    }
    
    /**
     * Functionally the same as moveUp, but moves in the positive Y direction (Down).
     * @param speed The amount to move
     */
    public void moveDown(int speed)
    {
        for(int i = 0; i < speed; i++)
        {
            if(getY() < getWorld().getHeight())
            {
                setLocation(getX(), getY() + 1);
            }
        }
    }
    
    /**
     * Functionally the same as moveUp, but moves in the negative X direction (Left).
     * @param speed The amount to move
     */
    public void moveLeft(int speed)
    {
        for(int i = 0; i < speed; i++)
        {
            if(getX() > 0)
            {
                setLocation(getX() - 1, getY());
            }
        }
    }
    
    /**
     * Functionally the same as moveUp, but moves in the positive X direction (Right).
     * @param speed The amount to move
     */
    public void moveRight(int speed)
    {
        for(int i = 0; i < speed; i++)
        {
            if(getX()<(getWorld().getWidth()-1))
            {
                setLocation(getX() + 1, getY());
            }
        }
    }
    
    /**
     * Method called for attacking an enemy. tests to see if it is able to deal damage
     * and if it is currently touching an enemy. If it touching an enemy, it deals damage
     * to one enemy that it is touching. After it deals damage, it sets itself to no longer
     * be able to deal damage. This means it will not attack multiple enemies in a single
     * lunge, and it will not deal damage to an enemy twice in a single lunge. 
     */
    public void hit()
    {
        if(canDoDamage && isTouching(Enemy.class))
        {
            Enemy a = (Enemy) getOneIntersectingObject(Enemy.class);
            a.takeDamage(damage);
            canDoDamage = false;
            
        }
    }
    
    /**
     * Method that is run every tick. Tests to see if it is at zero health, and removes
     * the player from the game if it is.
     */
    public void die()
    {
        if(health <= 0)
        {
            getWorld().removeObject(this);
            bgm.stop();
            gameover.play();
        }
    }
    
    /**
     * Method for adding stats to the player. Heals player no matter the parameters.
     * @param whatStat determines which stat to upgrade. 0 is health, 1 is speed, 2 is
     * damage.
     * @param howMuch how many points to upgrade the selected stat by. 
     */
    public void levelUp(int whatStat, int howMuch)
    {
        if(whatStat == 0)
        {
            maxHealth += howMuch;
        }
        
        if(whatStat == 1)
        {
            speed += howMuch;
        }
        
        if(whatStat == 2)
        {
            damage += howMuch;
        }
        
        this.heal();
    }
    
    /**
     * A full heal of the player. Sets health back to maximum.
     */
    public void heal()
    {
        health = maxHealth;
    }
    
    /**
     * A heal for a certain amount of the player. Adds that amount to the current health,
     * if health is at maximum it only goes to the maxHealth value.
     * 
     * @param amountHealed The amount to heal
     */
    public void heal(int amountHealed)
    {
        health = Math.max(health + amountHealed, maxHealth);
    }
    
    /**
     * Only called by the Enemy class. Subtracts an amount of damage from the Player's health.
     * 
     * @param dmg The amount of damage to take
     */
    public void takeDamage(int dmg)
    {
        health -= dmg;
        getHit.play();
    }
    
    /**
     * Takes money out of the player's balance. Can make balance negative,
     * but the classes calling removeMoney will prevent this from happening.
     * @param amount the amount of money removed from player's balance
     */
    public void removeMoney(int amount)
    {
        balance -= amount;
    }
    
    /**
     * Adds money to the player's balance.
     * @param amount the amount of money to add to the player's balance.
     */
    public void addMoney(int amount)
    {
        balance += amount;
    }
    
    /**
     * Used to access the player's balance.
     * @returns the player's balance.
     */
    public int getBalance()
    {
        return balance;
    }
    
    /**
     * JAVA DOC THIS
     */
    public void advance()
    {
        if(getWorld().getObjects(Enemy.class).size() == 0)
        {
            MyWorld world = (MyWorld) getWorld();
            boolean[] doors = world.findDoors(world.getRoomCode());
            if(doorNum() == 0 && doors[doorNum()])
            {
                world.nextRoom(doorNum());
                setLocation(300,500);
            }
            else if(doorNum() == 1 && doors[doorNum()])
            {
                world.nextRoom(doorNum());
                setLocation(100,300);
            }
            else if(doorNum() == 2 && doors[doorNum()])
            {
                world.nextRoom(doorNum());
                setLocation(300,100);
            }
            else if(doorNum() == 3 && doors[doorNum()])
            {
                world.nextRoom(doorNum());
                setLocation(500,300);
            }
        }
    }
    
    public int doorNum()
    {
        int door = -1;
        if(250 <= this.getX() && this.getX() < 350)
        {
            if(0 <= this.getY() && this.getY() < 50)
            {
                door = 0;
            }
            else if(550 <= this.getY() && this.getY() < 600)
            {
                door = 2;
            }
        }
        else if(250 <= this.getY() && this.getY() < 350)
        {
            if(0 <= this.getX() && this.getX() < 50)
            {
                door = 3;
            }
            else if(550 <= this.getX() && this.getX() < 600)
            {
                door = 1;
            }
        }
        return door;
    }
}
