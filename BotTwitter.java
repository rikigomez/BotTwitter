package NHHJava;

import java.io.IOException;
import java.util.List;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
/* 
 * Clase: BotTwitter
 * Creador: Riki Gomez
 * 
 * Esta clase contiene los metodos necesarios para realizar la funcionalidad del bot, el metodo principal
 * es el Botear() este metodo engloba todos los pasos que realiza el bot.
 * Importante esta clase hereda de Thread para que se ejecute el proceso de Botear() de forma independiente
 * del proceso principal, que seria el InterfazUsuario. Es decir la funcion Botear() se ejecutara en
 * un thread(hilo) a parte. 
 */

public class BotTwitter extends Thread { //Hereda de Thread
/////////////////////////////////// Atributos /////////////////////////////////////////////////
	private Autorizacion Autorizacion;       //clase de Autorizacion de la cual obtenemos los token
	private String Token;					//AccessToken de la Autorizacion
	private String TokenSecret;				//AccessToken secreto de la Autorizacion
	private Twitter twitter;				//Nos permite interactuar con twitter
	private String[] Usuarios;				//Array con las nombres de las cuentas para evitar repetir tweets
	private String miCuenta="nombre la cuenta sin @"; //nombre de la cuenta sin @ para evitar autoenviarse tweets
	private String[] mensaje;				//Array de mensajes para enviar
	private boolean[] mensajeintentado;   	//Array de valores booleanos que  indica  si el mensaje ya se ha intentado poner
/////////////////////////////////// Metodos //////////////////////////////////////////////////////////7
/**
 * Constructor de la clase que configura y obtiene los atributos necesarios de la clase autorizacion para poder manejar twitter
 * @param Auto   Autorizacion que permite a la aplicacion usar twitter
 * @throws IOException
 * @throws TwitterException
 */
BotTwitter(Autorizacion Auto,String nombre) throws IOException, TwitterException
{	
	super(nombre);// llamamos al constructor del padre con el nombre del hilo que se va a crear
	this.Autorizacion=Auto; //obtenemos la autorizacion
	this.Token=Autorizacion.GetAccessToken().getToken(); //obtiene el token de la autorizacion
	this.TokenSecret=Autorizacion.GetAccessToken().getTokenSecret(); //obtiene el token secreto de la autorizacion
	this.twitter=this.Autorizacion.getOAuthTwitter(); //obtenemos el objeto para interactuar con twitter

	mensaje=new String[X]; // X=numero de mensajes que se quieren tener
	mensajeintentado=new boolean[6];
	Usuarios=new String[100];
	//Inicializamos el array con los mensajes de animo, 
	mensaje[0]="mensaje"; 
	mensaje[1]="mensaje"; 
	mensaje[2]="mensaje"; 
	mensaje[3]="mensaje";  
	mensaje[4]="mensaje"; 
	mensaje[5]="mensaje"; 
	//asi hasta los X-1 mensajes
	for(int i=0;i<mensaje.length;i++) //recorremos el array de mensjaeintentado a false
		mensajeintentado[i]=false;
	for(int i=0;i<Usuarios.length;i++)
		Usuarios[i]="";
}
/**
 * Funcion que realiza al thread
 * 
 */
public void run()
{
	try {
		this.Botear();
	} catch (TwitterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
/**
 *  Funcion principal cuyo objetivo es obtener una lista con tweets que contienen las palabras claves y enviarle un tweet
 *  de apoyo. Ademas si mencionan a la cuenta le envia el mensaje definido y ahce RT y fav al tweet
 *  @throws TwitterException 
 */
public void Botear() throws TwitterException
{	
	String contenidoT; //string con el contenido del tweet
	Query busqueda; //clase que nos permite buscar en twitter;
	QueryResult resultado; //clase que nos devuelve los resultados de la busqueda;
	List<Status> BusquedaTweets; //lista que contiene los tweets de la busqueda
	StatusUpdate  Rtweet; //respuesta para el tweet
	boolean encontrado=false; //indica si se ha encontrado un usuario y se ha enviado correctamente
	busqueda=new Query();
	busqueda.setQuery("palabas claves"); //contenido que queremos buscar
	busqueda.setLang("es"); //en español
	busqueda.setResultType(Query.RECENT); //tweets recientes, es decir que no cuente los populares
	busqueda.setCount(10); //seleccionamos el numero de tweets de resultados de la busqueda
	int finLista=0;
	
	while(1<2)  //bucle infinito para que no pare la aplicacion (cameo a deadmau5)
	{
		try{
				resultado=twitter.search(busqueda); //obtenemos los resultados de la busqueda
				BusquedaTweets=resultado.getTweets(); //obtenemos la lista con los tweets
				for(Status tweet:BusquedaTweets) //recorremos los tweets de la busqueda
				{
					while(!encontrado)
					{
						int Nmensaje=this.RandomInt(); //numero random entre 0 y el numero de mensajes
						
						if(!mensajeintentado[Nmensaje] && !YaEnviado(tweet.getUser().getScreenName())) //si no se ha probado con el mensaje y no se ha enviado ningun tweet a ese usuario 
						{	
							mensajeintentado[Nmensaje]=true; // se marca como intentado
							contenidoT="@"+tweet.getUser().getScreenName()+" "+mensaje[Nmensaje];// contenido del tweet
							if(contenidoT.length()<=140 &&  !tweet.getUser().getScreenName().equals(miCuenta)  && !tweet.isRetweet()) //si el contenido se puede enviar, no me autorenvio y no es RT
							{
									
								if(finLista==100 )
									finLista=0; //reiniciamos a 0		
							    Usuarios[finLista]=tweet.getUser().getScreenName(); //metemos el usuario en la lista de enviados
							    System.out.println("Añadido:"+ Usuarios[finLista]+" en pos: "+finLista);
							    finLista++;
								Rtweet= new StatusUpdate(contenidoT);//obtenemos el tweet para para enviar
								Rtweet.inReplyToStatusId(tweet.getId());//decimos que el tweet es respuesta al tweet seleccionado
								encontrado=EnviarTweet(Rtweet);//escribimos el tweet
								if(encontrado)
								{
									System.out.println("Enviado con exito:");
									twitter.createFavorite(tweet.getId());//hacemos FAV al tweet
									System.out.println(contenidoT);
									
								}
								else
									System.out.println("No enviado");
							}
							else
								if(tweet.isRetweet())
									System.out.println("Es un RT, no se envia");
						}
						else
						{
							if(tweet.getUser().getScreenName().equals(miCuenta))
								System.out.println("Tweet mio, no se envia");
							if(SinMensajes() || !encontrado)
									break; //rompemos el bucle ya que no hay ningun mensaje valido para enviar
						}
						
						if (encontrado) //si se ha encontrado el tweet y se ha enviado
							break; //rompemos el bucle
					}//while
					for(int i=0;i<mensaje.length;i++) //recorremos el array de mensajeintentado a false
						mensajeintentado[i]=false;
					encontrado=false; //reiniciamos encontrado
				}//for
		RTRespuestas(); //RT,FAV y respuestas a las respuestas de los tweets
		TiempoDeEspera(4);//esperamos 4 minutos
		
		}catch (TwitterException e)
		{
			System.out.println("Error en Botear: "+e);
			break; //salimos del bucle;
		}
		
	}//while Infinito
	
}
/**
 * Funcion que devuelve si el usuario que se le va a responder se le ha enviado un mensaje
 * 
 * @param nombre de la cuenta
 * @return false no se ha enviado, true si se ha enviado
 * 
 */

private boolean YaEnviado(String nombre)
{
	boolean esta=false;
	for(int i=0; i<100 && !esta;i++)	
	if(nombre.equals(Usuarios[i]))
		esta=true;
	if(esta)
		System.out.println("ya esta en la lista");
	else
		System.out.println("No esta en la lista");
	return esta;	
}
/**
 * 	Funcion indica si el array de mensajeintentado esta todo a true, es decir si se ha intentado con todos
 * 
 * @return boolean indica si esta todos los elementos en true
 */
private boolean SinMensajes()
{
	boolean vacio=true;
	for(int i=0; i<mensajeintentado.length && vacio;i++)
		vacio=mensajeintentado[i];
	return vacio;
	
}
/**
 * 	Funcion que envia un tweet teniendo como contenido un string pasado por parametro
 * @param tweet que se va a escribir en el tweet
 * @return boolean indicando si se ha enviado correctamente
 */
private boolean EnviarTweet(StatusUpdate contenido)
{
	boolean correcto=true;
	try {		
			twitter.updateStatus(contenido);	
			
	} catch(TwitterException e)
	{
		System.out.println("Error a la hora de enviar el tweet: "+e);
		correcto=false;
	}
	return correcto;	
}
/**
 * Funcion que bloquea la aplicacion durante el tiempo(en minutos) expresado
 * @param tiempo, minutos que se quieren bloquear la aplicacion
 */
private void TiempoDeEspera(int tiempo) { //en minutos
   tiempo=tiempo*60;//pasamos a segundos
    try {            
        for (int i=tiempo; i>=0; i--) { 
             Thread.sleep(1000);//dormir un segundo
             //if(tiempo%60==0)//en minutos
            	 //System.out.println("Tiempo de espera :"+i/60+" minutos");
        }
        System.out.println("Tiempo de espera finalizado");
    } catch(InterruptedException b) {
         System.out.println("Error en el tiempo de espera: "+ b);
    }
}
/**
 * Funcion que marca como favorito y hace RT a las respuestas, ademas les envia un tweet indicandoles que no entiene lo que dicen que solo es un bot.
 */
private void RTRespuestas()
{
	List<Status> TweetsRespuestas;//lista con los tweets del apartado conecta de twitter
	StatusUpdate Respuesta; //respuesta al tweet
	String contenido="";
	try{
	TweetsRespuestas=twitter.getMentionsTimeline();//obtenemos la lista
	for(Status tweet:TweetsRespuestas)
	{
	
		if(!tweet.isRetweetedByMe())
		{
			twitter.retweetStatus(tweet.getId());//hacemos RT al tweet
			twitter.createFavorite(tweet.getId());//hacemos FAV al tweet
			contenido="@"+tweet.getUser().getScreenName()+" mensaje que se quiere enviar cuando alguien menciona la cuenta";//contenido de la respuesta
			Respuesta=new StatusUpdate(contenido); //creamos el tweet
			Respuesta.inReplyToStatusId(tweet.getId()); //indicamos que es una respuesta
			if(EnviarTweet(Respuesta)) //envia el tweet y muestra en consola el resultado
				System.out.println("Respuesta enviada correctamente");
			else
				System.out.println("Respuesta no enviada correctamente");	
		}	
	}
	}
	catch(TwitterException e)
	{
		System.out.println("Error a la hora de hacer RT a las respuestas: "+e);
	}
}

/**
 * Funcion que devuelve un numero random entre 0 y el numero de elementos del array de mensaje
 * 
 * 
 * @return int Numero random entre 0 y el numero de mensajes
 */
private int RandomInt()
{	
	return (int)(Math.random()*mensaje.length+0); //ajustamos el random desde 0 a el numero de mensajes
}
}
