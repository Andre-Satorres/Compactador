package informacao;

public class Informacao implements Cloneable
{
	protected Integer info;
	protected int freq;

	public Informacao()
	{
		this.info = null;
		this.freq = 0;
	}

	public Informacao(Integer info, int freq) throws Exception
	{
		if(freq<0)
			throw new Exception("frequencia invalida!!");
		
		if(info!=null)
			this.info = new Integer(info);
		else
			this.info = null;

		this.freq = freq;
	}

	public void setFreq(int freq) throws Exception
	{
		if(freq<0)
			throw new Exception("frequencia invalida!!");

		this.freq = freq;
	}

	public void setInfo(Integer info) throws Exception
	{
		if(info<0)
			throw new Exception("Valor invalido!!");

		this.info = info;
	}

	public Integer getInfo()
	{
		return this.info;
	}

	public int getFreq()
	{
		return this.freq;
	}

	public String toString()
    {
        String ret = "{";

        ret += this.info + "; " + this.freq;

        ret += "}";

        return ret;
    }

    public int hashCode()
    {
        int ret = 24;

        ret = ret *2 + this.info.hashCode();
        ret = ret *2 + new Integer(this.freq).hashCode();

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

        Informacao mraf = (Informacao)obj;

        if(!(this.info.equals(mraf.info)))
            return false;

        if(this.freq!=mraf.freq)
            return false;

        return true;
    }

    public Object clone()
    {
        Informacao ret = null;

        try
        {
            ret = new Informacao(this);
        }
        catch(Exception e)
        {}

        return ret;
    }

    public Informacao(Informacao modelo) throws Exception
    {
        if(modelo == null)
            throw new Exception("Modelo ausente!!");

        this.info = new Integer(modelo.info);
        this.freq = modelo.freq;
    }
}