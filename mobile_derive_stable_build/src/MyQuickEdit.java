import javax.microedition.lcdui.*;

public class MyQuickEdit extends Canvas implements Runnable
{
    //constants
    private static  int BK_COLOR = 0xffffff;
    private static  int LINE_COLOR = 0x000000;
    private static  int FONT_COLOR = 0x000000;
    private static  int FONT_SIZE = Font.SIZE_SMALL;
    private static  int BR = 5;
    private static  int BR2 = 20;
    private static  int INPUT_AREA_COLOR = 0xAAAAAA;
    private static  int INPUT_AREA_BORDER_COLOR = 0xeeeeeeee;

    //var
	private String str;
	private int posCur = 0;
        private int nDisplay = 0;
        private int bottonWidth = getWidth()/3-BR;
        private int bottonHeight = (getHeight()*3)/28;
        private int x[] = new int[21];
        private int y[] = new int[21];
        String buttonsNames[][] = {
            {           "","+ -","",
			"←","f(x)","→",
			"","* /","",
			"1","2","3",
			"4","5","6",
			"7","8","9",
			",","0","( )"},
			{"","^","",
			"←","0..9","→",
			"","sqrt()","",
			"abs()","ln()","exp()",
			"sin()","cos()","tg()",
			"ctg()","arcctg()","arccos()",
			"arctg()","arcctg()","X"}
        };

    public MyQuickEdit()
    {
        super();
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 7; j++){
                x[i+3*j] = BR+1 +(bottonWidth+2)*i;
                y[i+3*j] = getHeight()/4 + bottonHeight*j;
            }
    }

    public void setString( String str ){
        this.str = str;
		posCur = str.length();
    }

    public String getString(){
        return this.str;
    }

    public void paint(Graphics g){
	g.setFont( Font.getFont( Font.FACE_SYSTEM , Font.STYLE_PLAIN, FONT_SIZE) );
        flush(g);
        drawPanel(g);
	drawStr(g);
    }

    public void flush( Graphics g ){
            g.setColor( BK_COLOR );
            g.fillRect( 0, 0, getWidth(), getHeight() );
	}

    public void drawBotton( Graphics g, int x,int y,int width, int height, String str ) //draws button
    {
        g.setColor( LINE_COLOR );
        g.fillRect( x, y, width, height );
        g.setColor( BK_COLOR );
        g.fillRect( x+1, y+1, width-2, height-2 );
        g.setColor( LINE_COLOR );
        g.fillRect( x+2, y+2, width-4, height-4 );
        g.setColor( BK_COLOR );
        g.fillRect( x+4, y+4, width-8, height-8 );
        g.setColor( LINE_COLOR );
        g.drawString( str, x+width/2, y+height/2 + BR, Graphics.HCENTER | Graphics.BASELINE );
    }

    public void drawPanel( Graphics g )
    {
        for(int i=0; i < 21; i++)
			if( !buttonsNames[nDisplay][i].equals("") )
			{
				drawBotton( g, x[i],y[i], bottonWidth, bottonHeight, buttonsNames[nDisplay][i] );
			}
    }

	public void drawStr( Graphics g )
	{
		g.setColor( INPUT_AREA_COLOR );
                g.fillRoundRect(BR, BR, getWidth() - BR*2, BR2, BR2, BR2);
                g.setColor( FONT_COLOR );

		//String tmp = str.substring(0, posCur)+"_"+str.substring(posCur, str.length() );
		//g.drawString( tmp, 10, getHeight()/6, Graphics.BASELINE | Graphics.LEFT );
		g.drawString( str.substring(0, posCur), getWidth()/2, BR2,Graphics.BASELINE | Graphics.RIGHT );
		g.drawString( "_"+str.substring(posCur, str.length() ), getWidth()/2, BR2,Graphics.BASELINE | Graphics.LEFT );
	}

    public void run()
    {
	repaint();
    }

	String lastAdd;
	public void input( String add )
	{
		if( add.equals( buttonsNames[0][1] ) )
		{
			add = "+";
			if( lastAdd.equals("+") )
			{
				clear();
				add = "-";
			}
			if( lastAdd.equals("-") )
			{
				clear();
				add = "+";
			}
		}
		if( add.equals( buttonsNames[0][7] ) )
		{
			add = "*";
			if( lastAdd.equals("*") )
			{
				clear();
				add = "/";
			}
			if( lastAdd.equals("/") )
			{
				clear();
				add = "*";
			}
		}
		str = str.substring(0, posCur)+add+str.substring(posCur, str.length());
		posCur = posCur + add.length();
		repaint();
		
		lastAdd = add;
	}

	public void clear()
	{
		if( !str.equals("") && posCur > 0 )
		{
			str = str.substring(0, posCur-1)+str.substring(posCur, str.length() );
			posCur--;
			repaint();
		}
	}

    public void keyPressed( int keyCode )
    {

		if( getGameAction(keyCode) == UP )
		{
				input( buttonsNames[nDisplay][1] );
		}
		if( getGameAction(keyCode) == DOWN )
		{
				input( buttonsNames[nDisplay][7] );
		}

        if( getGameAction(keyCode) == FIRE  )
        {
			if( nDisplay == 0 ) nDisplay = 1; else
			if( nDisplay == 1 ) nDisplay = 0;
			repaint();
        }
		if( getGameAction(keyCode) == LEFT )
		{
			if( posCur > 0 ) posCur--;
			repaint();
			lastAdd = "";
		}
		if( getGameAction(keyCode) == RIGHT )
		{
			if( posCur < str.length() ) posCur++;
			repaint();
			lastAdd = "";
		}
		if( keyCode == KEY_NUM1 )
		{
			input( buttonsNames[nDisplay][9] );
		}
		if( keyCode == KEY_NUM2 )
		{
			input( buttonsNames[nDisplay][10] );
		}
		if( keyCode == KEY_NUM3 )
		{
			input( buttonsNames[nDisplay][11] );
		}
		if( keyCode == KEY_NUM4 )
		{
			input( buttonsNames[nDisplay][12] );
		}
		if( keyCode == KEY_NUM5 )
		{
			input( buttonsNames[nDisplay][13] );
		}
		if( keyCode == KEY_NUM6 )
		{
			input( buttonsNames[nDisplay][14] );
		}
		if( keyCode == KEY_NUM7 )
		{
			input( buttonsNames[nDisplay][15] );
		}
		if( keyCode == KEY_NUM8 )
		{
			input( buttonsNames[nDisplay][16] );
		}
		if( keyCode == KEY_NUM9 )
		{
			input( buttonsNames[nDisplay][17] );
		}
		if( keyCode == KEY_NUM0 )
		{
			input( buttonsNames[nDisplay][19] );
		}
		if( keyCode == Canvas.KEY_POUND )
		{
			input( buttonsNames[nDisplay][20] );
		}
		if( keyCode == Canvas.KEY_STAR )
		{
			input( buttonsNames[nDisplay][18] );
		}

    }

}
