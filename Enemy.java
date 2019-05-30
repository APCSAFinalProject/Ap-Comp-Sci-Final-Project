import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Write a description of class Enemy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Enemy extends Actor
{
    private int health;
    private int damage;
    private int speed;
    private boolean canDoDamage;
    private int attackTimer;
    private static int ATTACK_DISTANCE = 60;
    private int attackCooldownTimer;
    private int sprite;
    private Color col;
    /**
     * Constructor initializes all instance fields. For health, damage, and speed, it
     * takes in a value called points and distributes the total value of points between
     * the three ints randomly. Each one will be at a minimum 1, and at a maximum points-2.
     * Color is also determined, and the color is set based on health damage and speed.
     * @param points The value is divided randomly between health, damage, and speed.
     */
    public Enemy(int points)
    {
        canDoDamage = false;
        attackTimer = 0;
        attackCooldownTimer = 0;
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
        /*assigns the determined value to the col variable. Must find out how to set the
         * Enemy's color to col.
         */
        sprite = (int) (Math.random() * 3);
        GreenfootImage img = new GreenfootImage("images/enemy_" + sprite + ".png");
        col = new Color(red, green, blue, 100);
        img.setColor(col);
        img.fillOval(0, 0, img.getWidth(), img.getHeight());
        /* currently have no clue how to actually change colors, fill just
         * turns it into a square of whatever color col is.
         */
        //img.setColor(col);
        //img.fill();
        setImage(img);
    }
    
    /**
     * Act Runs attack, which determines the attack-based movement of the enemy, hit,
     * which determines when to deal damage to a player, and die, which determines when
     * health is at 0 and the enemy should be removed from the world. 
     */
    public void act() 
    {
        attack();
        hit();
        die();
    }
    
    /**
     * Runs on various timers to determine when to start attacking, to continue attacking,
     * and when to rest. When both attackCooldownTimer and attackTimer are zero, it is time
     * to start an attack and the largest section is run, which sets direction of attack and
     * resets the timers. When attackTimer is above zero, the actor just moves and decrements
     * the timer, does not decrement the cooldown timer. After this, the actor sits and does
     * nothing during a cooldown phase determined by the attackCooldownTimer.
     */
    public void attack()
    {
        List<Player> players = getWorld().getObjects(Player.class);
        if(attackTimer == 0 && attackCooldownTimer ==0 && players.size() > 0)
        {
            GreenfootImage img = new GreenfootImage("images/enemy_" + sprite + "_attack.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
            Player a = players.get(0);
            //Distances between the two, will be used to get an angle.
            int xDist = a.getX() - this.getX();
            int yDist = (a.getY() - this.getY());
            //This function gets an angle in radians based on an X and Y coordinate
            double angleRadians = Math.atan2(yDist, xDist); 
            //Converts the radians into usable integer degrees
            int angleDegrees = (int) (angleRadians * 180 / Math.PI);
            this.setRotation(angleDegrees);
            /*By dividing the preset attack distance by speed, you get (rougly) how
             * many times the actor must move speed pixels to go ATTACK_DISTANCE pixels.
             * This makes sure that no matter what speed the enemy goes, it will always
             * lunge about ATTACK_DISTANCE pixels.
             */
            attackTimer = ATTACK_DISTANCE / speed;
            //Sets a timer that will be used for waiting after the attack is over
            int attackCooldown = 50 + (int) (Math.random() * 20);
            attackCooldownTimer = attackCooldown;
            //Sets the enemy so it is allowed to deal damage when it touches a player.
            canDoDamage = true;
        }
        //This statement runs when the enemy is mid-attack. Just moves.
        else if(attackTimer > 0)
        {
            move(speed);
            attackTimer--;
        }
        //This runs after the attack, enemy cools down and cannot deal damage.
        else if(attackCooldownTimer > 0)
        {
            attackCooldownTimer--;
            canDoDamage = false;
            GreenfootImage img = new GreenfootImage("images/enemy_" + sprite + ".png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
        }
    }
    
    /**
     * Tests for whether it is currently able to do damage (which is when it is mid-attack and
     * has not already dealt damage yet in this attack) and whether it is touching a player.
     * If both are true, it deals damage to the player.
     */
    public void hit()
    {
        if(canDoDamage && isTouching(Player.class))
        {
            Player a = (Player) getOneIntersectingObject(Player.class);
            a.takeDamage(damage);
            canDoDamage = false;
        }
    }
    
    /**
     * Only called by the Player class. Subtracts an amount of damage from the enemy's health.
     */
    public void takeDamage(int dmg)
    {
        health -= dmg;
    }
    
    /**
     * Tests to see if health is zero or less. If true, gives the player
     * an amount of money between the sum of its stats divided by two and
     * the sum of its stats plus 1/2 the sum of its stats. It then removes 
     * itself from the game.
     */
    public void die()
    {
        if(health <= 0)
        {
            List<Player> players = getWorld().getObjects(Player.class);
            for(Player a : players)
            {
                int stats = health + damage + speed;
                int rand = (int) (Math.random() * stats) - (stats / 2);
                a.addMoney(stats + rand);
            }
            getWorld().removeObject(this);
        }
    }
}