package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Responsável pela operãções nos historicos
 */
public class UpdateFromHistory {
	
	private Connection con,con2;
	private Path origin,origin2,target,target2;
	private String pathName,pathName2;	
	
	/**
	 * Construtor do UpdateFromHistory. Criar uma copia do historico para fazer as operações.
	 */
	public UpdateFromHistory()
	{	
		try{		
				File f = new File("path.txt");
				if(f.exists() && !f.isDirectory())
				{
					BufferedReader bf = new BufferedReader(new FileReader("path.txt"));					
					String path = bf.readLine();
					origin = Paths.get(path);
					target = Paths.get(path+"2");
					pathName = path+"2";
					
					String path2 = bf.readLine();
					origin2 = Paths.get(path2);
					target2 = Paths.get(path2+"2");	
					pathName2 = path2+"2";
					
					bf.close();
				}
				else
				{
					String path = "\\Users\\Renan\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\evcc4g3m.default\\places.sqlite";
					origin = Paths.get(path);
					target = Paths.get(path+"2");
					pathName = path+"2";
					
					String path2 ="\\Users\\Renan\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\History";
					origin2 = Paths.get(path2);
					target2 = Paths.get(path2+"2");	
					pathName2 = path2+"2";
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	
	/**
	 *  Pega a url da ultima página e capitulo lidos do manga com a keyword informada
	 *
	 * @param      keyword   A keyword
	 * @param      lastCap   O ultimo capítulo
	 * @param      lastPage  A ultima página
	 *
	 * @return     A url.
	 */
	public String getUrl(String keyword,int lastCap,int lastPage)
	{
		String result=null;
		try
		{
			//Copia os históricos
			Files.copy(origin, target);
			Files.copy(origin2, target2);
			
			//Conecta nos históricos
			con = DriverManager.getConnection("jdbc:sqlite:"+pathName);	
			con2 = DriverManager.getConnection("jdbc:sqlite:"+pathName2);	
			Statement stm = con.createStatement();
		
			//Procura pela URL no histórico do Firefox
			ResultSet rs = stm.executeQuery("SELECT url FROM moz_places WHERE title like '%"+keyword+"%"+lastCap+"%Page%"+lastPage+"%'");
						
			//Se não encontrar, procurar no histórico do chrome
			if(rs.next())
			result = rs.getString("url");
			else 
			{
				stm = con2.createStatement();
				rs = stm.executeQuery("SELECT url FROM urls WHERE title like '%"+keyword+"%"+lastCap+"%Page%"+lastPage+"%'");
				if(rs.next())
					result = rs.getString("url");
			}
			
			//Fecha conexões
			stm.close();
			con.close();
			con2.close();
	
		}
		catch(Exception exp){exp.printStackTrace();}
		finally{
		try{
			//Deleta a copia dos históricos
			Files.deleteIfExists(target);
			Files.deleteIfExists(target2);
			System.out.println("delete");
		}
		catch(IOException ioe){ioe.printStackTrace();}}	
		
		return result;
	}
	
	/**
	 * Removes a url encontrada com a key word, capítulo e página informados.
	 *
	 * @param      keyword   A keyword
	 * @param      lastCap   O ultimo capítulo
	 * @param      lastPage  A ultima página
	 */
	public void RemoveUrl(String keyword,int lastCap,int lastPage)
	{
		//Encontra a url com a keyword, capítulo e página informados.
		String lastURL = getUrl(keyword, lastCap, lastPage);
		
		
		try {
		//Conecta no histórico
		con = DriverManager.getConnection("jdbc:sqlite:"+origin);		
		con2 = DriverManager.getConnection("jdbc:sqlite:"+origin2);			
		Statement stm = con.createStatement();
	
		//Deleta a URL do firefox
		stm.executeUpdate("DELETE FROM moz_places WHERE url like '"+ lastURL+"' ;");
		
		stm = con2.createStatement();
		
		//Deleta a URL do chrome
		stm.executeUpdate("DELETE FROM urls WHERE url like '"+ lastURL+"' ;");	
		
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

	
	/**
	 *  Pesquisa nos históricos(atualmente só funciona com firefox e chrome) a ultima página e capítulo
	 *  lidos. Caso seja maior que o armazenado no banco de dados retorna uma string com as novas informações
	 *
	 * @param      keyword   A keyword que será pesquisada nas URL, representando o manga
	 * @param      lastCap   O ultimo capítulo lido, armazenado no banco de dados
	 * @param      lastPage  A ultima página lida, amazenada no banco de dados
	 *
	 * @return     Array de String com as novas informações, caso uma url mais atual seja encontrada no histórico
	 */
	public String[] research(String keyword,int lastCap,int lastPage)
	{
		//Inicializa
		lastCap=1;
		String[] result=null;

		try
		{
			//Copia os históricos
			Files.copy(origin, target);
			Files.copy(origin2, target2);
			
			//Conecta nos históricos
			con = DriverManager.getConnection("jdbc:sqlite:"+pathName);	
			con2 = DriverManager.getConnection("jdbc:sqlite:"+pathName2);
			Statement stm = con.createStatement();		
			
				//Encontrar todas urls em que o título contenha a keyword
			    ResultSet rs = stm.executeQuery("SELECT url,title,last_visit_date FROM moz_places WHERE title like '%"+keyword+"%'");//For FireFox
				
				while(rs.next() )
				{
					//Separa todas as palavras e digitos
					String[] split = rs.getString("title").split("[^a-zA-Z0-9]");
					
					//Encontra a posição da palavra "Page" na array
					int pageFound = split.length-1;
					 for(int i=0;i<split.length;i++)
					 {
						 if(split[i].matches("Page"))
						 {
							 pageFound = i;						 
							
						 }
					 }
					
					//Procura pelo numeral abaixo da palavra "Page" (que é o número do capítulo)
					
					forSplit : for(int i=pageFound;i>=0;i--)
					{
						//Se for um número						
						if(split[i].matches("\\d+"))
						{ 
							//Transforma o número em int
							int x = Integer.parseInt(split[i].trim());							
							
							//Tratamento para um manga especifico com número no nome
							if(keyword.equals("Degrees"))
								System.out.println(x);
							if(keyword.equals("Degrees"))
								System.out.println(rs.getString("title"));
							
							//Se o número for maior que o ultimo capítulo informado armazenado no banco							
							if(x>lastCap)
							{
								lastCap = x;						
								
								//Pegar o número na frente da palavra "Page" (que é o número da página)
								for(int y = i;y<split.length;y++)
								{
									if(split[y].matches("Page"))
									{
										lastPage = Integer.parseInt(split[y+1].trim());									
									}
								}
								
								//Pegar a data
								java.util.Date d = new Date(((rs.getLong("last_visit_date")/1000 )));
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
									
								//Resultado
								result = new String[]{new Integer(lastCap).toString(),new Integer(lastPage).toString(),df.format(d),rs.getString("url")};								
							}
							
							//Se o número for igual ao ultimo capítulo informado armazenado no banco	
							if(lastCap == x)
							{
								for(int y = i;y<split.length;y++)
								{
									//Pesquisa pela página
									if(split[y].matches("Page"))
									{
										int z = Integer.parseInt(split[y+1].trim());	
										//Se a página for maior que a ultima armazenada
										if(z>lastPage)
										{
											//Pega a página
											lastPage = z;
																					
											//Pega a data
											java.util.Date d = new Date((rs.getLong("last_visit_date") /1000));
											DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
												
											//Resultado
											result = new String[]{new Integer(lastCap).toString(),new Integer(lastPage).toString(),df.format(d),rs.getString("url")};
											
										}
									}
								}
							}
							
							break forSplit;
						}
					}
				}				

				//Operação parecida com a anterior, porém voltada para o chrome
				//próxima versão existira um método para seguir o DRY(Don't Repeat Yourself)
				
				stm = con2.createStatement();	
				rs = stm.executeQuery("SELECT url,title,last_visit_time FROM urls WHERE title like '%"+keyword+"%'");
					
				while(rs.next() )
				{
					String[] split = rs.getString("title").split("[^a-zA-Z0-9]");
					
					int pageFound = split.length-1;
					 for(int i=0;i<split.length;i++)
					 {
						 if(split[i].matches("Page"))
						 {
							 pageFound = i;						 
							
						 }
					 }
					
					forSplit : for(int i=pageFound;i>=0;i--)// for(String s :split)
					{
						
						if(split[i].matches("\\d+"))
						{ 
							int x = Integer.parseInt(split[i].trim());							
							
							if(keyword.equals("Degrees"))
							System.out.println(x);

							if(keyword.equals("Degrees"))
								System.out.println(rs.getString("title"));
							
							
							if(x>lastCap)
							{
								lastCap = x;						
								
								for(int y = i;y<split.length;y++)
								{
									if(split[y].matches("Page"))
									{
										
										if(y<split.length-1)							
											lastPage = Integer.parseInt(split[y+1].trim());		
											
										
									}
								}
								
								java.util.Date d = new Date(((rs.getLong("last_visit_time")/1000000l-11644473600l)*1000));
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
									
								result = new String[]{new Integer(lastCap).toString(),new Integer(lastPage).toString(),df.format(d),rs.getString("url")};
								
							}
							
							if(lastCap == x)
							{
								for(int y = i;y<split.length;y++)
								{
									if(split[y].matches("Page"))
									{
										
										int z =0;
										
										if(y<split.length-1)
											z=Integer.parseInt(split[y+1].trim());	
										if(z>lastPage)
										{
											lastPage = z;										
											
											
											java.util.Date d = new Date((rs.getLong("last_visit_time") /1000000l-11644473600l)*1000);
											DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
												
											result = new String[]{new Integer(lastCap).toString(),new Integer(lastPage).toString(),df.format(d),rs.getString("url")};
											
										}
									}
								}
							}
							
							break forSplit;
						}
					}
				}
				
				//Fecha as conexões
				stm.close();
				con.close();
				con2.close();
			}
			catch(Exception e){e.printStackTrace();}
			finally{
				try{
					//Deleta a copia dos históricos
					Files.deleteIfExists(target);
					Files.deleteIfExists(target2);
					System.out.println("delete");
				}
				catch(IOException ioe){ioe.printStackTrace();}
			}	
		
			return result;			
		
		
	}
	
}
