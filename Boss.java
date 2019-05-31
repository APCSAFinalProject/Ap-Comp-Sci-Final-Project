import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Boss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Boss extends Actor
{
    private int health;
    private int damage;
    private int speed;
    private int red;
    private int green;
    private int blue;
    /**
     * Constructor initializes all instance fields. For health, damage, and speed, it
     * takes in a value called points and distributes the total value of points between
     * the three ints randomly. Each one will be at a minimum 1, and at a maximum points-2.
     * Color is also determined, and the color is set based on health damage and speed.
     * @param points The value is divided randomly between health, damage, and speed.
     */
    public Boss(int points, int type)
    {
        health = 1;
        damage = 1;
        speed = 1;
        for(int i = 1; i <= points; i++)
        {
            int rand = (int)(Math.random() * 3) + 1;
            if(rand == 1)
            {
                health++;
            }
            else if(rand == 2)
            {
                damage++;
            }
            else if(rand == 3)
            {
                speed++;
            }
        }
        /*Tests to see what the greatest stat value is between health, damage, and speed.
         * When it finds the highest, it sets the corresponding color to 255 (the max
         * value for colors). It then sets the other colors to a proportional value.
         * health = green, speed = blue, damage = red
         * 
         * EX: health = 2, damage = 1, speed = 1 results in green = 255, blue = 127,
         * red = 127. 
         */
        
        GreenfootImage img;
        img = new GreenfootImage("Boss" + type + ".png");
        
        int multiplier = 0;
        if(health >= damage && health >= speed)
        {
            multiplier = 255 / health;
        }
        else if(speed >= damage && speed >= health)
        {
            multiplier = 255 / speed;
        }
        else
        {
            multiplier = 255 / damage;
        }
        
        red = multiplier * damage;
        green = multiplier * health;
        blue = multiplier * speed;
        
        Color col = new Color(red, green, blue, 100);
        img.setColor(col);
        img.fillOval(0, 0, img.getWidth(), img.getHeight());
        setImage(img);
    }
    
    /**
     * Act - do whatever the Boss wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        attack();
        die();
    }
    
    /**
     * Every variation of boss has a different attack method, and they do not all share
     * similarities, therefore this is made into an abstract void so they may extend
     * it and do different things. It is declared here instead of in the subclasses so
     * that it can still be used in the act method of the superclass, which is shared
     * by all subclasses.
     */
    public abstract void attack();
    
    /**
     * Class that is the same for all subclasses. Just removes health from the boss
     * and plays the sound for getting hit.
     */
    public void takeDamage(int dmg)
    {
        health -= damage;
        GreenfootSound enemyHit = new GreenfootSound("sounds/EnemyAttacked.mp3");
        enemyHit.play();
    }
    
    /**
     * Class shared by all subclasses. Tests for whether it's health is below
     * zero, and if so, gives the player 50 money, brings the player to the
     * shop, and removes itself from the world.
     */
    public void die()
    {
        if(health <= 0)
        {
            Player player = getWorld().getObjects(Player.class).get(0);
            player.addMoney(50);
            ((MyWorld) getWorld()).enterShop();
            getWorld().removeObject(this);
        }
    }
    
    /**
     * @returns the boss's health
     */
    public int getHealth()
    {
        return health;
    }
    
    /**
     * @returns the boss's speed
     */
    public int getSpeed()
    {
        return speed;
    }
    
    /**
     * @returns the boss's damage
     */
    public int getDamage()
    {
        return damage;
    }
    
    public int getRed()
    {
        return red;
    }
    
    public int getBlue()
    {
        return blue;
    }
    
    public int getGreen()
    {
        return green;
    }
}
