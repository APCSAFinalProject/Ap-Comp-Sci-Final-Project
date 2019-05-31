import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BossDoor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
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
