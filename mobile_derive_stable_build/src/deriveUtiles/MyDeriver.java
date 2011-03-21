/*

Messy crappy code which is very old by now ! Be warned !

DONT ask me why it's implemented like it is, it actually started out as a JavaScript back in 1997
so that may explain some of the seemingly weird implementations. Also I was doing lots of stuff in Scheme
in school back then so that's why the expression is turned into a Scheme like structure, for example:

1+cos(x) => ( + 1 ( cos x ) )

*/


/**

	Derive.java

	<p>

	Copyright 1998, 1999, 2000, 2001, 2002, 2003, 2004 Patrik Lundin, patrik@lundin.info, 
	http://www.lundin.info
	
	<p>
 
	This file is part of com.javathings.math.

	<p>

    com.javathings.math is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

	<p>

    com.javathings.math is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

	<p>

    You should have received a copy of the GNU General Public License
    along with com.javathings.math; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

	<p>
	<hr>
	<p>

	<b>
	A Java class for performing symbolic differentiation of a mathematical expression given as a string.
	</b>

	<p>

	Example:

	<p>

	<xmp>
	Derive d = new Derive();
	String ans[] = d.diff( "cos( x-y )" , "x;y" );
	System.out.println( ans[ 0 ] + " , " );
	System.out.println( ans[ 1 ] );
	</xmp>

	This will print the following: -1*sin(x-y) , sin(x-y)

	@author Patrik Lundin, patrik@lundin.info, http://www.lundin.info

*/

package deriveUtiles;
import java.util.*;


