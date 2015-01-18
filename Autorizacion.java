package NHHJava;
//importamos las clases necesarias
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;
/*
 * Clase:Autorizacion
 * Creador:Mario Pérez Esteso (https://github.com/GeekyTheory/Java---Cliente-Twitter/blob/master/TwitterJavaGT/src/twitterjavagt/Autorizacion.java)
 * Modificaciones:Riki gomez
 * 
 * Clase que permite autorizar la aplicacion para que pueda usar la API REST de twitter y que permita a 
 * la aplicacion autentificarse como el usuario de twitter.
 *  
 */


public class Autorizacion {
	
/////////////////////////////////////////// Atributos ///////////////////////////////////////////////////////////////////////////////////////////////
	private RequestToken requestToken;	 			//clave de acesso al sevicio
	private AccessToken accessToken;  				//clave de acesso a fuentes protegidas, se obtiene a traves del OAuth pasandole el pin y el requestToken
	private String url; 							//url donde se pide la verificacion del pin
	private String pin; 							//pin que autoriza a la aplicacion a ejecutarse
	//Sirven para configurar java para que me permita usar la API de twitter
	private ConfigurationBuilder configBuilder;
	private Twitter OAuthTwitter;
	//claves que se obtienen para poder utilizar la API y dar permiso a la aplicacion, estas se consiguen al registrar la aplicacion en twitter(https://apps.twitter.com/) en el apartado "key and access Tokens"
	private String ConsumerKey="clave";	
	private String ConsumerSecret="clave";
	
////////////////////////////////////////////// Metodos ////////////////////////////////////////////////////////////
/**
 *  Constructor que inicializa las variables a null y configura java para que te permita usar la API de twitter
 ** @throws IOException
 * @throws TwitterException
 */
	
Autorizacion()  throws TwitterException //constructor de la clase
{
	requestToken=null; //clave de acesso al sevicio
	accessToken=null; //clave de acesso a fuentes protegidas, se obtiene a traves del OAuth pasandole el pin y el requestToken
	url=null; //url donde se pide la verificacion del pin
	pin=null; //pin que autoriza a la aplicacion a ejecutarse
	configBuilder=new ConfigurationBuilder();
	//configuro java para que me permita usar la API de twitter
	configBuilder.setDebugEnabled(true).setOAuthConsumerKey(ConsumerKey).setOAuthConsumerSecret(ConsumerSecret);
	OAuthTwitter = new TwitterFactory(configBuilder.build()).getInstance();
}	
/**  Metodo que obtiene las claves AccessToken y RequestToken, a traves de la clave que le pasan por parametro se usa en AutorizacionVentana
 *  @param codigo, pin para validar la aplicacion
 *  @throws IOException
 *  @throws TwitterException
 * 
 */

public boolean ObtenerCLave(String codigo)
{
	//////////////////////////validacion del pin ////////////////////////
	//pide el pin por pantalla
	System.out.print("Introduzca el pin y pulse intro. PIN: ");
	pin=codigo;
	try{
	if(pin.length()>0)
	accessToken=OAuthTwitter.getOAuthAccessToken(requestToken,pin);
	else
	return false;
	} catch(TwitterException e)
	{	//muestra mensaje de error informando que se ha salido del programa
	System.out.println("Fallo a la hora de autenficiar el pin: Excepcion:"+e);
	System.out.println("Fatal error, saliendo del programa");
	return false;
	}
	
	return true;
}
/**  Metodo que obtiene las claves AccessToken y RequestToken a traves de la consola
 *  @throws IOException
 *  @throws TwitterException
 * 
 */
public void ObtenerClave() throws IOException, TwitterException
{
	BufferedReader lectorTeclado = new BufferedReader(new InputStreamReader(System.in)); //permite leer de consola
	/////////////////////// Obtenemos las claves /////////////////////////////
	do{
		try
		{
			requestToken=OAuthTwitter.getOAuthRequestToken(); //obtenemos la clave de acesso al servicio a traves del OAuth
			System.out.println("Request Token Obtenido con exito: "+requestToken.getToken());
			System.out.println("Request Token secret: "+ requestToken.getTokenSecret());
			url=requestToken.getAuthorizationURL();
			System.out.println("URL: "+url);
		} 
		catch (TwitterException ex) //si se produce una exception (fallo al obtener las claves)
		{
			Logger.getLogger(BotTwitter.class.getName()).log(Level.SEVERE, null, ex); //logea el error
		}
		
		////////////////////////// validacion del pin ////////////////////////
		openURL(url); //abirmos el navegador
		//pide el pin por pantalla
		System.out.print("Introduzca el pin y pulse intro. PIN: ");
		pin=lectorTeclado.readLine(); //leemos el pin
		try{
		if(pin.length()>0)
			accessToken=OAuthTwitter.getOAuthAccessToken(requestToken,pin);
		else
			accessToken=OAuthTwitter.getOAuthAccessToken(requestToken);
		} catch(TwitterException e)
		{	//muestra mensaje de error informando que se ha salido del programa
			System.out.println("Fallo a la hora de autenficiar el pin: Excepcion:"+e);
			System.out.println("Fatal error, saliendo del programa");
			System.exit(-1);
		}
	}while(accessToken==null);
	
	System.out.println("Access Tokens obtenidos con exito.");
	System.out.println("Access Token: " + accessToken.getToken());
	System.out.println("Access Token secret: " + accessToken.getTokenSecret());

}

/**
 * Metodo que devuelve el AccessToken obtenido
 * @return accessToken
 */
public AccessToken GetAccessToken()
{
	return accessToken;
}

/**
 * Metodo que devuelve el OAuthTwitter obtenido
 * @return ConfigurationBuilder
 */

/**
 *  Abre el navegador con la url pasada por parametro
 * @param url
 */
public void openURL(String url) {
    String osName = System.getProperty("os.name");
    try {
        if (osName.startsWith("Windows")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else if (osName.startsWith("Mac OS X")) {
            // Runtime.getRuntime().exec("open -a safari " + url);
            // Runtime.getRuntime().exec("open " + url + "/index.html");
            Runtime.getRuntime().exec("open " + url);
        } else {
            System.out.println("Fallo a la hora de abrir el navegador, por favor abra su navegdor y vaya a la direccion:  "+ url);
        }
    } catch (IOException e) {
        System.out.println("Fallo a la hora de abrir el navegador, por favor abra su navegdor y vaya a la direccion:  " + url);
        e.printStackTrace();
    }
}
/**
 * funcion que Asigna al atributo RequestToken el valor x pasado por parametro
 * @param x RequestToken
 */
public void SetRequestToken( RequestToken x)
{
	requestToken=x;
}

/**
 * funcion que devuelve el atributo RequesToken
 * @return requestToken
 */
public RequestToken getRequestToken()
{
	return requestToken;
}
/**
 * funcion que devuelve el atributo OAutTwitter
 * @return OAuthTwitter 
 */
public Twitter getOAuthTwitter()
{
	return OAuthTwitter;
}
/**
 * funcion que asigna al atributo AccessToken el nuevo valor pasado por parametro
 * @param x  AccessToken
 */
public void setAccessToken(AccessToken x)
{
	accessToken=x;
}
/**
 * funcion que devuelve el atributo accessToken
 * @return accessToken
 */
public AccessToken getAccessToken()
{
	return accessToken;
}
/**
 * funcion que asigna al atributo URL el nuevo valor pasado por parametro
 * @param x  String
 */
public void setURL(String x)
{
	url=x;
}
/**
 * funcion que devuelve el atributo URL
 * @return URL string
 */
public String getURL()
{
	return url;
}
/**
 * funcion que asigna al atributo PIN el nuevo valor pasado por parametro
 * @param x string
 */
public void setPin(String x)
{
	pin=x;
}
/**
 * funcion que devuelve el atributo pin
 * @return pin string
 */
public String getPin()
{
	return pin;
}


}
