package deriveUtiles;

public class MyString
{
    public static int toArray( String s, String[] a)
    {
        int a_size = 0;
        String tmp;
        for(int i = 0; i < s.length(); i++)
        {
            //Next Word
            tmp = "";
            //is special symbol or operator
            if( isSpesialSymbol( s.charAt(i) ) || isOperator( s.charAt(i) ) )
			{
				tmp += s.charAt(i);
			}
			else
            //is number
            if( isDigit( s.charAt(i) ) )
			{
				while( i < s.length() && (isDigit( s.charAt(i) ) || s.charAt(i) == '.') )
				{
					tmp += s.charAt(i++);
				}
				i--;
			}
			else
            //is word
			if( isAlpha( s.charAt(i) ) )
			{
				while( i < s.length() && isAlpha( s.charAt(i) ) )
				{
					tmp += s.charAt(i++);
				}
				i--;
				tmp = tmp.toLowerCase();
			}
			else
            //is tab letter
            {
                continue;
            }

            //add word
			a[ a_size++ ] = tmp;
		}
        a[ a_size ] = "";
        return a_size;
    }

	public static boolean isSpesialSymbol( char c )
	{
		if(
			c == '(' ||
			c == ')' ||
			c == '[' ||
			c == ']'
		) return true;
		return false;
	}

    public static boolean isOperator( char c )
    {
		if(
			c == '+' ||
			c == '-' ||
			c == '*' ||
			c == '/' ||
			c == '^'
		) return true;
		return false;
    }

	public static boolean isDigit( char c )
	{
		return ( '0' <= c && c <= '9' );
	}

	public static boolean isAlpha( char c )
	{
		return ('a' <= c && c <= 'z') || ( 'A'  <= c && c <= 'Z') ;
	}

    public static boolean isNumber( String s )
    {
        for(int i = 0; i < s.length(); i++)
        {
            if( isDigit( s.charAt(i) ) || s.charAt(i) == '.' ) continue;
            return false;
        }
        return true;
    }

    public static boolean isAbs( String s )
    {
        return s.equalsIgnoreCase("abs");
    }

    public static boolean isSqrt( String s )
    {
        return s.equalsIgnoreCase("sqrt");
    }

    public static boolean isSin( String s )
    {
        return s.equalsIgnoreCase("sin");
    }

    public static boolean isCos( String s )
    {
        return s.equalsIgnoreCase("cos");
    }

    public static boolean isTan( String s )
    {
        return s.equalsIgnoreCase("tan") || s.equalsIgnoreCase("tg");
    }

    public static boolean isCotan( String s )
    {
        return s.equalsIgnoreCase("cotan") || s.equalsIgnoreCase("ctg");
    }

    public static boolean isArcSin( String s )
    {
        return s.equalsIgnoreCase("arcsin") || s.equalsIgnoreCase("asin");
    }

    public static boolean isArcCos( String s )
    {
        return s.equalsIgnoreCase("arccos") || s.equalsIgnoreCase("acos");
    }

    public static boolean isArcTan( String s )
    {
        return s.equalsIgnoreCase("arctan") || s.equalsIgnoreCase("atg");
    }

    public static boolean isArcCotan( String s )
    {
        return s.equalsIgnoreCase("arccotan") || s.equalsIgnoreCase("actg");
    }

    public static boolean isExp( String s )
    {
        return s.equalsIgnoreCase("exp");
    }

    public static boolean isLn( String s )
    {
        return s.equalsIgnoreCase("ln");
    }

    public static boolean isFunction( String s )
    {
        return  isAbs(s) ||
                isSqrt(s) ||
                isSin(s) ||
                isCos(s) ||
                isTan(s) ||
                isCotan(s) ||
                isArcSin(s) ||
                isArcCos(s) ||
                isArcTan(s) ||
                isArcCotan(s) ||
                isExp(s) ||
                isLn(s);
    }

    public static boolean isVerible( String s )
    {
        return s.equalsIgnoreCase("x") || s.equalsIgnoreCase("y");
    }

    public static boolean isConstant( String s )
    {
        return s.equalsIgnoreCase("e") || s.equalsIgnoreCase("pi");
    }

