import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A special door entering the boss room.
 * 
 * @author Aaron Saporito, Adam Rutledge, Aiden G, Luke Arsenalt, and Anthony
 * @version 1.1
 */
public class BossDoor extends Actor
{
    public BossDoor()
    {
        setImage("BossDoor.png");
    }
    /**
     * Very simple class. If the player touches the door, it calls the method
     * enterBossRoom from MyWorld. enterBossRoom does the difficult work
     * like determining if the player is allowed to enter yet.
     */
    public void act() 
    {
        if(isTouching(Player.class))
        {
            ((MyWorld)getWorld()).enterBossRoom();
        }
    }    
}
