import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The explosion created by Boss2. The explosion
 * image gets larger until it reaches a set maximum
 * size, then removes itself.
 * @author Aaron Saporito, Adam Rutledge, Aiden G, Luke Arsenalt, and Anthony
 * @version 1.1
 */
public class Explosion extends Actor
{
    private GreenfootImage img;
    private int damage;
    private boolean hasHit;
    
    /**
     * Constructs an explosion.
     * @param damage how much damage the explosion deals to
     * the player on contact.
     */
    public Explosion(int damage)
    {
        img = new GreenfootImage("beeper.png");
        setImage(img);
        this.damage = damage;
        hasHit = false;
    }
    
    /**
     * Deals with the growing of the explosion. Every tick the
     * explosion increases in size by 10%. Also calls
     * hit and removes itself when it gets too big.
     */
    public void act() 
    {
        img.scale((int)(img.getWidth() * 1.1), (int) (img.getHeight() * 1.1));
        setImage(img);
        hit();
        if(img.getWidth() > 200)
        {
            getWorld().removeObject(this);
        }
    }    
    
    /**
     * Deals damage to the player when it hits it. When called,
     * it sets hasHit to true, and then will never be able to get
     * called again. This means the player does not get dealt more
     * damage than just a single hit.
     */
    public void hit()
    {
        if(isTouching(Player.class) && !hasHit)
        {
            Player player = (Player) getOneIntersectingObject(Player.class);
            player.takeDamage(damage);
            hasHit = true;
        }
    }
}
