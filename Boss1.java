import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;

/**
 * Works identically to the normal enemy class. The only difference
 * is that this boss will always have higher stats than the enemies
 * that the player faced on this level.
 */
public class Boss1 extends Boss
{
    private int health;
    private int damage;
    private int speed;
    private boolean canDoDamage;
    private int attackTimer;
    private static int ATTACK_DISTANCE = 60;
    private int attackCooldownTimer;
    private Color col;
    /**
     * Constructs a Boss1 object with a given amount of points.
     * @param points determines the stats of the boss randomly
     * through the superclass Boss.
     */
    public Boss1(int points)
    {
        super(points, 1);
        health = super.getHealth();
        damage = super.getDamage();
        speed = super.getSpeed();
        col = new Color(super.getRed(), super.getGreen(), super.getBlue(), 100);
    }
    
    /**
     * Uses the superclass's act method, but also adds
     * in the hit method which is necessary for this boss
     * to function.
     */
    public void act()
    {
        super.act();
        hit();
    }
    
    /**
     * Identical to the enemy's attack method. Moves forward for
     * a certain amount of time while attacking, then waits
     * for a period of time while not attacking.
     */
    public void attack()
    {
        List<Player> players = getWorld().getObjects(Player.class);
        if(attackTimer == 0 && attackCooldownTimer ==0 && players.size() > 0)
        {
            Player a = players.get(0);
            
            GreenfootImage img = new GreenfootImage("Boss1Attack.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
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
            GreenfootImage img = new GreenfootImage("Boss1.png");
            img.setColor(col);
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
        }
    }
    
    /**
     * Class that allows the boss to directly hit the
     * player when it makes contact with it. Identical
     * to the hit method in enemy.
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
}
