import javax.microedition.lcdui.*;
import deriveUtiles.*;

public class DrawFormCanvas extends Canvas implements Runnable
{
	//type
		class MyGraphicFunction
		{
			MyExpression myFunction = new MyExpression();
			double f[] = new double[getWidth()];
			boolean enable;
			int color;
		};
    //const
		int BK_COLOR = 0xffffff;
		int GRID_COLOR = 0x000000;
		int AXES_COLOR = 0x000000;
		int FONT_COLOR = 0x000000;
		int FONT_SIZE = Font.SIZE_SMALL;
		int ZOOM = 2;
		int MAX_ZOOM = 4;
		int MIN_ZOOM = 16;
		int LENGTH_VECTOR_HEAD = 5;
		int HEIGHT_DIVISION = 2;
		int RADIUS_POINT = 5;
		int PRECISION = 1000;
		double EPS = 1e-8;
	//var
		MyGraphicFunction function[] = new MyGraphicFunction[ MainMidlet.COUNT_FUNCTIONS ];
		//math
		double x0 = 0;
		double y0 = 0;
		double max_x = 3;
		double widthGrid = getWidth() / 6;
		double nGrid = 5;
		boolean isDraw[] = new boolean[ MainMidlet.myOptions.length+1 ];

    public DrawFormCanvas()
    {
        super();
		for(int i=0; i<MainMidlet.COUNT_FUNCTIONS; i++)
		{
			function[i] = new MyGraphicFunction();
		}
		if( 1 <= MainMidlet.COUNT_FUNCTIONS )
			function[0].color = 0xff0000;
		if( 2 <= MainMidlet.COUNT_FUNCTIONS )
			function[1].color = 0x00ff00;
		if( 3 <= MainMidlet.COUNT_FUNCTIONS )
			function[2].color = 0x0000ff;
    }

	public void setFunction( int i, String str )
	{
		function[i].enable = function[i].myFunction.setExpression( str );
	}

	public void makeFunctions()
	{
		//make fucntions
		for(int i = 0; i < MainMidlet.COUNT_FUNCTIONS; i++)
			if( function[i].enable )
			{
				for(int x = 0; x < getWidth(); x++)
				{
					double X = x0 + ( ((x - (getWidth()/2))*max_x) / (getWidth()/2) );
					function[i].f[x] = function[i].myFunction.getResult(X);
				}
			}
	}
    
    public void paint(Graphics g)
    {
        g.setFont( Font.getFont( Font.FACE_SYSTEM , Font.STYLE_PLAIN, FONT_SIZE) );
        
		flush(g);

		if( isDraw[0] )
			drawGrid(g);

		if( isDraw[1] )
			drawAxes(g);

		if( isDraw[2] )
			drawDivisionValue(g);

		makeFunctions();
		drawFunctions(g);

        if( isDraw[4] )
            drawPointsP(g);

		if( isDraw[5] )
			drawNameOfFunctions(g);
                
    }

	public void flush(Graphics g)
	{
		g.setColor( BK_COLOR );
        g.fillRect( 0, 0, getWidth(), getHeight() );
	}

	public void drawGrid(Graphics g)
	{
		g.setColor( GRID_COLOR );

		for(int x = getWidth()/2; x > 0; x -= widthGrid)
			for(int y = 0; y < getHeight(); y += nGrid)
			{
				g.drawLine(x, y, x, y);
			}

		for(int x = getWidth()/2; x < getWidth(); x += widthGrid)
			for(int y = 0; y < getHeight(); y += nGrid)
			{
				g.drawLine(x, y, x, y);
			}

		for(int y = getHeight()/2; y > 0; y -= widthGrid)
			for(int x = 0; x < getWidth(); x+= nGrid)
			{
				g.drawLine(x, y, x, y);
			}

		for(int y = getHeight()/2; y < getHeight(); y += widthGrid)
			for(int x = 0; x < getWidth(); x+= nGrid)
			{
				g.drawLine(x, y, x, y);
			}
	}

