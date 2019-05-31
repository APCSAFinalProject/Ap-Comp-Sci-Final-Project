import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The projectile that Boss0 shoots at the player.
 * Has a certain amount of damage and a direction it
 * moves in.
 *
 * @author Aaron Saporito, Adam Rutledge, Aiden G, Luke Arsenalt, and Anthony
 * @version 1.1
 *
 */
public class Spell extends Actor
{
    private int damage;
    
    /**
     * Constructs a spell.
     * @param rotation what direction the spell will move in
     * @param damage how much damage the spell does to the player
     */
    public Spell(int rotation, int damage)
    {
        setRotation(rotation);
        this.damage = damage;
        setImage("Spell.png");
    }
    
    /**
     * Moves 2, calls hit, and calls expire every tick.
     */
    public void act() 
    {
        move(2);
        hit();
        expire();
    }
    
    /**
     * Detects if it is touching the player, if so it
     * deals damage.
     */
    public void hit()
    {
        if(isTouching(Player.class))
        {
            Player player = (Player) getOneIntersectingObject(Player.class);
            player.takeDamage(damage);
        }
    }
    
    /**
     * Detects if it is in a position where the spell should
     * disappear. This is when it is touching the player or
     * when it hits a wall. If it is touching a player, hit
     * is called before expire so damage is dealt and then
     * the spell removes itself.
     */
    public void expire()
    {
        if(getX() == 0 || getX() == 599 || getY() == 0 || getY() == 599 || isTouching(Player.class))
        {
            getWorld().removeObject(this);
        }
    }
}
