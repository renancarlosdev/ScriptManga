package db;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import objetos.Manga;


/**
 *  Responsável pelas operações no banco de dados
 */
public enum MySQLMangaDAOImpl {
	
	INSTANCE;
	
	private Connection con;
	
	/**
	 * Conecta ao banco de dados que contem as informações dos mangas
	 */
	MySQLMangaDAOImpl()
	{
		try{ con = DriverManager.getConnection("jdbc:mysql:host","user","pass");}
		catch(Exception e){e.printStackTrace();}
	}
	

	/**
	 * Adiciona um manga
	 *
	 * @param      manga  O manga
	 * @param      date   A data da ultima leitura
	 * @param      url    A url da imagem
	 */
	public void addManga(Manga manga,Date date,String url) {
		try(Statement stm = con.createStatement())
		{
			stm.execute("INSERT INTO manga(NOME,ULTIMOCAP,CAPVERIFICADO,PAGINA,DIA,KEYWORD,IMG)" +
						"VALUES('"+manga.getNome()+"',"+manga.getUltimoCap()+","+manga.getCapVerificado() +
						","+manga.getPagina()+",'"+date+"','"+manga.getKeyword()+ "','"+url+ "');");
			
			
		}
		catch(Exception e){e.printStackTrace();}
	}

	
	/**
	 *  Remove um manga
	 *
	 * @param      id    O id do manga a ser removido
	 */
	public void deleteManga(int id) {
		try(Statement stm = con.createStatement())
		{
			stm.execute("DELETE FROM manga WHERE ID ="+id+";");
			ResultSet rs= stm.executeQuery("SELECT MAX(ID) FROM manga;");
			rs.next();		
		}
		catch(Exception e){e.printStackTrace();};
		
	}


	/**
	 * Atualiza a URL do manga
	 *
	 * @param      id     O id do manga a ser atualizado
	 * @param      url    A URL da pagina do manga
	 */
	public void updateMangaURL(int id,String url) {
		try(Statement stm = con.createStatement())
		{
			stm.execute("UPDATE manga SET URL='"+url+
						"' WHERE ID ="+id+";");
			
		}
		catch(Exception e){e.printStackTrace();} System.out.println("");
		System.out.println("END UPDATE");
	}
	
	/**
	 *  Atualzia um manga
	 *
	 * @param      manga  Informações de atualização
	 */
	public void updateManga(Manga manga) {
		try(Statement stm = con.createStatement())
		{
			stm.execute("UPDATE manga SET NOME='"+manga.getNome()+
						"' ,ULTIMOCAP = "+manga.getUltimoCap()+
						", CAPVERIFICADO ="+manga.getCapVerificado()+
						", PAGINA = "+manga.getPagina()+
						", DIA = '"+manga.getDiaHorario()+
						"', KEYWORD = '"+manga.getKeyword()+
						"', IMG = '"+manga.getImg()+
						"', URL = '"+manga.getUrl()+
						"' WHERE ID ="+manga.getId()+";");
			
		}
		catch(Exception e){e.printStackTrace();} System.out.println("");
		System.out.println("END UPDATE");
	}

	
	/**
	 *  Pega um manga
	 *
	 * @param      id    O id do manga a ser retornado
	 *
	 * @return     O manga.
	 */
	public Manga getManga(int id) {
		
		Manga manga = new Manga();
		
		try(Statement stm = con.createStatement())
		{
			
			ResultSet rs = stm.executeQuery("SELECT * FROM manga WHERE ID ="+id+";");
			rs.next();
			
			manga.setCapVerificado(rs.getInt("CAPVERIFICADO"));
			manga.setDiaHorario(rs.getString("DIA"));
			manga.setId(rs.getInt("ID"));
			manga.setKeyword(rs.getString("KEYWORD"));
			manga.setNome(rs.getString("NOME"));
			manga.setPagina(rs.getInt("PAGINA"));
			manga.setUltimoCap(rs.getInt("ULTIMOCAP"));
			manga.setUrl(rs.getString("URLNEW"));
		}
		catch(Exception e){e.printStackTrace();}
		return manga;
	}

	/**
	 * Pega todos os mangas
	 *
	 * @return     Todos os mangas
	 */
	public List<Manga> getAllMangas() {
		
		List<Manga> mangas = new LinkedList<Manga>();
		
		try(Statement stm = con.createStatement())
		{
			ResultSet rs = stm.executeQuery("SELECT * FROM manga;");
			
			while(rs.next())
			{
				Manga manga = new Manga();
				
				manga.setCapVerificado(rs.getInt("CAPVERIFICADO"));
				manga.setDiaHorario(rs.getString("DIA"));
				manga.setId(rs.getInt("ID"));
				manga.setKeyword(rs.getString("KEYWORD"));
				manga.setNome(rs.getString("NOME"));
				manga.setPagina(rs.getInt("PAGINA"));
				manga.setUltimoCap(rs.getInt("ULTIMOCAP"));
				manga.setUrl(rs.getString("URL"));
				manga.setImg(rs.getString("IMG"));
				
				mangas.add(manga);
				
			}
		}
		catch(Exception e){e.printStackTrace();}
	
		
		
		return mangas;
	}


	/**
	 *  Adiciona uma URLNEW no manga
	 *
	 * @param      id    o identifier
	 * @param      url   a url
	 */
	public void addUrlManga(int id,String url) {
		
		try(Statement stm = con.createStatement())
		{
		
			stm.execute("UPDATE manga  SET URLNEW='"+url+"' WHERE ID="+id);
			
		}
		catch(Exception e){e.printStackTrace();}
	}

}
