package compactador;

import meurandomaccessfile.*;

import java.io.File;
import java.io.IOException;

import codigo.*;
import informacao.*;

public class Compactador implements Cloneable
{
    protected class No
    {
        protected No esq;
        protected Informacao info;
        protected No dir;

        public No getEsq ()
        {
            return this.esq;
        }

        public Informacao getInfo ()
        {
            return this.info;
        }

        public No getDir ()
        {
            return this.dir;
        }

        public void setEsq (No e)
        {
            this.esq=e;
        }

        public void setInfo (Informacao x)
        {
            this.info=x;
        }

        public void setDir (No d)
        {
            this.dir=d;
        }

        public No (No e, Informacao x, No d)
        {
            this.esq =e;
            this.info=x;
            this.dir =d;
        }

        public No (Informacao x)
        {
            this (null,x,null);
        }
    }

    protected int[] frequencias;
    
    protected No[] codigos;    
    
    protected Codigo c;
    
    protected Codigo[] cod;
      
    protected int qtd;
    
    protected MeuRandomAccessFile arquivo; //vou usar a funcao read p ler 

    public Compactador(String filename) throws Exception
    {
    	this.zerarVetor();
    	this.arquivo = new MeuRandomAccessFile(filename, "r");

    	this.codigos = new No[256];

    	this.qtd = 0;

    	for (int i=0; i<256; i++)
	   		this.codigos[i] = null; 	
    }

    protected void zerarCodigo()
    {
    	this.cod = new Codigo[256];
    	for (int i=0; i<256; i++)
	   		this.cod[i] = null;
    }

    public void compactar()
    {
    	try
    	{
    		this.preencher();
    		
    		this.formarArvore();
	    	
	    	this.formarCodigos();
	        
	        String s = this.arquivo.getFilename();
	        	       
	        
	        this.arquivo.close();
	        
	        MeuRandomAccessFile arq = new MeuRandomAccessFile(s, "r");
	        
	        String extensao = s.substring(s.lastIndexOf(".")+1, s.length());
	        s = s.substring(0, s.lastIndexOf("."));
	        
	        File strFile = new File(s);
	        
	        if(strFile.exists())
	        	s += "(1)";
	        
	        this.arquivo = new MeuRandomAccessFile(s+".god", "rw");	        
	        this.arquivo.write(0);
	        this.arquivo.write(extensao.length());
	        
	        for (int i=0; i<extensao.length(); i++)
	        	this.arquivo.write(extensao.charAt(i));	
	        
	        this.fazerCabecalho();
	        this.escreverCodigo(arq);
	        
	        this.arquivo.close();
	        arq.close();
       	}
       	catch(Exception e)
    	{
       		e.printStackTrace();
    	}
    }
    
    protected void formarCodigos()
    {
    	this.cod = new Codigo[256];

        this.c = new Codigo();
        this.criarCodigo(codigos[0]);
    }

    protected No percorrerGalhos(No r, Character c)
    {
    	if(c.equals('0'))
    		r = r.getEsq();
    	else
    		r = r.getDir();
    	
    	return r;
    }
    
