import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Write a description of class Boss0 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Boss0 extends Boss
{
    private int health;
    private int damage;
    private int speed;
    private boolean canDoDamage;
    private int attackTimer;
    private static int ATTACK_DISTANCE = 60;
    private int attackCooldownTimer;
    public Boss0(int points)
    {
        super(points, 0);
        
    }    
    
    public void act()
    {
        super.act();
        hit();
    }
    
    public void attack()
    {
        List<Player> players = getWorld().getObjects(Player.class);
        if(attackTimer == 0 && attackCooldownTimer ==0 && players.size() > 0)
        {
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
        }
    }
    
    public void hit()
    {
        if(canDoDamage && isTouching(Player.class))
        {
            Player a = (Player) getOneIntersectingObject(Player.class);
            a.takeDamage(damage);
            canDoDamage = false;
        }
    }
}