	public void drawAxes( Graphics g )
	{
		g.setColor(AXES_COLOR);
		//Horizontal
			//line
			g.drawLine( 0, getHeight()/2, getWidth(), getHeight()/2 );
			g.drawLine( getWidth()/6, getHeight()/2-HEIGHT_DIVISION, getWidth()/6, getHeight()/2+HEIGHT_DIVISION);
			g.drawLine( getWidth()-getWidth()/6, getHeight()/2-HEIGHT_DIVISION, getWidth()-getWidth()/6, getHeight()/2+HEIGHT_DIVISION);
			//vector head
			g.drawLine(  getWidth(), getHeight()/2, getWidth()-LENGTH_VECTOR_HEAD, getHeight()/2-LENGTH_VECTOR_HEAD );
			g.drawLine(  getWidth(), getHeight()/2, getWidth()-LENGTH_VECTOR_HEAD, getHeight()/2+LENGTH_VECTOR_HEAD );
		//Vertical
			//line
			g.drawLine( getWidth()/2, 0, getWidth()/2, getHeight() );
			g.drawLine( getWidth()/2-HEIGHT_DIVISION, getHeight()/2-getWidth()/3, getWidth()/2+HEIGHT_DIVISION, getHeight()/2-getWidth()/3);
			g.drawLine( getWidth()/2-HEIGHT_DIVISION, getHeight()/2+getWidth()/3, getWidth()/2+HEIGHT_DIVISION, getHeight()/2+getWidth()/3);
			//vector head
			g.drawLine( getWidth()/2, 0, getWidth()/2-LENGTH_VECTOR_HEAD, 0+LENGTH_VECTOR_HEAD );
			g.drawLine( getWidth()/2, 0, getWidth()/2+LENGTH_VECTOR_HEAD, 0+LENGTH_VECTOR_HEAD );
	}

	public void drawDivisionValue( Graphics g )
	{
		g.setColor(FONT_COLOR);
		//center
		//drawPoint( g, x0,y0, Graphics.TOP | Graphics.HCENTER );
		//horizontal
		drawPoint( g, x0-(max_x*2)/3, y0, Graphics.TOP | Graphics.HCENTER );
		drawPoint( g, x0+(max_x*2)/3, y0, Graphics.TOP | Graphics.HCENTER );
		//vectical
		drawPoint( g, x0,y0-(max_x*2)/3, Graphics.TOP | Graphics.HCENTER );
		drawPoint( g, x0,y0+(max_x*2)/3, Graphics.TOP | Graphics.HCENTER );
	}

	public int Xtox( double X )
	{
		return (int)( (getWidth()/2) +  ((X-x0)*(getWidth()/2) / max_x ) );
	}

	public int Ytoy( double Y )
	{
		return (int)( (getHeight()/2) - ((Y-y0)*(getWidth()/2) / max_x ) );
	}

	public double xtoX( int x )
	{
		return x0 + ( ((x - (getWidth()/2))*max_x) / (getWidth()/2) );
	}

	public void drawPoint( Graphics g, double X, double Y, int STYLE )
	{
		int x = Xtox( X );
		int y = Ytoy( Y );
		X = Math.floor(X*PRECISION) / PRECISION;
		Y = Math.floor(Y*PRECISION) / PRECISION;
		g.drawString("("+X+","+Y+")", x, y, STYLE);
	}

	public void drawFunctions( Graphics g )
	{
		for(int i = 0; i < MainMidlet.COUNT_FUNCTIONS; i++)
			if( function[i].enable )
			{
				g.setColor( function[i].color );
				int old_y,y;

				old_y = Ytoy( function[i].f[0] );
				for(int x = 1; x < getWidth(); x++ )
				{
					y = Ytoy( function[i].f[x] );

					if(( 0<function[i].f[x-1] && function[i].f[x]<0 ) ||
                       ( function[i].f[x-1]<0 && 0<function[i].f[x] ) )
					{
						old_y = y;
						continue;
					}

					if( Double.isNaN( function[i].f[x-1] ) )
					{
						if( !Double.isNaN( function[i].f[x] ) )
						{
							g.drawLine(x, y, x, y);
						}
					} else
					{
						if( !Double.isNaN( function[i].f[x] ) )
						{
							g.drawLine( x-1, old_y, x, y);
						}
					}

					old_y = y;
				}
			}
	}

