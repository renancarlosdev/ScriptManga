package objetos;

public class Manga {
	
	private String nome,diaHorario,keyword,url,img;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	private int id,ultimoCap,capVerificado,pagina;
	
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDiaHorario() {
		return diaHorario;
	}
	public void setDiaHorario(String diaHorario) {
		this.diaHorario = diaHorario;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUltimoCap() {
		return ultimoCap;
	}
	public void setUltimoCap(int ultimoCap) {
		this.ultimoCap = ultimoCap;
	}
	public int getCapVerificado() {
		return capVerificado;
	}
	public void setCapVerificado(int capVerificado) {
		this.capVerificado = capVerificado;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	

}