public class MyDeriver{



/**
* 
* Constructor.
*
**/

public MyDeriver()
{
  super(); 
}


/**
*
* Array of all valid operators.
*
**/
  
private static final String allowedops[] = 
{
"^","+","-","/","*",
"cos","sin","exp","ln",
"tan","acos","asin",
"atan","cosh","sinh",
"tanh","sqrt","cotan",
"acotan"
};



/**
*
* Array of operators that takes two arguments.
*
**/

private static final String twoargops[] = 
{
"^","+",
"-","/",
"*"
};



/**
*
* The max operator length.
*
* In this case "acotan" => 6
*
**/

private static final int maxoplength = 6;


/**
*
* Vector that will contain a list of all variables
* in the argument expression after a call to diff(..)
*
* Access the variables with the accessor method getVariables
*
**/

private Vector variables = new Vector( 50 );



/**
*
* Defaultvariable, used if no variables are given
* and no variables can be found in the expression.
* ( i.e the argument is a constant )
* 
**/

private String defaultvar = "x";


/**
*
* Default StringBuffer length 
*
* This will be set to the length of the expression
* by eval.
*
**/

private int sb_init = 50;


/**
*
* Returns the first element in a prefix expression.
* <p>
* @param   str a prefix expression.
* @return  the first element in str.
* <p>
* Ex:<br>
* If str is the expression ( exp x )<br>
* car( str ) => "exp"<br>
* If str is the expression ( * 3 x )<br>
* car( str ) => "*"
* <p> 
* Why is it called car ? :-)
* <p>
**/

private String
car( String str )
{
  int len = str.length();
  int end = 0;
  int i = 2;
  int count = 0;
 
  if( str.charAt(2) == '(' )
  {
    while( i < str.length() )
    {
      if( str.charAt(i) == '(' ){
        count++;
      }else if( str.charAt(i) == ')' ){
        count--;
      }

      if( count == 0 ){
        end=i;
        break;
      }

      i++;
    }

    return( str.substring( 2 , end + 1 ) );
  }
  
  return( str.substring( 2 , str.indexOf( " " , 2 )) );
}







/**
*
* Returns the rest of a prefix expression.
* <p>
* @param   str a prefix expression.
* @return  the rest of the prefix expression when the first element has been removed.
* <p>
* Ex:<br>
* If str is the expression ( ^ x 3 )<br>
* cdr( str ) => "( x 3 )"<br>
* If str is the expression ( * 3 ( + 2 y ) )<br>
* cdr( str ) => "( 3 ( + 2 y ) )"
* <p>  
* Why is it called cdr ? :-)
* <p>
**/
private String cdr( String exp )
{
  StringBuffer buf = new StringBuffer();
  buf.append( '(' ).append( exp.substring( car( exp ).length() + 2 , exp.length() ) );
  return( buf.toString() );
}


/**
*
* Returns the first argument from a prefix expression.
* <p>
* @param   str prefix expression.
* @return  the argument.
* <p>
* Ex:<br>
* If str is the expression "( + 2 x )"<br>
* arg1( str ) => "2"<br>
* If str is the expression "( + ( * x y ) 5 )<br>
* arg1( str ) => "( * x y )"
* <p>
**/
private String arg1( String str )
{
  return( car( cdr( str ) ) );
}

/**
*
* Returns the second argument from a prefix expression.
* <p>
* @param   str prefix expression.
* @return  the argument.
* <p>
* Ex:<br>
* If str is the expression "( + 2 x )"<br>
* arg2( str ) => "x"<br>
* If str is the expression "( + 5 ( - x 3 ) )<br>
* arg2( str ) => "( - x 3 )"
* <p>
**/
private String arg2( String str )
{
  return( car( cdr( cdr( str ) ) ) );
}


/**
* Checks the syntax of exp. Throws an java.lang.Exception if the syntax is wrong.
* <p>
*
* @param  exp  string to be examined
* 
* <p>
* Only letters a-z, numbers 0-9, the symbols '(' ')' '.' and<br>
* one character operators are allowed in exp.<br>
* Syntax will also check for non matching brackets and some other cases<br>
* for example: 3cos(x) and 3+*x etc..
* <p>
**/
private void Syntax( String exp ) throws java.lang.Exception
{
  int i = 0;
  String op = null;
  String nop = null;  

  if(  ! MatchParant(exp) )
  {
    throw new java.lang.Exception("Non matching brackets");
  }

  int l = exp.length();

  while( i < l )
  {
    try{
      if( ( op = getOp( exp , i ) ) != null)
      {
        nop = getOp( exp, op.length() + i );
        if(  isTwoArgOp( nop ) && ! nop.equals("+") && ! nop.equals("-") )
        {
          throw new java.lang.Exception( "Syntax error near -> " + exp.substring( i , exp.length() ) );
        }
      }
      else if(  ! isAlpha( exp.charAt(i) ) && ! isConstant( exp.charAt(i) ) && ! isAllowedSym( exp.charAt(i) ) )
      {
        throw new java.lang.Exception( "Syntax error near -> " + exp.substring( i , exp.length() ));
      }

    }catch( StringIndexOutOfBoundsException e ){
      // just ignore.
    }

    i++;
    
    nop = op = null;
  }

  return;
}


/**
* Adds support for juxtaposition
*
* Inserts a multiplication symbol in between a number
* juxtaposed with a parenthesis or a variable.<br>
*
* @param   exp infix expression.
* @param   index where to start search for the operator in exp.
* @return  exp with the multiplication symbol inserted, if needed.
* <p>
* Ex:<br>
* If str is "34cos(2x)"<br>
* putMult( str ) => "34cos(2x)"<br>
* putMult( str ) => "34cos(2*x)"<br>
* putMult( str ) => "34*cos(2x)"<br>
* <p>  
**/
private String putMult( String exp )
{
  int i = 0, p = 0;
  String op = null;
  StringBuffer str = new StringBuffer( exp );

  int l = exp.length();

  while( i < l )
  {	
    try{

      if( ( op = getOp( exp , i )) != null && ! isTwoArgOp( op ) 
	&& isAlpha( exp.charAt( i - 1 ) ) )  
      {
        // case: variable jp one-arg-op , xcos(x)
	str.insert( i + p , '*' );
	p++;
      }
      else if( isAlpha( exp.charAt( i ) ) && isConstant( exp.charAt( i - 1 ) ) )  
      {
        // case: const jp variable or one-arg-op , 2x , 2tan(x)
	str.insert( i + p , '*' );
	p++;
      }
      else if( exp.charAt( i ) == '(' && isConstant( exp.charAt( i - 1 ) ) )
      {  
	// case: "const jp ( expr )" , 2(3+x)
	str.insert( i + p , '*' );
	p++;
      }
      else if( isAlpha( exp.charAt( i ) ) && exp.charAt( i - 1 ) == ')' )
      { 
	// case: ( expr ) jp variable or one-arg-op , (2-x)x , (2-x)sin(x)
	str.insert( i + p , '*' );
	p++;
      }      
      else if( exp.charAt( i ) == '('  && exp.charAt( i - 1 ) == ')' ) 
      { 
       // case: ( expr ) jp  ( expr ) , (2-x)(x+1) , sin(x)(2-x) 
       str.insert( i + p , '*' ); 
       p++; 
      }
      else if( exp.charAt( i ) == '('  && isAlpha( exp.charAt( i - 1 ) ) && backTrack( exp.substring( 0 , i ) ) == null ) 
      { 
       // case: var jp  ( expr ) , x(x+1) , x(1-sin(x))
       str.insert( i + p , '*' ); 
       p++; 
      }
    }catch( StringIndexOutOfBoundsException e ){}

    if( op != null ) 
    {
	i += op.length();
    }
    else
    {
    	i++;
    }

    op = null;  
  }

  return str.toString();
}

/**
* matches brackets in exp.
* <p>
*
* @param  exp  string expression to check.
* @return boolean, true if brackets match false otherwise.
* <p>
*
**/
private boolean MatchParant(String exp)
{
  int count = 0;
  int i = 0;
  int len = exp.length();

  for( i = 0; i < len ; i++)
  {

    if(exp.charAt(i) == '(')
      count++;
    else if(exp.charAt(i) == ')')
      count--;

  }

  return( count == 0 );
}

/**
* Constructs a prefix expression: ( <i>op a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @return b   second argument.
* <p>
*
**/
private String list( String op,String a,String b ){
  return( "( " + op + " " + a + " " + b + " )" );
}

/**
* Constructs a prefix expression: ( <i>op a</i> )
* <p>
*
* @param  op  operator.
* @return a   argument.
* <p>
*
**/
private String list( String op,String a ){
  return("( " + op + " " + a + " )");
}


/**
*
* Checks to see if x is a valid variable.
* <p>
* @param   exp string to check.
* @return  boolean, true or false.
**/
private boolean isVariable( String s )
{
  int i = 0;
  int len = s.length();

  if( isAllNumbers( s ) )
    return false;

  for( i = 0; i < len ; i++ )
  {
    if( getOp( s , i ) != null || isAllowedSym( s.charAt( i )) )
      return false; 
  }
  return true;
}


/**
*
* Checks to see if exp is a single letter a-z or A-Z
* <p>
* @param   exp string to check.
* @return  boolean, true or false.
**/
private boolean isAlpha( String exp )
{ 
  if( (exp == null) || (exp.length() > 1) )
    return false;
	
  char ch = exp.charAt( 0 );
  if( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
    return true;

  return false;
}


/**
*
* Checks to see if ch is a letter a-z or A-Z
* <p>
* @param   ch character to check.
* @return  boolean, true or false.
**/
private boolean isAlpha( char ch )
{
  return( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') );
}


/**
*
* Checks to see if sym is '(' ')' or '.'
* <p>
* @param   sym chracter to check.
* @return  boolean, true or false.
**/
private boolean isAllowedSym( char sym )
{
  return( ( sym == '(' ) || ( sym == ')' ) || ( sym == '.' ) );
}


/**
*
* Checks to see if ch is numeric.
* <p>
* @param   ch character to check.
* @return  boolean, true or false.
**/
private boolean isConstant( char ch )
{
  return( Character.isDigit( ch ) );
}


/**
*
* Checks to see if exp is numeric.
* <p>
* @param   exp string to check.
* @return  boolean, true or false.
**/
private boolean isConstant( String exp )
{ 
  try{  
    if( Double.isNaN( Double.valueOf( exp ).doubleValue() ) )
      return false;
  }catch( Exception e ){
    return false;
  }
  
  return true;
}


/**
*
* Checks to see if str is numeric.<br>
* <p>
* @param   str string to check.
* @return  boolean, true or false.
**/
private boolean isAllNumbers( String str )
{
    char ch;
    int i = 0, l = 0;
    boolean dot = false;
    
    ch = str.charAt( 0 );
    
    if(  ch == '-' || ch == '+'  ) i = 1;
   
    l = str.length();

    while( i < l  )
    {
      ch = str.charAt( i );
      if( ! ( Character.isDigit( ch ) || ( ch == '.' && ! dot ) ) )     
		return false;    
  
      if(  ch == '.'  ) dot = true;
	  
      i++;
    }
    
    return true;
}


/**
*
* Checks to see if a and b are valid variables and equal.
* <p>
* @param   a string to check and compare with b.
* @param   b string to check and compare with a.
* @return  boolean, true or false.
**/
private boolean isSameVariable( String a , String b )
{
  return( isVariable(a) && isVariable(b) && a.equalsIgnoreCase(b) );
}

/**
*
* Checks to see if exp is a valid operator.
* <p>
* @param   s string to check.
* @return  boolean, true or false.
**/
private boolean isOperator( String s )
{
  for(int i = 0; i < MyDeriver.allowedops.length ; i++ )
    if( MyDeriver.allowedops[i].equalsIgnoreCase( s ) ) return true;
  return false;
}

/**
*
* Checks to see if op is an operator that takes two arguments.
* <p>
* @param   op string to check.
* @return  boolean, true or false.
**/
private boolean isTwoArgOp( String op )
{
  for(int i = 0 ; i < MyDeriver.twoargops.length ; i++ )
    if( MyDeriver.twoargops[i].equalsIgnoreCase(op) ) return true;
  return false;
}


/**
*
* Checks to see if b equals 1.0 .
* <p>
* @param   b string to check.
* @return  boolean, true or false.
* <p>
* isConstant should be called before this method
**/
private boolean isOne( String b )
{
  return( Double.valueOf(b).doubleValue() == 1.0 );
}

/**
*
* Checks to see if b equals 0.0 .
* <p>
* @param   b string to check.
* @return  boolean, true or false.
* <p>
* isConstant should be called before this method
**/
private boolean isZero( String b )
{
  return( Double.valueOf(b).doubleValue() == 0.0 );
}

/**
*
* Checks to see if a is a mathematical integer.
* <p>
* @param   a value to check.
* @return  boolean, true or false.
* <p>
* Ex: isInteger(4.0) => true, isInteger(4.3) => false
**/
private boolean isInteger( double a )
{
  return( ( a - (int)a ) == 0 );
}

/**
*
* Checks to see if a is even.
* <p>
* @param   a value to check.
* @return  boolean, true or false.
* <p>
* Ex: isEven(4) => true, isEven(5) => false
**/
private boolean isEven( int a )
{
  return( a/2 == 0);
}

private boolean isEven( double a )
{
  return( isInteger( a/2 ) );
}

private boolean isEven( float a )
{
  return( isInteger( a/2 ) );
}


/**
*
* Checks the first element in a prefix expression<br>
* too se if it's the operator '+'
* <p>
* @param   str prefix expression to check.
* @return  boolean, true or false.
* <p>
* Ex:<br>
* if str is the expression ( + 3 x )<br>
* isSum( str ) => true<br>
* If str is the expression ( * 3 x )<br>
* isSum( str ) => false
* <p>  
**/
private boolean isSum( String str )
{
  return( car( str ).equals( "+" ) );
}

private boolean isSubtraction( String str )
{
  return( car( str ).equals( "-" ) );
}

private boolean isProduct( String str )
{
  return( car( str ).equals( "*" ) );
}

private boolean isDivision(String str)
{
  return( car( str ).equals( "/" ) );
}

private boolean isSquareroot( String exp )
{
  return( car( exp ).equals( "sqrt" ) );
}

private boolean isCosine( String exp )
{
  return( car( exp ).equals( "cos" ) );
}

private boolean isSine( String exp )
{
  return( car( exp ).equals( "sin" ) );
}

private boolean isTan( String exp )
{
  return( car( exp ).equals( "tan" ) );
}

private boolean isAtan( String exp )
{
  return( car( exp ).equals( "atan" ) );
}

private boolean isAcos( String exp )
{
  return( car( exp ).equals( "acos" ) );
}

private boolean isAsin( String exp )
{
  return( car( exp ).equals( "asin" ) );
}


private boolean isSinhyp( String exp )
{
  return( car( exp ).equals( "sinh" ) );
}

private boolean isCoshyp( String exp )
{
  return( car( exp ).equals( "cosh" ) );
}

private boolean isTanhyp( String exp )
{
  return( car( exp ).equals( "tanh" ) );
}

private boolean isLn( String exp )
{
  return( car( exp ).equals( "ln" ) );
}

private boolean isPower( String exp )
{
  return( car( exp ).equals( "^" ) );
}

private boolean isE( String exp )
{
  return( car( exp ).equals( "exp" ) );
}

private boolean isCotan( String exp )
{
  return( car( exp ).equals( "cotan" ) );
}
private boolean isAcotan( String exp )
{
  return( car( exp ).equals( "acotan" ));
}






/**
* Constructs the prefix expression: ( exp <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* <p>
*
**/
private String makeE( String a )
{
  if( ! isConstant(a) && ! isVariable(a) && isLn(a) )
    return( arg1(a) );
  else if( isConstant(a) && isZero(a) )
    return "1";
  
  return(list("exp",a));
}






/**
* Constructs the prefix expression: ( + <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @param  b   second argument
* <p>
*
**/

private String 
makeSum( String a , String b )
{
  if( isConstant(a) && isConstant(b) ){
    return( ( new StringBuffer().append( Double.valueOf(a).doubleValue() + Double.valueOf(b).doubleValue() ) ).toString() );
  }else if( isConstant(a) && isZero(a) ){
    return b;
  }else if( isConstant(b) && isZero(b) ){
    return a;
  }else if( a.equals(b) ){
    return( makeProduct( "2" ,a ) );
  }
  else if( ! isConstant(a) && ! isVariable(a) )
  {
    if( isConstant(b) ){
		// error here
      return( makeSumSimplifyConstant( a , b ) );
	  //
    }else if( isVariable(b) ){
      return( makeSumSimplifyVariable( a , b ));     
    }else{
      return( makeSumSimplifyTwoExpressions( a , b ));
    }
  }else if( ! isConstant(b) && ! isVariable(b) ){
    if( isConstant(a) ){
      return( makeSumSimplifyConstant( b , a ) );
    }else if( isVariable(a) ){
      return( makeSumSimplifyVariable( b , a ) );     
    }else{
      return( makeSumSimplifyTwoExpressions( b , a ) );
    }
  }

  return( list( "+" , a , b ) );
}






/**
* Constructs the prefix expression: ( sqrt <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeSquareroot( String a )
{
  if( isConstant(a) && isEven( Math.sqrt( Double.valueOf(a).doubleValue() )))
  {
    return( ( new StringBuffer().append( Math.sqrt( Double.valueOf(a).doubleValue() ) ) ).toString() );  
  }
  else if( ! isConstant(a) && ! isVariable(a) &&  isPower(a) )
  {
    if( isEven( Double.valueOf( arg2(a) ).doubleValue() ) )
    {
      return( makePower( arg1(a), ( new StringBuffer().append( Double.valueOf( arg2(a) ).doubleValue() / 2 ) ).toString() ));
    }
  }
  
  return( list( "sqrt" , a ) );
}







/**
* Constructs the prefix expression: ( * <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @param  b   second argument
* <p>
*
**/

private String 
makeProduct( String a , String b )
{
  if( isConstant(a) && isConstant(b) ){
      return( ( new StringBuffer().append( Double.valueOf(a).doubleValue() * Double.valueOf(b).doubleValue() ) ).toString() );
  }else if( isConstant(a) && isZero(a) ){
      return "0";
  }else if( isConstant(a) && isOne(a) ){
      return b;
  }else if( isConstant(b) && isZero(b) ){
      return "0";
  }else if( isConstant(b) && isOne(b) ){
      return a;
  }else if( a.equals(b) ){
      return( makePower( a , "2" ) );
  }
  else if( ! isConstant(a) && ! isVariable(a) && ! isConstant(b) && ! isVariable(b) )
  {
        return( makeProductSimplifyTwoExp( a , b ) );
  }
  else if( ! isConstant(a) && ! isVariable(a) )
  {
    if( isConstant(b) ){
      return( makeProductSimplifyConstant( a , b ) );
    }else if( isVariable( b ) ){
      return( makeProductSimplifyVariable( a , b ) );
    }   
  }else if( ! isConstant(b) && ! isVariable(b) ){
    if( isConstant(a) ){
      return( makeProductSimplifyConstant( b , a ) );
    }else if( isVariable( a ) ){
      return( makeProductSimplifyVariable( b , a ) );
    }
  }

  return(list("*",a,b));
}







/**
* Constructs the prefix expression: ( / <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @param  b   second argument
* <p>
*
**/

private String 
makeDivision( String a , String b )
{
  if( isConstant(a) && isConstant(b) ){
    if( Double.valueOf(b).doubleValue() != 0 && isInteger( Double.valueOf(a).doubleValue() / Double.valueOf(b).doubleValue() ) )
      return( ( new StringBuffer().append( Double.valueOf(a).doubleValue() / Double.valueOf(b).doubleValue() ) ).toString() );
  }else if( isConstant(a) && isZero(a) ){
    return "0";
  }else if( isConstant(b) && isOne(b) ){
    return a;
  }else if( a.equals(b) ){
    return "1";
  }else if( ! isConstant(a) && ! isVariable(a) ){
    if( isSum(a) || isSubtraction(a) ){
      return( makeDivisionSimplifyExprThruVar( a , b ) );
    }else if( isDivision(a) ){
      if( isVariable(b) || isConstant(b) ){ 
        return( makeDivision( arg1(a) , makeProduct( b , arg2(a) ) ) );   
      }else if( isDivision(b) ){
        return( makeDivision( makeProduct( arg1(a) , arg2(b) ) , makeProduct( arg2(a) , arg1(b) ) ) );
      }
    }
  }


  return(list("/",a,b));
}







/**
* Constructs the prefix expression: ( - <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @param  b   second argument
* <p>
*
**/

private String 
makeSubtraction( String a , String b )
{
  if(isConstant(a) && isConstant(b) )
	return( ( new StringBuffer().append( Double.valueOf(a).doubleValue() - Double.valueOf(b).doubleValue() ) ).toString() );  
  else if( isConstant(a) && isZero(a) )  
    return( makeProduct( "-1" , b ) );
  else if( isConstant(b) && isZero(b) )
    return a;
  else if( a.equals(b) )
    return "0";
  else if( ! isConstant(b) && ! isVariable(b) && (isConstant(a) || isVariable(a)) ){
      return( makeSubtractionSimplifyConstantVariableArg1( a , b ) ); 
  }else if( ! isConstant(a) && ! isVariable(a) && (isConstant(b) || isVariable(b)) ){
      return( makeSubtractionSimplifyConstantVariableArg2( a , b ) );
  }
  else if( ! isConstant(a) && ! isVariable(a) && ! isConstant(b) && ! isVariable(b) ){
      return( makeSubtractionSimplifyTwoExp( a , b ) );
  }

  return( list( "-" , a , b ) );
}







/**
* Constructs the prefix expression: ( ^ <i>a b</i> )
* <p>
*
* @param  op  operator.
* @param  a   first argument.
* @param  b   second argument
* <p>
*
**/

private String 
makePower( String a , String b )
{

  if( isConstant(a) && isConstant(b) )
  {
    if( isOne(a) || isZero(b) ){
      return "1";
    }else if( isOne(b) ){
      return a;
    }
  }else if( isConstant(b) && isZero(b) ){
    return "1";
  }else if( isConstant(b) && isOne(b) ){
    return a;
  }else if( ! isConstant(a) && ! isVariable(a) && isPower(a) ){
    if( isConstant(b) && isConstant(arg2(a))){
      return( makePower( arg1(a) , makeProduct( arg2(a) , b ) ) );
    }
  }

  return( list( "^" , a , b ) );
}






/**
* Constructs the prefix expression: ( sin <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/
private String makeSine( String a )
{
  if( ! isVariable(a) && ! isConstant(a) && isAsin(a) )
  {
    return(arg1(a));
  }
  else if( ! isVariable(a) && ! isConstant(a) && isAcos(a) )
  {
    return( makeSquareroot( makeSubtraction( "1" , makePower( arg1(a) , "2" ) ) ) );
  }

  return( list( "sin" , a ) );
}







/**
* Constructs the prefix expression: ( cos <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeCosine( String a )
{
  if( ! isVariable(a) && ! isConstant(a) && isAcos(a) )
  {   
    return(arg1(a));
  }
  else if( ! isVariable(a) && ! isConstant(a) && isAsin(a) )
  {
    return( makeSquareroot( makeSubtraction( "1" , makePower( arg1(a) , "2" ) ) ) );
  }
  
  return( list( "cos" , a ) );
}






/**
* Constructs the prefix expression: ( tan <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeTan( String a )
{

  if( ! isVariable(a) && ! isConstant(a) && isAtan(a) )
  {
    return( arg1(a) );
  }
  else if( ! isVariable(a) && ! isConstant(a) && isAcotan(a) )
  {
    return( makeDivision( "1" , arg1(a) ) );
  }

  return( list( "tan" , a ) );
}







/**
* Constructs the prefix expression: ( ln <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeLn( String a )
{
  if( ! isVariable(a) && ! isConstant(a) && isE(a) )
  {
    return(arg1(a));
  }

  return( list( "ln" , a ) );
}






/**
* Constructs the prefix expression: ( sinh <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeSinhyp( String a )
{
  return( list( "sinh" , a ) );
}






/**
* Constructs the prefix expression: ( cosh <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeCoshyp( String a )
{
  return( list( "cosh" , a ) );
}






/**
* Constructs the prefix expression: ( tanh <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeTanhyp( String a )
{
  return( list( "tanh" , a ) );
}






/**
* Constructs the prefix expression: ( cotan <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeCotan(String a)
{
  if( ! isVariable(a) && ! isConstant(a) && isAcotan(a) )
  {
    return( arg1( a ) );
  }

  return( list( "cotan" , a ) );
}






/**
* Constructs the prefix expression: ( acotan <i>a</i> )
* <p>
*
* @param  op  operator.
* @param  a   argument.
* <p>
*
**/

private String 
makeAcotan( String a )
{
  if( ! isVariable(a) && ! isConstant(a) && isCotan(a) )
  {
    return( arg1( a ) );
  }

  return( list( "acotan" , a ) );
}








/** 
* 
* Help method for makeSum.<br>
* makes some simplifications.<br>
* Ex: ( + 5  ( + 3 x ) ) => ( + 8 x )
* <p>
**/

private String 
makeSumSimplifyConstant( String a , String b )
{
  double tmp = 0.0; 
  if( isSum(a) )
  {
    if( isConstant( arg1(a) ) )
    {
      tmp = Double.valueOf(b).doubleValue() + Double.valueOf( arg1(a) ).doubleValue();
      if( tmp >= 0 )
      {
        return( makeSum( ( new StringBuffer().append( tmp ) ).toString() , arg2(a) ) );
      }
      else
    {
      return( makeSubtraction( arg2(a) , ( new StringBuffer().append( -1 * tmp ) ).toString() ) );
      }
    }
    else if( isConstant( arg2( a ) ) )
    {
      tmp = Double.valueOf(b).doubleValue() + Double.valueOf( arg2( a ) ).doubleValue();
      if( tmp >= 0 )
      {
        return( makeSum( ( new StringBuffer().append( tmp ) ).toString() , arg1(a) ) );
      }
      else
      {
        return( makeSubtraction( arg1(a), ( new StringBuffer().append( -1 * tmp ) ).toString() ) );
      }
    }
  }
  else if( isSubtraction(a) )
  {
    if( isConstant( arg1(a) ) )
    {
      tmp = Double.valueOf(b).doubleValue() + Double.valueOf( arg1(a) ).doubleValue();
     // if( tmp >= 0 ){
        return( makeSubtraction( ( new StringBuffer().append( tmp ) ).toString() , arg2(a) ) );
     // }
     // else
      //{
      //  return( makeSubtraction( arg2(a) , ( new StringBuffer().append( -1 * tmp ) ).toString() ) );
      //}
    }
    else if( isConstant( arg2(a) ) )
    {
      tmp = Double.valueOf(b).doubleValue() - Double.valueOf( arg2(a) ).doubleValue();
      if( tmp >= 0 )
      {
        return( makeSum( ( new StringBuffer().append( tmp ) ).toString() ,arg1(a) ));
      }
      else
      {
      return( makeSubtraction( arg1(a) , ( new StringBuffer().append( -1 * tmp ) ).toString() ) );  
      }
    }
  }

  return( list( "+" , a , b ) );
}







/** 
* 
* Help method for makeSum.<br>
* makes some simplifications.<br>
* Ex: ( + x  ( + 3 x ) ) => ( + 3 ( * 2 x ) )<br>
* <p>
**/


private String 
makeSumSimplifyVariable( String a , String b )
{
  if( isSum(a) )
  {
    if( isVariable( arg1(a) ) && isSameVariable( b , arg1(a) ) ){
      return( makeSum( makeProduct( "2" ,b ) , arg2(a) ) );
    }else if( isVariable( arg2(a) ) && isSameVariable( b , arg2(a) ) ){
      return( makeSum( makeProduct( "2" , b ) , arg1(a) ) );
    } 
  }
  else if( isSubtraction(a) )
  {
    if( isVariable( arg1(a) ) && isSameVariable( b , arg1(a) ) ){
      return( makeSum( makeProduct( "2" , b ) , arg2(a) ) );
    }else if( isVariable( arg2( a ) ) && isSameVariable( b , arg2(a) ) ){
      return( arg1(a) );
    }
  }
  else if( isProduct(a) )
  {
    if( isConstant( arg1(a) ) && arg2(a).equals(b) ){
        return( makeProduct( makeSum( "1" , arg1(a) ) , b ) );
    }else if( isConstant( arg2(a) ) && arg1(a).equals(b) ){
        return( makeProduct( makeSum( "1" , arg2(a) ) ,b ) );
    }
  }

  return( list( "+" , a , b ) );
}






/** 
* 
* Help method for makeSum.<br>
* makes some simplifications.<br>
* Ex: ( + ( + 2 x )  ( + 2 x ) ) => ( + 4 ( * 2 x ) )
* <p>
**/


private String 
makeSumSimplifyTwoExpressions( String a , String b )
{
  if( isSum(a) && isSum(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(a) ) , makeSum( arg2(a) , arg2(b) ) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSum( makeProduct( "2" , arg2(a) ) , makeSum( arg1(a) , arg1(b) ) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(a) ) , makeSum( arg2(a) , arg1(b) ) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(b) ) , makeSum( arg1(a) , arg2(b) ) ) );
    }
  }else if( isSum(a) && isSubtraction(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(a) ) , makeSubtraction( arg2(a) , arg2(b) ) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSum( arg2(a) , arg1(b) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(b) ) , makeSubtraction( arg1(a) , arg2(b) ) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSum( arg1(a) , arg1(b) ) );
    }
  }else if( isSum(a) && isProduct(b) ){
    if( isConstant( arg1(b) ) ){
      if( arg1(a).equals( arg2(b) ) ){
        return( makeSum( arg2(a) , makeProduct( makeSum( "1" , arg1(b) ) , arg1(a) ) ) );
      }else if( arg2(a).equals( arg2(b) ) ){
        return( makeSum( arg1(a) , makeProduct( makeSum( "1" , arg1(b) ) , arg2(a) ) ) );
      }
    }else if( isConstant( arg2(b) ) ){
      if( arg1(a).equals( arg1(b) ) ){
        return( makeSum( arg2(a) , makeProduct( makeSum( "1" , arg2(b) ) , arg1(a) ) ) );
      }else if( arg2(a).equals( arg1(b) ) ){
        return( makeSum( arg1(a) , makeProduct( makeSum( "1" , arg2(b) ) , arg2(a) ) ) );
      }
    }
  }else if( isSubtraction(a) && isSum(b) ){
    return( makeSumSimplifyTwoExpressions( b , a ) );
  }else if( isSubtraction(a) && isSubtraction(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSubtraction( makeProduct( "2" , arg1(a) ) , makeSum( arg2(a) , arg2(b) ) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSubtraction( arg1(b) , arg2(a) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSubtraction( arg1(a) , arg2(b) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSubtraction( makeSum( arg1(a) , arg1(b) ) , makeProduct( "2" , arg2(a) ) ) );
    }
  }else if( isSubtraction(a) && isProduct(b) ){
    if( isConstant( arg1(b) ) ){
      if( arg1(a).equals( arg2(b) ) ){
        return( makeSubtraction( makeProduct( makeSum( "1" , arg1(b) ) , arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg2(b) ) ){
        return( makeSum( makeProduct( makeSubtraction( arg1(b) , "1" ) , arg2(a) ) , arg1(a) ) );
      }
    }else if( isConstant( arg2(b) ) ){
      if( arg1(a).equals( arg1(b) ) ){
        return( makeSubtraction( makeProduct( makeSum( "1" , arg2(b) ) , arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg1(b) ) ){ 
        return( makeSum( makeProduct( makeSubtraction( arg2(b) , "1" ) , arg2(a) ) , arg1(a) ) );
      }
    }
  }

  return( list( "+" , a , b ) );    
}






/** 
* 
* Help method for makeProduct.<br>
* makes some simplifications.<br>
* Ex: ( * ( + 3 x ) x ) => ( + ( * 3 x ) ( * x x ) )
* <p>
**/


private String 
makeProductSimplifyVariable( String a , String b )
{ 
  if( isSum(a) )
  {
    return( makeSum( makeProduct( b , arg1(a) ) , makeProduct( b , arg2(a) ) ) ); 
  }
  else if( isSubtraction(a) ) 
  {
    return( makeSubtraction( makeProduct( b , arg1(a) ) , makeProduct( b , arg2(a) ) ) );
  }
  else if( isPower(a) )
  {
    if( b.equals( arg1(a) ) )
    {
      return( makePower( b , makeSum( "1" , arg2(a) ) ) );
    }
  }

  return( list( "*" , b , a ) );  
}







/** 
* 
* Help method for makeProduct.<br>
* makes some simplifications.<br>
* Ex: ( * ( + 3 x ) ( - 3 x ) ) => ( - ( ^ 3 2 ) ( ^ x 2 ) )
* <p>
**/


private String 
makeProductSimplifyTwoExp( String a , String b )
{
  if( isSum(a)
    && isSubtraction(b)
    && arg1(a).equals( arg1(b) )
    && arg2(a).equals( arg2(b) ) )
  { 
    return( makeSubtraction( makePower( arg1(a) , "2" ) , makePower( arg2(a) , "2" ) ) );
      
  }else if( isSum(a)
    && isSubtraction(b)
    && arg1(a).equals( arg2(b) )
    && arg2(a).equals( arg1(b) ) )
  { 
    return( makeSubtraction( makePower( arg2(a) , "2" ) , makePower( arg1(a) , "2" ) ) ); 

  }else if( isSubtraction(a)    
    && isSum(b)
    && arg1(a).equals( arg1(b ))
    && arg2(a).equals( arg2(b) ) )
  { 
    return( makeSubtraction( makePower( arg1(a) , "2" ) , makePower( arg2(a) , "2" ) ) ); 

  }else if( isSubtraction(a)
    && isSum(b)
    && arg1(a).equals(arg2(b))
    && arg2(a).equals(arg1(b)) )
  { 
    return( makeSubtraction( makePower( arg1(a) , "2" ) , makePower( arg2(a) , "2" ) ) ); 

  }

  return( list( "*" , a , b ) );
}







/** 
* 
* Help method for makeProduct.<br>
* makes some simplifications.<br>
* Ex: ( * ( + 3 x ) 5 ) => ( + 15 ( * 5 x ) )
* <p>
**/


private String 
makeProductSimplifyConstant( String a , String b )
{
  double temp = Double.valueOf( b ).doubleValue();

  if( isSum(a) )
  {    
    if( temp < 0 ){
      return( makeSubtraction( makeProduct( b , arg1(a) ) , makeProduct( ( new StringBuffer().append( -1 * temp ) ).toString() , arg2(a) ) ) );
    }else if( temp > 0 ){
      return( makeSum( makeProduct( b, arg1(a) ) , makeProduct( b , arg2(a) ) ) );
    }else{
      return "0";
    } 
  }
  else if( isSubtraction(a) )
  {
    if( temp > 0 ){
      return( makeSubtraction( makeProduct( b , arg1(a) ) , makeProduct( b , arg2(a) ) ) );
    }else if( temp < 0 ){
      return( makeSum( makeProduct( b , arg1(a) ) , makeProduct( ( new StringBuffer().append( -1 * temp ) ).toString() ,arg2(a) ) ) );
    }else{
      return "0";
    }
  }
  else if( isProduct(a) )
  {
    if( isConstant( arg1(a) ) ){
      return( makeProduct( ( new StringBuffer().append( temp * Double.valueOf( arg1(a) ).doubleValue() ) ).toString() , arg2(a) ) );
    }else if( isConstant( arg2(a) ) ){
      return( makeProduct( ( new StringBuffer().append( temp * Double.valueOf( arg2(a) ).doubleValue() ) ).toString() , arg1(a)  ) );
    }
  }

  return( list( "*" , b , a ) );  
}







/** 
* 
* Help method for makeDivision.<br>
* makes some simplifications.<br>
* Ex: ( / ( + 3 x ) x ) => ( + ( / 3 x ) 1 )
* <p>
**/


private String 
makeDivisionSimplifyExprThruVar( String a , String b )
{
  if( b.equals( arg1(a) ) )
  {
    if( isSum(a) ){
      return( makeSum( "1" , makeDivision( arg2(a) , b ) ) );
    }else if( isSubtraction(a) ){
      return( makeSubtraction( "1" , makeDivision( arg2(a) , b ) ) );
    }else if( isProduct(a) ){
      return( makeDivision( arg2(a) , b ) );
    }
  }
  else if( b.equals( arg2(a) ) )
  {
    if( isSum(a) ){
      return( makeSum( makeDivision( arg1(a) , b ) , "1" ) );
    }else if( isSubtraction(a) ){
      return( makeSubtraction( makeDivision( arg1(a) , b ) , "1" ) );
    }else if( isProduct(a) ){
      return( makeDivision( arg1(a) , b ) );
    }
  }

  return( list( "/" , a , b ) );
}







/** 
* 
* Help method for makeSubtraction.<br>
* makes some simplifications.<br>
* Ex: ( - ( + 5 x ) 2 ) => ( + 3 x )
* <p>
**/


private String 
makeSubtractionSimplifyConstantVariableArg2( String a , String b )
{
if( isConstant(b) )
  {  
    if( isSum(a) ){
      if( isConstant( arg1(a) ) ){
        return( makeSum( ( new StringBuffer().append( Double.valueOf( arg1(a) ).doubleValue() 
          - Double.valueOf(b).doubleValue() ) ).toString() , arg2(a) ) );
      }else if( isConstant( arg2(a) ) ){
        return(makeSum( ( new StringBuffer().append( Double.valueOf(arg2(a)).doubleValue() 
          - Double.valueOf(b).doubleValue() ) ).toString() , arg1(a) ) );
      }
    }else if( isSubtraction(a) ){
      // The problem was here, original makeSum replaced with makeSubtraction
	  // fixed 2002-12-28
	  if( isConstant( arg1(a) ) ){
        return( makeSubtraction( ( new StringBuffer().append( Double.valueOf( arg1(a) ).doubleValue() 
          - Double.valueOf(b).doubleValue() ) ).toString() , arg2(a) ) );
      } 

	  else if( isConstant( arg2(a) ) ){
        return( makeSubtraction(arg1(a), ( new StringBuffer().append( Double.valueOf( arg2(a) ).doubleValue() 
          - Double.valueOf(b).doubleValue() ) ).toString()) );
      }//
    }
  }
  else if( isVariable(b) )
  {
    if( isSum(a) ){
      if( isVariable( arg1(a) ) && isSameVariable( b , arg1(a) ) ){
        return( arg2(a) );
      }else if( isVariable( arg2(a) ) && isSameVariable( b , arg2(a) ) ){
        return( arg1(a) );  
      }
    }else if( isSubtraction(a) ){
      if( isVariable( arg1(a) ) && isSameVariable( b , arg1(a) ) ){
        return( makeProduct( "-1" , arg2(a) ) );
      }else if( isVariable( arg2(a) ) && isSameVariable( b , arg2(a) ) ){
        return( makeSubtraction( arg1(a) , makeProduct( "2" , arg2(a) ) ) );
      }
    }else if( isProduct(a) ){
      if( isConstant( arg1(a) ) && arg2(a).equals(b) ){
        return( makeProduct( makeSubtraction( arg1(a) , "1" ) , b ) );
      }else if( isConstant( arg2(a) ) && arg1(a).equals(b) ){
        return( makeProduct( makeSubtraction( arg2(a) , "1" ) , b ) );
      }
    }
  
  }

  return( list( "-" , a , b ) );
}







/** 
* 
* Help method for makeSubtraction.<br>
* makes some simplifications.<br>
* Ex: ( - 5 ( + 2 x ) ) => ( - 3 x )
* <p>
**/

private String 
makeSubtractionSimplifyConstantVariableArg1( String a , String b )
{
  if( isConstant(a) )
  {
    if( isSum(b) ){
      if( isConstant( arg1(b) ) ){
        return( makeSubtraction( ( new StringBuffer().append( Double.valueOf(a).doubleValue()
          - Double.valueOf( arg1(b) ).doubleValue() ) ).toString() , arg2(b) ) );
      }else if( isConstant( arg2(b) ) ){
        return(makeSubtraction( ( new StringBuffer().append( Double.valueOf(a).doubleValue() 
		  - Double.valueOf( arg2(b) ).doubleValue() ) ).toString() , arg1(b) ) );
      }
    }else if( isSubtraction(b) ){
      if( isConstant( arg1( b ) ) ){
        return( makeSum( ( new StringBuffer().append(Double.valueOf(a).doubleValue() 
			- Double.valueOf( arg1(b) ).doubleValue() ) ).toString() , arg2(b) ) );
      }else if( isConstant( arg2(b) ) ){
        return(makeSubtraction( ( new StringBuffer().append( Double.valueOf(a).doubleValue()
          + Double.valueOf( arg2(b) ).doubleValue() ) ).toString() , arg1(b) ) );
      }
    }
  }
  else if( isVariable(a) )
  {
    if( isSum(b) ){
      if( isVariable( arg1(b) ) && isSameVariable( a , arg1(b) ) ){
        return( makeProduct( "-1" , arg2(b) ) );
      }else if( isVariable( arg2(b) ) && isSameVariable( a , arg2(b) ) ){
        return( makeProduct( "-1" , arg1(b) ) );
      } 
    }else if( isSubtraction(b) ){
      if( isVariable( arg1(b) ) && isSameVariable( a , arg1(b) ) ){
        return( arg2(b) );
      }else if( isVariable( arg2(b) ) && isSameVariable( a , arg2(b) ) ){
        return( makeSubtraction( arg1(b) , makeProduct( "2" , arg2(b) ) ) );
      }
    }else if( isProduct(b) ){
      if( isConstant( arg1(b) ) && arg2(b).equals(a) ){
        return( makeProduct( makeSubtraction( "1" , arg1(b) ) , a ) );
      }else if( isConstant( arg2(b) ) && arg1(b).equals(a) ){
        return( makeProduct( makeSubtraction( "1" , arg2(b) ) , a) );
      }
    }
  }
  return( list( "-" , a , b ) );
}







/** 
* 
* Help method for makeSubtraction.<br>
* makes some simplifications.<br>
* Ex: ( - ( + 5 x ) ( + 2 x ) ) => ( - 5 0 )
* <p>
**/

private String 
makeSubtractionSimplifyTwoExp( String a , String b )
{
  if( isSum(a) && isSum(b) )
  {
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSubtraction( arg2(a) , arg2(b) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSubtraction( arg2(a) , arg1(b) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSubtraction( arg1(a) , arg2(b) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSubtraction( arg1(a) , arg1(b) ) );
    }
  }else if( isSum(a) && isSubtraction(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSum( arg2(a) , arg2(b) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSum( makeProduct( "2" , arg1(a) ) , makeSubtraction( arg2(a) , arg1(b) ) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSum( arg1(a) , arg2(b) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSum( makeProduct( "2" , arg2(a) ) , makeSubtraction( arg1(a) , arg1(b) ) ) );
    }
  }else if( isSum(a) && isProduct(b) ){
    if( isConstant( arg1(b) ) ){
      if( arg1(a).equals( arg2(b) ) ){
        return( makeSum( makeProduct( makeSubtraction( "1" , arg1(b) ) ,arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg2(b) ) ){
        return( makeSum( makeProduct( makeSubtraction( "1" , arg1(b) ) , arg2(a) ) , arg1( a ) ) );
      }
    }else if( isConstant( arg2(b) ) ){
      if( arg1(a).equals( arg1(b) ) ){
        return( makeSum( makeProduct( makeSubtraction( "1" , arg2(b) ) , arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg1(b) ) ){
        return( makeSum( makeProduct( makeSubtraction( "1" , arg2(b) ) , arg2(a) ) , arg1(a) ) );
      }
    }
  }else if( isSubtraction(a) && isSum(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSubtraction( makeProduct( "-1" , arg2(a) ) , arg2(b) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSubtraction( makeProduct( "-1" , arg2(a) ) , arg1(b) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSubtraction( makeSubtraction( arg1(a) , arg2(b) ) , makeProduct( "2" , arg1(b) ) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSubtraction( makeSubtraction( arg1(a) , arg1(b) ) , makeProduct( "2" , arg2(a) ) ) );
    }
  }else if( isSubtraction(a) && isSubtraction(b) ){
    if( arg1(a).equals( arg1(b) ) ){
      return( makeSubtraction( arg2(b) , arg2(a) ) );
    }else if( arg1(a).equals( arg2(b) ) ){
      return( makeSubtraction( makeProduct( "2" , arg1(a) ) , makeSum( arg2(a) , arg1(b) ) ) );
    }else if( arg2(a).equals( arg1(b) ) ){
      return( makeSubtraction( makeSum( arg1(a) , arg2(b) ) , makeProduct( "2" , arg1(b) ) ) );
    }else if( arg2(a).equals( arg2(b) ) ){
      return( makeSubtraction( arg1(a) , arg1(b) ) );
    }
  }else if( isSubtraction(a) && isProduct(b) ){
    if( isConstant( arg1(b) ) ){
      if( arg1(a).equals( arg2(b) ) ){
        return( makeSubtraction( makeProduct( makeSubtraction( "1" , arg1(b) ) , arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg2(b) ) ){
        return( makeSubtraction( makeProduct( makeSubtraction( "-1" , arg1(b) ) , arg2(a) ) , arg1(a) ) );
      }
    }else if( isConstant( arg2(b) ) ){
      if( arg1(a).equals( arg1(b) ) ){
        return( makeSubtraction( makeProduct( makeSubtraction( "1" , arg2(b) ) , arg1(a) ) , arg2(a) ) );
      }else if( arg2(a).equals( arg1(b) ) ){
        return( makeSubtraction( makeProduct( makeSubtraction( "-1" , arg2(b) ) , arg2(a) ) , arg1(a) ) );
      }
    }
  }

  return( list( "-" , a , b ) );
}







/**
* Differentiates a prefix expression in the form ( <i>operator arg1 arg2</i> )<br>
* in regards to the variable vari.<br>
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
Derive( String exp , String vari )
{
  if( isConstant( exp ) ){
    return "0";
  }else if( isVariable( exp ) ){
    return( deriveVariable( exp , vari ) );
  }else if( isSum( exp ) )
    return( deriveSum( exp , vari ) );    
  else if( isSubtraction( exp ) )
    return( deriveSubtraction( exp , vari ) );  
  else if( isProduct( exp ) )
    return( deriveProduct( exp , vari ) );  
  else if( isDivision( exp ) )
    return( deriveDivision( exp , vari ) );   
  else if( isSquareroot( exp ) )
    return( deriveSqrt( exp , vari ) ); 
  else if( isSine( exp ) )
    return( deriveSin( exp , vari ) );
  else if( isCosine( exp ) )
    return( deriveCos( exp , vari ) );    
  else if( isTan( exp ) )
    return( deriveTan( exp , vari ) );    
  else if( isPower( exp ) )
    return( derivePower( exp , vari ) );
  else if( isLn( exp ) )
    return( deriveLn( exp , vari ) );
  else if( isE( exp ) )      
    return( deriveE( exp , vari ) );
  else if( isAtan( exp ) )
    return( deriveAtan( exp , vari ) );   
  else if( isAsin( exp ) )
    return( deriveAsin( exp , vari ) );   
  else if( isAcos( exp ) )
    return( deriveAcos( exp , vari ) );
  else if( isSinhyp( exp ) )
    return( deriveSinhyp( exp , vari ) );
  else if( isCoshyp( exp ) )
    return( deriveCoshyp( exp , vari ) );   
  else if( isTanhyp( exp ) )
    return( deriveTanhyp( exp , vari ) );
  else if( isCotan( exp ) )
    return( deriveCotan( exp , vari ) );
  else if( isAcotan( exp ) )
    return( deriveAcotan( exp , vari ) );
  else  
    return( "" ); 
}





/**
* Derives the prefix expression ( acotan <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveAcotan( String exp , String vari )
{
  return( makeDivision( makeProduct( "-1" , Derive( arg1( exp ) , vari ) ),
    makeSum( "1" , makePower( arg1( exp ) , "2" ) ) ) );
}






/**
* Derives the prefix expression ( cotan <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/


private String 
deriveCotan( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ),
    makeSubtraction( "-1" , makePower( makeCotan( arg1( exp ) ) , "2" ) ) ) );
}






/**
* Derives the prefix expression ( tanh <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/


private String 
deriveTanhyp( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ),
    makeSubtraction( "1" , makePower( makeTanhyp( arg1(exp) ) , "2" ) ) ) );
}





/**
* Derives the prefix expression ( cosh <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveCoshyp( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ) , makeSinhyp( arg1(exp) ) ) );
}





/**
* Derives the prefix expression ( sinh <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/


private String 
deriveSinhyp( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ) , makeCoshyp( arg1(exp) ) ) );
}





/**
* Derives the prefix expression ( acos <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveAcos( String exp , String vari )
{
  return( makeProduct( "-1" , makeDivision( Derive( arg1(exp) , vari ),
    makeSquareroot( makeSubtraction( "1" , makePower( arg1(exp) , "2" ) ) ) ) ) );
}





/**
* Derives the prefix expression ( asin <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveAsin( String exp , String vari )
{
  return( makeDivision( Derive( arg1(exp) , vari ),
    makeSquareroot( makeSubtraction( "1" , makePower( arg1(exp) , "2" ) ) ) ) );
}






/**
* Derives the prefix expression ( atan <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveAtan( String exp , String vari )
{
  return( makeDivision( Derive( arg1(exp) , vari ),
    makeSum( "1" , makePower( arg1(exp) , "2" ) ) ) );
}






/**
* Derives the prefix expression ( exp <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/


private String 
deriveE( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ) , makeE( arg1(exp) ) ) );
}







/**
* Derives the prefix expression ( ln <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveLn( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ) , makeDivision( "1" , arg1(exp) ) ) );
}






/**
* Derives the prefix expression ( tan <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

String 
deriveTan( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ),
    makeSum( "1" , makePower( makeTan( arg1(exp) ) , "2" ) ) ) );
}





/**
* Derives the prefix expression ( sin <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveSin( String exp , String vari )
{
  return( makeProduct( Derive( arg1(exp) , vari ),
    makeCosine( arg1(exp) ) ) );
}






/**
* Derives the prefix expression ( cos <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String 
deriveCos(String exp,String vari)
{
  return( makeProduct( Derive( arg1(exp) , vari ),
    makeProduct( "-1" , makeSine( arg1(exp) ) ) ) );
}






/**
* Derives the prefix expression ( sqrt <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String
deriveSqrt( String exp , String vari )
{
  return( makeDivision( Derive( arg1(exp) , vari ),
    makeProduct( "2" , makeSquareroot( arg1(exp) ) ) ) );
}






/**
* Derives the prefix expression ( / <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String
deriveDivision( String exp , String vari )
{
  return( makeDivision( makeSubtraction( makeProduct( arg2(exp) , Derive( arg1(exp) , vari )),
    makeProduct( arg1(exp) , Derive( arg2(exp) , vari ))) , makePower( arg2(exp) , "2" )));
}






/**
* Derives the prefix expression ( * <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String
deriveProduct( String exp , String vari )
{
  return( makeSum( makeProduct( arg1(exp) , Derive( arg2(exp) , vari ) ),
     makeProduct( Derive( arg1(exp) , vari ) , arg2(exp) ) ) );
}






/**
* Derives the prefix expression ( - <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String
deriveSubtraction( String exp , String vari )
{
  return( makeSubtraction( Derive( arg1(exp) , vari ) , Derive( arg2(exp) , vari ) ) );
}






/**
* Derives the prefix expression ( + <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/

private String
deriveSum( String exp , String vari )
{
  return( makeSum( Derive( arg1(exp) , vari ) , Derive( arg2(exp) , vari ) ) );
}






/**
* Derives the prefix expression ( ^ <i>arg1 arg2</i> ).
* <p>
*
* @param  exp  string expression with prefix notation to derive.
* @param  vari string representing the variable.
* @return derived prefix expression.
* <p>
*
**/


private String
derivePower( String exp , String vari )
{
  if( ! isConstant( arg1(exp) ) )
  {
    if( isConstant( arg2( exp ) ) )
    {
      return( makeProduct( Derive( arg1(exp) , vari ) , makeProduct( arg2(exp),
        makePower( arg1(exp) , makeSubtraction( arg2(exp) , "1" ) ) ) ) );
    }
    else
    {
      return( makeProduct( exp , makeSum( makeProduct( Derive( arg2(exp) , vari ) , makeLn( arg1(exp) ) ),
        makeProduct( Derive( makeLn( arg1(exp) ) , vari ) , arg2(exp) ) ) ) );
    }     
  }
  else
  {
    return( makeProduct( makeProduct( makeLn( arg1(exp) ) , Derive( arg2(exp) , vari ) ) , exp ) );
  }
}





/**
* Derives a variable.
* <p>
*
* @param  exp  the variable to derive.
* @param  vari string representing the current variable.
* @return 1 or 0 depending on wether exp.equals(vari) or not.
* <p>
*
**/

private String
deriveVariable( String exp , String vari )
{
  if( isSameVariable( exp , vari ) )
  {
    return "1";
  }else{
    return "0";
  }
}






/**
*
* Will force the expression exp through the constructors again.
* The effect is some additional simplification.
*
* @param exp prefix expression to simplify
*
**/

private String 
Simplify( String exp )
{
 if( isConstant(exp) )
    if( isInteger( Double.valueOf(exp).doubleValue() ) ){
      return( ( new StringBuffer().append( Double.valueOf(exp).intValue() ) ).toString() );
    }else{
      return exp;
    }
  else if( isVariable(exp) )
    return exp;
  else if( isSum(exp) )
    return( makeSum( Simplify( arg1(exp) ) , Simplify( arg2( exp ) ) ) );
  else if( isSubtraction( exp ) )
    return( makeSubtraction( Simplify( arg1( exp ) ) , Simplify( arg2( exp ) ) ) );
  else if( isProduct( exp ) )
    return( makeProduct( Simplify( arg1( exp ) ) , Simplify( arg2( exp ) ) ) );
  else if( isDivision( exp ) )
    return( makeDivision( Simplify( arg1( exp ) ) , Simplify( arg2( exp ) ) ) );
  else if( isSquareroot( exp ) )
    return( makeSquareroot( Simplify( arg1( exp ) ) ) );
  else if( isSine( exp ) )
    return( makeSine( Simplify( arg1( exp ) ) ) );
  else if( isCosine( exp ) )
    return( makeCosine( Simplify( arg1( exp ) ) ) );
  else if( isTan( exp ) )
    return( makeTan( Simplify( arg1( exp ) ) ) );
  else if( isPower( exp ) )
    return( makePower( Simplify( arg1( exp ) ) , Simplify( arg2( exp ) ) ) );
  else if( isLn( exp ) )
    return( makeLn( Simplify( arg1( exp ) ) ) );
  else if( isE( exp ) )     
    return( makeE( Simplify( arg1( exp ) ) ) );
  else if( isSinhyp( exp ) )
    return( makeSinhyp( Simplify( arg1( exp ) ) ) );
  else if( isCoshyp( exp ) )
    return( makeCoshyp( Simplify( arg1( exp ) ) ) );
  else if( isTanhyp( exp ) )
    return( makeTanhyp( Simplify( arg1( exp ) ) ) );
  else if( isCotan( exp ) )
    return( makeCotan( Simplify( arg1( exp ) ) ) );
  else if( isAcotan( exp ) )
    return( makeAcotan( Simplify( arg1( exp ) ) ) );


  return exp;
}






/**
*
* Will call simplify repeatedly with its argument.<br>
* When the returned string from simplify is unchanged<br>
* it is returned. The effect is some additional simplification.
* <p>
* @param exp prefix expression to simplify
* <p>
**/

private String 
SimplifyAsMuchAsPossible( String exp )
{
  String lastTime = "";
  String now = "";

  now = exp;

  while( true )
  {
    now = Simplify(now);
    if( lastTime.equalsIgnoreCase( now ) )
    {
      break;
    }
    lastTime = now;
  }

  return now;
}





/**
*
* returns the operator in a prefix expression
* <p>
* @param   exp prefix expression.
* @return  the operator.
* <p>
**/

private String
firstOp( String exp )
{
  return( car(exp) );
}





/**
* Converts a prefix expression to a infix.
* <p>
*
* @param  exp  string expression to convert.
* @return a infix expression.
* <p>
* Ex: ( + 5 x ) => 5+x
*
* NOTE: Some obvious improvements in efficency could be made
* if all string appending is changed to use a StringBuffer
*
**/

private String 
preToInfix( String exp )
{
  String fop,farg,sarg; 

  // fop - first operator
  // farg - first argument
  // sarg - second argument

  if( isVariable(exp) || isConstant(exp) ) return exp;

  fop = firstOp( exp );
  farg = arg1( exp );
  sarg = "";

  if( ! isTwoArgOp(fop) )
  {
    return( ( new StringBuffer().append( fop ).append( '(' ).append( preToInfix( farg ) ).append( ')' ) ).toString() );
  }
  else
  {
    sarg = arg2(exp);
  
    if( isConstant( farg ) || isVariable( farg ))
    {
      if( isConstant( sarg ) || isVariable( sarg ))
      {
        return( ( new StringBuffer().append( farg ) ).append( fop ).append( sarg ).toString() );
      }
      else
      {
        if( fop.equalsIgnoreCase("+") )
        {
            return( ( new StringBuffer().append( farg ).append( fop ).append( preToInfix(sarg) ) ).toString() );
        }
        else if( fop.equalsIgnoreCase("-") && ( isDivision(sarg) || isProduct(sarg) ) )
        {
            return(  ( new StringBuffer().append( farg ).append( fop ).append( preToInfix(sarg) ) ).toString() );
        }
        else if( fop.equalsIgnoreCase("*") && ( isPower(sarg) || isProduct(sarg) || ! isTwoArgOp(firstOp(sarg)) ) )
        {
            return(  ( new StringBuffer().append( farg ).append( fop ).append( preToInfix(sarg) ) ).toString()  );
        }
        else if(isTwoArgOp(firstOp(sarg)))
        {
            return( ( new StringBuffer().append( farg ).append( fop ).append( '(' ).append( preToInfix(sarg) ).append( ')' ) ).toString() );
        }
        else
        {
            return( ( new StringBuffer().append( farg ).append( fop ).append( preToInfix(sarg) ) ).toString() );
        } 
      }

    }
    else if( isConstant( sarg ) || isVariable( sarg ))
    {
      if( fop.equalsIgnoreCase("+") || fop.equalsIgnoreCase("-") ){
        return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( sarg ) ).toString() );
      }else if( isTwoArgOp( firstOp( farg ) ) ){
        return( (new StringBuffer().append( '(' ).append( preToInfix(farg) ).append( ')' ).append( fop ).append( sarg ) ).toString() );
      }else{
        return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( sarg ) ).toString() );
      }

    }else{  

      if( fop.equalsIgnoreCase("+") ){
        return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( preToInfix( sarg ) ) ).toString() );
      }else if( fop.equalsIgnoreCase("-") ){
        if( isProduct(sarg) || isDivision(sarg) ){
          return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( preToInfix( sarg ) ) ).toString()  );
        }else{
          return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( '(' ).append( preToInfix(sarg) ).append( ')' ) ).toString() );
        }
      }else if( isTwoArgOp( firstOp(farg) ) && isTwoArgOp( firstOp(sarg) )){
        return( (new StringBuffer().append( '(' ).append( preToInfix(farg) ).append( ')' ).append( fop ).append( '(' ).append( preToInfix(sarg) ).append( ')' ) ).toString() );
      }else if(isTwoArgOp(firstOp(farg)) && ! isTwoArgOp(firstOp(sarg))){
        return( (new StringBuffer().append( '(' ).append( preToInfix(farg) ).append( ')' ).append( fop ).append( preToInfix(sarg) ) ).toString() );
      }else if(isTwoArgOp(firstOp(sarg)) && ! isTwoArgOp(firstOp(farg))){
        return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( '(' ).append( preToInfix(sarg) ).append( ')' ) ).toString()  );
      }else{
        return( (new StringBuffer().append( preToInfix(farg) ).append( fop ).append( preToInfix( sarg ) ) ).toString() );
      } 
    }
  }

}






/**
* Converts a infix expression to a prefix.
* <p>
*
* @param  exp  string expression to convert.
* @return a prefix expression.
* <p>
* Ex: 5*x^2+3*x => ( + ( * 5 ( ^ x 2 ) ) ( * 3 x ) )
*
* The method also calls storeVars() with all variables
* found so they will be stored.
* Use the method getVars() to retrieve the list of variables.
*
* NOTE: Some obvious improvements in efficency could be made
* if all string appending is changed to use a StringBuffer
*
**/

private String 
InToPrefix(String exp) throws java.lang.Exception
{
  int i,ma;
  String str,farg,sarg,fop;
  
  farg = sarg = fop = str = "";
  ma = i = 0;

  if( exp == "" ){
    throw new java.lang.Exception("Wrong number of arguments to operator");
  }
  else if(isVariable( exp ) )
  {
    // store variablename for use with diff(String exp)
    storeVars( exp );
    return exp;
  }
  else if(isAllNumbers( exp ))
  {
  	return exp;
  }
  else if(exp.charAt( 0 ) == '(' && (( ma = match( exp,0)) == ( exp.length() - 1)))
  {
    return( InToPrefix( exp.substring( 1 , ma )));
  }

  while( i < exp.length() )
  {
    if( ( fop = getOp(exp,i)) !=null )
    {
      if( isTwoArgOp(fop) )
      {
        if(fop.equalsIgnoreCase("+") || fop.equalsIgnoreCase("-"))
        {       
          if( str == "" ){      
            str = "0";
          }

          farg = argToPlusOrMinus( exp,i + 1);
        }
        else if(fop.equalsIgnoreCase("*") || fop.equalsIgnoreCase("/") )
        {
          if( str == "" ){
            throw new java.lang.Exception("Wrong number of arguments to operator");
          }

          farg = argToAnyOpExcept( exp,i + 1,"^");
        }
        else
        {
          if( str == "" ){
            throw new java.lang.Exception("Wrong number of arguments to operator");
          }

          farg = arg(exp,i + fop.length());
        }
      
        str = "( " + fop + " " + str + " " + InToPrefix(farg) + " )";
        i += fop.length() + farg.length();

      }else{

        farg = arg( exp,i + fop.length());
        str += "( " + fop + " " + InToPrefix(farg) + " )";
        i += fop.length() + farg.length();
      }

    }else{

      farg = arg( exp,i);
      fop = getOp(exp,i + farg.length());
    
      if(fop ==  null ){
        throw new java.lang.Exception("Missing operator");
      }
    
      if(isTwoArgOp(fop)){

        if(fop.equalsIgnoreCase("+") || fop.equalsIgnoreCase("-")){
          sarg = argToPlusOrMinus( exp,i + 1 + farg.length());
        }else if(fop.equalsIgnoreCase("*") || fop.equalsIgnoreCase("/") ){
          sarg = argToAnyOpExcept( exp,i + 1 + farg.length(),"^");
        }else{
          sarg = arg( exp,i + fop.length() + farg.length());
        }
        
        str += "( " + fop + " " + InToPrefix(farg) + " " + InToPrefix(sarg) + " )";
        i += farg.length() + sarg.length() + fop.length();
      }else{
        str += "( " + fop + " " + InToPrefix(farg) + " )";
        i += fop.length() + farg.length();
      }
    }
    
    farg = fop = sarg = "";
  }
  

  return str;
}






/**
*
* Returns an argument from the infix expression exp,<br>
* starting at index and ending at the beginning of<br>
* next operator or at the end of exp.
* If '(' appears in the argument, the matching ')' will<br>
* be searched for.
*
* @param   exp infix expression.
* @param   index the index to start from
* @return  the argument.
* <p>
* Ex:<br>
* If exp is the expression "2+3*cos(x+2)"<br>
* arg( exp , 0 ) => "2"<br>
* arg( exp , 4 ) => "cos(x+2)"
* <p>
**/

private String
arg(  String exp, int index )
{
  int ma, i;
  String op = null;
  StringBuffer str = new StringBuffer();

  i = index;
  ma = 0;

  while( i < exp.length() )
  {
    if( exp.charAt( i ) == '('){
      ma = match( exp, i );
      str.append( exp.substring( i , ma + 1 ));
      i = ma + 1;
    }else if( ( op = getOp( exp, i )) != null ){
      if( str.length() != 0 &&  ! isTwoArgOp( backTrack( str.toString() ) )){
        return str.toString();
      }
      str.append( op );
      i += op.length();
    }else{
      str.append( exp.charAt( i ) );
      i++;
    }
  }

  return str.toString();
}





/**
*
* Returns an argument from the infix expression exp,<br>
* starting at index and ending at the beginning of<br>
* next operator, if that operator is not equal to except,<br>
* or ending at the end of exp.<br> 
* If '(' appears in the argument, the matching ')' will<br>
* be searched for.
* <p>
* @param   exp infix expression.
* @return  the argument.
* Ex:<br>
* If exp is the expression "3*x^2+5"<br>
* argToAnyOpExcept( exp , 2 ) => "x^2"<br>
* <p>
**/

private String
argToAnyOpExcept(  String exp, int index, String except )
{

  int ma, i;
  String op = null;
  StringBuffer str = new StringBuffer();

  i = index;
  ma = 0;

  while( i < exp.length() )
  {
    if( exp.charAt( i ) == '(' ){
      ma = match(exp,i);
      str.append( exp.substring(i , ma + 1));
      i = ma + 1;
    }else if( ( op = getOp( exp, i )) != null ){
      if( str.length() != 0 &&  ! isTwoArgOp( backTrack( str.toString() ) ) && ! op.equalsIgnoreCase( except )){
        return str.toString();
      }
      str.append( op );
      i += op.length();
    }else{
      str.append( exp.charAt( i ));
      i++;
    }
  }

  return str.toString();
}






/**
*
* Returns an argument from the infix expression exp,<br>
* starting at index and ending at the beginning of<br>
* any of the operators '+' or '-' or at the end of exp.<br>
* If '(' appears in the argument, the matching ')' will<br>
* be searched for.
* <p>
* @param   exp infix expression.
* @return  the argument.
* Ex:<br>
* If exp is the expression "2+3*cos(x+2)"<br>
* argToPlusOrMinus( exp , 2 ) => "3*cos(x+2)"<br>
* argToPlusOrMinus( exp , 0 ) => "2"
* <p>
**/

private String 
argToPlusOrMinus(  String exp , int index )
{
  int ma = 0;
  int i = index;
  int end = 0;
  String op = "";
  String str = "";

  while( i < exp.length() )
  {
    if( exp.charAt(i) == '(' ){
      ma = match( exp,i);
      str += exp.substring(i , ma + 1);
      i = ma;
    }else if( ( exp.charAt(i) == '+' || exp.charAt(i) == '-' ) && str != "" ){
   
      // backtracking. The end of str must not be a two arg op, case -1*-1
  
      if( isTwoArgOp( backTrack(  str ) )){
        str += exp.charAt(i);
      }else{
        return str;
      }
    }else{
      str += exp.charAt(i);
    }
    i++;
  }

  return str;
}







/**
* Searches the infix expression str backwards to see if there<br>
* is an operator present at the end of str.
* <p>
* @param  str  string expression to check.
* @return the operator, if any, or null.
* <p>
* Ex:<br>
* backTrack(  "5+x" ) => null<br>
* backTrack(  "5^" ) => "^"
* <p>
* The purpose with this method is to check for combinations like ^- or *-<br>
* in some methods that need to do that.
* <p>
**/

private String 
backTrack(  String str )
{
  int i = 0;
  String op = "";

  try{
    for( i = 0; i <= maxoplength ; i++ ){
      if(( op = getOp( str , ( str.length() - 1 - maxoplength + i ))) != null 
         && ( str.length() - maxoplength - 1 + i + op.length() ) == str.length())
      {
      
        return op;
      }
    }
  }catch(Exception e){}

  return null;
}
  

/**
*
* Takes an operator out of an infix expression.<br>
* @param   exp infix expression.
* @param   index where to start search for the operator in exp.
* @return  the operator if one is found or null if not.
* <p>
* Ex:<br>
* If str is "34+cos(2*x)"<br>
* getOp( str , 2 ) => "+"<br>
* getOp( str , 3 ) => "cos"<br>
* getOp( str , 0 ) => null
* <p>  
**/

private String 
getOp( String exp , int index )
{
  String tmp; 
  int i = 0;
  int len = exp.length();
  
  for( i = 0 ; i < maxoplength ; i++ )
  {
    if( index >= 0 && ( index + maxoplength - i ) <= len )
    {
      tmp = exp.substring( index , index + ( maxoplength - i ) );
      if( isOperator( tmp ) )
      {
        return( tmp );
      }
    }
  }

  return null;
}


/**
*
* Replaces all occurrencies of substrings of type<br>
* nen , n.nen , ne+n , ne-n , n.ne-n , n.ne+n<br>
* where n is a digit 0 - 9, with respectivly<br>
* n*10^n , n.n*10^n , n*10^n , n*10^-n , n.n*10^-n, n.n*10^n
* <p>
* The purpose is to add support for "scientific" notation<br>
* like for example: 1e-3, 2.3e-6 , 5.3e6
* <p>
*
* NOTE: One problem with this method is that it allows decimal values
* like for example 1e1.2
**/


private String
parseE( String exp )
{
  String tmp;
  int i, p , len;

  StringBuffer newstr = new StringBuffer( exp );

  i = p = 0;
  len = exp.length();

  while( i < len )
  {
   try{
    if( exp.charAt( i ) == 'e' && Character.isDigit( exp.charAt( i - 1 ) ) )
    {
	if( Character.isDigit( exp.charAt( i + 1 ) ) || ( ( exp.charAt( i + 1 ) == '-' || exp.charAt( i + 1 ) == '+' ) && Character.isDigit( exp.charAt( i + 2 ) ) ) )
        {
          // replace the 'e'
	  newstr.setCharAt( i + p , '*' );
	  // insert the rest
          newstr.insert( i + p + 1 , "10^" );
	  p = p + 3; // buffer growed by 3 chars
        }
     }
   }catch( Exception e ){}
     i++;
  }
  
  return newstr.toString();
}







/**
*
* Parses out all spaces in str.
*
**/

private String 
SkipSpaces( String str )
{
  StringBuffer newstr = new StringBuffer( 100 );
  int i = 0;
  int len = str.length();

  for( i = 0 ; i < len ; i++ )
  {
    if( str.charAt(i) != ' ' ) newstr.append( str.charAt(i) );
  }

  return newstr.toString();
}







/**
*
* Parses out all ++ +- -+ --
*
* An "easy" no good way to simplify
* expressions like x+-1*cos(x)
* This should instead be made inside all constructors.
*
**/

private String 
parseSigns( String str )
{

  StringBuffer newstr = new StringBuffer( 100 );
  int i = 0;

  while( i < str.length() )
  {
     try{
    	if(str.charAt(i) == '+' && str.charAt( i + 1 ) == '+' )
	{
      		newstr.append( '+' );
      		i++;
 	}else if( str.charAt(i) == '+' && str.charAt( i + 1 ) == '-' ){
      		newstr.append( '-' );
      		i++;
 	}else if( str.charAt(i) == '-' && str.charAt( i + 1 ) == '+' ){
      		newstr.append( '-' );
      		i++;
 	}else if( str.charAt(i) == '-' && str.charAt( i + 1 ) == '-' ){
      		newstr.append( '+' );
      		i++;
 	}else{
 		newstr.append( str.charAt(i) );
 	}
    }catch(Exception e){}
    
    i++;
  }
  return newstr.toString();
}









/**
* matches brackets in exp.
* <p>
*
* @param  exp  string expression to check.
* @return index of matching ")".
* <p>
*
**/

private int 
match( String exp , int index )
{
  int i = index;
  int count = 0;

  while( i < exp.length() )
  {

    if( exp.charAt(i) == '(' ){
      count++;
    }else if( exp.charAt(i) == ')' ){
      count--;
    }

    if( count == 0 ){
      return i;
    }

    i++;
  }

  return index;
}







/**
*
* Extracts variables from a string, x;y;z
* <p>
* @param str the string containing variables separated with ";"
* @return each call returns the next variable from the string or null.
*
* NOTE: and obvious improvement would be to use a StringTokenizer here.
*
**/

private int lastIndex = 0;

private String 
findVariable( String str )
{
  int thisindex = 0;
  String var = "";  

  if(lastIndex >= str.length() ){
    lastIndex = 0;
    return null;
  }

  thisindex = str.indexOf(";" , lastIndex );

  if( thisindex == -1 ){
    var = str.substring( lastIndex , str.length() );
    lastIndex = str.length();
    return( var );
  }

  var = str.substring( lastIndex , thisindex );

  lastIndex = thisindex + 1;
  
  return( var );
}



/**
* Doubles the array arr and copies everything in arr inTo the new array.
* <p>
*
* @param  arr array to double and copy.
* @return a new string array, twice as big as arr and with the same elements.
* <p>
* This method should not be called in normal cases.
*
*
* NOTE: Could use System.arraycopy for this to improve performance.
*
**/

private String[]
doubleAndCopyArray( String arr[] )
{
  int i = 0;
  int len = arr.length;

  String new_arr[] = new String[ len * 2 ];

  for(i = 0; i < len; i++){
    new_arr[ i ] = arr[ i ];
  }

  return new_arr;
}
  

/**
*
* Stores the string var in the variables vector
* 
* Used to store a list of variables.
*
**/


void
storeVars(String var)
{
  if( ! variables.contains( var ) )
  {
	variables.addElement( var );
  }
}




/**
*
*
* Gets stored variables as a semi-colon delimited string.<br>
* Example:<br>
* <xmp>
* Derive d = new Derive();
* d.diff( "x+y+z" );
* String vars = d.getVariables();
* </xmp>
* <p>
* The variable vars will contain the string "x;y;z" , note that the variables appear<br>
* in the list in the same order as they were found in the expression.
* @return semi-colon delimited string of variables found in the expression<br>
* after a call to any of the methods diff or an empty string if no call has yet been made.
*
**/

public synchronized String
getVariables()
{
	StringBuffer tmp = new StringBuffer( 50 );
	Enumeration en = variables.elements();
	boolean first = false;

	while( en.hasMoreElements() )
	{
		if( ! first )
		{
			tmp.append( (String)en.nextElement() );
		}
		else
		{
			tmp.append( ';' ).append( (String)en.nextElement() );
		}

		first = true;
	}	

	return tmp.toString();
}


/**
*
* Clears the variable storedvars.
*
**/

void
clearVars()
{
	variables.removeAllElements();
}





/**
* This method takes a mathematical expression with infix notation
* and performs symbolic differentiation in regards to<br>
* the variables listed in <i>variables</i>.
* <p>
*
* @param  exp  string expression with infix notation to derive.
* @param  vars string representing the variables.
* @return string array with the derivatives of exp.
* @exception notValidSyntaxException if the expression has invalid syntax
* <p>
**/

public synchronized String[] 
diff( String exp , String vars ) throws Exception
{
  String tmpstr = "";
  String variable = "";
  String answer = "";
  String expression = "";
  String prefixExp = "";
  String derivPrefixExp = "";
  String storedvars = "";
  String ans[] = new String[ 100 ];
  int count = 0;
  
  if( exp == null || exp.equals("") )//just ignore
  {
    
  }
  
  // clear all previously stored variables
  clearVars();
  
  expression = SkipSpaces( exp );
  expression = expression.toLowerCase();
  expression = putMult( parseE( expression ) );
  
  Syntax( expression ); 
      
  prefixExp = InToPrefix( expression );
	//  System.out.println(prefixExp);
    prefixExp = SimplifyAsMuchAsPossible( prefixExp );
    //  System.out.println(prefixExp);
      if( vars == null || vars.equals("") )
      {
		storedvars = getVariables();
  		tmpstr = ( storedvars.equals( "" ) ? this.defaultvar : storedvars );
  		tmpstr = SkipSpaces( tmpstr.toLowerCase() );
      }
      else
      {
  		tmpstr = SkipSpaces( vars.toLowerCase() ); 
      }

      while( ( variable = findVariable( tmpstr ) ) != null )
      {
        Syntax( variable );

             
      
		// set stringbuffer length
		sb_init = prefixExp.length();

        derivPrefixExp = Derive( prefixExp , variable );
		//System.out.println(derivPrefixExp);
		derivPrefixExp = SimplifyAsMuchAsPossible( derivPrefixExp );
       // System.out.println(derivPrefixExp);
        answer = preToInfix( derivPrefixExp );
      //System.out.println(answer);
        if(count > ( ans.length - 1) )
	{
          ans = doubleAndCopyArray( ans );
        }
          
        ans[ count ] = parseSigns( answer );
        count++;
      }
    
      return ans;


}// end diff



/**
* Takes an mathematical expression with infix notation and performs symbolic differentiation in regards to all<br>
* the variables found in the expression.
* <p>
* @param  exp  string expression with infix notation to derive.
* @return string array with the derivatives of exp.
* @exception notValidSyntaxException if the expression has invalid syntax
**/

public synchronized String[] 
diff(String exp) throws Exception
{
	return diff( exp, "" );
}


}// end class derive







