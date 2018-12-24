public class Enemy
{
    private String type;
    private int health;
    private int attack;
    private boolean frozen;
    public Enemy(String spawn)
    {
        frozen=false;
        type=spawn;
        if(spawn.equals("armored++"))
        {
            health=80;
            attack=5;
        }
        else if(spawn.equals("armored+"))
        {
            health=65;
            attack=5;
        }
        else if(spawn.equals("armored"))
        {
            health=28;
            attack=5;
        }
        else if(spawn.equals("normal")||spawn.equals("flag"))
        {
            health=10;
            attack=5;  //
        }
    }
    public boolean isFrozen()
    {
        return frozen;
    }
    public void setFrozenStatus(boolean status)
    {
        frozen=status;
    }
    public void buffHP(int boost)
    {
        health+=boost;
    }
    public void buffAtk(int boost)
    {
        attack+=boost;
    }
    public int HP()
    {
        return health;
    }
    public int dmg()
    {
        return attack;
    }
    public String type()
    {
        return type;
    }
    public void reduceHP(int n)
    {
        if(n>health)
            health=0;
        else
            health-=n;
    }
    public String name()
    {
        if(type.equals("flag"))
            return "Flag Zombie";
        else if(type.equals("normal"))
            return "Regular Zombie";
        else if(type.equals("armored"))
            return "Conehead Zombie";
        else if(type.equals("armored+"))
            return "Buckethead Zombie";
        else
            return "Football Zombie";
    }
    public String ogPic()
    {
        if(type.equals("flag"))
            return "ZombieFlag.png";
        else if(type.equals("normal"))
            return "zombieReg.png";
        else if(type.equals("armored"))
            return "zombieCone.png";
        else if(type.equals("armored+"))
            return "zombieBucket.png";
        else
            return "Football.png";
    }
    public String selPic()
    {
        if(type.equals("flag"))
            return "ZombieFlagSEL.png";
        else if(type.equals("normal"))
            return "zombieRegSEL.png";
        else if(type.equals("armored"))
            return "zombieConeSEL.png";
        else if(type.equals("armored+"))
            return "zombieBucketSEL.png";
        else
            return "FootballSEL.png";
    }
}