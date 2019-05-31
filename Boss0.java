import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * This boss is a ranged attack boss. It shoots out spells
 * at the player while moving around randomly.
 */
public class Boss0 extends Boss
{
    private int health;
    private int damage;
    private int speed;
    private static int TIMER_CONSTANT = 60;
    private int attackCooldownTimer;
    private Color col;
    
    /**
     * @param points determine the stats of the boss.
     */
    public Boss0(int points)
    {
        super(points, 0);
        health = super.getHealth();
        damage = super.getDamage();
        speed = super.getSpeed();
        col = new Color(super.getRed(), super.getGreen(), super.getBlue(), 100);
    }    
    
    /**
     * Similar to the enemy class's attack, except instead of
     * using the rotation value to set rotation and move towards
     * the player, it uses it to shoot spells in the player's 
     * direction. It moves randomly and does not stop moving.
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
            
            getWorld().addObject(new Spell(angleDegrees, damage), getX(), getY());
            this.setRotation((int) (Math.random() * 360));
            GreenfootImage img = new GreenfootImage("Boss0Attack.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
            
            attackCooldownTimer = TIMER_CONSTANT / speed;
            
        }
        //This statement runs when the enemy is mid-attack. Just moves.
        else if(attackCooldownTimer > 0)
        {
            move(speed);
            attackCooldownTimer--;
        }
        
        if(attackCooldownTimer < (TIMER_CONSTANT / speed) - 18)
        {
            GreenfootImage img = new GreenfootImage("Boss0.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
        }
    }
}