	public void drawPointsP( Graphics g )
	{
		int segment = 0;
		int last_function = -1;

		for(int x = 1; x < getWidth(); x++ )
		{
			boolean islastDraw = false;

		    for(int i = 0; i < MainMidlet.COUNT_FUNCTIONS; i++ )
			if( function[i].enable )
			{
				int count_points = 1;
				int color = 0;

			    for(int j = i+1; j < MainMidlet.COUNT_FUNCTIONS; j++)
				if( function[j].enable )
					if(  (function[i].f[x] - function[j].f[x])*( function[i].f[x-1] - function[j].f[x-1] ) < 0  )
					{
						if(  Math.abs( (function[i].f[x] - function[j].f[x])*( function[i].f[x-1] - function[j].f[x-1] ) ) <= max_x/1e+3  )
						{
							count_points = count_points + 1;
							color |= (function[i].color | function[j].color)&0x888888;
						}
					}
					else
					if( (function[i].f[x] - function[j].f[x])*( function[i].f[x-1] - function[j].f[x-1] ) <= max_x/1e+17  )
					{
							count_points = count_points + 1;
							color |= (function[i].color | function[j].color)&0x888888;
					}
					
				if( count_points > 1 )
				{
					islastDraw = true;
					segment += 1;
					last_function = i;

					int y = Ytoy( function[i].f[x] );

					if( isDraw[3] )
					{
						g.setColor(color);
						g.fillArc( x-RADIUS_POINT/2, y-RADIUS_POINT/2, RADIUS_POINT, RADIUS_POINT, 0, 360 );
					}
					
					if( segment == 1 && isDraw[4] )
					{
						g.setColor( FONT_COLOR );
						drawPoint( g, xtoX(x), function[i].f[x], Graphics.TOP | Graphics.HCENTER );
					}
				}
			}//for all functions
			if( !islastDraw  )
			{
				if( segment >= 5 && isDraw[4] )
				{
					g.setColor( FONT_COLOR );
					drawPoint( g, xtoX(x-1), function[last_function].f[x-1], Graphics.TOP | Graphics.LEFT );
				}
				segment = 0;
			}

		}//for x
	}

	public void drawNameOfFunctions( Graphics g )
	{
		int y = 10;
		for(int i = 0; i < MainMidlet.COUNT_FUNCTIONS; i++ )
            if( function[i].enable )
            {
    			g.setColor( function[i].color );
    			g.drawString("y="+function[i].myFunction.nameFunction, 20, y, Graphics.TOP | Graphics.LEFT );
    			y += 20;
    		}
	}

	public void zoom_in()
	{
		if( max_x > 3.0/MIN_ZOOM )
		{
			max_x /= ZOOM;
			widthGrid *= 2;
			if( widthGrid*2 > getWidth() )
			{
				widthGrid /= 8;
			}
			repaint();
		}
	}

	public void zoom_out()
	{
		if( max_x < 3.0*MAX_ZOOM )
		{
			max_x *= ZOOM;
			widthGrid /= 2;
			if( widthGrid < getWidth()/12 )
			{
				widthGrid *= 8;
			}
			repaint();
		}
	}

	public void moveTo(double x, double y)
	{
		x0 = x;
		y0 = y;
		repaint();
	}

	public void run()
	{
	}

	public void keyPressed( int keyCode )
	{
		if( keyCode == KEY_NUM1 )
		{
	    	zoom_in();
		}
		if( keyCode == KEY_NUM3 )
		{
			zoom_out();
		}
			//move up
		if( keyCode == KEY_NUM2 || getGameAction(keyCode) == UP )
		{
			moveTo( x0, y0 + max_x/2 );
		}
		//move down
		if( keyCode == KEY_NUM8 || getGameAction(keyCode) == DOWN )
		{
			moveTo( x0, y0 - max_x/2 );
		}
		//move left
		if( keyCode == KEY_NUM4 || getGameAction(keyCode) == LEFT )
		{
			moveTo( x0 - max_x/2, y0 );
		}
		//move right
		if( keyCode == KEY_NUM6 || getGameAction(keyCode) == RIGHT )
		{
			moveTo( x0 + max_x/2, y0 );
		}

		if( keyCode == KEY_NUM0 )
		{
			max_x = 3;
			widthGrid = getWidth() / 6;
			moveTo( 0, 0 );
		}
    }

}