package edu.truman.barala;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class FlyingObject extends MouseAdapter implements ActionListener, Runnable
{
   public static FlyingObject flyingObject;
   
   public final int WIDTH = 1350;
   public final int HEIGHT = 825;
   public Renderer renderer;
   public Random rand;
   public int ticks;
   public int yMotion;
   public boolean gameOver;
   public boolean started = false;
   public int score;
   public KeyInput inputKey;
   public ArrayList <Rectangle> columns;
   
   private static final int PLAYERSHAPE_WIDTH = 60;
   private static final int PLAYERSHAPE_HEIGHT = 15;
   private Thread thread;
   
   public Rectangle playerShape;
   
   public FlyingObject()
   {
    JFrame jframe = new JFrame();
    Timer timer = new Timer(22, this);
    thread = new Thread(this, "Display");
    thread.start();
    
   
    inputKey = new KeyInput();
    renderer = new Renderer();
    rand = new Random();
    
    jframe.add(renderer);
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jframe.setTitle("Flying Object");
    jframe.addMouseListener(this);
    jframe.setSize(WIDTH, HEIGHT);
    jframe.setResizable(false);
    jframe.setVisible(true);
    jframe.addKeyListener(inputKey);
    
    
    playerShape = new Rectangle (WIDTH /2 - 15, HEIGHT / 2 - 15, PLAYERSHAPE_WIDTH, PLAYERSHAPE_HEIGHT);
    columns = new ArrayList<Rectangle>();
    
    addColumn(true);
    addColumn(true);
    addColumn(true);
    addColumn(true);
    
    timer.start();
    
    
   }
   
   public void addColumn(boolean start)
   {
      int space= 350;
      int width = 125;
      int height = 45 + rand.nextInt(325);
      if (start)
      {
      columns.add(new Rectangle(WIDTH + width + columns.size()*275, HEIGHT - height- 100, width, height));
      columns.add(new Rectangle(WIDTH + width + (columns.size()-1) * 275, 0, width, HEIGHT - height-space));
      }
      else
      {
         columns.add(new Rectangle(columns.get(columns.size()-1).x + 625, HEIGHT - height- 125, width, height));
         columns.add(new Rectangle(columns.get(columns.size()-1).x, 0, width, HEIGHT - height-space));
      }
      
   }
   public void paintColumn(Graphics g, Rectangle column)
   {
      g.setColor(Color.red.darker());
      g.fillRect(column.x, column.y, column.width, column.height);
   }
   
   public void jump()
   {
      if (gameOver)
      {
         playerShape = new Rectangle (WIDTH /2 - 10, HEIGHT / 2 - 10, PLAYERSHAPE_WIDTH, PLAYERSHAPE_HEIGHT);
         columns.clear();
         yMotion = 0;
         score = 0;
         
         addColumn(true);
         addColumn(true);
         addColumn(true);
         addColumn(true);

         gameOver = false;
      }
      
      if(!started)
      {   
         started = true;
      }
      
      else if (!gameOver)
      {
         if (yMotion > 0)
         {
            yMotion = 0;
         }
         yMotion -= 10;
      }
   }
   @Override
   public void actionPerformed(ActionEvent e)
   { 
      ticks++;
      
      if (started)
      {
      
      int speed = 10;
      
      for (int i = 0; i < columns.size(); i++)
      {
         Rectangle column = columns.get(i);
         
         column.x -= speed;
      }
      
      if (ticks % 2 ==0 && yMotion < 15)
      {
         yMotion +=2;
      }
      
      for (int i = 0; i < columns.size(); i++)
      {
         Rectangle column = columns.get(i);
         
         if (column.x + column.width <0)
         {
            columns.remove(column);
            
            if (column.y ==0)
            { 
            addColumn(false);
            }
         }

      }
      
      playerShape.y += yMotion;
      
      for(Rectangle column : columns)
      {
         if (column.y == 0 && playerShape.x + playerShape.width / 2 > column.x + column.width / 2 - 10 && playerShape.x + playerShape.width / 2 < column.x + column.width / 2 + 10 )
         {
            score++;
         }
         if (column.intersects(playerShape))
         {
            gameOver = true;
            
            if(playerShape.x <= column.x)
            {   
            playerShape.x = column.x - playerShape.width;
            }
            else
            {
               if (column.y != 0)
               {
                playerShape.y = column.y - playerShape.height;  
               }
               else if (playerShape.y < column.height)
               {
                  playerShape.y = column.height;
               }
            }
               
         }
      }
      
      if (playerShape.y > HEIGHT -100 || playerShape.y < 0)
      {
        
         gameOver = true;
      }
      if (playerShape.y + yMotion >= HEIGHT - 100)
      {
         playerShape.y = HEIGHT - 100- playerShape.height;
      }
      
      } 
      renderer.repaint();
      
   }
   
  
   
   public void repaint(Graphics g)
   {
      g.setColor(Color.black);
      g.fillRect(0, 0, WIDTH, HEIGHT);
      
      g.setColor(Color.red);
      g.fillRect(0, HEIGHT - 125, WIDTH, 125);
      
      g.setColor(Color.blue);
      g.fillRect(0, HEIGHT - 125, WIDTH, 25);
      
      g.setColor(Color.white);
      g.fillRect(playerShape.x, playerShape.y, playerShape.width, playerShape.height);
      
      for (Rectangle column : columns)
      {
         paintColumn(g, column);
      }
     
      g.setColor(Color.white); 
      g.setFont(new Font ("Comic sans ms",50 , 60));
     
      if (!started)
      {
         g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
      }
      
      if (gameOver)
      {
         g.drawString("Game Over! Click to Restart.", 100, HEIGHT / 2 - 50);
         g.drawString("Score: " + String.valueOf(score), 100, 75);
      }
      
      if (!gameOver && started)
      {
         g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
      }
      
      
   }

   public void mouseClicked(MouseEvent e)
   {
      jump();
      
   }
   

   public static void main (String [] args)
   {
      flyingObject = new FlyingObject();
      
   }

   @Override
   public void run()
   {
      // TODO Auto-generated method stub
      
   }


         
}




