package deriveUtiles;

public class MyMath
{
    public static double MAX_PRECISION = 50;
    public static double PI = 3.1415926535897932384;
    public static double E = 2.71828182845904523536;
    public static double EPS = 1e-8;

    //Math
    public static double exp( double x )
    {
        double res = 0;
        double p = 1;
        double q = 1;
        for(int i = 1; i < MAX_PRECISION; i++ ) {
    	    res += p / q;
    	    p *= x;
    	    q *= i;
    	}
    	return res;
    }

	public static double abs(double x)
	{
		if (x < 0.0)
			return (-x);

		return x;
	}

    public static double sqrt( double x )
    {
        return Math.sqrt(x);
    }

	public static double sin(double x)
	{
		return Math.sin(x);
		/*
		double res = 0;
		double k = 1;
		double f = 1;
		double e = -1.0;

		for (int i = 1; i < MAX_PRECISION; i++)
		{
			k = k*x;
			f = f*i;

			if (i%2 != 0)
			{
				res += e*(k/f);
				e = e * (-1.0);
			}
		}

		return res;
		*/
	}

	public static double cos(double x)
	{
		return Math.cos(x);
		/*
		double res = 1;
		double k = 1;
		double f = 1;
		double e = -1.0;

		for (int i = 1; i < MAX_PRECISION; i++)
		{
			k = k*x;
			f = f*i;

			if (i%2 == 0)
			{
				res += e*(k/f);
				e = e * (-1.0);
			}
		}

		return res;
		*/
	}

	public static double tg(double x)
	{
		return Math.tan(x);
		/*
		if (abs(cos(x)) < EPS)
			return Double.NaN;

		return sin(x)/cos(x);
		 */
	}

    public static double ctg(double x)
	{
		if (Math.abs(Math.tan(x)) > EPS)
			return 1/Math.tan(x);

		return Double.NaN;
	}

	public static double asin(double x)
	{
		if (Math.abs(x) > 1)
			return Double.NaN;

		double coef = x;
		double res = x;

		double b = x;
		for (int i = 1; i < MAX_PRECISION; i++)
		{
			coef *= i*2*(i*2-1)*b*b;
			coef /= (i*i);
			coef /= 4;

			res += coef/(2*i+1);
		}

		return res;
	}

	public static double acos(double x)
	{
		return (PI/2 - asin(x));
	}

	public static double atg(double x)
	{
		double l = -PI/2;
		double r = PI/2;
		double m;

		while (Math.abs(r-l) > EPS)
		{
			m = (l+r)/2;
			if (Math.tan(m) > x)
				r = m;
			else
				l = m;
		}

		return ( (l+r)/2 );
	}

	public static double actg(double x)
	{
		double l = PI;
		double r = 0;
		double m;

		while (Math.abs(r-l) > EPS)
		{
			m = (l+r)/2;
			if ((1/Math.tan(m)) > x)
				r = m;
			else
				l = m;
		}

		return ( (l+r)/2 );
	}

	public static double ln(double x)
	{
		if (x <= 0)
			return Double.NaN;


		double y, z, t;
		double a = E;

		y = 0;
		z = x;
		t = 1.0;


		int k = 0;
		while ((t > EPS) || (z <= 1.0/a) || (z >= a))
		{
			k++;
			if (z >= a)
			{
				z /= a;
				y += t;
			}
			else if (z <= 1.0/a)
			{
				z *= a;
				y -= t;
			}
			else
			{
				z *= z;
				t /= 2;
			}
		}

		return y;
	}

	public static double intPow(double x, int n)
	{
		double b = x;
		double p = 1.0;
		int k;
		if (n >= 0)
		{
			k = n;
		}
		else
		{
			k = -n;
			b = 1/b;
		}

		while (k > 0)
		{
			if (k%2 == 0)
			{
				k /= 2;
				b *= b;
			}
			else
			{
				k--;
				p *= b;
			}
		}

		return p;
	}

	public static double pow(double x, double n)
	{
		if (x == 1)
			return 1;
		if (x == 0)
			return 0;

		if (Math.abs(n-Math.ceil(n)) < EPS)
		{
			return intPow(x, (int)Math.ceil(n));
		}

		if (x < 0)
			return Double.NaN;

		return exp(n*ln(x));
	}

}