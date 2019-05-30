import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Boss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Boss extends Actor
{
    private int health;
    private int damage;
    private int speed;
    private boolean canDoDamage;
    private int attackTimer;
    private static int ATTACK_DISTANCE = 60;
    private int attackCooldownTimer;
    /**
     * Constructor initializes all instance fields. For health, damage, and speed, it
     * takes in a value called points and distributes the total value of points between
     * the three ints randomly. Each one will be at a minimum 1, and at a maximum points-2.
     * Color is also determined, and the color is set based on health damage and speed.
     * @param points The value is divided randomly between health, damage, and speed.
     */
    public Boss(int points, int type)
    {
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
        int red = 0;
        int green = 0;
        int blue = 0;
        /*Tests to see what the greatest stat value is between health, damage, and speed.
         * When it finds the highest, it sets the corresponding color to 255 (the max
         * value for colors). It then sets the other colors to a proportional value.
         * health = green, speed = blue, damage = red
         * 
         * EX: health = 2, damage = 1, speed = 1 results in green = 255, blue = 127,
         * red = 127. 
         */
        
        GreenfootImage img;
        if(type == 0)
        {
            img = new GreenfootImage("Boss_0.png");
        }
        else if(type == 1)
        {
            img = new GreenfootImage("Boss_1.png");
        }
        else
        {
            img = new GreenfootImage("Boss_2.png");
        }
        
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
        // Add your action code here.
    }
}
