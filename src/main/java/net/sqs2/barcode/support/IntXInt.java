package net.sqs2.barcode.support;

class IntXInt
{
    final int first;
    final int second;
    final boolean hasX;
    
    public IntXInt(String ratio) throws NumberFormatException
    {
        final int xPos = ratio.toLowerCase().indexOf('x');
        if (xPos != -1)
        {
            hasX = true;
            first = new Integer(ratio.substring(0, xPos)).intValue();
            second = new Integer(ratio.substring(xPos + 1)).intValue();
        }
        else
        {
            hasX = false;
            first = new Integer(ratio).intValue();
            second = 0;
            
        }
       
    }
    
    public static String print(int first, int second) {
        return first + "x" + second;
    }
    
    public String toString()
    {
        return super.toString() + "{" + print(this.first, this.second) + "}";
    }
    
    
    
    public static IntXInt parse(String s)
    {
        try
        {
            return new IntXInt(s);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("An error happened trying to parse [" + s + "] as an [int]'x'[int] like [10x15], details are:" + e.getMessage(), e);
        }
        
    }
    
}