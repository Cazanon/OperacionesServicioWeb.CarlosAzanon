package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ServicioWebBD extends Activity {

	private int INSERTAR=1;
	private int MODIFICAR=2;
	private int BORRAR=3;
	private int CONFIGURACION=4;
	private boolean config=false;
	private String usuario="miw04";
	private String pass="139285330";
	private String urlConexion="http://demo.calamar.eui.upm.es/dasmapi/v1";
	private String conexion=urlConexion+"/"+usuario+"/connect/"+pass;
	
	private EditText dni;
	private String URL="http://demo.calamar.eui.upm.es/dasmapi/v1/miw04/fichas";
	//http://demo.calamar.eui.upm.es/dasmapi/v1/miw04/connect/139285330
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

		dni = (EditText)findViewById(R.id.dni);
        new Conexion().execute();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

    private class Conexion extends AsyncTask<String,Void,String>{
    
		@Override
		protected String doInBackground(String... params) {		
			String datos = "";
			try {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpGet = new HttpGet(conexion);
				HttpResponse response = httpClient.execute(httpGet);
				datos = EntityUtils.toString(response.getEntity());
				httpClient.close();
			}catch(IOException e){
				procesarError();
				return "";
			}
			return datos;	
		}
		
		@Override
		protected void onPostExecute(String datos){
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int n=arrayDatos.getJSONObject(0).getInt("NUMREG");
				if(n!=0){
					procesarError();
					return;
				}
			}catch(Exception e){
				procesarError();
				return;
			}	
			procesarConfiguracion();			
		}	
		
		private void procesarError(){
			if(config){
				String mensaje="Fallo al Conectar:\nUsuario: "+usuario+" Contraseña: "+pass+"\nURL: "+conexion;
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				config=false;
			}else{
				Toast.makeText(ServicioWebBD.this, "Fallo al conectar", Toast.LENGTH_SHORT).show();
			}
			deshabilitarFuncionalidad();
		}
		
		private void procesarConfiguracion(){
			if(config){
				String mensaje="Conexion establecida\nUsuario: "+usuario+" Contraseña: "+pass;
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				config=false;
			}else{
				Toast.makeText(ServicioWebBD.this, "Conexion establecida", Toast.LENGTH_SHORT).show();
			}			
			habilitarFuncionalidad();
		}
    }  	
    
	private void deshabilitarFuncionalidad() {		
		TextView dni = (TextView)findViewById(R.id.dni);
		ImageButton consultar=(ImageButton)findViewById(R.id.consultar);
		ImageButton modificar=(ImageButton)findViewById(R.id.modificar);
		ImageButton insertar=(ImageButton)findViewById(R.id.insertar);
		ImageButton borrar=(ImageButton)findViewById(R.id.borrar);
		
		dni.setEnabled(false);
		consultar.setEnabled(false);
		modificar.setEnabled(false);
		insertar.setEnabled(false);
		borrar.setEnabled(false);		
	}
	
	private void habilitarFuncionalidad() {		
		TextView dni = (TextView)findViewById(R.id.dni);
		ImageButton consultar=(ImageButton)findViewById(R.id.consultar);
		ImageButton modificar=(ImageButton)findViewById(R.id.modificar);
		ImageButton insertar=(ImageButton)findViewById(R.id.insertar);
		ImageButton borrar=(ImageButton)findViewById(R.id.borrar);
		
		dni.setEnabled(true);
		consultar.setEnabled(true);
		modificar.setEnabled(true);
		insertar.setEnabled(true);
		borrar.setEnabled(true);		
	}
	
	public void goConsultar(View v){
    	new ConsultaBD().execute(dni.getText().toString()); 
    }
    
    public void goInsertar(View v){
    	new InsertarBD().execute(dni.getText().toString());
    }
    
    public void goModificar(View v){
    	new ModificarBD().execute(dni.getText().toString());	
    }
    
    public void goBorrar(View v){
    	new BorrarBD().execute(dni.getText().toString());
    }    
    
    public void goConfiguracion(MenuItem item){
    	Intent i=new Intent(ServicioWebBD.this, Configuracion.class);
    	i.putExtra("url", conexion);
		startActivityForResult(i,CONFIGURACION);
	}

    @Override
    public void onActivityResult(int actividad,int resultado,Intent datos){
    	if(actividad!=CONFIGURACION){
	   		String respuesta=datos.getStringExtra("respuesta");
	   		Toast.makeText(ServicioWebBD.this, respuesta, Toast.LENGTH_SHORT).show();
    	}else{
    		String urlRecibida=datos.getStringExtra("url");
    		usuario=datos.getStringExtra("usuario");
    		pass=datos.getStringExtra("pass");
    		if(!urlRecibida.equals("")){
    			conexion=urlRecibida+"/"+usuario+"/connect/"+pass;
        		config=true; 			
    		}
            new Conexion().execute();
    	}
    }
    
    private class ConsultaBD extends AsyncTask<String,Void,String>{
	//Parametro de la entrada
	//Parametro que manda a la interfaz (por ejemplo el progreso)
	//Parametro que devuelve el doInBackground y recibe el post execute
    	
    	//POST -> Inserta registro nuevo
    	//PUT  -> Actualizar registro
    	private ProgressDialog pDialog;
    	private boolean error;
    	
		@Override
		protected void onPreExecute() {			
			super.onPreExecute();
			error=false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")){
				url_final+="/"+dni;
			}
			try {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpGet = new HttpGet(url_final);
				HttpResponse response = httpClient.execute(httpGet);
				datos = EntityUtils.toString(response.getEntity());
				httpClient.close();
			} catch (IOException e) {
				error=true;
				Log.e("Error en la operacion",e.toString());
			}
			return datos;			
		}
		
		@Override
		protected void onPostExecute(String info) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "La consulta genera un error";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(info);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La consulta genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default:
					Intent i = new Intent(ServicioWebBD.this, Consulta.class);
					i.putExtra("datos", info);
					startActivity(i);
				}
			} catch (Exception e) {
				mensaje = "La consulta genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}    	
    }
    
    private class InsertarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para crear el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La insercion genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Intent i = new Intent(ServicioWebBD.this, Insercion.class);
					i.putExtra("dni", dni.getText().toString());
					i.putExtra("url",URL);
					startActivityForResult(i,INSERTAR);
					break;
				default: 
					mensaje = "Ya existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				mensaje = "La insercion genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class ModificarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para modificar el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La modificacion genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe ningun registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default: 
					Intent i = new Intent(ServicioWebBD.this, Modificacion.class);
					i.putExtra("datos", datos);
					i.putExtra("url", URL);
					startActivityForResult(i,MODIFICAR);
				}
			} catch (Exception e) {
				mensaje = "La modificacion genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class BorrarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";

			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para borrar el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "El borrado genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default: 
					Intent i = new Intent(ServicioWebBD.this, Borrado.class);
					i.putExtra("datos", datos);
					i.putExtra("url", URL);
					startActivityForResult(i,BORRAR);
				}
			} catch (Exception e) {
				mensaje = "El borrado genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
