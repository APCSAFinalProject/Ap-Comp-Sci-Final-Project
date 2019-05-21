import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends Actor
{
    private int speed;
    private int maxHealth;
    private int damage;
    private int health;
    private int attackTimer;
    private boolean canDoDamage;
    private static int ATTACK_DISTANCE = 70;
    
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
        canDoDamage = false;
    }
    
    /**
     * The Act method runs the player. Every tick, whatever is contained within
     * this method is run. 
     */
    public void act() 
    {
        hit();
        if(attackTimer == 0)
        {
            canDoDamage = false;
            followMouse();
            takeInput();
        }
        else
        {
            move(speed * 3);
            attackTimer--;
        }
        die();
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
        
        if(Greenfoot.isKeyDown("space"))
        {
            attackTimer = ATTACK_DISTANCE / (speed * 3);
            canDoDamage =  true;
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
    }
}
