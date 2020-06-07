public class Vector
{
    public double x;
    public double y;

    public Vector()
    {
        this.set(0,0);
    }

    public Vector(double x, double y)
    {
        this.set(x,y);
    }

    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void add(double dx, double dy)
    {
        this.x += dx;
        this.y += dy;
    }
    
    public void add(Vector other)
    {
        this.add( other.x, other.y );
    }
    
    public void multiply(double m)
    {
        this.x *= m;
        this.y *= m;
    }

    public double getLength()
    {
        return Math.sqrt( this.x * this.x + this.y * this.y );
    }

    public void setLength(double L)
    {
        double currentLength = this.getLength();
        // if current length is 0, then current angle is undefined;
        //   assume current angle is 0 (pointing to right)
        if (currentLength == 0)
        {
            this.set(L,0);
        }
        else // able to preserve current angle
        {
            // scale vector to have length 1
            this.multiply(1 / currentLength);
            // scale vector to have length L
            this.multiply(L);
        }
    }

    public double getAngle()
    {
        return Math.toDegrees( Math.atan2( this.y, this.x ) );
    }

    public void setAngle(double angleDegrees)
    {
        double L = this.getLength();
        double angleRadians = Math.toRadians( angleDegrees );
        this.x = L * Math.cos(angleRadians);
        this.y = L * Math.sin(angleRadians);
    }
}
