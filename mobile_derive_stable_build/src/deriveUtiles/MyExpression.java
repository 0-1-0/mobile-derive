package deriveUtiles;

public class MyExpression
{
	//const
		static int MAX_LENGTH_EXPRESSION = 256;
	//var
        public String nameFunction = "";
		String str[] = new String[ MAX_LENGTH_EXPRESSION ];
		int lengthExpression = 0;
		static String stack[] = new String[ MAX_LENGTH_EXPRESSION ];
		int stack_size = 0;
        static double st[] = new double[ MAX_LENGTH_EXPRESSION ];
        int st_size = 0;

	public MyExpression() {}

    public double getResult( double x )
    {
        st_size = 0;
        for(int i = 0; i < lengthExpression; i++)
        {
            if( st_size > 0 && st[ st_size-1 ] == Double.NaN ) return Double.NaN;

            if( MyString.isNumber( str[i] ) )
            {
                st[ st_size++ ] = Double.parseDouble( str[i] );
            }

            if( MyString.isVerible( str[i] ) )
            {
                if( str[i].equalsIgnoreCase("x") )
                    st[ st_size++ ] = x;
            }

            if( MyString.isConstant( str[i] ) )
            {
                if( str[i].equalsIgnoreCase("e") )
                    st[ st_size++ ] = MyMath.E;
                if( str[i].equalsIgnoreCase("pi") )
                    st[ st_size++ ] = MyMath.PI;
            }

            if( str[i].equals("--") )
            {
                st[ st_size-1 ] = - st[ st_size-1 ];
            }

            if( str[i].equals("+") )
            {
                st[ st_size-2 ] = st[ st_size-2 ] + st[ st_size-1 ];
                st_size--;
            }
            if( str[i].equals("-") )
            {
                st[ st_size-2 ] = st[ st_size-2 ] - st[ st_size-1 ];
                st_size--;
            }
            if( str[i].equals("*") )
            {
                st[ st_size-2 ] = st[ st_size-2 ] * st[ st_size-1 ];
                st_size--;
            }
            if( str[i].equals("/") )
            {
                st[ st_size-2 ] = st[ st_size-2 ] / st[ st_size-1 ];
                st_size--;
            }

            if( str[i].equals("^") )
            {
                st[ st_size-2 ] = MyMath.pow(st[ st_size-2 ], st[ st_size-1 ]);
                st_size--;
            }

            if( MyString.isFunction( str[i] ) && st_size == 0 ) return Double.NaN;

            if( MyString.isAbs( str[i] ) )
            {
                st[ stack_size-1 ] = MyMath.abs( st[ st_size-1 ] );
            }

            if( MyString.isSqrt( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.sqrt( st[ st_size-1 ] );
            }

            if( MyString.isSin( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.sin( st[ st_size-1 ] );
            }

            if( MyString.isCos( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.cos( st[ st_size-1 ] );
            }

            if( MyString.isTan( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.tg( st[ st_size-1 ] );
            }

            if( MyString.isCotan( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.ctg( st[ st_size-1 ] );
            }

            if( MyString.isArcSin( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.asin( st[ st_size-1 ] );
            }

            if( MyString.isArcCos( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.acos( st[ st_size-1 ] );
            }

            if( MyString.isArcTan( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.atg( st[ st_size-1 ] );
            }

            if( MyString.isArcCotan( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.actg( st[ st_size-1 ] );
            }

            if( MyString.isExp( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.exp( st[ st_size-1 ] );
            }

            if( MyString.isLn( str[i] ) )
            {
                st[ st_size-1 ] = MyMath.ln( st[ st_size-1 ] );
            }

        }

        return st[0];
    }

	public boolean setExpression( String s )
	{
		this.nameFunction = s;

		int m = MyString.toArray( "("+s+")", str );

        m = reductionToMathMode( str );

		lengthExpression = 0;
		stack_size = 0;
        for(int i = 0; i < m; i++ )
        {
            if( str[i].equals("(") )
            {
                stack[ stack_size++ ] = str[i];
            }

            if( str[i].equals(")") )
            {
                while( stack_size >= 2 && !stack[ stack_size-1 ].equals("(") )
                {
                    str[ lengthExpression++ ] = stack[ stack_size-1 ];
                    stack_size--;
                }
                if( stack_size > 0 && stack[ stack_size-1 ].equals("(") )
                {
                    stack_size--;
                } else return false;
            }

            if( MyString.isNumber( str[i] ) || MyString.isVerible( str[i] ) || MyString.isConstant( str[i] ) )
            {
                str[ lengthExpression++ ] = str[i];
            }
            //the all operators are left-associate
            if( MyString.isFunction( str[i] ) || MyString.isOperator( str[i].charAt(0) ) )
            {
                while( priority( stack[ stack_size-1 ] ) >= priority( str[i] ) )
                {
                    str[ lengthExpression++ ] = stack[ stack_size-1 ];
                    stack_size--;
                }
                stack[ stack_size++ ] = str[i];
            }

        }

		return (stack_size == 0) && ( lengthExpression > 0 );
	}

    public int reductionToMathMode( String[] str )
    {
        int str_size = 0;
        
        for(int i = 0; str[i].length() > 0; i++)
        {
            if( (str[i].equals("-") || str[i].equals("+")) && (0 < i) )
            {
                if( str[i-1].equals("(") ||
                    str[i-1].equals("-") ||
                    str[i-1].equals("+") ||
                    str[i-1].equals("*") ||
                    str[i-1].equals("/") ||
                    str[i-1].equals("^") )
                {
                    str[i] = str[i]+str[i];
                }
            }

            str[ str_size++ ] = str[i];
        }

        return str_size;
    }
    
    int priority( String s )
    {
        if( s.equals("--") || s.equals("++") ) return 4;
        if( s.equals("^") ) return 3;
        if( s.equals("*") || s.equals("/") ) return 2;
        if( s.equals("+") || s.equals("-") ) return 1;
        if( MyString.isFunction( s ) ) return 5;
        return 0;
    }
}
