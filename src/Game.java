//Kevin Chen & Francis Sy
//PLANTS VS. ZOMBIES
//Project 8

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.applet.*;

public class Game
{
    //Instance Variables for Menus
    private ActionListener gameListener;
    private Timer gameRepeat;
    
    private MainMenu menu;
    private HelpMenu hMenu;
    private Win won;
    private Score board;
    
    private Grid grid;
    private int userRow;
    private int userCol;
    private int msElapsed;
    private int timesGet;
    private int timesAvoid;
    private boolean lost;
    private AudioClip beforeZombies;
    private AudioClip zombiesArrived;
    private AudioClip zombieMunch;
    private AudioClip explosion;
    private AudioClip zombieBrains;
    
    private boolean win;
    
    private int cannonCharge;
    private boolean cannonCharging;
    private Enemy[][] enemies;
    private Plant[][] plants;
    private int sun;
    private int level;
    private int spawnTimer;
    private int finalWave;
    private boolean finale;
    private boolean firstWave;
    private boolean started;
    private boolean nextLevel;
    private Profile[][] infos;
    private Projectile[][] projs;
    private boolean[] cooldowns;
    private int[] rechargeTimers;
    
    private AudioClip introSong;
    
    private boolean already;
    
    public Game()
    {
        already = true;
        
        introSong = Applet.newAudioClip(this.getClass().getResource("music.wav"));
        introSong.loop();

        try
        {
            Thread.sleep(1000);
        }
        catch(Exception e)
        {

        }
        //Creating all the 
        menu = new MainMenu();
        hMenu = new HelpMenu();
        board = new Score();
        
        gameListener = new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(menu.gameStarted())
                {
                    //introSong.stop();
                    gameRepeat.stop();
                    menu.hideGui();
                    init();
                    play();
                }
                if(menu.helpClicked())
                {
                    hMenu.showGui();
                    menu.setHelpF();
                }
                if(hMenu.exitClicked())
                {
                    hMenu.hideGui();
                    hMenu.setExitF();
                }
            }
        };
        
        gameRepeat = new Timer(30, gameListener);
        gameRepeat.start();
    }
    
    public void init()
    {
        grid = new Grid(5, 9,"backgroundA.jpg"); //5,10
        userRow = 0;
        userCol = 0;
        msElapsed = 0;
        timesGet = 0;
        timesAvoid = 0;
        enemies=new Enemy[grid.getNumRows()][grid.getNumCols()];
        plants=new Plant[grid.getNumRows()][grid.getNumCols()];
        updateTitle();
        lost=false;
        win=false;
        beforeZombies=Applet.newAudioClip(this.getClass().getResource("beforeZombies.wav"));
        zombiesArrived=Applet.newAudioClip(this.getClass().getResource("zombiesArrived.wav"));
        zombieMunch=Applet.newAudioClip(this.getClass().getResource("munch.wav"));
        zombieBrains=Applet.newAudioClip(this.getClass().getResource("brain.wav"));
        explosion=Applet.newAudioClip(this.getClass().getResource("explode.wav"));
        
        cannonCharge=0;
        cannonCharging=false;
        level=1;
        sun=100;
        finalWave=25;
        spawnTimer=5000;
        finale=false;
        started=true;
        nextLevel=true;
        firstWave=true;
        infos=new Profile[grid.getNumRows()][grid.getNumCols()];
        projs=new Projectile[grid.getNumRows()][grid.getNumCols()];
        cooldowns=new boolean[7]; //6 Plants + Upgrade
        rechargeTimers=new int[7];
        for(int r=0;r<grid.getNumRows();r++)
            for(int c=0;c<grid.getNumCols();c++)
                infos[r][c]=new Profile(null,"BGSelect.png");
        for(int i=0;i<cooldowns.length;i++)
            cooldowns[i]=false;
    }
    
    public void play()
    {
        beforeZombies.play();
        
        gameListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                background();
            
                //Handles Levels 1-5 NOT INCL. BOSS LVL --- BUGGY MUST FIX
                if(finalWave<=0 && !nextLevel){ //final wave ran out, FINALE starts
                    finale=true;
                    nextLevel=true;
                }
                else if(finalWave<=0){
                    nextLevel=false;
                    level++;
                    spawnTimer*=2; //Return to normal
                    finalWave=25;
                }

                if(finale){
                    spawnTimer/=2; //Spawn 2x as FAST
                    finalWave=10;
                    finale=false;
                }
                grid.pause(100);
                handleKeyPress();

                for(int i=0;i<cooldowns.length;i++)
                    if(cooldowns[i])
                    {
                        if(i==0){ //upgrades 
                            rechargeTimers[i]+=100;
                            if(rechargeTimers[i]>=40000){ //Twin SunFlower, Gatling Pea, Tallnut
                                cooldowns[i]=false;
                                rechargeTimers[i]=0;
                            }
                        }
                        else if(i==1 || i==2 || i==3){ //flower, peashooter, repeater
                            rechargeTimers[i]+=100;
                            if(rechargeTimers[i]>=7500){
                                cooldowns[i]=false;
                                rechargeTimers[i]=0;
                            }
                        }
                        else if(i==4){ //Walnut
                            rechargeTimers[i]+=100;
                            if(rechargeTimers[i]>=30000){
                                cooldowns[i]=false;
                                rechargeTimers[i]=0;
                            }
                        }
                        else if(i==5){ //Cherry Bomb
                            rechargeTimers[i]+=100;
                            if(rechargeTimers[i]>=50000){
                                cooldowns[i]=false;
                                rechargeTimers[i]=0;
                            }
                        }
                        else if(i==6){ //Coconut Cannon
                            rechargeTimers[i]+=100;
                            if(rechargeTimers[i]>=10000){
                                cooldowns[i]=false;
                                rechargeTimers[i]=0;
                            }
                        }
                    }

                int sunSpawnChance=(int)(Math.random()*100)+1;
                if(msElapsed % 1000 == 0 && sun<800){
                    if(sunSpawnChance>=90){
                        if(sunSpawnChance>=97)
                            sun+=100;
                        else 
                            sun+=50;
                    }
                }
                if(cannonCharging){
                    cannonCharge+=100;
                    if(cannonCharge==8000){
                        cannonCharging=false;
                        cannonCharge=0;
                    }
                }
                if(msElapsed % 10000 == 0 && msElapsed!=0) //produce sun every 10 sec
                    regulate(); //Produce sun //originally 25 sec
                if(msElapsed > 20000 && msElapsed % spawnTimer == 0 && finalWave>0){ //spawns after 25 sec, every 5 sec from there
                    if(msElapsed==25000){
                        beforeZombies.stop();
                        zombiesArrived.play();
                    }
                    spawn(level);
                }
                if(msElapsed % 1500 == 0)
                    detection();
                if(msElapsed % 800 == 0) //move projectiles
                    moveProjectiles();
                if(msElapsed % 500 == 0)
                    attackingZombies();
                if(msElapsed % 1000 == 0)
                    detonate();
                ZeroHPNPCs();
                if (msElapsed % 6000 == 0) //Move every 6 seconds
                {
                    scrollLeft();
                    //spawn();
                }
                if(lost&&already)
                {
                    already = false;
                    grid.hideGui();
                    won = new Win(false);
                }
                if(win&&already)
                {
                    already = false;
                    grid.hideGui();
                    won = new Win(true);
                }
                updateTitle();
                msElapsed += 100;
            }
        };
        
        gameRepeat = new Timer(100, gameListener);
        gameRepeat.start();
    }
    
    public void handleKeyPress()
    {
        int key=grid.checkLastKeyPressed();
        //System.out.println(key);
        boolean moved=false;
        Profile prev=infos[userRow][userCol];
        Location prevLoc=new Location(userRow,userCol);
        
        if(key==38 && userRow>0){ //Up
            userRow--;
            moved=true;}
        else if(key==40 && userRow<grid.getNumRows()-1){ //Down
            userRow++;
            moved=true;}
        else if(key==37 && userCol>0){ //Left
            userCol--;
            moved=true;}
        else if(key==39 && userCol<grid.getNumCols()-1){ //Right
            userCol++;
            moved=true;}
        
        //Keyboard Controls - Spawn Plants based on Sun & Cooldown
        if(key==32 && (plants[userRow][userCol]!=null||enemies[userRow][userCol]!=null) && !cooldowns[0]) // Space - Upgrade plant
        {
            //&& enemies[userRow][userCol]==null && projs[userRow][userCol]==null 
            if(enemies[userRow][userCol]!=null)
            {
                Enemy chosen=enemies[userRow][userCol];
                boolean cannonPresent=false;
                for(int c=0;c<grid.getNumCols();c++)
                    if(plants[userRow][c]!=null && plants[userRow][c].type().equals("cannon"))
                        cannonPresent=true;
                if(cannonPresent && !cannonCharging){
                    chosen.reduceHP(90);
                    cannonCharging=true;
                }
            }
            else
            {
                Plant chosen=plants[userRow][userCol];
                if(chosen.type().equals("flower") && sun>=150){
                    infos[userRow][userCol].setOg("TwinSF.png");
                    infos[userRow][userCol].setSel("TwinSFSEL.png");
                    plants[userRow][userCol]=new Plant("twinSF");
                    sun-=150;
                    cooldowns[0]=true;
                }
                else if(chosen.type().equals("pea") && sun>=175){
                    infos[userRow][userCol].setOg("SnowPea.png");
                    infos[userRow][userCol].setSel("SnowPeaSEL.png");
                    plants[userRow][userCol]=new Plant("snowPea");
                    sun-=175;
                    cooldowns[0]=true;
                }
                else if(chosen.type().equals("repeater") && sun>=200){
                    infos[userRow][userCol].setOg("GatlingPea.png");
                    infos[userRow][userCol].setSel("GatlingPeaSEL.png");
                    plants[userRow][userCol]=new Plant("gatling");
                    sun-=200;
                    cooldowns[0]=true;
                }
                else if(chosen.type().equals("walnut") && sun>=250){
                    infos[userRow][userCol].setOg("tallnut.png");
                    infos[userRow][userCol].setSel("tallnutSEL.png");
                    plants[userRow][userCol]=new Plant("tallnut");
                    sun-=250;
                    cooldowns[0]=true;
                }
            }
        }
        else if(key==49 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=50 && !cooldowns[1]) //1 - Flower
        {
            infos[userRow][userCol].setOg("Sunflower.png");
            infos[userRow][userCol].setSel("SunflowerSEL.png");
            plants[userRow][userCol]=new Plant("flower");
            sun-=50; //50
            cooldowns[1]=true;
        }
        else if(key==50 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=100 && !cooldowns[2]) //2 - Peashooter
        {
            infos[userRow][userCol].setOg("Peashooter.png");
            infos[userRow][userCol].setSel("PeashooterSEL.png");
            plants[userRow][userCol]=new Plant("pea");
            sun-=100;
            cooldowns[2]=true; 
        }
        else if(key==51 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=200 && !cooldowns[3]) //3 - Repeater
        {
            infos[userRow][userCol].setOg("Repeater.png");
            infos[userRow][userCol].setSel("RepeaterSEL.png");
            plants[userRow][userCol]=new Plant("repeater");
            sun-=200;
            cooldowns[3]=true;
        }
        else if(key==52 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=50 && !cooldowns[4]) //4 - Walnut
        {
            infos[userRow][userCol].setOg("Walnut.png");
            infos[userRow][userCol].setSel("WalnutSEL.png");
            plants[userRow][userCol]=new Plant("walnut");
            sun-=50;
            cooldowns[4]=true;
        }
        else if(key==53 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=150 && !cooldowns[5]) //5 - Cherry Bomb
        {
            infos[userRow][userCol].setOg("cherryBomb.png");
            infos[userRow][userCol].setSel("cherryBombSEL.png");
            plants[userRow][userCol]=new Plant("cherryBomb");
            sun-=150;
            cooldowns[5]=true;
        }
        else if(key==54 && plants[userRow][userCol]==null && enemies[userRow][userCol]==null && projs[userRow][userCol]==null && sun>=400 && !cooldowns[6]) //6 - Coconut Cannon
        {
            infos[userRow][userCol].setOg("coconutCannon.png");
            infos[userRow][userCol].setSel("coconutCannonSEL.png");
            plants[userRow][userCol]=new Plant("cannon");
            sun-=400;
            cooldowns[6]=true;
        }
        else if(key==82 && enemies[userRow][userCol]==null && projs[userRow][userCol]==null) //Key "R" - REMOVES PLANT!
        {
            infos[userRow][userCol].setOg(null);
            infos[userRow][userCol].setSel("BGSelect.png");
            plants[userRow][userCol]=null;
        }
        
        boolean pause = true;
        if(key==80)
        {
            while(pause)
            {
                Grid.pause(1);
                int key2 = grid.checkLastKeyPressed();
                if(key2==80)
                    pause=false;
            }
        }
        
        Profile curr=infos[userRow][userCol];
        Location currLoc=new Location(userRow,userCol);
        if(moved)
        {
            grid.setImage(prevLoc, prev.getOg());
            grid.setImage(currLoc,curr.getSel());
        }
        else
        {
            grid.setImage(currLoc, curr.getSel());
        }
    }
    
    public void background()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            for(int c=0;c<grid.getNumCols();c++)
            {
                if(grid.getImage(new Location(r,c))==null)
                    infos[r][c]=new Profile(null,"BGSelect.png");
            }
        }
    }
    public void ZeroHPNPCs()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            for(int c=0;c<grid.getNumCols();c++){
                if(enemies[r][c]!=null && enemies[r][c].HP()==0) //Clean up dead enemies
                {
                    enemies[r][c]=null;
                    grid.setImage(new Location(r,c), null);
                    infos[r][c]=null;
                    finalWave--;
                }
                if(plants[r][c]!=null && plants[r][c].HP()==0) //Clean up dead plants
                {
                    plants[r][c]=null;
                    grid.setImage(new Location(r,c),null);
                    infos[r][c]=null;
                }
            }
        }
    }
    public void regulate()
    {
        for(int r=0;r<grid.getNumRows();r++)
            for(int c=0;c<grid.getNumCols();c++)
            {
                Location curr=new Location(r,c);
                if(plants[r][c]!=null){
                    if(plants[r][c].type().equals("flower"))
                        sun+=50;
                    else if(plants[r][c].type().equals("twinSF"))
                        sun+=100;
                }
            }
    }
    //For Cherry Bombs
    public boolean isValid(int r, int c)
    {
        boolean rowValid= r>=0 && r<grid.getNumRows();
        boolean colValid= c>=0 && c<grid.getNumCols();
        return rowValid && colValid;
    }
    public void detonate()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            for(int c=0;c<grid.getNumCols();c++)
            {
                Location curr=new Location(r,c);
                Plant currP=plants[r][c];
                if(currP!=null && currP.type().equals("cherryBomb"))
                {
                    grid.setImage(curr, null);
                    infos[r][c]=null;
                    plants[r][c]=null;
                    //Handling explosion
                    for(int x=r-1;x<=r+1;x++)
                        for(int y=c-1;y<=c+1;y++)
                            if(isValid(x,y) && enemies[x][y]!=null)
                                enemies[x][y].reduceHP(900);
                }
            }
        }
    }
    //LEVELS
    public void spawn(int tier) //populateRightEdge();
    {
        if(tier==1) //Regular
        {
            int chance=(int)(Math.random()*grid.getNumRows());
            if(chance==0 && firstWave){
                int row=(int)(Math.random()*grid.getNumRows());
                grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                started=false;
            }
            
            for(int i=0;i<chance;i++)
            {
                int row=(int)(Math.random()*grid.getNumRows());
                int startingRow=0;
                if(started && i==0)
                {
                    grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                    enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                    infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                    
                    startingRow=row;
                    started=false;
                }
                else
                {
                    while(row==startingRow && firstWave)
                        row=(int)(Math.random()*grid.getNumRows());
                    grid.setImage(new Location(row,grid.getNumCols()-1), "zombieReg.png");
                    enemies[row][grid.getNumCols()-1]=new Enemy("normal");
                    infos[row][grid.getNumCols()-1]=new Profile("zombieReg.png","zombieRegSEL.png");
                }
            }
            firstWave=false;
        }
        else if(tier==2)//Regular & Cone
        {
            int chance=(int)(Math.random()*grid.getNumRows());
            //int[] pastSpawn=new int[chance];
            if(chance==0 && firstWave){
                int row=(int)(Math.random()*grid.getNumRows());
                grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                started=false;
            }
            
            for(int i=0;i<chance;i++)
            {
                int row=(int)(Math.random()*grid.getNumRows());
                int startingRow=0;
                if(started && i==0)
                {
                    grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                    enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                    infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                    
                    startingRow=row;
                    started=false;
                }
                else
                {
                    while(row==startingRow && firstWave)
                        row=(int)(Math.random()*grid.getNumRows());
                    int spawn=(int)(Math.random()*10)+1;
                    if(spawn>=7){
                        grid.setImage(new Location(row,grid.getNumCols()-1), "zombieReg.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("normal");
                        infos[row][grid.getNumCols()-1]=new Profile("zombieReg.png","zombieRegSEL.png");
                    }
                    else{
                        grid.setImage(new Location(row,grid.getNumCols()-1), "zombieCone.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("armored");
                        infos[row][grid.getNumCols()-1]=new Profile("zombieCone.png","zombieConeSEL.png");
                    }
                }
            }
            firstWave=false;
        }
        else if(tier==3)//Bucket & Football
        {
            int chance=(int)(Math.random()*grid.getNumRows());
            //int[] pastSpawn=new int[chance];
            if(chance==0 && firstWave){
                int row=(int)(Math.random()*grid.getNumRows());
                grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                started=false;
            }
            
            for(int i=0;i<chance;i++)
            {
                int row=(int)(Math.random()*grid.getNumRows());
                int startingRow=0;
                if(started && i==0)
                {
                    grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                    enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                    infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                    
                    startingRow=row;
                    started=false;
                }
                else
                {
                    while(row==startingRow && firstWave)
                        row=(int)(Math.random()*grid.getNumRows());
                    int spawn=(int)(Math.random()*10)+1;
                    if(spawn>=7){
                        grid.setImage(new Location(row,grid.getNumCols()-1), "zombieBucket.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("armored+");
                        infos[row][grid.getNumCols()-1]=new Profile("zombieBucket.png","zombieBucketSEL.png");
                    }
                    else{
                        grid.setImage(new Location(row,grid.getNumCols()-1), "zombieCone.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("armored");
                        infos[row][grid.getNumCols()-1]=new Profile("zombieCone.png","zombieConeSEL.png");
                    }
                }
            }
            firstWave=false;
        }
        else if(tier==4)//Bucket & Football
        {
            int chance=(int)(Math.random()*grid.getNumRows());
            //int[] pastSpawn=new int[chance];
            if(chance==0 && firstWave){
                int row=(int)(Math.random()*grid.getNumRows());
                grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                started=false;
            }
            
            for(int i=0;i<chance;i++)
            {
                int row=(int)(Math.random()*grid.getNumRows());
                int startingRow=0;
                if(started && i==0)
                {
                    grid.setImage(new Location(row,grid.getNumCols()-1), "ZombieFlag.png");
                    enemies[row][grid.getNumCols()-1]=new Enemy("flag");
                    infos[row][grid.getNumCols()-1]=new Profile("ZombieFlag.png","ZombieFlagSEL.png");
                    
                    startingRow=row;
                    started=false;
                }
                else
                {
                    while(row==startingRow && firstWave)
                        row=(int)(Math.random()*grid.getNumRows());
                    int spawn=(int)(Math.random()*10)+1;
                    if(spawn>=7){
                        grid.setImage(new Location(row,grid.getNumCols()-1), "Football.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("armored++");
                        infos[row][grid.getNumCols()-1]=new Profile("Football.png","FootballSEL.png");
                    }
                    else{
                        grid.setImage(new Location(row,grid.getNumCols()-1), "zombieBucket.png");
                        enemies[row][grid.getNumCols()-1]=new Enemy("armored+");
                        infos[row][grid.getNumCols()-1]=new Profile("zombieBucket.png","zombieBucketSEL.png");
                    }
                }
            }
            firstWave=false;
        }
        else if(tier==5)
        {
            win=true;
        }
    }
    
    public void moveProjectiles()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            for(int c=0;c<grid.getNumCols();c++)
            {
                Location curr=new Location(r,c);
                if(projs[r][c]!=null && !projs[r][c].isMoved())
                {
                    if(c==grid.getNumCols()-1) //Last row = disappear
                    {
                        infos[r][c]=null;
                        projs[r][c]=null;
                        grid.setImage(curr,null);
                    }
                    else if(enemies[r][c+1]!=null) //Enemy in front
                    {
                        enemies[r][c+1].reduceHP(projs[r][c].dmg());
                        if(projs[r][c].effect().equals("snow")){
                            int chance=(int)(Math.random()*100)+1;
                            if(chance>=80)
                                enemies[r][c+1].setFrozenStatus(true);
                        }
                        projs[r][c]=null;
                        infos[r][c]=null;
                        grid.setImage(curr,null);
                    }
                    else if(plants[r][c+1]!=null) //Plant in front
                    {
                        infos[r][c]=null;
                        grid.setImage(curr,null);
                        projs[r][c+1]=projs[r][c];
                        projs[r][c+1].setMove(true);
                        projs[r][c]=null;
                    }
                    else //Nothing in front
                    {
                        projs[r][c+1]=projs[r][c];
                        projs[r][c+1].setMove(true);
                        projs[r][c]=null;
                        if(plants[r][c]==null){
                            infos[r][c+1]=infos[r][c];
                            infos[r][c]=null;
                            grid.setImage(curr, null);
                        }
                        else{
                            infos[r][c+1]=new Profile("pea.png","peaSEL.png");
                        }
                        if(enemies[r][c+1]==null && plants[r][c+1]==null)
                            grid.setImage(new Location(r,c+1), infos[r][c+1].getOg());
                    }
                }
            }
        }
        for(int r=0;r<grid.getNumRows();r++)
            for(int c=0;c<grid.getNumCols();c++)
                if(projs[r][c]!=null)
                    projs[r][c].setMove(false);
    }
    //Projectile Method Varying Intervals
    public void detection()
    {
        for(int row=0;row<grid.getNumRows();row++)
        {
            boolean peaPresent=false;
            boolean enemyPresent=false;
            for(int c=0;c<grid.getNumCols();c++){
                if(plants[row][c]!=null)
                    peaPresent=true;
                else if(enemies[row][c]!=null)
                    enemyPresent=true;
            }
            if(peaPresent && enemyPresent){
                shootProj(row);
                zombieBrains.play();
            }
        }
    }
    public void shootProj(int row)
    {
            for(int c=0;c<grid.getNumCols();c++)
            {
                Location curr=new Location(row,c);
                if(plants[row][c]!=null && (plants[row][c].type().equals("pea") || plants[row][c].type().equals("repeater") || plants[row][c].type().equals("gatling") || plants[row][c].type().equals("snowPea"))) //Shooting (placing a pea down)
                {
                    boolean canPlace=false;
                    int col=c+1;
                    int combinedDmg=0;
                    boolean consecutiveLeft=true;
                    for(int i=c+1;i<grid.getNumCols();i++)
                    {
                        if(enemies[row][i]!=null){
                            enemies[row][i].reduceHP(plants[row][c].dmg());
                            i=grid.getNumCols();
                        }
                        else if(enemies[row][i]==null && plants[row][i]==null)
                        {
                            canPlace=true;
                            col=i;
                            i=grid.getNumCols();
                        }
                    }
                    //COMBINING CONSECUTIVE PEA DAMAGE
                    for(int i=col-1;i>c;i--) //Going from proj to plant(that fired proj)
                    {
                        Plant currP=plants[row][i];
                        if(currP.type().equals("pea") || currP.type().equals("repeater") || currP.type().equals("gatling") || currP.type().equals("snowPea"))
                            combinedDmg+=currP.dmg();
                    }
                    for(int i=c-1;i>=0;i--) //Going from plant(that fired proj) to 0 --- stops when isn't consecutive shooters
                    {
                        Plant currP=plants[row][i];
                        if(currP!=null && (currP.type().equals("pea") || currP.type().equals("repeater") || currP.type().equals("gatling") || currP.type().equals("snowPea")))
                            combinedDmg+=currP.dmg();
                        else
                            i=-1;
                    }
                    if(canPlace)
                    {
                        if(plants[row][c].type().equals("snowPea")){
                            infos[row][col]=new Profile("frozenPea.png","frozenPeaSEL.png");
                            grid.setImage(new Location(row,col), "frozenPea.png");
                            projs[row][col]=new Projectile(plants[row][c].dmg(),plants[row][c].power());
                        }
                        else{
                            infos[row][col]=new Profile("pea.png","peaSEL.png");
                            grid.setImage(new Location(row,col), "pea.png");
                            projs[row][col]=new Projectile(plants[row][c].dmg(),plants[row][c].power());
                        }
                        projs[row][col].combineDmg(combinedDmg);
                    }
                }
            }
    }
    //HELPER METHOD
    public void scrollRow(int row)
    {
        for(int c=0;c<grid.getNumCols();c++)
        {
            Location curr=new Location(row,c);
            Enemy foe=enemies[row][c];
            if(foe!=null) //Enemy present (only thing that needs to move
            {
                if(c==0) //At end
                {
                    grid.setImage(curr, null);
                    lost=true;
                    c=grid.getNumCols();
                }
                else if(plants[row][c-1]!=null) //Plant in spot
                {
                    Plant victim=plants[row][c-1];
                    if(victim.HP()==0)
                    {
                        //Updating
                        plants[row][c-1]=null;
                        enemies[row][c-1]=foe;
                        enemies[row][c]=null;
                        infos[row][c-1]=new Profile(foe.ogPic(),foe.selPic());
                        infos[row][c]=null;
                        
                        grid.setImage(curr, null);
                        grid.setImage(new Location(row,c-1), foe.ogPic());
                    }
                }
                else if(enemies[row][c-1]!=null) //Enemy in spot
                {
                    Enemy foe2=enemies[row][c-1];
                    if(foe2.HP()>foe.HP()){//DOESN'T COVER ULTRAS
                        grid.setImage(new Location(row,c-1), foe2.ogPic());
                        grid.setImage(curr,null);
                        
                        enemies[row][c]=null;
                        infos[row][c]=null;
                        foe2.buffHP(foe.HP());
                        foe2.buffAtk(foe.dmg());
                    }
                    else{
                        grid.setImage(new Location(row,c-1),foe.ogPic());
                        grid.setImage(curr, null);
                        
                        enemies[row][c]=null;
                        enemies[row][c-1]=foe;
                        infos[row][c]=null;
                        infos[row][c-1]=new Profile(foe.ogPic(),foe.selPic());
                        foe.buffHP(foe2.HP());
                        foe.buffAtk(foe2.dmg());
                    }
                }
                else //Nothing in spot
                {
                    if(!foe.isFrozen()){
                        enemies[row][c-1]=foe;
                        enemies[row][c]=null;
                        infos[row][c-1]=new Profile(foe.ogPic(),foe.selPic());
                        infos[row][c]=null;
                        grid.setImage(curr,null);
                        grid.setImage(new Location(row,c-1),foe.ogPic());
                        zombieBrains.play();
                    }
                    foe.setFrozenStatus(false);
                }
            }
        }
    }
    public void scrollLeft()
    {
        for(int r=0;r<grid.getNumRows();r++)
            scrollRow(r);
    }
    
    public void attackingZombies()
    {
        for(int r=0;r<grid.getNumRows();r++)
            for(int c=0;c<grid.getNumCols();c++){
                if(c!=0 && plants[r][c-1]!=null && enemies[r][c]!=null && !enemies[r][c].isFrozen())
                {
                    Plant victim=plants[r][c-1];
                    Enemy attacker=enemies[r][c];
                    victim.reduceHP(attacker.dmg());
                    zombieMunch.play();
                }
            }
    }
    
    public int getScore()
    {
        int score=timesGet-timesAvoid;
        if(score<0)
            score=0;
        
        return score;
        //int score=msElapsed/1000;
    }
    
    public void updateTitle()
    {
        String selectLife="";
        String selectType="";
        String selectDmg="";
        if(enemies[userRow][userCol]!=null) 
        {
            selectLife=""+enemies[userRow][userCol].HP();
            selectType=enemies[userRow][userCol].name()+" HP:";
            selectDmg="        Damage:"+enemies[userRow][userCol].dmg();
        }
        else if(plants[userRow][userCol]!=null)
        {
            selectLife=""+plants[userRow][userCol].HP();
            selectType=plants[userRow][userCol].name()+" HP:";
            selectDmg="        Damage:"+plants[userRow][userCol].dmg();
        }
        
        board.changeSun(sun);
        board.changeLife(selectLife);
        board.changeType(selectType);
        board.changeDamage(selectDmg);
        
        grid.setTitle("Sun: "+sun+"        "+selectType+selectLife+selectDmg);
        //"Score:  " + getScore()+ "    "+
    }
    
    public boolean isGameOver()
    {
        return lost || win;
    }
    
    public static void main(String[] args)
    {
        Game game = new Game();
    }
}