        public static boolean isBinaryOp(String op){
        try{
            return(isBinaryOp(getCode(op)));
        }catch(Exception e){return false;}
    }

    public static boolean isBinaryOp(int code)
    {
        return(isDiv(code) || isMul(code) || isPower(code) || isSub(code) || isSum(code) );
    }

    public static double StrToDouble(String s)
    {
       if(isNumber(s)) return Double.valueOf(s).doubleValue();
       return Double.NaN;
    }

    public static boolean isSum( String str ){
        return( str.equals( "+" ) );
    }

    public static boolean isSub( String str ){
        return(str.equals( "-" ) );
    }

    public static boolean isMul( String str ){
        return( str.equals( "*" ) );
    }

    public static boolean isDiv(String str){
        return( str.equals( "/" ) );
    }

    public static boolean isTg( String exp ){
        return( exp.equals( "tg" ) );
    }

    public static boolean isAtg( String exp ){
        return( exp.equals( "atg" ) );
    }

    public static boolean isAcos( String exp ){
        return( exp.equals( "acos" ) );
    }

    public static boolean isAsin( String exp ){
        return( exp.equals( "asin" ) );
    }

    public static boolean isPower( String exp ){
        return( exp.equals( "^" ) );
    }

    public static boolean isE( String exp ){
        return( exp.equals( "exp" ) );
    }

    public static boolean isCtg( String exp ){
        return( exp.equals( "ctg" ) );
    }

    public static boolean isActg( String exp ){
        return( exp.equals( "actg" ));
    }


    public static boolean isMinus( String exp){
        return( exp.equals("--"));
    }

    public static boolean isX(String exp){
        return("x".equalsIgnoreCase(exp));
    }

    public static boolean isY(String exp){
        return("y".equalsIgnoreCase(exp));
    }

    public static int getCode(String exp)
    {
        if(MyString.isNumber(exp )) return 0;

        if(isX(exp)) return 1;
        if(isY(exp)) return 2;
        //etc.
        if(isAcos(exp)) return 3;
        if(isActg(exp)) return 4;
        if(isAsin(exp)) return 5;
        if(isAtg(exp)) return 6;
        if(isCos(exp)) return 7;
        if(isCtg(exp)) return 8;
        if(isDiv(exp)) return 9;
        if(isE(exp)) return 10;
        if(isLn(exp)) return 11;
        if(isMul(exp)) return 12;
        if(isPower(exp)) return 13;
        if(isSin(exp)) return 14;
        if(isSqrt(exp)) return 15;
        if(isSub(exp)) return 16;
        if(isSum(exp)) return 17;
        if(isTg(exp)) return 18;
        if(isAbs(exp)) return 19;
        if(isMinus(exp)) return 20;

        return -1;//bad expression
    }

    public static boolean isSum( int code ){
        return( code == 17);
    }

    public static boolean isSub( int code ){
        return(code == 16 );
    }

    public static boolean isMul( int code ){
        return( code == 12 );
    }

    public static boolean isDiv(int code){
        return( code == 9 );
    }

    public static boolean isSqrt( int code ){
        return( code == 15 );
    }

    public static boolean isCos( int code ){
        return( code == 7 );
    }

    public static boolean isSin( int code ){
        return( code == 14 );
    }

    public static boolean isTg( int code ){
        return( code == 18 );
    }

    public static boolean isAtg( int code ){
        return( code == 6 );
    }

    public static boolean isAcos( int code ){
        return( code == 3 );
    }

    public static boolean isAsin( int code ){
        return( code == 5 );
    }

    public static boolean isLn( int code ){
        return( code == 11 );
    }

    public static boolean isPower( int code ){
        return( code == 13 );
    }

    public static boolean isE( int code ){
        return( code == 10 );
    }

    public static boolean isCtg( int code ){
        return( code == 8 );
    }

    public static boolean isActg( int code ){
        return( code == 4);
    }

    public static boolean isAbs( int code){
        return( code == 19);
    }

    public static boolean isMinus( int code){
        return( code == 20);
    }

    public static boolean isNumber( int code )
	{
		return (code == 0);
	}

    public static boolean isX(int code){
        return(code == 1);
    }

    public static boolean isY(int code){
        return(code == 2);
    }

}
