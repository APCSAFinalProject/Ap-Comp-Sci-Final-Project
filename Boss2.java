import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Boss2 is an area of effect type boss. It works similarly to Boss0
 * by spawning in an entity every time it attacks and constantly moves.
 * The only differences are that this boss spawns in an explosion instead
 * of a spell, and always moves towards the player. It also moves slowly
 * and attacks less frequently than other bosses.
 *
 * @author Aaron Saporito, Adam Rutledge, Aiden G, Luke Arsenalt, and Anthony
 * @version 1.1
 */
public class Boss2 extends Boss
{
    private int health;
    private int speed;
    private int damage;
    private int attackCooldownTimer;
    private static int TIMER_CONSTANT = 120;
    private Color col;
    /**
     * Construct a Boss2.
     * @param points determines the stats of the boss randomly
     * through the superclass Boss.
     */
    public Boss2(int points)
    {
        super(points, 2);
        health = super.getHealth();
        damage = super.getDamage();
        speed = Math.max(1, super.getSpeed() / 2);
        col = new Color(super.getRed(), super.getGreen(), super.getBlue(), 100);
    }
    
    /**
     * Works similarly to enemy and Boss0. It has a timer that counts
     * down to 0 from TIMER_CONSTANT/speed. When it hits 0, it changes
     * its direction to face the player, and sets off an explosion
     * based on the damage stat of the Boss2.
     */
    public void attack()
    {
        List<Player> players = getWorld().getObjects(Player.class);
        if(attackCooldownTimer == 0 && players.size() > 0)
        {
            Player a = players.get(0);
            //Distances between the two, will be used to get an angle.
            int xDist = a.getX() - this.getX();
            int yDist = (a.getY() - this.getY());
            //This function gets an angle in radians based on an X and Y coordinate
            double angleRadians = Math.atan2(yDist, xDist); 
            //Converts the radians into usable integer degrees
            int angleDegrees = (int) (angleRadians * 180 / Math.PI);
            
            getWorld().addObject(new Explosion(damage), getX(), getY());
            this.setRotation(angleDegrees);
            /*By dividing the preset attack distance by speed, you get (rougly) how
             * many times the actor must move speed pixels to go ATTACK_DISTANCE pixels.
             * This makes sure that no matter what speed the enemy goes, it will always
             * lunge about ATTACK_DISTANCE pixels.
             */
            attackCooldownTimer = TIMER_CONSTANT / speed;
            
            //Sets a timer that will be used for waiting after the attack is over
            //Sets the enemy so it is allowed to deal damage when it touches a player.
        }
        //This statement runs when the enemy is mid-attack. Just moves.
        else if(attackCooldownTimer < (TIMER_CONSTANT / speed) / 2)
        {
            move(speed);
            GreenfootImage img = new GreenfootImage("Boss2.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
            attackCooldownTimer--;
        }
        else if(attackCooldownTimer >= (TIMER_CONSTANT / speed) / 2)
        {
            GreenfootImage img = new GreenfootImage("Boss2Attack.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
            attackCooldownTimer--;
        }
    }
}
