package meurandomaccessfile;

import java.io.*;
import codigo.*;

public class MeuRandomAccessFile extends RandomAccessFile 
{
    protected int bits;
    protected int qtd;
    protected int qtdLixo;
    protected String filename;
    protected String modo;
    
    public MeuRandomAccessFile(File arquivo, String modo) throws FileNotFoundException
    {                
        super(arquivo, modo);
        this.bits = 0;
        this.qtd = 0;
        this.filename = arquivo.getAbsolutePath();
        this.modo = modo;
    }
    
    public MeuRandomAccessFile(String arquivo, String modo) throws FileNotFoundException
    {
        super(arquivo, modo);
        this.bits = 0;
        this.qtd = 0;
        this.filename = arquivo;
        this.modo = modo;
    }
    
    public String getFilename()
    {
    	return this.filename;
    }

    public int getQtd()
    {
        return this.qtd;
    }
    
    public int getQtdLixo()
    {
        return this.qtdLixo;
    }
    
    public void writeCodigo(Codigo cod) throws IOException
    {
        //enquanto vai formando o codigo, so pode escrever ele num arquivo quando tiver 8 bits
        //ai vc chama super.escreve()
        //no fim qdo acabar pode sobrar bits. tem q escrevelos mesmo que ja acabou. Ai faz tipo um flush tbm

        if(cod == null)
            throw new IOException("Nulo");

        for(int i = 0;i<cod.getCodigo().length();i++)
        { 
            if(qtd < 8)
            {            	
                this.bits = this.bits << 1;
                String b  = "0000000"+ cod.getCodigo().charAt(i);
                int bit   = Integer.parseInt(b);
                this.bits = this.bits | bit;
                this.qtd++;
                this.qtdLixo = 8 - this.qtd;
            }
            else
            {
                this.write(this.bits);
                this.bits = 0;
                this.qtd = 1;
                this.qtdLixo = 7;
                String b = "0000000" + cod.getCodigo().charAt(i);
                int bit = Integer.parseInt(b);
                this.bits = this.bits | bit;                   
            }                 
        }               
    }
    
    public void completeComLixo() throws Exception
    {
        this.bits = this.bits << this.qtdLixo;
        this.write(this.bits);
        this.qtd = 0;
        this.bits = 0;
    }
    
    public String toString()
    {
        String ret = "{";

        ret += this.bits + "; " + this.qtd + "; " + this.qtdLixo;

        ret += "}";

        return ret;
    }

    public int hashCode()
    {
        int ret = 24;

        ret = ret *2 + new Integer(this.bits).hashCode();
        ret = ret *2 + new Integer(this.qtd).hashCode();
        ret = ret *2 + new Integer(this.qtdLixo).hashCode();

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

        MeuRandomAccessFile mraf = (MeuRandomAccessFile)obj;

        if(this.bits!=mraf.bits)
            return false;

        if(this.qtdLixo!=mraf.qtdLixo)
            return false;

        if(this.qtd!=mraf.qtd)
            return false;

        return true;
    }

    public Object clone()
    {
        MeuRandomAccessFile ret = null;

        try
        {
            ret = new MeuRandomAccessFile(this);
        }
        catch(Exception e)
        {}

        return ret;
    }

    public MeuRandomAccessFile(MeuRandomAccessFile modelo) throws Exception
    {
        super(modelo.filename, modelo.modo);
        if(modelo == null)
            throw new Exception("Modelo ausente!!");

        this.qtd = modelo.qtd;
        this.bits = modelo.bits;
        this.qtdLixo = modelo.qtdLixo;
    }
}
