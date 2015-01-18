package NHHJava;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;

import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
/*
 * Clase:InterfazUsuario
 * Creador:Riki Gomez
 * 
 * Clase de la interfaz de usuario de la aplicacion creada a partir del plugin de eclipse windowBuilder,
 * https://eclipse.org/windowbuilder/, esta clase es la main de la aplicacion, desde esta se crea el objeto
 * NHH de la clase NoHayHuevos y se inicializa con el objeto autorizacion. Despues de eso se inicializa el
 * thread de NHH que realiza la funcion del bot.
 */
public class InterfazUsuario {
/////////////////////////////////// ATRIBUTOS ///////////////////////////////////////////////////////////////////////////////
	private JFrame frmventana;
	private JTextField PIN;
	private JTextField TextoURL;
	private Thread BOT;
	private Autorizacion Autorizacion;
	private JButton btnValidarPin;
	private JButton btnCerraraplicacion;
//////////////////////////////////// METODOS ///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Funcion Principal que lanza la ventana de la aplicacion
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazUsuario window = new InterfazUsuario();
					window.frmventana.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws TwitterException 
	 */
	public InterfazUsuario() throws TwitterException {
		Autorizacion=new Autorizacion(); //inicializamos autorizacion
		try{ //obtenemos requestTOken y la URL de validacion, esto se hace aqui para poder obtener la url y mostrarla en la labelURL
			//Autorizacion.requestToken=Autorizacion.OAuthTwitter.getOAuthRequestToken();
			Autorizacion.SetRequestToken(Autorizacion.getOAuthTwitter().getOAuthRequestToken());  //obtenemos la clave de acesso al servicio a traves del OAuth
			System.out.println("Request Token Obtenido con exito: "+Autorizacion.getRequestToken().getToken());
			System.out.println("Request Token secret: "+ Autorizacion.getRequestToken().getTokenSecret());
			//Autorizacion.url=Autorizacion.requestToken.getAuthorizationURL();
			Autorizacion.setURL(Autorizacion.getRequestToken().getAuthorizationURL());
			System.out.println("URL: "+Autorizacion.getURL());
			Autorizacion.openURL(Autorizacion.getURL()); //abrimos el navegador
			}
			catch (TwitterException ex) //si se produce una exception (fallo al obtener las claves)
			{
				Logger.getLogger(BotTwitter.class.getName()).log(Level.SEVERE, null, ex); //logea el error
			}
		
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmventana = new JFrame();
		frmventana.setTitle("Bot para twitter");
		frmventana.setBounds(100, 100, 845, 162);
		frmventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblIntroduzcaPin = new JLabel("Introduzca Pin:");
		
		PIN = new JTextField();
		PIN.setColumns(10);
		
		btnValidarPin = new JButton("Validar Pin");
		
		btnValidarPin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//codigo para validar el pin
				
				if(Autorizacion.ObtenerCLave(PIN.getText()))
				{
					System.out.println("Clave obtenida correctamente");
					try{
						try {
							BOT=new BotTwitter(Autorizacion,"NHH"); //creamos NHH con la autorizacion
						} catch (IOException e) {
							
							e.printStackTrace();
						} //inicializamos la aplicacion con la autorizacion
						BOT.start(); //iniciamos el thread para que se ejecute independientemente
						btnValidarPin.setEnabled(false);//bloqueamos el boton de validacion
						PIN.setText("Pin correcto, aplicacion en ejecuccion");
						PIN.setEnabled(false);
						btnCerraraplicacion.setEnabled(true);// se activa el boton de terminar aplicacion
						
					}catch(TwitterException e)
					{
						System.out.println("error: "+e);
					}		
				}
				else
				{	
					PIN.setText("Pin incorrecto,intentelo de nuevo");
					System.out.println("Clave no obtenida");
				}
							
			}
			
		});
		
		JLabel LabelAUX = new JLabel("Si no se abre el navegador,abra esta pagina en su navegador:");
		LabelAUX.setForeground(Color.GRAY);
		
		TextoURL = new JTextField();
		TextoURL.setEditable(false);
		TextoURL.setColumns(10);
		TextoURL.setText(Autorizacion.getURL());//url de la pagina de validacion
		
		btnCerraraplicacion = new JButton("CerrarAplicacion");
		btnCerraraplicacion.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//si se clikea para cerrar
				BOT.stop();//se detiene la aplicacion
				PIN.setText("Aplicacion detenida, cierre la ventana");
				
			}
		});
		btnCerraraplicacion.setEnabled(false);
		GroupLayout groupLayout = new GroupLayout(frmventana.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblIntroduzcaPin)
							.addGap(18)
							.addComponent(PIN, 312, 312, 312)
							.addGap(18)
							.addComponent(btnValidarPin))
						.addComponent(LabelAUX, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(TextoURL, GroupLayout.PREFERRED_SIZE, 568, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
							.addComponent(btnCerraraplicacion)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(13)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIntroduzcaPin, GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
						.addComponent(PIN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnValidarPin))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(LabelAUX, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(TextoURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCerraraplicacion)))
					.addGap(19))
		);
		frmventana.getContentPane().setLayout(groupLayout);
	}

}
