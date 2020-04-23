//tera basicamente os metodos MAIS e TIRAULTIMO. Alem dos MO
package codigo;

public class Codigo implements Cloneable
{
	protected String codigo;

	public Codigo()
	{
		this.codigo = "";
	}

	public Codigo(String c) throws Exception
	{
		if(c==null)
			throw new Exception("Codigo ausente!!");

		this.codigo = c;
	}

	public void setCodigo(String c) throws Exception
	{
		if(c==null)
			throw new Exception("Codigo ausente!!");

		this.codigo = c;
	}

	public String getCodigo()
	{
		return this.codigo;
	}

	public void mais(int c)
	{
		this.codigo += c;
	}

	public void tirarUltimo()
	{
		if(this.codigo.length()<1)
			this.codigo = "";
		else
		this.codigo = this.codigo.substring(0, this.codigo.length() - 1);
	}
	
	public String toString()
    {
        String ret = "{";

        ret += this.codigo;

        ret += "}";

        return ret;
    }

    public int hashCode()
    {
        int ret = 24;

        ret = ret *2 + this.codigo.hashCode();

        return ret;
    }

    public boolean equals(Object obj)
    {
        if(this==obj)
            return true;

        if(obj==null)
            return false;

        if(!(obj.getClass().equals(this.getClass())))
            return false;

        Codigo mraf = (Codigo)obj;

        if(!(this.codigo.equals(mraf.codigo)))
            return false;

        return true;
    }

    public Object clone()
    {
        Codigo ret = null;

        try
        {
            ret = new Codigo(this);
        }
        catch(Exception e)
        {}

        return ret;
    }

    public Codigo(Codigo modelo) throws Exception
    {
        if(modelo == null)
            throw new Exception("Modelo ausente!!");

        this.codigo = modelo.codigo;
    }
}