    protected void formarArvore()
    {
    	try
    	{
			int contador = 0;
			
	    	for (int i=0; i<256; i++)
	    		if(this.frequencias[i]>0)
	    		{
	    		    Informacao info = new Informacao(i, this.frequencias[i]);
	
	    			this.codigos[contador] = new No(info);
	    			this.qtd++;
	    			
	    			contador++;
	    		}
	    	
	    	int aux = qtd;
	
	    	while(this.qtd >1)
	    	{
	    		this.ordenarVetor();
	    		this.juntarUltimos();
	    	}
	    	
	    	qtd = aux; //para eu saber a qtd logica do vetor de freq e escrever no arq
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
          
    public void descompactar()
    {
    	try 
    	{
			int qtdLixo = arquivo.read();
			int len = arquivo.read(); //TAMANHO DA EXTensao
			String extensao = "";
			
			for(int i=0; i<len; i++)
			{
				char c = (char)arquivo.read();
				extensao += c;
			}
			
			len = arquivo.readInt();
			
			for(int i=0; i<len; i++)
			{
				int sla = (int)arquivo.read();
				this.frequencias[sla] = arquivo.readInt();
			}
			
	    	this.formarArvore();
	    	
	    	this.formarCodigos();
	    	
	    	String s = this.arquivo.getFilename();
	    	s = s.substring(0, s.lastIndexOf("."));
	        
	    	File fileee = new File(s);
	    	if(fileee.exists())
	    		s += "(1)";	    		
	    	
	        MeuRandomAccessFile rnd = new MeuRandomAccessFile(s+"."+extensao, "rw");
	        
	        String st = "";
	        
	        No atual = this.codigos[0];
	        
	        while (this.arquivo.getFilePointer() < this.arquivo.length())
	        {
	        	int codigo = this.arquivo.read();
	        	
	            st = "";
	        	
	        	for(int i=0; i<8; i++) 
	        	{
	        		 int aux = (codigo >> i) & 1;
	        		 st = aux + st;
	        	}
	        		        	
	        	if(this.arquivo.getFilePointer() == this.arquivo.length())
	        		st = st.substring(0, st.length()-qtdLixo);
	        	
	        	for (int i=0; i<8; i++)
	        	{
	        		try
	        		{
	        			atual = this.percorrerGalhos(atual, st.charAt(i));
	        		}
	        		catch(Exception e)
	        		{}

	        		if(atual.getEsq()==null && atual.getDir()==null)
	        		{
		 	        	rnd.write(atual.getInfo().getInfo());		 	        	
		 	        	atual = this.codigos[0];	 	        	
	        		}
	        	}
	        }	        	       
	        
	        this.arquivo.close();
	        rnd.close();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}  	
    }
    
    protected void fazerCabecalho()
    {
    	try 
    	{ 		   		
    		this.arquivo.writeInt(qtd);
    		
    		for(int i=0; i<256; i++)
    		{
    			if (frequencias[i] > 0)
    			{
    				this.arquivo.write(i);
    				this.arquivo.writeInt(frequencias[i]);
    			}
    		}
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    }
    
    protected void juntarUltimos()
    {
    	try
    	{
	    	if(qtd>1)
	    	{
	    		Informacao i = new Informacao(null, this.codigos[qtd-1].getInfo().getFreq()+this.codigos[qtd-2].getInfo().getFreq());
	    		this.codigos[qtd-2] = new No(this.codigos[qtd-2], i, this.codigos[qtd-1]);
	    		this.codigos[qtd-1] = null;
	    		qtd--;
	    	}
    	}
    	catch(Exception e)
    	{}
    }
    
    protected void criarCodigo(No r)
    {
    	try
    	{
	    	if(r==null)
	    		return;

	    	if(r.getInfo().getInfo() != null)
	            cod[r.getInfo().getInfo()] = (Codigo)c.clone();
	        else
	        {
	        	try
	        	{
	 	        	c.mais(0);
		        	criarCodigo(r.getEsq());
		        	c.tirarUltimo();
		        	c.mais(1);
		        	criarCodigo(r.getDir());
		        	c.tirarUltimo();
	        	}
	        	catch (Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        }
    	}
    	catch (Exception e)
    	{}
    }
 
    protected void preencher()
    {  	
    	try
    	{	
	    	int b = this.arquivo.read();
	    	
	    	while (this.arquivo.getFilePointer() <= this.arquivo.length())
	    	{
	    		this.frequencias[b]++;
	    		b = this.arquivo.read();
	    	}
	    	
	    	this.arquivo.seek(0);
    	}
    	catch (Exception e)
    	{
    	}
    }

    protected void escreverCodigo(MeuRandomAccessFile arq)
    {
    	try
    	{	
	    	int b = arq.read();
	    	
	    	while (arq.getFilePointer() <= arq.length())
	    	{
	    		try
	    		{
	    			if(cod[b]!=null)    		
	    				this.arquivo.writeCodigo(cod[b]);
	    		}
	    		catch(Exception e)
	    		{
	    			break;
	    		}
	    			    		
	    		b = arq.read();
	    	}
	    	
	    	this.arquivo.completeComLixo();
	    	arq.seek(0);
	    	this.arquivo.seek(0);
	    	this.arquivo.write(this.arquivo.getQtdLixo());
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
	protected void ordenarVetor()
	{
		int cont1, cont2;  
        No aux;
        for(cont1 =0; cont1<qtd; cont1++) 
        for(cont2 =0; cont2 <qtd; cont2++)            
           if(codigos[cont2].getInfo().getFreq() < codigos[cont1].getInfo().getFreq())
           {  
               aux = codigos[cont1];  
               codigos[cont1] = codigos[cont2];  
               codigos[cont2] = aux;  
           }        
	}
    
	protected void zerarVetor()
    {
    	this.frequencias = new int[256];

    	for(int i=0; i<256; i++)
    		this.frequencias[i] = 0;
    }
    
    public String toString(No r)
    {
    	String ret = "";
    	if(r==null)
    		return ret;
    	ret+= "(";

    	ret+= toString(r.esq);

    	ret += ")";

    	ret += r.info;

    	ret += "(";

    	ret+= toString(r.dir);

    	ret+= ")";

    	return ret;
    }

    public String toString ()
    {		
    	String ret = "(";

    	ret += this.qtd;

    	ret+= this.arquivo;
    	
    	ret += this.c;

    	for(int i=0; i<256;i++) 
    		ret += toString(codigos[i]);

    	for(int i=0; i<256;i++) 
    		ret += frequencias[i];

    	for(int i=0; i<256;i++) 
    		ret += cod[i];

    	ret += ")";

    	return  ret;
    }
  
    protected boolean equals(No r1, No r2)
    {
    	if(r1==r2)
        	return true;

        if(r1==null)
        	return false; //r2 n eh null senao entraria no 1� if

        if(r2==null)
        	return false;

        if(!(this.equals(r1.esq, r2.esq)))
        	return false;

        if(!(r1.info.equals(r2.info)))
        	return false;

        if(!(this.equals(r1.dir, r2.dir)))
        	return false;

        return true;
    }

    public boolean equals (Object obj)
    {
        if(this==obj)
        	return true;

        if(obj==null)
        	return false;

        if(!(this.getClass().equals(obj.getClass())))
        	return false;

        Compactador comp = (Compactador)obj;

        for(int i=0; i<256;i++) 
            if(!(this.equals(this.codigos[i], comp.codigos[i])))
                return false;

        for(int i=0; i<256;i++) 
            if(!(this.frequencias[i] == comp.frequencias[i]))
                return false;

        for(int i=0; i<256;i++) 
            if(!(this.cod[i].equals(comp.cod[i])))
                return false;

        if(this.qtd!= comp.qtd)
            return false;
        
        if(!(this.c.equals(comp.c)))
            return false;

        if(!(this.arquivo.equals(comp.arquivo)))
            return false;

        return true;
    }
 
    protected int hashCode(No r)
    {
    	int ret = 7;

    	if(r==null)
    		return ret;

    	if(r.esq!=null)
    		ret = ret *2 + hashCode(r.esq);

    	ret = ret*2 + r.info.hashCode();

    	if(r.dir!=null)
    		ret = ret*2 + hashCode(r.dir);

    	return ret;
    }

    public int hashCode ()
    {
        int ret = 7;

        for(int i=0; i<256;i++) 
            ret = ret * 2 + hashCode(this.codigos[i]);

        for(int i=0; i<256;i++) 
            ret = ret * 2 + new Integer(this.frequencias[i]).hashCode();

        for(int i=0; i<256;i++) 
            ret = ret * 2 + this.cod[i].hashCode();

        ret = ret * 2 + new Integer(this.qtd).hashCode();

        ret = ret * 2 + this.arquivo.hashCode();
        
        ret = ret * 2 + this.c.hashCode();

        return ret;
    }

    protected No ConstroiArvoreBinaria(No n)
    {
    	if(n==null)
    		return null;

    	No aFazer=null;

    	if(n.info instanceof Cloneable)
    		aFazer = new No((Informacao)n.info.clone());
    	else
    		aFazer = new No(n.info);

    	if(n.esq!=null)
    		aFazer.esq=ConstroiArvoreBinaria(n.esq);

    	if(n.dir!=null)
    		aFazer.dir = ConstroiArvoreBinaria(n.dir);

    	return aFazer;
    }

    public Compactador (Compactador modelo) throws Exception
    {
        if(modelo == null)
        	throw new Exception("Modelo ausente!");

        for(int i=0; i<256;i++) 
           this.codigos[i] = this.ConstroiArvoreBinaria(modelo.codigos[i]);

        for(int i=0; i<256;i++) 
            this.frequencias[i] = modelo.frequencias[i];

        for(int i=0; i<256;i++) 
            this.cod[i] = (Codigo)modelo.cod[i].clone();

        this.qtd = modelo.qtd;
        this.c = modelo.c;

        this.arquivo = new MeuRandomAccessFile(modelo.arquivo);
    }

    public Object clone ()
    {
        Compactador ret = null;

        try
        {
        	ret = new Compactador(this);
        }
        catch(Exception e)
        {}

        return ret;
    }
}